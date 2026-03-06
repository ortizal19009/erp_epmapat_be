package com.epmapat.erp_epmapat.controlador;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.AuditoriaMobileRequest;
import com.epmapat.erp_epmapat.servicio.AuditoriaTxtService;

@RestController
@RequestMapping("/mobile/auditoria")
public class AuditoriaMobileApi {

    @Autowired
    private AuditoriaTxtService service;

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody AuditoriaMobileRequest req) {
        try {
            String path = service.save(req);
            Map<String, Object> out = new HashMap<>();
            out.put("ok", true);
            out.put("path", path);
            return ResponseEntity.ok(out);
        } catch (Exception e) {
            Map<String, Object> out = new HashMap<>();
            out.put("ok", false);
            out.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(out);
        }
    }
}
