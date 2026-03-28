package com.epmapat.erp_epmapat.gestiondocumental.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConfiguracionDocumentalService {

    private final JdbcTemplate jdbc;

    public ConfiguracionDocumentalService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listSystemSettings(String entityCode) {
        return jdbc.queryForList("""
                SELECT setting_key, setting_value, updated_by, updated_at
                FROM system_settings
                WHERE entity_code = ?
                ORDER BY setting_key
                """, entityCode);
    }

    public Map<String, Object> upsertSystemSetting(String entityCode, String key, String value, String updatedBy) {
        return jdbc.queryForMap("""
                INSERT INTO system_settings(entity_code, setting_key, setting_value, updated_by)
                VALUES (?,?,?,?)
                ON CONFLICT(entity_code, setting_key)
                DO UPDATE SET setting_value = EXCLUDED.setting_value,
                              updated_by = EXCLUDED.updated_by,
                              updated_at = now()
                RETURNING setting_key, setting_value, updated_by, updated_at
                """, entityCode, key, value, updatedBy);
    }

    public List<Map<String, Object>> listSeries(String entityCode) {
        return jdbc.queryForList("SELECT id, code, name, active FROM public.document_series WHERE entity_code = ? ORDER BY code", entityCode);
    }

    public Map<String, Object> upsertSeries(String entityCode, String code, String name) {
        return jdbc.queryForMap("""
                INSERT INTO public.document_series(entity_code, code, name)
                VALUES (?,?,?)
                ON CONFLICT(entity_code, code) DO UPDATE SET name = EXCLUDED.name
                RETURNING id, code, name, active
                """, entityCode, code.toUpperCase(), name);
    }

    public List<Map<String, Object>> listSubseries(String seriesId) {
        return jdbc.queryForList("SELECT id, series_id, code, name, active FROM public.document_subseries WHERE series_id = ?::uuid ORDER BY code", seriesId);
    }

    public Map<String, Object> upsertSubseries(String seriesId, String code, String name) {
        return jdbc.queryForMap("""
                INSERT INTO public.document_subseries(series_id, code, name)
                VALUES (?::uuid, ?, ?)
                ON CONFLICT(series_id, code) DO UPDATE SET name = EXCLUDED.name
                RETURNING id, series_id, code, name, active
                """, seriesId, code.toUpperCase(), name);
    }

    public List<Map<String, Object>> listTrd(String entityCode) {
        return jdbc.queryForList("""
                SELECT t.id, t.entity_code, t.series_id, s.code AS series_code, s.name AS series_name,
                       t.subseries_id, ss.code AS subseries_code, ss.name AS subseries_name,
                       t.active_years, t.semi_active_years, t.final_disposition, t.legal_basis, t.active
                FROM retention_schedule t
                JOIN public.document_series s ON s.id = t.series_id
                LEFT JOIN public.document_subseries ss ON ss.id = t.subseries_id
                WHERE t.entity_code = ?
                ORDER BY s.code, ss.code NULLS FIRST
                """, entityCode);
    }

    public Map<String, Object> upsertTrd(String entityCode, Map<String, Object> body) {
        return jdbc.queryForMap("""
                INSERT INTO retention_schedule(entity_code, series_id, subseries_id, active_years, semi_active_years, final_disposition, legal_basis)
                VALUES (?, ?::uuid, ?::uuid, ?, ?, ?, ?)
                ON CONFLICT(entity_code, series_id, subseries_id)
                DO UPDATE SET active_years = EXCLUDED.active_years,
                              semi_active_years = EXCLUDED.semi_active_years,
                              final_disposition = EXCLUDED.final_disposition,
                              legal_basis = EXCLUDED.legal_basis,
                              active = true
                RETURNING id
                """, entityCode,
                body.get("series_id"), body.get("subseries_id"),
                body.getOrDefault("active_years", 1), body.getOrDefault("semi_active_years", 1),
                body.getOrDefault("final_disposition", "CONSERVAR"), body.get("legal_basis"));
    }

    public List<Map<String, Object>> listCaseFiles(String entityCode) {
        return jdbc.queryForList("""
                SELECT id, entity_code, code, title, owner_dependency_id, status, opened_at, closed_at, closed_by, created_by, created_at,
                       closure_index_hash, closure_sealed_at
                FROM electronic_case_file
                WHERE entity_code = ?
                ORDER BY opened_at DESC
                """, entityCode);
    }

    public Map<String, Object> upsertCaseFile(String entityCode, String code, String title, String ownerDependencyId, String createdBy) {
        return jdbc.queryForMap("""
                INSERT INTO electronic_case_file(entity_code, code, title, owner_dependency_id, created_by)
                VALUES (?, ?, ?, ?::uuid, ?::uuid)
                ON CONFLICT(entity_code, code)
                DO UPDATE SET title = EXCLUDED.title,
                              owner_dependency_id = EXCLUDED.owner_dependency_id
                RETURNING id, entity_code, code, title, owner_dependency_id, status, opened_at, closed_at, closed_by, created_by, created_at,
                          closure_index_hash, closure_sealed_at
                """, entityCode, code, title, ownerDependencyId, createdBy);
    }

    public Map<String, Object> addCaseFileItem(String caseFileId, String documentId, String actorUserId) {
        Integer next = jdbc.queryForObject("SELECT COALESCE(MAX(order_index),0)+1 FROM case_file_items WHERE case_file_id = ?::uuid", Integer.class, caseFileId);
        String folio = String.format("F-%05d", next == null ? 1 : next);

        List<Map<String, Object>> rows = jdbc.queryForList("""
                INSERT INTO case_file_items(case_file_id, document_id, order_index, folio, incorporated_by)
                VALUES (?::uuid, ?::uuid, ?, ?, ?::uuid)
                ON CONFLICT(case_file_id, document_id) DO NOTHING
                RETURNING id, case_file_id, document_id, order_index, folio, incorporated_at, incorporated_by
                """, caseFileId, documentId, next, folio, actorUserId);

        if (rows.isEmpty()) return Map.of("ok", true, "detail", "Already linked");

        jdbc.update("UPDATE documentos SET case_file_id = ?::uuid WHERE id = ?::uuid", caseFileId, documentId);
        return Map.of("ok", true, "item", rows.get(0));
    }

    public List<Map<String, Object>> listCaseFileItems(String caseFileId) {
        return jdbc.queryForList("""
                SELECT i.id, i.case_file_id, i.document_id, i.order_index, i.folio, i.incorporated_at, i.incorporated_by,
                       d.asunto, d.estado, d.fecha_elaboracion
                FROM case_file_items i
                JOIN documentos d ON d.id = i.document_id
                WHERE i.case_file_id = ?::uuid
                ORDER BY i.order_index ASC
                """, caseFileId);
    }

    public int closeCaseFile(String caseFileId, String closedBy) {
        return jdbc.update("""
                UPDATE electronic_case_file
                SET status = 'CERRADO', closed_at = now(), closed_by = ?::uuid
                WHERE id = ?::uuid AND status <> 'CERRADO'
                """, closedBy, caseFileId);
    }
}


