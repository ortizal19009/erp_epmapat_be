package com.epmapat.erp_epmapat.gestiondocumental.controller;

import com.epmapat.erp_epmapat.gestiondocumental.dto.catalogo.CatalogoRequests;
import com.epmapat.erp_epmapat.gestiondocumental.service.CatalogoDocumentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CatalogoDocumentalController {

    private final CatalogoDocumentalService service;

    public CatalogoDocumentalController(CatalogoDocumentalService service) {
        this.service = service;
    }

    @GetMapping({"/entities", "/entidades"})
    public ResponseEntity<?> listEntities(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(service.listEntities(q));
    }

    @PostMapping({"/entities", "/entidades"})
    public ResponseEntity<?> createEntity(@RequestBody CatalogoRequests.EntidadRequest req) {
        return ResponseEntity.ok(service.createEntity(req));
    }

    @PutMapping({"/entities/{id}", "/entidades/{id}"})
    public ResponseEntity<?> updateEntity(@PathVariable String id, @RequestBody CatalogoRequests.EntidadRequest req) {
        return ResponseEntity.ok(service.updateEntity(id, req));
    }

    @PutMapping({"/entities/{id}/status", "/entidades/{id}/estado"})
    public ResponseEntity<?> setEntityStatus(@PathVariable String id, @RequestBody CatalogoRequests.EntidadRequest req) {
        return ResponseEntity.ok(service.setEntityStatus(id, req.getActive()));
    }

    @GetMapping({"/dependencies", "/dependencias"})
    public ResponseEntity<?> listDependencies(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listDependencies(entityCode));
    }

    @PostMapping({"/dependencies", "/dependencias"})
    public ResponseEntity<?> createDependency(@RequestBody CatalogoRequests.DependenciaRequest req) {
        return ResponseEntity.ok(service.createDependency(req));
    }

    @PutMapping({"/dependencies/{id}", "/dependencias/{id}"})
    public ResponseEntity<?> updateDependency(@PathVariable String id, @RequestBody CatalogoRequests.DependenciaRequest req) {
        return ResponseEntity.ok(service.updateDependency(id, req));
    }

    @PutMapping({"/dependencies/{id}/status", "/dependencias/{id}/estado"})
    public ResponseEntity<?> setDependencyStatus(@PathVariable String id, @RequestBody CatalogoRequests.DependenciaRequest req) {
        return ResponseEntity.ok(service.setDependencyStatus(id, req.getActive()));
    }

    @GetMapping({"/document-types", "/tipos-documento"})
    public ResponseEntity<?> listDocumentTypes(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listDocumentTypes(entityCode));
    }

    @PostMapping({"/document-types", "/tipos-documento"})
    public ResponseEntity<?> createDocumentType(@RequestBody CatalogoRequests.TipoDocumentoRequest req) {
        return ResponseEntity.ok(service.createDocumentType(req));
    }

    @PutMapping({"/document-types/{id}", "/tipos-documento/{id}"})
    public ResponseEntity<?> updateDocumentType(@PathVariable String id, @RequestBody CatalogoRequests.TipoDocumentoRequest req) {
        return ResponseEntity.ok(service.updateDocumentType(id, req));
    }

    @PutMapping({"/document-types/{id}/status", "/tipos-documento/{id}/estado"})
    public ResponseEntity<?> setDocumentTypeStatus(@PathVariable String id, @RequestBody CatalogoRequests.TipoDocumentoRequest req) {
        return ResponseEntity.ok(service.setDocumentTypeStatus(id, req.getActive()));
    }

    @GetMapping({"/lookups/users", "/lookups/usuarios"})
    public ResponseEntity<?> lookupUsers(@RequestParam("entity_code") String entityCode,
                                         @RequestParam(required = false) String q,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.lookupUsers(entityCode, q, page, pageSize));
    }

    @GetMapping({"/lookups/persons", "/lookups/personas"})
    public ResponseEntity<?> lookupPersons(@RequestParam("entity_code") String entityCode,
                                           @RequestParam(required = false) String q,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.lookupPersons(entityCode, q, page, pageSize));
    }
}
