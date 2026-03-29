package com.epmapat.erp_epmapat.rrhh.controlador.v1;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.dto.EmployeeRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PageResponse;
import com.epmapat.erp_epmapat.rrhh.dto.StatusUpdateRequest;
import com.epmapat.erp_epmapat.rrhh.servicio.RrhhEmployeeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rrhh/employees")
public class RrhhEmployeeController {

    private final RrhhEmployeeService service;

    @GetMapping
    public ResponseEntity<PageResponse<?>> list(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String dependency,
            @RequestParam(required = false) String contractType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDate,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.list(area, dependency, contractType, status, hireDate, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(service.updateStatus(id, request));
    }

    @GetMapping("/{id}/contracts")
    public ResponseEntity<?> contracts(@PathVariable Long id) {
        return ResponseEntity.ok(service.contracts(id));
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<?> files(@PathVariable Long id) {
        return ResponseEntity.ok(service.files(id));
    }

    @GetMapping("/{id}/leaves")
    public ResponseEntity<?> leaves(@PathVariable Long id) {
        return ResponseEntity.ok(service.leaves(id));
    }

    @GetMapping("/{id}/actions")
    public ResponseEntity<?> actions(@PathVariable Long id) {
        return ResponseEntity.ok(service.actions(id));
    }
}
