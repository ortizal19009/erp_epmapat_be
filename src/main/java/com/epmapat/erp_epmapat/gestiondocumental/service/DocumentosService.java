package com.epmapat.erp_epmapat.gestiondocumental.service;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class DocumentosService {

    private final JdbcTemplate jdbc;

    public DocumentosService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private String s(Map<String, Object> data, String... keys) {
        for (String k : keys) {
            Object v = data.get(k);
            if (v != null && !String.valueOf(v).isBlank()) return String.valueOf(v);
        }
        return null;
    }

    private String actorUserId(Map<String, Object> data) {
        String id = s(data, "usuario_id", "user_id", "owner_user_id", "creado_por", "actualizado_por");
        if (id != null) return id;
        try {
            return jdbc.queryForObject("SELECT id::text FROM gd_usuarios WHERE activo = true ORDER BY creado_en ASC LIMIT 1", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    private void validateCreateUpdate(Map<String, Object> data, boolean creating) {
        if (s(data, "entity_code", "entidad_codigo") == null) throw new IllegalArgumentException("entity_code es obligatorio");
        if (s(data, "tipo_doc_id", "type_id") == null) throw new IllegalArgumentException("type_id es obligatorio");
        if (s(data, "dependencia_emisora_id", "dependency_id") == null) throw new IllegalArgumentException("dependency_id es obligatorio");
        if (s(data, "asunto", "subject") == null) throw new IllegalArgumentException("subject es obligatorio");
        if (s(data, "flujo", "flow") == null) throw new IllegalArgumentException("flow es obligatorio");
        if (s(data, "origen", "origin") == null) throw new IllegalArgumentException("origin es obligatorio");
        Object req = data.getOrDefault("requiere_respuesta", data.get("requires_response"));
        boolean requires = req instanceof Boolean ? (Boolean) req : "true".equalsIgnoreCase(String.valueOf(req));
        if (requires && s(data, "fecha_plazo", "due_date") == null) {
            throw new IllegalArgumentException("due_date es obligatorio cuando requires_response=true");
        }
    }

    private void applyAccessByUser(StringBuilder where, List<Object> params, String userId) {
        if (userId == null || userId.isBlank()) return;
        where.append("""
                 AND (
                    d.owner_user_id::text = ?
                    OR d.creado_por::text = ?
                    OR EXISTS (
                        SELECT 1 FROM documento_destinatarios dd
                        WHERE dd.documento_id = d.id AND dd.to_user_id::text = ?
                    )
                    OR EXISTS (
                        SELECT 1 FROM documento_derivaciones dv
                        WHERE dv.documento_id = d.id AND dv.para_user_id::text = ?
                    )
                 )
                """);
        params.add(userId);
        params.add(userId);
        params.add(userId);
        params.add(userId);
    }

    private void insertInitialTargets(String docId, Map<String, Object> data) {
        Object usersObj = data.get("to_user_ids");
        if (usersObj instanceof List<?> users) {
            for (Object u : users) {
                if (u == null || String.valueOf(u).isBlank()) continue;
                try {
                    jdbc.update("""
                            INSERT INTO documento_destinatarios (id, documento_id, to_user_id, estado, creado_en)
                            VALUES (gen_random_uuid(), ?::uuid, ?::uuid, 'PENDIENTE', now())
                            """, docId, String.valueOf(u));
                } catch (Exception ignored) {}
            }
        }

        Object depsObj = data.get("to_dependency_ids");
        if (depsObj instanceof List<?> deps) {
            for (Object d : deps) {
                if (d == null || String.valueOf(d).isBlank()) continue;
                try {
                    jdbc.update("""
                            INSERT INTO documento_destinatarios (id, documento_id, to_dependency_id, estado, creado_en)
                            VALUES (gen_random_uuid(), ?::uuid, ?::uuid, 'PENDIENTE', now())
                            """, docId, String.valueOf(d));
                } catch (Exception ignored) {}
            }
        }

        try {
            jdbc.update("""
                    INSERT INTO documento_eventos (id, documento_id, event_type, description, occurred_at)
                    VALUES (gen_random_uuid(), ?::uuid, 'ETIQUETADO_INICIAL', 'Destinatarios iniciales asignados', now())
                    """, docId);
        } catch (Exception ignored) {}
    }

    private void insertInitialReceptionsForIngreso(String docId, Map<String, Object> data, String actor) {
        Object usersObj = data.get("to_user_ids");
        if (usersObj instanceof List<?> users) {
            for (Object u : users) {
                if (u == null || String.valueOf(u).isBlank()) continue;
                try {
                    jdbc.update("""
                            INSERT INTO documento_recepciones (documento_id, receptor_id, dependencia_id, estado, comentario, confirmado_por)
                            VALUES (?::uuid, ?::uuid, NULL, 'PENDIENTE', 'Asignado en mesa de entrada', ?::uuid)
                            ON CONFLICT (documento_id, receptor_id) DO NOTHING
                            """, docId, String.valueOf(u), actor);
                } catch (Exception ignored) {}
            }
        }

        Object depsObj = data.get("to_dependency_ids");
        if (depsObj instanceof List<?> deps) {
            for (Object d : deps) {
                if (d == null || String.valueOf(d).isBlank()) continue;
                try {
                    jdbc.update("""
                            INSERT INTO documento_recepciones (documento_id, receptor_id, dependencia_id, estado, comentario, confirmado_por)
                            VALUES (?::uuid, NULL, ?::uuid, 'PENDIENTE', 'Asignado en mesa de entrada', ?::uuid)
                            ON CONFLICT DO NOTHING
                            """, docId, String.valueOf(d), actor);
                } catch (Exception ignored) {}
            }
        }

        if (!(depsObj instanceof List<?>) && !(usersObj instanceof List<?>)) {
            String dep = s(data, "dependencia_emisora_id", "dependency_id");
            if (dep != null && !dep.isBlank()) {
                try {
                    jdbc.update("""
                            INSERT INTO documento_recepciones (documento_id, receptor_id, dependencia_id, estado, comentario, confirmado_por)
                            VALUES (?::uuid, NULL, ?::uuid, 'PENDIENTE', 'Recepción inicial por dependencia', ?::uuid)
                            ON CONFLICT DO NOTHING
                            """, docId, dep, actor);
                } catch (Exception ignored) {}
            }
        }

        try {
            jdbc.update("""
                    INSERT INTO documento_eventos (id, documento_id, event_type, description, occurred_at)
                    VALUES (gen_random_uuid(), ?::uuid, 'REGISTRO_INGRESO', 'Documento registrado en mesa de entrada', now())
                    """, docId);
        } catch (Exception ignored) {}
    }

    public Map<String, Object> list(String entityCode, String estado, String flujo, String q,
                                    String dependencyId, String typeId, String userId,
                                    String seriesId, String subseriesId,
                                    String dateFrom, String dateTo,
                                    int page, int pageSize) {
        page = Math.max(1, page);
        pageSize = Math.max(1, Math.min(200, pageSize));
        int offset = (page - 1) * pageSize;

        StringBuilder where = new StringBuilder(" d.entidad_id = (SELECT id FROM public.entidades WHERE codigo = ?) ");
        List<Object> params = new java.util.ArrayList<>();
        params.add(entityCode);

        if (estado != null && !estado.isBlank()) { where.append(" AND d.estado = ? "); params.add(estado); }
        if (flujo != null && !flujo.isBlank()) { where.append(" AND d.flujo = ? "); params.add(flujo); }
        if (q != null && !q.isBlank()) { where.append(" AND (d.asunto ILIKE ? OR COALESCE(d.remitente_externo,'') ILIKE ?) "); params.add("%"+q+"%"); params.add("%"+q+"%"); }
        if (dependencyId != null && !dependencyId.isBlank()) { where.append(" AND d.dependencia_emisora_id::text = ? "); params.add(dependencyId); }
        if (typeId != null && !typeId.isBlank()) { where.append(" AND d.tipo_doc_id::text = ? "); params.add(typeId); }
        applyAccessByUser(where, params, userId);
        if (seriesId != null && !seriesId.isBlank()) { where.append(" AND d.series_id::text = ? "); params.add(seriesId); }
        if (subseriesId != null && !subseriesId.isBlank()) { where.append(" AND d.subseries_id::text = ? "); params.add(subseriesId); }
        if (dateFrom != null && !dateFrom.isBlank()) { where.append(" AND d.fecha_elaboracion >= ?::date "); params.add(dateFrom); }
        if (dateTo != null && !dateTo.isBlank()) { where.append(" AND d.fecha_elaboracion <= ?::date "); params.add(dateTo); }

        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM documentos d WHERE " + where, Integer.class, params.toArray());

        String sql = """
                SELECT d.id, d.flujo, d.origen, d.estado, d.prioridad, d.confidencialidad,
                       d.tipo_doc_id, td.codigo AS tipo_codigo, td.nombre AS tipo_nombre,
                       d.dependencia_emisora_id, dep.codigo AS dep_codigo, dep.nombre AS dep_nombre,
                       d.series_id, s.code AS series_code, s.name AS series_name,
                       d.subseries_id, ss.code AS subseries_code, ss.name AS subseries_name,
                       d.retention_schedule_id,
                       d.numero_oficial, d.fecha_elaboracion, d.fecha_emision, d.fecha_recepcion,
                       d.requiere_respuesta, d.fecha_plazo, d.estado_respuesta,
                       d.remitente_persona_id, d.remitente_externo,
                       d.asunto, d.referencia,
                       d.creado_en, d.actualizado_en
                FROM documentos d
                JOIN tipos_documento td ON td.id = d.tipo_doc_id
                LEFT JOIN dependencias dep ON dep.id = d.dependencia_emisora_id
                LEFT JOIN public.document_series s ON s.id = d.series_id
                LEFT JOIN public.document_subseries ss ON ss.id = d.subseries_id
                WHERE %s
                ORDER BY d.creado_en DESC
                LIMIT ? OFFSET ?
                """.formatted(where);

        List<Object> listParams = new java.util.ArrayList<>(params);
        listParams.add(pageSize);
        listParams.add(offset);
        List<Map<String, Object>> items = jdbc.queryForList(sql, listParams.toArray());

        int pages = (int) Math.ceil((total == null ? 0 : total) / (double) pageSize);
        return Map.of("items", items, "page", page, "page_size", pageSize, "total", total == null ? 0 : total, "pages", pages);
    }

    public Map<String, Object> dashboard(String entityCode, String dateFrom, String dateTo) {
        StringBuilder where = new StringBuilder(" d.entidad_id = (SELECT id FROM public.entidades WHERE codigo = ?) ");
        List<Object> params = new java.util.ArrayList<>();
        params.add(entityCode);
        if (dateFrom != null && !dateFrom.isBlank()) { where.append(" AND d.fecha_elaboracion >= ?::date "); params.add(dateFrom); }
        if (dateTo != null && !dateTo.isBlank()) { where.append(" AND d.fecha_elaboracion <= ?::date "); params.add(dateTo); }

        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM documentos d WHERE " + where, Integer.class, params.toArray());
        List<Map<String, Object>> byStatus = jdbc.queryForList("SELECT estado, COUNT(*) AS total FROM documentos d WHERE " + where + " GROUP BY estado ORDER BY estado", params.toArray());
        List<Map<String, Object>> byFlow = jdbc.queryForList("SELECT flujo, COUNT(*) AS total FROM documentos d WHERE " + where + " GROUP BY flujo ORDER BY flujo", params.toArray());

        return Map.of(
                "total_documents", total == null ? 0 : total,
                "by_status", byStatus,
                "by_flow", byFlow
        );
    }

    public Map<String, Object> get(String docId, String userId) {
        Map<String, Object> doc = jdbc.queryForMap("""
                SELECT d.*,
                       td.codigo AS tipo_codigo, td.nombre AS tipo_nombre,
                       dep.codigo AS dep_codigo, dep.nombre AS dep_nombre
                FROM documentos d
                JOIN tipos_documento td ON td.id = d.tipo_doc_id
                LEFT JOIN dependencias dep ON dep.id = d.dependencia_emisora_id
                WHERE d.id::text = ?
                """, docId);

        if (userId != null && !userId.isBlank()) {
            Integer allowed = jdbc.queryForObject("""
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
            if (allowed == null || allowed == 0) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para ver este documento");
            }
        }

        return doc;
    }

    public String create(Map<String, Object> data) {
        validateCreateUpdate(data, true);
        String actor = actorUserId(data);
        String id = jdbc.queryForObject("""
                INSERT INTO documentos (
                    entidad_id, flujo, origen, estado, prioridad, confidencialidad,
                    requiere_respuesta, fecha_plazo, estado_respuesta,
                    tipo_doc_id, dependencia_emisora_id,
                    series_id, subseries_id, retention_schedule_id,
                    fecha_elaboracion,
                    remitente_persona_id, remitente_externo,
                    asunto, cuerpo, referencia, observaciones,
                    creado_por, actualizado_por
                ) VALUES (
                    (SELECT id FROM public.entidades WHERE codigo = ?),
                    ?::doc_flujo, ?::doc_origen, COALESCE(?::doc_estado, 'BORRADOR'), COALESCE(?::doc_prioridad, 'MEDIA'), COALESCE(?::confidencialidad, 'INTERNA'),
                    COALESCE(?, false), ?::timestamp, COALESCE(?::estado_respuesta, 'NO_REQUIERE'),
                    ?::uuid, ?::uuid,
                    ?::uuid, ?::uuid, ?::uuid,
                    COALESCE(?::date, CURRENT_DATE),
                    ?::uuid, ?,
                    ?, ?, ?, ?,
                    ?::uuid, ?::uuid
                ) RETURNING id::text
                """,
                String.class,
                s(data, "entity_code", "entidad_codigo"),
                s(data, "flujo", "flow"), s(data, "origen", "origin"), s(data, "estado"), s(data, "prioridad", "priority"), s(data, "confidencialidad"),
                data.getOrDefault("requiere_respuesta", data.get("requires_response")), s(data, "fecha_plazo", "due_date"), s(data, "estado_respuesta"),
                s(data, "tipo_doc_id", "type_id"), s(data, "dependencia_emisora_id", "dependency_id"),
                s(data, "series_id"), s(data, "subseries_id"), s(data, "retention_schedule_id"),
                s(data, "fecha_elaboracion", "draft_date"), s(data, "remitente_persona_id"), s(data, "remitente_externo"),
                s(data, "asunto", "subject"), s(data, "cuerpo", "body"), s(data, "referencia", "reference"), s(data, "observaciones"),
                actor, actor
        );
        if (id != null) {
            insertInitialTargets(id, data);
            String flow = s(data, "flujo", "flow");
            String origin = s(data, "origen", "origin");
            if ("INGRESO".equalsIgnoreCase(flow) && "EXTERNO".equalsIgnoreCase(origin)) {
                insertInitialReceptionsForIngreso(id, data, actor);
            }
        }
        return id;
    }

    public int update(String docId, Map<String, Object> data) {
        validateCreateUpdate(data, false);
        String actor = actorUserId(data);
        return jdbc.update("""
                UPDATE documentos SET
                    flujo = ?::doc_flujo,
                    origen = ?::doc_origen,
                    estado = COALESCE(?::doc_estado, 'BORRADOR'),
                    prioridad = COALESCE(?::doc_prioridad, 'MEDIA'),
                    confidencialidad = COALESCE(?::confidencialidad, 'INTERNA'),
                    requiere_respuesta = COALESCE(?, false),
                    fecha_plazo = ?::timestamp,
                    estado_respuesta = COALESCE(?::estado_respuesta, 'NO_REQUIERE'),
                    tipo_doc_id = ?::uuid,
                    dependencia_emisora_id = ?::uuid,
                    series_id = ?::uuid,
                    subseries_id = ?::uuid,
                    retention_schedule_id = ?::uuid,
                    fecha_elaboracion = COALESCE(?::date, fecha_elaboracion),
                    remitente_persona_id = ?::uuid,
                    remitente_externo = ?,
                    asunto = ?,
                    cuerpo = ?,
                    referencia = ?,
                    observaciones = ?,
                    actualizado_por = COALESCE(?::uuid, actualizado_por),
                    actualizado_en = now()
                WHERE id::text = ?
                """,
                s(data, "flujo", "flow"), s(data, "origen", "origin"), s(data, "estado"), s(data, "prioridad", "priority"), s(data, "confidencialidad"),
                data.getOrDefault("requiere_respuesta", data.get("requires_response")), s(data, "fecha_plazo", "due_date"), s(data, "estado_respuesta"),
                s(data, "tipo_doc_id", "type_id"), s(data, "dependencia_emisora_id", "dependency_id"), s(data, "series_id"), s(data, "subseries_id"), s(data, "retention_schedule_id"),
                s(data, "fecha_elaboracion", "draft_date"), s(data, "remitente_persona_id"), s(data, "remitente_externo"),
                s(data, "asunto", "subject"), s(data, "cuerpo", "body"), s(data, "referencia", "reference"), s(data, "observaciones"),
                actor, docId
        );
    }

    public int delete(String docId) {
        return jdbc.update("DELETE FROM documentos WHERE id::text = ?", docId);
    }
}




