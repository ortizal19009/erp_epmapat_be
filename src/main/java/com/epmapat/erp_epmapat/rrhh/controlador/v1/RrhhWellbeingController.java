package com.epmapat.erp_epmapat.rrhh.controlador.v1;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.dto.ClimateSurveyRequest;
import com.epmapat.erp_epmapat.rrhh.dto.ConflictCaseRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PageResponse;
import com.epmapat.erp_epmapat.rrhh.dto.WellbeingProgramRequest;
import com.epmapat.erp_epmapat.rrhh.servicio.RrhhWellbeingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rrhh")
public class RrhhWellbeingController {

    private final RrhhWellbeingService service;

    @GetMapping("/climate-surveys")
    public ResponseEntity<PageResponse<?>> listClimateSurveys(@RequestParam(required = false) String area,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listClimateSurveys(area, status, date, page, size)));
    }

    @PostMapping("/climate-surveys")
    public ResponseEntity<?> createClimateSurvey(@Valid @RequestBody ClimateSurveyRequest request) {
        return ResponseEntity.ok(service.createClimateSurvey(request));
    }

    @GetMapping("/climate-results")
    public ResponseEntity<PageResponse<?>> listClimateResults(@RequestParam(required = false) String area,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listClimateResults(area, page, size)));
    }

    @GetMapping("/wellbeing-programs")
    public ResponseEntity<PageResponse<?>> listWellbeingPrograms(@RequestParam(required = false) String area,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listWellbeingPrograms(area, status, date, page, size)));
    }

    @PostMapping("/wellbeing-programs")
    public ResponseEntity<?> createWellbeingProgram(@Valid @RequestBody WellbeingProgramRequest request) {
        return ResponseEntity.ok(service.createWellbeingProgram(request));
    }

    @GetMapping("/conflicts")
    public ResponseEntity<PageResponse<?>> listConflicts(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listConflicts(status, date, page, size)));
    }

    @PostMapping("/conflicts")
    public ResponseEntity<?> createConflict(@Valid @RequestBody ConflictCaseRequest request) {
        return ResponseEntity.ok(service.createConflict(request));
    }

    @PutMapping("/conflicts/{id}")
    public ResponseEntity<?> updateConflict(@PathVariable UUID id, @Valid @RequestBody ConflictCaseRequest request) {
        return ResponseEntity.ok(service.updateConflict(id, request));
    }
}
