package com.epmapat.erp_epmapat.gestiondocumental.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class WorkflowDocumentosService {

    public record FileDownload(Resource resource, String contentType, long size, String originalFilename) {}

    private final JdbcTemplate jdbc;
    private final Path storageRoot;

    public WorkflowDocumentosService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.storageRoot = Paths.get(System.getProperty("user.home"), "erp-gd-files");
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar almacenamiento de archivos GD", e);
        }
    }

    private String resolveRoleFromExternal(String userId, String fallbackRole) {
        String fallback = (fallbackRole == null || fallbackRole.isBlank()) ? "RECEPCION" : fallbackRole.trim().toUpperCase();
        if (userId == null || userId.isBlank()) return fallback;

        String[] queries = new String[] {
                "SELECT UPPER(COALESCE(rol,'RECEPCION')) FROM public.usuario_roles WHERE user_id::text = ? AND COALESCE(activo,true)=true LIMIT 1",
                "SELECT UPPER(COALESCE(rol,'RECEPCION')) FROM public.usuario_roles WHERE idusuario::text = ? AND COALESCE(activo,true)=true LIMIT 1",
                "SELECT UPPER(COALESCE(nombre_rol,'RECEPCION')) FROM public.usuario_roles WHERE user_id::text = ? AND COALESCE(activo,true)=true LIMIT 1"
        };

        for (String q : queries) {
            try {
                String role = jdbc.queryForObject(q, String.class, userId);
                if (role != null && !role.isBlank()) return role.trim().toUpperCase();
            } catch (Exception ignored) {}
        }
        return fallback;
    }

    public void ensureActionAllowed(String docId, String userId, String role, String action) {
        String userRole = resolveRoleFromExternal(userId, role);
        Set<String> allowed;
        switch (action) {
            case "EMITIR" -> allowed = Set.of("SUPERVISOR", "ADMIN");
            case "RECIBIR" -> allowed = Set.of("RECEPCION", "SUPERVISOR", "ADMIN");
            case "DERIVAR", "RESPONDER" -> allowed = Set.of("RESPONSABLE", "SUPERVISOR", "ADMIN");
            default -> allowed = Set.of("RECEPCION", "RESPONSABLE", "SUPERVISOR", "ADMIN");
        }
        if (!allowed.contains(userRole)) {
            throw new SecurityException("No autorizado para acción " + action + " con rol " + userRole);
        }

        if (userId != null && !userId.isBlank()) {
            Integer access = jdbc.queryForObject("""
                    SELECT COUNT(*)
                    FROM documentos d
                    WHERE d.id::text = ?
                      AND (
                        d.owner_user_id::text = ?
                        OR d.creado_por::text = ?
                        OR EXISTS (SELECT 1 FROM documento_destinatarios dd WHERE dd.documento_id = d.id AND dd.to_user_id::text = ?)
                        OR EXISTS (SELECT 1 FROM documento_derivaciones dv WHERE dv.documento_id = d.id AND dv.para_user_id::text = ?)
                      )
                    """, Integer.class, docId, userId, userId, userId, userId);
            if (access == null || access == 0) {
                throw new SecurityException("No autorizado para este documento");
            }
        }
    }

    private String currentDocState(String docId) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT estado FROM documentos WHERE id::text = ?", docId);
        if (rows.isEmpty()) throw new RuntimeException("Documento no encontrado");
        return String.valueOf(rows.get(0).get("estado"));
    }

    private void ensureDocStateForAction(String docId, String action, Set<String> allowedStates) {
        String state = currentDocState(docId);
        if (!allowedStates.contains(state)) {
            throw new RuntimeException("No se puede " + action + " en estado: " + state + " (permitidos: " + String.join(", ", allowedStates) + ")");
        }
    }

    private void logAuditEvent(String docId, String actorUserId, String actorRole, String action, String fromState, String toState, String detail) {
        try {
            jdbc.update("""
                    INSERT INTO documento_eventos (id, documento_id, actor_user_id, actor_rol, evento, detalle, created_at)
                    VALUES (
                        gen_random_uuid(),
                        ?::uuid,
                        ?::uuid,
                        ?,
                        ?,
                        jsonb_build_object('action', ?, 'from', ?, 'to', ?, 'detail', ?),
                        now()
                    )
                    """,
                    docId,
                    actorUserId,
                    actorRole == null ? "SISTEMA" : actorRole,
                    action,
                    action,
                    fromState,
                    toState,
                    detail
            );
        } catch (Exception ignored) {
        }
    }

    public String assign(String docId, Map<String, Object> body) {
        return jdbc.queryForObject("""
                INSERT INTO documento_asignaciones(
                    documento_id, asignado_a_user_id, asignado_por_user_id,
                    dependencia_id, rol_responsable, principal, estado, observacion
                ) VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, COALESCE(?, true), 'ACTIVA', ?)
                RETURNING id::text
                """, String.class,
                docId,
                body.get("assigned_to_user_id"),
                body.get("assigned_by_user_id"),
                body.get("dependency_id"),
                body.getOrDefault("role", "RESPONSABLE"),
                body.get("principal"),
                body.get("note")
        );
    }

    public String derive(String docId, Map<String, Object> body) {
        String fromState = currentDocState(docId);
        ensureDocStateForAction(docId, "derivar", Set.of("RECIBIDO", "EN_REVISION", "DERIVADO"));

        String derivationId = jdbc.queryForObject("""
                INSERT INTO documento_derivaciones(
                    documento_id, de_user_id, de_dependencia_id,
                    para_user_id, para_dependencia_id,
                    sumilla, requiere_respuesta, fecha_plazo, estado
                ) VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, COALESCE(?, false), ?::timestamp, 'PENDIENTE')
                RETURNING id::text
                """, String.class,
                docId,
                body.get("from_user_id"), body.get("from_dependency_id"),
                body.get("to_user_id"), body.get("to_dependency_id"),
                body.get("note"), body.get("requires_response"), body.get("due_at")
        );

        jdbc.update("""
                UPDATE documentos
                SET estado = 'DERIVADO', actualizado_en = now()
                WHERE id::text = ? AND estado IN ('RECIBIDO','EN_REVISION')
                """, docId);

        String toState = currentDocState(docId);
        logAuditEvent(
                docId,
                body.get("from_user_id") == null ? null : String.valueOf(body.get("from_user_id")),
                body.get("user_role") == null ? null : String.valueOf(body.get("user_role")),
                "DERIVAR",
                fromState,
                toState,
                body.get("note") == null ? "Derivación registrada" : String.valueOf(body.get("note"))
        );

        return derivationId;
    }

    public Map<String, Object> deriveBulk(String docId, Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> targets = (List<Map<String, Object>>) body.get("targets");
        List<String> ids = new ArrayList<>();
        if (targets != null) {
            for (Map<String, Object> t : targets) {
                if (t.get("to_user_id") == null && t.get("to_dependency_id") == null) continue;
                String id = derive(docId, Map.of(
                        "from_user_id", body.get("from_user_id"),
                        "from_dependency_id", body.get("from_dependency_id"),
                        "to_user_id", t.get("to_user_id"),
                        "to_dependency_id", t.get("to_dependency_id"),
                        "note", body.get("note"),
                        "requires_response", body.get("requires_response"),
                        "due_at", body.get("due_at")
                ));
                ids.add(id);
            }
        }
        return Map.of("created", ids.size(), "derivation_ids", ids);
    }

    public List<Map<String, Object>> listDerivations(String docId) {
        return jdbc.queryForList("""
                SELECT id, documento_id, de_user_id, de_dependencia_id, para_user_id, para_dependencia_id,
                       sumilla, requiere_respuesta, fecha_plazo, estado, leido_en, respondido_en, cerrado_en, creado_en
                FROM documento_derivaciones
                WHERE documento_id = ?::uuid
                ORDER BY creado_en DESC
                """, docId);
    }

    public Map<String, Object> pendingDerivations(String toUserId, String toDependencyId, int page, int pageSize) {
        if ((toUserId == null || toUserId.isBlank()) && (toDependencyId == null || toDependencyId.isBlank())) {
            throw new IllegalArgumentException("Debes enviar to_user_id o to_dependency_id para consultar pendientes");
        }

        page = Math.max(1, page);
        pageSize = Math.max(1, Math.min(200, pageSize));
        int offset = (page - 1) * pageSize;

        StringBuilder where = new StringBuilder(" d.estado IN ('PENDIENTE','LEIDO','EN_GESTION') ");
        ArrayList<Object> params = new ArrayList<>();
        if (toUserId != null && !toUserId.isBlank()) { where.append(" AND para_user_id::text = ? "); params.add(toUserId); }
        if (toDependencyId != null && !toDependencyId.isBlank()) { where.append(" AND para_dependencia_id::text = ? "); params.add(toDependencyId); }

        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM documento_derivaciones d WHERE " + where, Integer.class, params.toArray());
        ArrayList<Object> lp = new ArrayList<>(params);
        lp.add(pageSize); lp.add(offset);
        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT d.id, d.documento_id, d.para_user_id, d.para_dependencia_id,
                       d.sumilla, d.requiere_respuesta, d.fecha_plazo, d.estado, d.creado_en
                FROM documento_derivaciones d
                WHERE %s
                ORDER BY d.fecha_plazo NULLS LAST, d.creado_en DESC
                LIMIT ? OFFSET ?
                """.formatted(where), lp.toArray());

        int pages = (int) Math.ceil((total == null ? 0 : total) / (double) pageSize);
        return Map.of("items", items, "page", page, "page_size", pageSize, "total", total == null ? 0 : total, "pages", pages);
    }

    public int markRead(String derivationId, String readerUserId) {
        return jdbc.update("""
                UPDATE documento_derivaciones
                SET estado = CASE WHEN estado = 'PENDIENTE' THEN 'LEIDO' ELSE estado END,
                    leido_en = COALESCE(leido_en, now())
                WHERE id::text = ?
                """, derivationId);
    }

    public int attendDerivation(String derivationId, String userId, String note) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT documento_id::text AS documento_id FROM documento_derivaciones WHERE id::text = ?", derivationId);
        if (rows.isEmpty()) return 0;
        String docId = String.valueOf(rows.get(0).get("documento_id"));
        String fromState = currentDocState(docId);
        ensureDocStateForAction(docId, "atender", Set.of("DERIVADO", "EN_REVISION", "RECIBIDO"));

        int n = jdbc.update("""
                UPDATE documento_derivaciones
                SET estado = CASE WHEN estado IN ('PENDIENTE','LEIDO') THEN 'EN_GESTION' ELSE estado END,
                    leido_en = COALESCE(leido_en, now())
                WHERE id::text = ?
                """, derivationId);

        if (n > 0) {
            jdbc.update("""
                    UPDATE documentos
                    SET estado = 'EN_REVISION', actualizado_en = now()
                    WHERE id::text = ? AND estado IN ('DERIVADO','RECIBIDO')
                    """, docId);

            String toState = currentDocState(docId);
            logAuditEvent(
                    docId,
                    userId,
                    null,
                    "ATENDER_DERIVACION",
                    fromState,
                    toState,
                    (note == null || note.isBlank()) ? "Derivación atendida" : note
            );
        }
        return n;
    }

    public String respond(String docId, Map<String, Object> body) {
        String fromState = currentDocState(docId);
        ensureDocStateForAction(docId, "responder", Set.of("DERIVADO", "EN_REVISION", "RECIBIDO"));

        String responseId = jdbc.queryForObject("""
                INSERT INTO documento_respuestas(documento_id, derivacion_id, respondido_por_user_id, asunto, cuerpo)
                VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?)
                RETURNING id::text
                """, String.class,
                docId, body.get("derivation_id"), body.get("responded_by_user_id"), body.get("subject"), body.get("body"));

        Object derivationId = body.get("derivation_id");
        if (derivationId != null) {
            jdbc.update("""
                    UPDATE documento_derivaciones
                    SET estado = 'RESPONDIDO',
                        respondido_en = now(),
                        cerrado_en = now()
                    WHERE id::text = ? AND documento_id::text = ?
                    """, String.valueOf(derivationId), docId);
        }

        jdbc.update("""
                UPDATE documentos
                SET estado_respuesta = 'RESPONDIDO',
                    actualizado_por = COALESCE(?::uuid, actualizado_por),
                    actualizado_en = now()
                WHERE id::text = ?
                """, body.get("responded_by_user_id"), docId);

        String toState = currentDocState(docId);
        logAuditEvent(
                docId,
                body.get("responded_by_user_id") == null ? null : String.valueOf(body.get("responded_by_user_id")),
                body.get("user_role") == null ? null : String.valueOf(body.get("user_role")),
                "RESPONDER_DERIVACION",
                fromState,
                toState,
                body.get("subject") == null ? "Respuesta registrada" : String.valueOf(body.get("subject"))
        );

        return responseId;
    }

    public Map<String, Object> createNestedResponse(String docId, Map<String, Object> body) {
        Object responder = body.get("responded_by_user_id");

        String responseId = respond(docId, body);

        String childId = jdbc.queryForObject("""
                INSERT INTO documentos(
                  entidad_id, flujo, origen, estado, prioridad, confidencialidad,
                  requiere_respuesta, estado_respuesta,
                  tipo_doc_id, dependencia_emisora_id,
                  fecha_elaboracion, asunto, cuerpo,
                  creado_por, actualizado_por
                )
                SELECT
                  d.entidad_id, 'SALIDA', 'INTERNO', 'BORRADOR', 'MEDIA', 'INTERNA',
                  false, 'NO_REQUIERE', d.tipo_doc_id, d.dependencia_emisora_id,
                  CURRENT_DATE, ?, ?, ?::uuid, ?::uuid
                FROM documentos d WHERE d.id::text = ?
                RETURNING id::text
                """, String.class,
                body.getOrDefault("nested_subject", body.get("subject")),
                body.getOrDefault("nested_body", body.get("body")),
                responder, responder, docId);

        String relationId = jdbc.queryForObject("""
                INSERT INTO documento_relaciones(documento_padre_id, documento_hijo_id, tipo_relacion, creado_por_user_id, detalle)
                VALUES (?::uuid, ?::uuid, 'RESPUESTA_A', ?::uuid, jsonb_build_object('derivation_id', ?, 'response_id', ?))
                RETURNING id::text
                """, String.class,
                docId, childId, responder, body.get("derivation_id"), responseId);

        return Map.of("relation_id", relationId, "child_document_id", childId, "response_id", responseId);
    }

    public List<Map<String, Object>> listRelations(String docId, String relationType) {
        return jdbc.queryForList("""
                SELECT r.id, r.tipo_relacion, r.creado_en, r.creado_por_user_id,
                       r.documento_padre_id, r.documento_hijo_id,
                       h.numero_oficial AS hijo_numero_oficial,
                       h.asunto AS hijo_asunto,
                       h.estado AS hijo_estado,
                       h.fecha_emision AS hijo_fecha_emision
                FROM documento_relaciones r
                JOIN documentos h ON h.id = r.documento_hijo_id
                WHERE r.documento_padre_id = ?::uuid
                  AND (? IS NULL OR r.tipo_relacion = ?)
                ORDER BY r.creado_en DESC
                """, docId, relationType, relationType);
    }

    public String addFileRecord(String docId, Map<String, Object> body) {
        int version = jdbc.queryForObject("SELECT COALESCE(MAX(version),0)+1 FROM documento_archivos WHERE documento_id = ?::uuid", Integer.class, docId);
        return jdbc.queryForObject("""
                INSERT INTO documento_archivos(
                    documento_id, version, tipo,
                    nombre_original, nombre_storage, extension,
                    mime_type, size_bytes, sha256,
                    storage_path, subido_por_user_id
                ) VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::uuid)
                RETURNING id::text
                """, String.class,
                docId, version,
                body.getOrDefault("file_kind", "ANEXO"),
                body.get("nombre_original"), body.get("nombre_storage"), body.get("extension"),
                body.getOrDefault("mime_type", "application/octet-stream"), body.getOrDefault("size_bytes", 0), body.get("sha256"),
                body.get("storage_path"), body.get("uploaded_by_user_id"));
    }

    public List<Map<String, Object>> listFiles(String docId) {
        return jdbc.queryForList("""
                SELECT id, documento_id, version, tipo, nombre_original, nombre_storage,
                       extension, mime_type, size_bytes, sha256, storage_path, subido_por_user_id, subido_en
                FROM documento_archivos
                WHERE documento_id = ?::uuid AND activo = true
                ORDER BY version DESC, subido_en DESC
                """, docId);
    }

    public Map<String, Object> getFile(String docId, String fileId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT id, documento_id, version, tipo, nombre_original, nombre_storage,
                       extension, mime_type, size_bytes, sha256, storage_path, subido_por_user_id, subido_en
                FROM documento_archivos
                WHERE documento_id = ?::uuid AND id = ?::uuid AND activo = true
                """, docId, fileId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Map<String, Object> saveFile(String docId, MultipartFile file, String uploadedByUserId, String fileKind) {
        if (file == null || file.isEmpty()) throw new RuntimeException("Archivo requerido");

        String originalName = file.getOriginalFilename() == null ? "archivo" : file.getOriginalFilename();
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) extension = originalName.substring(dot + 1).toLowerCase();

        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String storageName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path docFolder = storageRoot.resolve(docId);

        try {
            Files.createDirectories(docFolder);
            Path target = docFolder.resolve(storageName).normalize();
            file.transferTo(target);

            String sha256;
            try (InputStream in = Files.newInputStream(target)) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) md.update(buf, 0, n);
                sha256 = HexFormat.of().formatHex(md.digest());
            }

            java.util.HashMap<String, Object> payload = new java.util.HashMap<>();
            payload.put("file_kind", fileKind == null ? "ANEXO" : fileKind);
            payload.put("nombre_original", safeName);
            payload.put("nombre_storage", storageName);
            payload.put("extension", extension);
            payload.put("mime_type", file.getContentType() == null ? "application/octet-stream" : file.getContentType());
            payload.put("size_bytes", file.getSize());
            payload.put("sha256", sha256);
            payload.put("storage_path", target.toString());
            payload.put("uploaded_by_user_id", uploadedByUserId);

            String fileId = addFileRecord(docId, payload);

            return Map.of(
                    "id", fileId,
                    "file_id", fileId,
                    "document_id", docId,
                    "original_filename", safeName,
                    "stored_filename", storageName,
                    "mime_type", file.getContentType() == null ? "application/octet-stream" : file.getContentType(),
                    "size", file.getSize(),
                    "sha256", sha256
            );
        } catch (Exception e) {
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }
    }

    public FileDownload downloadFile(String docId, String fileId) {
        Map<String, Object> row = getFile(docId, fileId);
        if (row == null) throw new RuntimeException("Archivo no encontrado");

        String storagePath = String.valueOf(row.get("storage_path"));
        if (storagePath == null || storagePath.isBlank()) throw new RuntimeException("Archivo sin ruta física");

        try {
            Path path = Paths.get(storagePath).normalize();
            if (!Files.exists(path) || !Files.isRegularFile(path)) throw new RuntimeException("Archivo físico no encontrado");

            Resource resource = new UrlResource(path.toUri());
            String contentType = row.get("mime_type") == null ? Files.probeContentType(path) : String.valueOf(row.get("mime_type"));
            if (contentType == null || contentType.isBlank()) contentType = "application/octet-stream";
            long size = ((Number) row.getOrDefault("size_bytes", Files.size(path))).longValue();
            String original = row.get("nombre_original") == null ? path.getFileName().toString() : String.valueOf(row.get("nombre_original"));
            return new FileDownload(resource, contentType, size, original);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo", e);
        }
    }

    public Map<String, Object> generateAlerts(String entityCode) {
        Integer t24 = jdbc.queryForObject("""
                WITH ins AS (
                INSERT INTO documento_alertas(documento_id, user_id, tipo, scheduled_at, estado, payload)
                SELECT d.id, d.owner_user_id, 'T_24H', now(), 'PENDIENTE',
                       jsonb_build_object('asunto', d.asunto, 'fecha_plazo', d.fecha_plazo)
                FROM documentos d
                JOIN public.entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo BETWEEN now() AND now() + interval '24 hour'
                  AND d.estado_respuesta <> 'RESPONDIDO'
                RETURNING id)
                SELECT COUNT(*) FROM ins
                """, Integer.class, entityCode);

        Integer venc = jdbc.queryForObject("""
                WITH ins AS (
                INSERT INTO documento_alertas(documento_id, user_id, tipo, scheduled_at, estado, payload)
                SELECT d.id, d.owner_user_id, 'VENCIDO', now(), 'PENDIENTE',
                       jsonb_build_object('asunto', d.asunto, 'fecha_plazo', d.fecha_plazo)
                FROM documentos d
                JOIN public.entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo < now()
                  AND d.estado_respuesta <> 'RESPONDIDO'
                RETURNING id)
                SELECT COUNT(*) FROM ins
                """, Integer.class, entityCode);

        return Map.of("t24_created", t24 == null ? 0 : t24, "overdue_created", venc == null ? 0 : venc);
    }

    public Map<String, Object> listAlerts(String entityCode, String state, int page, int pageSize) {
        page = Math.max(1, page);
        pageSize = Math.max(1, Math.min(200, pageSize));
        int offset = (page - 1) * pageSize;

        Integer total = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM documento_alertas a
                JOIN documentos d ON d.id = a.documento_id
                JOIN public.entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR a.estado = ?)
                """, Integer.class, entityCode, state, state);

        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT a.id, a.documento_id, a.user_id, a.tipo, a.scheduled_at, a.sent_at, a.estado, a.payload,
                       d.asunto, d.fecha_plazo
                FROM documento_alertas a
                JOIN documentos d ON d.id = a.documento_id
                JOIN public.entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR a.estado = ?)
                ORDER BY a.scheduled_at DESC
                LIMIT ? OFFSET ?
                """, entityCode, state, state, pageSize, offset);

        int pages = (int) Math.ceil((total == null ? 0 : total) / (double) pageSize);
        return Map.of("items", items, "page", page, "page_size", pageSize, "total", total == null ? 0 : total, "pages", pages);
    }

    public Map<String, Object> dispatchAlerts(String entityCode, int limit) {
        Integer updated = jdbc.queryForObject("""
                WITH cte AS (
                  SELECT a.id
                  FROM documento_alertas a
                  JOIN documentos d ON d.id = a.documento_id
                  JOIN public.entidades e ON e.id = d.entidad_id
                  WHERE e.codigo = ? AND a.estado = 'PENDIENTE'
                  ORDER BY a.scheduled_at ASC
                  LIMIT ?
                ), upd AS (
                  UPDATE documento_alertas a
                  SET estado = 'ENVIADA', sent_at = now()
                  FROM cte
                  WHERE a.id = cte.id
                  RETURNING a.id
                )
                SELECT COUNT(*) FROM upd
                """, Integer.class, entityCode, limit);
        return Map.of("channel", "INTERNAL", "sent", updated == null ? 0 : updated);
    }

    public Map<String, Object> issue(String docId, String userId) {
        String fromState = currentDocState(docId);

        Map<String, Object> doc = jdbc.queryForMap("""
                SELECT d.id, d.entidad_id, d.tipo_doc_id, d.dependencia_emisora_id, d.estado, d.numero_oficial,
                       COALESCE(d.fecha_elaboracion, CURRENT_DATE) AS fecha_elaboracion,
                       dep.codigo AS dep_codigo, td.codigo AS tipo_codigo
                FROM documentos d
                JOIN tipos_documento td ON td.id = d.tipo_doc_id
                LEFT JOIN dependencias dep ON dep.id = d.dependencia_emisora_id
                WHERE d.id::text = ?
                """, docId);

        if (doc.get("numero_oficial") != null) {
            throw new RuntimeException("El documento ya está emitido");
        }
        String estado = String.valueOf(doc.get("estado"));
        if (!("BORRADOR".equals(estado) || "EN_REVISION".equals(estado))) {
            throw new RuntimeException("No se puede emitir en estado: " + estado);
        }

        int anio = Integer.parseInt(String.valueOf(doc.get("fecha_elaboracion")).substring(0, 4));
        String area = (doc.get("dep_codigo") == null ? "GEN" : String.valueOf(doc.get("dep_codigo"))).trim().toUpperCase();
        String tipo = (doc.get("tipo_codigo") == null ? "DOC" : String.valueOf(doc.get("tipo_codigo"))).trim().toUpperCase();

        List<Map<String, Object>> series = jdbc.queryForList("""
                SELECT id, prefijo, longitud_seq, siguiente_seq
                FROM series_numeracion
                WHERE entidad_id = ?::uuid
                  AND tipo_doc_id = ?::uuid
                  AND anio = ?
                  AND activo = TRUE
                  AND (dependencia_id = ?::uuid OR dependencia_id IS NULL)
                ORDER BY (dependencia_id IS NULL) ASC
                """, doc.get("entidad_id"), doc.get("tipo_doc_id"), anio, doc.get("dependencia_emisora_id"));
        if (series.isEmpty()) throw new RuntimeException("No hay serie configurada para emisión");

        Map<String, Object> serie = series.get(0);
        String prefijo = (serie.get("prefijo") == null ? tipo : String.valueOf(serie.get("prefijo"))).trim().toUpperCase();
        int seq = ((Number) serie.get("siguiente_seq")).intValue();
        int length = ((Number) serie.get("longitud_seq")).intValue();
        String numero = prefijo + "-" + area + "-" + anio + "-" + String.format("%0" + length + "d", seq);

        jdbc.update("UPDATE series_numeracion SET siguiente_seq = siguiente_seq + 1 WHERE id = ?::uuid", serie.get("id"));
        int upd = jdbc.update("""
                UPDATE documentos
                SET numero_oficial = ?, serie_id = ?::uuid, estado = 'EMITIDO', fecha_emision = now(),
                    actualizado_por = COALESCE(?::uuid, actualizado_por), actualizado_en = now()
                WHERE id::text = ? AND numero_oficial IS NULL
                """, numero, serie.get("id"), userId, docId);
        if (upd == 0) throw new RuntimeException("No se pudo emitir (concurrencia)");

        List<Map<String, Object>> dests = jdbc.queryForList("SELECT persona_id, dependencia_id, externo_nombre FROM documento_destinatarios WHERE documento_id = ?::uuid", docId);
        int creadas = 0;
        for (Map<String, Object> d : dests) {
            if (d.get("externo_nombre") != null) continue;
            if (d.get("persona_id") != null) {
                creadas += jdbc.update("INSERT INTO documento_recepciones (documento_id, receptor_id, estado) VALUES (?::uuid, ?::uuid, 'PENDIENTE') ON CONFLICT DO NOTHING", docId, d.get("persona_id"));
            } else if (d.get("dependencia_id") != null) {
                creadas += jdbc.update("INSERT INTO documento_recepciones (documento_id, receptor_id, dependencia_id, estado) VALUES (?::uuid, NULL, ?::uuid, 'PENDIENTE') ON CONFLICT DO NOTHING", docId, d.get("dependencia_id"));
            }
        }

        String toState = currentDocState(docId);
        logAuditEvent(docId, userId, null, "EMITIR", fromState, toState, "Documento emitido con número " + numero);

        return Map.of("id", docId, "numero_oficial", numero, "anio", anio, "prefijo", prefijo, "area", area, "pendientes", creadas);
    }

    public Map<String, Object> receive(String docId, String receptorId, String dependenciaId, String userId, String comentario) {
        if ((receptorId == null && dependenciaId == null) || (receptorId != null && dependenciaId != null)) {
            throw new RuntimeException("Debe enviar receptor_id o dependencia_id, pero no ambos");
        }

        String fromState = currentDocState(docId);
        ensureDocStateForAction(docId, "recibir", Set.of("EMITIDO", "DERIVADO", "RECIBIDO"));

        String recId;
        if (receptorId != null) {
            recId = jdbc.queryForObject("""
                    INSERT INTO documento_recepciones(documento_id, receptor_id, dependencia_id, recibido_en, confirmado_por, estado, comentario)
                    VALUES (?::uuid, ?::uuid, NULL, now(), ?::uuid, 'RECIBIDO', ?)
                    ON CONFLICT (documento_id, receptor_id)
                    DO UPDATE SET recibido_en = COALESCE(documento_recepciones.recibido_en, now()), confirmado_por = EXCLUDED.confirmado_por,
                                  estado = 'RECIBIDO', comentario = COALESCE(EXCLUDED.comentario, documento_recepciones.comentario)
                    RETURNING id::text
                    """, String.class, docId, receptorId, userId, comentario);
        } else {
            List<Map<String, Object>> ex = jdbc.queryForList("SELECT id FROM documento_recepciones WHERE documento_id = ?::uuid AND receptor_id IS NULL AND dependencia_id::text = ?", docId, dependenciaId);
            if (ex.isEmpty()) {
                recId = jdbc.queryForObject("INSERT INTO documento_recepciones(documento_id, receptor_id, dependencia_id, recibido_en, confirmado_por, estado, comentario) VALUES (?::uuid, NULL, ?::uuid, now(), ?::uuid, 'RECIBIDO', ?) RETURNING id::text", String.class, docId, dependenciaId, userId, comentario);
            } else {
                recId = String.valueOf(ex.get(0).get("id"));
                jdbc.update("UPDATE documento_recepciones SET recibido_en = COALESCE(recibido_en, now()), confirmado_por = ?::uuid, estado='RECIBIDO', comentario=COALESCE(?,comentario) WHERE id::text = ?", userId, comentario, recId);
            }
        }

        jdbc.update("UPDATE documentos SET estado='RECIBIDO', fecha_recepcion=COALESCE(fecha_recepcion, now()), actualizado_por=COALESCE(?::uuid, actualizado_por), actualizado_en=now() WHERE id::text = ?", userId, docId);

        String toState = currentDocState(docId);
        logAuditEvent(
                docId,
                userId,
                null,
                "RECIBIR",
                fromState,
                toState,
                comentario == null ? "Recepción registrada" : comentario
        );

        return Map.of("documento_id", docId, "recepcion_id", recId, "estado_documento", "RECIBIDO");
    }

    public List<Map<String, Object>> pendingReceptions(String entityCode, String receptorId, String dependenciaId) {
        StringBuilder where = new StringBuilder(" d.entidad_id = (SELECT id FROM public.entidades WHERE codigo = ?) ");
        List<Object> params = new ArrayList<>();
        params.add(entityCode);
        if (receptorId != null && !receptorId.isBlank()) { where.append(" AND r.receptor_id::text = ? "); params.add(receptorId); }
        if (dependenciaId != null && !dependenciaId.isBlank()) { where.append(" AND r.dependencia_id::text = ? "); params.add(dependenciaId); }
        return jdbc.queryForList("""
                SELECT r.id AS recepcion_id, r.estado AS estado_recepcion, r.receptor_id, r.dependencia_id, r.recibido_en,
                       d.id AS documento_id, d.numero_oficial, d.estado AS estado_documento, d.asunto, d.fecha_elaboracion, d.fecha_emision
                FROM documento_recepciones r
                JOIN documentos d ON d.id = r.documento_id
                WHERE %s AND r.estado = 'PENDIENTE'
                ORDER BY d.fecha_emision DESC NULLS LAST, d.creado_en DESC
                """.formatted(where), params.toArray());
    }

    public List<Map<String, Object>> timeline(String docId) {
        return jdbc.queryForList("""
                SELECT id, actor_user_id, actor_rol, evento, detalle, created_at
                FROM documento_eventos
                WHERE documento_id = ?::uuid
                ORDER BY created_at ASC, id ASC
                """, docId);
    }

    public List<Map<String, Object>> overdue(String entityCode) {
        return jdbc.queryForList("""
                SELECT d.id, d.asunto, d.fecha_plazo, d.estado_respuesta
                FROM documentos d
                JOIN public.entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo < now()
                  AND d.estado_respuesta <> 'RESPONDIDO'
                ORDER BY d.fecha_plazo ASC
                """, entityCode);
    }

    public List<Map<String, Object>> dueSoon(String entityCode, int hours) {
        return jdbc.queryForList("""
                SELECT d.id, d.asunto, d.fecha_plazo, d.estado_respuesta
                FROM documentos d
                JOIN public.entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo >= now()
                  AND d.fecha_plazo <= now() + (? || ' hour')::interval
                  AND d.estado_respuesta <> 'RESPONDIDO'
                ORDER BY d.fecha_plazo ASC
                """, entityCode, hours);
    }
}

