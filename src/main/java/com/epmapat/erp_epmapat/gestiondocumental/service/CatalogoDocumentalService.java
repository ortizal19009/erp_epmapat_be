package com.epmapat.erp_epmapat.gestiondocumental.service;

import com.epmapat.erp_epmapat.gestiondocumental.dto.catalogo.CatalogoRequests;
import com.epmapat.erp_epmapat.gestiondocumental.model.Dependencia;
import com.epmapat.erp_epmapat.gestiondocumental.model.Entidad;
import com.epmapat.erp_epmapat.gestiondocumental.model.TipoDocumento;
import com.epmapat.erp_epmapat.gestiondocumental.repository.DependenciaRepository;
import com.epmapat.erp_epmapat.gestiondocumental.repository.EntidadRepository;
import com.epmapat.erp_epmapat.gestiondocumental.repository.TipoDocumentoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CatalogoDocumentalService {

    private final EntidadRepository entidadRepository;
    private final DependenciaRepository dependenciaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final JdbcTemplate jdbc;

    public CatalogoDocumentalService(EntidadRepository entidadRepository,
                                     DependenciaRepository dependenciaRepository,
                                     TipoDocumentoRepository tipoDocumentoRepository,
                                     JdbcTemplate jdbc) {
        this.entidadRepository = entidadRepository;
        this.dependenciaRepository = dependenciaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.jdbc = jdbc;
    }

    public List<Entidad> listEntities(String q) {
        if (q == null || q.isBlank()) return entidadRepository.findAllByOrderByCodigo();
        return entidadRepository.findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCaseOrderByCodigo(q, q);
    }

    public Map<String, Object> createEntity(CatalogoRequests.EntidadRequest req) {
        Entidad e = new Entidad();
        e.setId(UUID.randomUUID());
        e.setCodigo(req.getCodigo());
        e.setNombre(req.getNombre());
        e.setActivo(req.getActive() == null || req.getActive());
        entidadRepository.save(e);
        return Map.of("id", e.getId().toString());
    }

    public Map<String, Object> updateEntity(String id, CatalogoRequests.EntidadRequest req) {
        Entidad e = entidadRepository.findById(UUID.fromString(id)).orElseThrow();
        e.setCodigo(req.getCodigo());
        e.setNombre(req.getNombre());
        entidadRepository.save(e);
        return Map.of("id", id);
    }

    public Map<String, Object> setEntityStatus(String id, Boolean active) {
        Entidad e = entidadRepository.findById(UUID.fromString(id)).orElseThrow();
        e.setActivo(active == null || active);
        entidadRepository.save(e);
        return Map.of("id", id, "active", e.getActivo());
    }

    public List<Dependencia> listDependencies(String entityCode) {
        UUID entidadId = entidadRepository.findByCodigo(entityCode).orElseThrow().getId();
        return dependenciaRepository.findByEntidadIdOrderByCodigo(entidadId);
    }

    public Map<String, Object> createDependency(CatalogoRequests.DependenciaRequest req) {
        UUID entidadId = entidadRepository.findByCodigo(req.getEntity_code()).orElseThrow().getId();
        Dependencia d = new Dependencia();
        d.setId(UUID.randomUUID());
        d.setEntidadId(entidadId);
        d.setCodigo(req.getCodigo());
        d.setNombre(req.getNombre());
        d.setPadreId(req.getPadre_id() == null ? null : UUID.fromString(req.getPadre_id()));
        d.setActivo(req.getActive() == null || req.getActive());
        dependenciaRepository.save(d);
        return Map.of("id", d.getId().toString());
    }

    public Map<String, Object> updateDependency(String id, CatalogoRequests.DependenciaRequest req) {
        Dependencia d = dependenciaRepository.findById(UUID.fromString(id)).orElseThrow();
        d.setCodigo(req.getCodigo());
        d.setNombre(req.getNombre());
        d.setPadreId(req.getPadre_id() == null ? null : UUID.fromString(req.getPadre_id()));
        dependenciaRepository.save(d);
        return Map.of("id", id);
    }

    public Map<String, Object> setDependencyStatus(String id, Boolean active) {
        Dependencia d = dependenciaRepository.findById(UUID.fromString(id)).orElseThrow();
        d.setActivo(active == null || active);
        dependenciaRepository.save(d);
        return Map.of("id", id, "active", d.getActivo());
    }

    public List<TipoDocumento> listDocumentTypes(String entityCode) {
        UUID entidadId = entidadRepository.findByCodigo(entityCode).orElseThrow().getId();
        return tipoDocumentoRepository.findByEntidadIdOrderByCodigo(entidadId);
    }

    public Map<String, Object> createDocumentType(CatalogoRequests.TipoDocumentoRequest req) {
        UUID entidadId = entidadRepository.findByCodigo(req.getEntity_code()).orElseThrow().getId();
        TipoDocumento t = new TipoDocumento();
        t.setId(UUID.randomUUID());
        t.setEntidadId(entidadId);
        t.setCodigo(req.getCodigo());
        t.setNombre(req.getNombre());
        t.setFlujo(req.getFlujo());
        t.setActivo(req.getActive() == null || req.getActive());
        tipoDocumentoRepository.save(t);
        return Map.of("id", t.getId().toString());
    }

    public Map<String, Object> updateDocumentType(String id, CatalogoRequests.TipoDocumentoRequest req) {
        TipoDocumento t = tipoDocumentoRepository.findById(UUID.fromString(id)).orElseThrow();
        t.setCodigo(req.getCodigo());
        t.setNombre(req.getNombre());
        t.setFlujo(req.getFlujo());
        tipoDocumentoRepository.save(t);
        return Map.of("id", id);
    }

    public Map<String, Object> setDocumentTypeStatus(String id, Boolean active) {
        TipoDocumento t = tipoDocumentoRepository.findById(UUID.fromString(id)).orElseThrow();
        t.setActivo(active == null || active);
        tipoDocumentoRepository.save(t);
        return Map.of("id", id, "active", t.getActivo());
    }

    public Map<String, Object> lookupUsers(String entityCode, String q, int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        String qq = (q == null || q.isBlank()) ? null : q;

        Integer total = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM public.usuarios u
                JOIN public.entidades e ON e.id = u.entidad_id
                LEFT JOIN public.personas p ON p.id = u.persona_id
                WHERE e.codigo = ?
                  AND (COALESCE(?, '') = '' OR u.username::text ILIKE ('%' || ? || '%') OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%'))
                """, Integer.class, entityCode, qq, qq, qq, qq);

        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT u.id, u.username::text AS username, u.activo,
                       p.id AS persona_id,
                       COALESCE((p.nombres || ' ' || p.apellidos)::text, u.username::text) AS nombre,
                       p.email::text AS email,
                       d.id AS dependencia_id,
                       d.codigo AS dependencia_codigo,
                       d.nombre AS dependencia_nombre
                FROM public.usuarios u
                JOIN public.entidades e ON e.id = u.entidad_id
                LEFT JOIN public.personas p ON p.id = u.persona_id
                LEFT JOIN dependencias d ON d.id = p.dependencia_id
                WHERE e.codigo = ?
                  AND (COALESCE(?, '') = '' OR u.username::text ILIKE ('%' || ? || '%') OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%'))
                ORDER BY u.activo DESC, nombre ASC
                OFFSET ? LIMIT ?
                """, entityCode, qq, qq, qq, qq, offset, pageSize);

        return Map.of("items", items, "total", total == null ? 0 : total, "page", page, "page_size", pageSize);
    }

    public Map<String, Object> lookupPersons(String entityCode, String q, int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        String qq = (q == null || q.isBlank()) ? null : q;

        Integer total = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM public.personas p
                JOIN public.entidades e ON e.id = p.entidad_id
                WHERE e.codigo = ?
                  AND (COALESCE(?, '') = '' OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%') OR p.identificacion ILIKE ('%' || ? || '%'))
                """, Integer.class, entityCode, qq, qq, qq, qq);

        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT p.id, p.identificacion, p.nombres, p.apellidos, p.cargo, p.email::text AS email, p.activo,
                       d.id AS dependencia_id, d.codigo AS dependencia_codigo, d.nombre AS dependencia_nombre
                FROM public.personas p
                JOIN public.entidades e ON e.id = p.entidad_id
                LEFT JOIN dependencias d ON d.id = p.dependencia_id
                WHERE e.codigo = ?
                  AND (COALESCE(?, '') = '' OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%') OR p.identificacion ILIKE ('%' || ? || '%'))
                ORDER BY p.activo DESC, p.apellidos ASC, p.nombres ASC
                OFFSET ? LIMIT ?
                """, entityCode, qq, qq, qq, qq, offset, pageSize);

        return Map.of("items", items, "total", total == null ? 0 : total, "page", page, "page_size", pageSize);
    }
}

