package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.ThPerformanceRequest;
import com.epmapat.erp_epmapat.rrhh.modelo.ThPerformanceReview;
import com.epmapat.erp_epmapat.rrhh.servicio.ThPerformanceAccessService;
import com.epmapat.erp_epmapat.rrhh.servicio.ThPerformanceServicio;
import lombok.RequiredArgsConstructor;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/th-performance")
public class ThPerformanceApi {
    private final ThPerformanceAccessService access;
    private final ThPerformanceServicio service;

    @GetMapping("/permissions")
    public Map<String, Object> permissions(HttpServletRequest request) {
        Long actor = access.require(request, false);
        boolean write = false;
        try { access.require(request, true); write = true; }
        catch (ResponseStatusException e) { if (e.getStatus() != HttpStatus.FORBIDDEN) throw e; }
        return Map.of("userId", actor, "canWrite", write);
    }

    @GetMapping("/persona/{idpersonal}")
    public ResponseEntity<Page<ThPerformanceReview>> listar(@PathVariable Long idpersonal,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(service.listar(idpersonal, page, size));
    }

    @PostMapping
    public ResponseEntity<ThPerformanceReview> registrar(@Valid @RequestBody ThPerformanceRequest body,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(body, access.require(request, true)));
    }
}
