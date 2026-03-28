package com.epmapat.erp_epmapat.gestiondocumental.controller;

import com.epmapat.erp_epmapat.gestiondocumental.service.ConfiguracionDocumentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConfiguracionDocumentalController {

    private final ConfiguracionDocumentalService service;

    public ConfiguracionDocumentalController(ConfiguracionDocumentalService service) {
        this.service = service;
    }

    @GetMapping({"/settings/system", "/configuracion/sistema"})
    public ResponseEntity<?> listSystemSettings(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listSystemSettings(entityCode));
    }

    @PostMapping({"/settings/system", "/configuracion/sistema"})
    public ResponseEntity<?> upsertSystemSetting(@RequestParam("entity_code") String entityCode,
                                                 @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.upsertSystemSetting(entityCode,
                String.valueOf(body.get("setting_key")),
                body.get("setting_value") == null ? null : String.valueOf(body.get("setting_value")),
                body.get("updated_by") == null ? null : String.valueOf(body.get("updated_by"))));
    }

    @GetMapping({"/ccd/series", "/cuadro-clasificacion/series"})
    public ResponseEntity<?> listSeries(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listSeries(entityCode));
    }

    @PostMapping({"/ccd/series", "/cuadro-clasificacion/series"})
    public ResponseEntity<?> createSeries(@RequestParam("entity_code") String entityCode,
                                          @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.upsertSeries(entityCode,
                String.valueOf(body.get("code")),
                String.valueOf(body.get("name"))));
    }

    @GetMapping({"/ccd/series/{seriesId}/subseries", "/cuadro-clasificacion/series/{seriesId}/subseries"})
    public ResponseEntity<?> listSubseries(@PathVariable String seriesId) {
        return ResponseEntity.ok(service.listSubseries(seriesId));
    }

    @PostMapping({"/ccd/series/{seriesId}/subseries", "/cuadro-clasificacion/series/{seriesId}/subseries"})
    public ResponseEntity<?> createSubseries(@PathVariable String seriesId,
                                             @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.upsertSubseries(seriesId,
                String.valueOf(body.get("code")),
                String.valueOf(body.get("name"))));
    }

    @GetMapping({"/trd", "/retencion-documental"})
    public ResponseEntity<?> listTrd(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listTrd(entityCode));
    }

    @PostMapping({"/trd", "/retencion-documental"})
    public ResponseEntity<?> upsertTrd(@RequestParam("entity_code") String entityCode,
                                       @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.upsertTrd(entityCode, body));
    }

    @GetMapping({"/case-files", "/expedientes"})
    public ResponseEntity<?> listCaseFiles(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listCaseFiles(entityCode));
    }

    @PostMapping({"/case-files", "/expedientes"})
    public ResponseEntity<?> createCaseFile(@RequestParam("entity_code") String entityCode,
                                            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.upsertCaseFile(entityCode,
                String.valueOf(body.get("code")),
                String.valueOf(body.get("title")),
                body.get("owner_dependency_id") == null ? null : String.valueOf(body.get("owner_dependency_id")),
                body.get("created_by") == null ? null : String.valueOf(body.get("created_by"))));
    }

    @PostMapping({"/case-files/{caseFileId}/items", "/expedientes/{caseFileId}/items"})
    public ResponseEntity<?> addCaseFileItem(@PathVariable String caseFileId,
                                             @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.addCaseFileItem(caseFileId,
                String.valueOf(body.get("document_id")),
                body.get("actor_user_id") == null ? null : String.valueOf(body.get("actor_user_id"))));
    }

    @GetMapping({"/case-files/{caseFileId}/items", "/expedientes/{caseFileId}/items"})
    public ResponseEntity<?> listCaseFileItems(@PathVariable String caseFileId) {
        return ResponseEntity.ok(service.listCaseFileItems(caseFileId));
    }

    @PostMapping({"/case-files/{caseFileId}/close", "/expedientes/{caseFileId}/cerrar"})
    public ResponseEntity<?> closeCaseFile(@PathVariable String caseFileId,
                                           @RequestBody(required = false) Map<String, Object> body) {
        String closedBy = body == null ? null : (String) body.get("closed_by");
        int n = service.closeCaseFile(caseFileId, closedBy);
        return n > 0 ? ResponseEntity.ok(Map.of("ok", true)) : ResponseEntity.badRequest().body(Map.of("ok", false, "detail", "Case file already closed or not found"));
    }
}

