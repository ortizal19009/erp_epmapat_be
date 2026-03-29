package com.epmapat.erp_epmapat.rrhh.controlador.v1;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.dto.AuditRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PageResponse;
import com.epmapat.erp_epmapat.rrhh.dto.PolicyRequest;
import com.epmapat.erp_epmapat.rrhh.dto.SafetyTrainingRequest;
import com.epmapat.erp_epmapat.rrhh.servicio.RrhhComplianceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rrhh")
public class RrhhComplianceController {

    private final RrhhComplianceService service;

    @GetMapping("/audits")
    public ResponseEntity<PageResponse<?>> listAudits(@RequestParam(required = false) String auditType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listAudits(auditType, status, date, page, size)));
    }

    @PostMapping("/audits")
    public ResponseEntity<?> createAudit(@Valid @RequestBody AuditRequest request) {
        return ResponseEntity.ok(service.createAudit(request));
    }

    @GetMapping("/policies")
    public ResponseEntity<PageResponse<?>> listPolicies(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listPolicies(status, date, page, size)));
    }

    @PostMapping("/policies")
    public ResponseEntity<?> createPolicy(@Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.ok(service.createPolicy(request));
    }

    @GetMapping("/safety-trainings")
    public ResponseEntity<PageResponse<?>> listSafetyTrainings(@RequestParam(required = false) String area,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listSafetyTrainings(area, status, date, page, size)));
    }

    @PostMapping("/safety-trainings")
    public ResponseEntity<?> createSafetyTraining(@Valid @RequestBody SafetyTrainingRequest request) {
        return ResponseEntity.ok(service.createSafetyTraining(request));
    }
}
