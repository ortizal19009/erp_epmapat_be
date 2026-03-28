package com.epmapat.erp_epmapat.gestiondocumental.controller;

import com.epmapat.erp_epmapat.gestiondocumental.service.DocumentosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/documents", "/api/documentos"})
public class DocumentosController {

    private final DocumentosService service;

    public DocumentosController(DocumentosService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam("entity_code") String entityCode,
                                  @RequestParam(required = false) String estado,
                                  @RequestParam(required = false) String flujo,
                                  @RequestParam(required = false) String q,
                                  @RequestParam(required = false) String dependency_id,
                                  @RequestParam(required = false) String type_id,
                                  @RequestParam(required = false) String user_id,
                                  @RequestParam(required = false) String series_id,
                                  @RequestParam(required = false) String subseries_id,
                                  @RequestParam(required = false) String date_from,
                                  @RequestParam(required = false) String date_to,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.list(entityCode, estado, flujo, q, dependency_id, type_id, user_id, series_id, subseries_id, date_from, date_to, page, pageSize));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestParam("entity_code") String entityCode,
                                       @RequestParam(required = false) String date_from,
                                       @RequestParam(required = false) String date_to) {
        return ResponseEntity.ok(service.dashboard(entityCode, date_from, date_to));
    }

    @GetMapping("/{docId}")
    public ResponseEntity<?> get(@PathVariable String docId,
                                 @RequestParam(required = false) String user_id) {
        return ResponseEntity.ok(service.get(docId, user_id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            String id = service.create(body);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("detail", e.getMessage()));
        }
    }

    @PutMapping("/{docId}")
    public ResponseEntity<?> update(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        try {
            int n = service.update(docId, body);
            return n > 0 ? ResponseEntity.ok(Map.of("ok", true)) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("detail", e.getMessage()));
        }
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<?> delete(@PathVariable String docId) {
        int n = service.delete(docId);
        return n > 0 ? ResponseEntity.ok(Map.of("ok", true)) : ResponseEntity.notFound().build();
    }
}


