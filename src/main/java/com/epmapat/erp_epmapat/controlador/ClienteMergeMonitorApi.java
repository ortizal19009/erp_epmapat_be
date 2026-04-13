package com.epmapat.erp_epmapat.controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.ClienteMergeMonitorDetalleDTO;
import com.epmapat.erp_epmapat.interfaces.ClienteMergeMonitorView;
import com.epmapat.erp_epmapat.servicio.ClienteMergeMonitorService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cliente-merge-monitor")
public class ClienteMergeMonitorApi {
    private final ClienteMergeMonitorService service;

    @GetMapping
    public ResponseEntity<Page<ClienteMergeMonitorView>> listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Long masterId,
            @RequestParam(required = false) Long usuario,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findMonitor(
                q,
                masterId,
                usuario,
                parseDesde(desde),
                parseHasta(hasta),
                pageable));
    }

    @GetMapping("/{idMerge}")
    public ResponseEntity<ClienteMergeMonitorDetalleDTO> detalle(@PathVariable Long idMerge) {
        return ResponseEntity.ok(service.findDetalle(idMerge));
    }

    private LocalDateTime parseDesde(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(value.trim()).atStartOfDay();
    }

    private LocalDateTime parseHasta(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(value.trim()).atTime(23, 59, 59);
    }
}
