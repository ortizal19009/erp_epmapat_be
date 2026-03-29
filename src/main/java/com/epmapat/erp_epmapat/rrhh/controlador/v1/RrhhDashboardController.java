package com.epmapat.erp_epmapat.rrhh.controlador.v1;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.servicio.RrhhDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rrhh/dashboard")
public class RrhhDashboardController {

    private final RrhhDashboardService service;

    @GetMapping
    public ResponseEntity<?> getDashboard(@RequestParam(required = false) String area,
            @RequestParam(required = false) String contractType,
            @RequestParam(required = false) String dependency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(service.getDashboard(area, contractType, fromDate, toDate, dependency));
    }
}
