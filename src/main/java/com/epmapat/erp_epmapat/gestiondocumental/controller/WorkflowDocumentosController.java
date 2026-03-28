package com.epmapat.erp_epmapat.gestiondocumental.controller;

import com.epmapat.erp_epmapat.gestiondocumental.service.WorkflowDocumentosService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping({"/api/documents", "/api/documentos"})
public class WorkflowDocumentosController {

    private final WorkflowDocumentosService service;

    public WorkflowDocumentosController(WorkflowDocumentosService service) {
        this.service = service;
    }

    @PostMapping({"/{docId}/assign", "/{docId}/asignar"})
    public ResponseEntity<?> assign(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("assignment_id", service.assign(docId, body)));
    }

    @PostMapping({"/{docId}/derive", "/{docId}/derivar"})
    public ResponseEntity<?> derive(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        try {
            service.ensureActionAllowed(docId, (String) body.get("user_id"), (String) body.get("user_role"), "DERIVAR");
            return ResponseEntity.ok(Map.of("derivation_id", service.derive(docId, body)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("detail", e.getMessage()));
        }
    }

    @PostMapping("/{docId}/derive/bulk")
    public ResponseEntity<?> deriveBulk(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.deriveBulk(docId, body));
    }

    @GetMapping("/{docId}/derivations")
    public ResponseEntity<?> derivations(@PathVariable String docId) {
        return ResponseEntity.ok(service.listDerivations(docId));
    }

    @GetMapping("/derivations/pending")
    public ResponseEntity<?> pending(@RequestParam(required = false) String to_user_id,
                                     @RequestParam(required = false) String to_dependency_id,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        try {
            return ResponseEntity.ok(service.pendingDerivations(to_user_id, to_dependency_id, page, pageSize));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("detail", e.getMessage()));
        }
    }

    @PatchMapping("/derivations/{derivationId}/read")
    public ResponseEntity<?> markRead(@PathVariable String derivationId, @RequestBody(required = false) Map<String, Object> body) {
        String reader = body == null ? null : (String) body.get("reader_user_id");
        int n = service.markRead(derivationId, reader);
        return n > 0 ? ResponseEntity.ok(Map.of("ok", true)) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/derivations/{derivationId}/attend")
    public ResponseEntity<?> attend(@PathVariable String derivationId, @RequestBody(required = false) Map<String, Object> body) {
        body = body == null ? Map.of() : body;
        int n = service.attendDerivation(derivationId, (String) body.get("user_id"), (String) body.get("note"));
        return n > 0 ? ResponseEntity.ok(Map.of("ok", true)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{docId}/responses")
    public ResponseEntity<?> respond(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        try {
            service.ensureActionAllowed(docId, (String) body.get("user_id"), (String) body.get("user_role"), "RESPONDER");
            return ResponseEntity.ok(Map.of("response_id", service.respond(docId, body)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("detail", e.getMessage()));
        }
    }

    @PostMapping("/{docId}/responses/nested")
    public ResponseEntity<?> respondNested(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        try {
            service.ensureActionAllowed(docId, (String) body.get("user_id"), (String) body.get("user_role"), "RESPONDER");
            return ResponseEntity.ok(service.createNestedResponse(docId, body));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("detail", e.getMessage()));
        }
    }

    @GetMapping("/{docId}/relations")
    public ResponseEntity<?> relations(@PathVariable String docId,
                                       @RequestParam(required = false) String relation_type) {
        return ResponseEntity.ok(service.listRelations(docId, relation_type));
    }

    @PostMapping(value = "/{docId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @PathVariable String docId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploaded_by_user_id", required = false) String uploadedByUserId,
            @RequestParam(value = "file_kind", required = false, defaultValue = "ANEXO") String fileKind
    ) {
        return ResponseEntity.ok(service.saveFile(docId, file, uploadedByUserId, fileKind));
    }

    @GetMapping("/{docId}/files")
    public ResponseEntity<?> listFiles(@PathVariable String docId) {
        return ResponseEntity.ok(service.listFiles(docId));
    }

    @GetMapping("/{docId}/files/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable String docId, @PathVariable String fileId) {
        Map<String, Object> row = service.getFile(docId, fileId);
        return row == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(row);
    }

    @GetMapping("/{docId}/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String docId, @PathVariable String fileId) {
        WorkflowDocumentosService.FileDownload file = service.downloadFile(docId, fileId);
        String filename = file.originalFilename() == null ? "archivo" : file.originalFilename();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentLength(file.size())
                .body(file.resource());
    }

    @PostMapping({"/{docId}/issue", "/{docId}/emitir"})
    public ResponseEntity<?> issue(@PathVariable String docId, @RequestBody(required = false) Map<String, Object> body) {
        String userId = body == null ? null : (String) body.get("user_id");
        String role = body == null ? null : (String) body.get("user_role");
        try {
            service.ensureActionAllowed(docId, userId, role, "EMITIR");
            return ResponseEntity.ok(service.issue(docId, userId));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("detail", e.getMessage()));
        }
    }

    @PostMapping({"/{docId}/receive", "/{docId}/recibir"})
    public ResponseEntity<?> receive(@PathVariable String docId, @RequestBody(required = false) Map<String, Object> body) {
        body = body == null ? Map.of() : body;
        try {
            service.ensureActionAllowed(docId, (String) body.get("user_id"), (String) body.get("user_role"), "RECIBIR");
            return ResponseEntity.ok(service.receive(docId,
                    (String) body.get("receiver_id"),
                    (String) body.get("dependency_id"),
                    (String) body.get("user_id"),
                    (String) body.get("comment")));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("detail", e.getMessage()));
        }
    }

    @GetMapping({"/receptions/pending", "/recepciones/pendientes"})
    public ResponseEntity<?> pendingReceptions(@RequestParam("entity_code") String entityCode,
                                               @RequestParam(required = false) String receiver_id,
                                               @RequestParam(required = false) String dependency_id) {
        return ResponseEntity.ok(service.pendingReceptions(entityCode, receiver_id, dependency_id));
    }

    @GetMapping("/{docId}/timeline")
    public ResponseEntity<?> timeline(@PathVariable String docId) {
        return ResponseEntity.ok(service.timeline(docId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> overdue(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.overdue(entityCode));
    }

    @GetMapping("/due-soon")
    public ResponseEntity<?> dueSoon(@RequestParam("entity_code") String entityCode,
                                     @RequestParam(defaultValue = "48") int hours) {
        return ResponseEntity.ok(service.dueSoon(entityCode, hours));
    }

    @PostMapping("/alerts/generate")
    public ResponseEntity<?> generateAlerts(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.generateAlerts(entityCode));
    }

    @GetMapping("/alerts")
    public ResponseEntity<?> listAlerts(@RequestParam("entity_code") String entityCode,
                                        @RequestParam(required = false, defaultValue = "PENDIENTE") String state,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.listAlerts(entityCode, state, page, pageSize));
    }

    @PostMapping("/alerts/dispatch")
    public ResponseEntity<?> dispatchAlerts(@RequestParam("entity_code") String entityCode,
                                            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(service.dispatchAlerts(entityCode, limit));
    }
}
