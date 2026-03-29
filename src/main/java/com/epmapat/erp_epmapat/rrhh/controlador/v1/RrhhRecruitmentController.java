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

import com.epmapat.erp_epmapat.rrhh.dto.CandidateRequest;
import com.epmapat.erp_epmapat.rrhh.dto.InterviewRequest;
import com.epmapat.erp_epmapat.rrhh.dto.OnboardingRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PageResponse;
import com.epmapat.erp_epmapat.rrhh.dto.VacancyRequest;
import com.epmapat.erp_epmapat.rrhh.servicio.RrhhRecruitmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rrhh")
public class RrhhRecruitmentController {

    private final RrhhRecruitmentService service;

    @GetMapping("/vacancies")
    public ResponseEntity<PageResponse<?>> listVacancies(@RequestParam(required = false) String area,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listVacancies(area, stage, status, date, page, size)));
    }

    @PostMapping("/vacancies")
    public ResponseEntity<?> createVacancy(@Valid @RequestBody VacancyRequest request) {
        return ResponseEntity.ok(service.createVacancy(request));
    }

    @PutMapping("/vacancies/{id}")
    public ResponseEntity<?> updateVacancy(@PathVariable UUID id, @Valid @RequestBody VacancyRequest request) {
        return ResponseEntity.ok(service.updateVacancy(id, request));
    }

    @GetMapping("/candidates")
    public ResponseEntity<PageResponse<?>> listCandidates(@RequestParam(required = false) UUID vacancy,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listCandidates(vacancy, area, stage, status, date, page, size)));
    }

    @PostMapping("/candidates")
    public ResponseEntity<?> createCandidate(@Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.ok(service.createCandidate(request));
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable UUID id, @Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.ok(service.updateCandidate(id, request));
    }

    @GetMapping("/interviews")
    public ResponseEntity<PageResponse<?>> listInterviews(@RequestParam(required = false) UUID vacancy,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listInterviews(vacancy, stage, status, date, page, size)));
    }

    @PostMapping("/interviews")
    public ResponseEntity<?> createInterview(@Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(service.createInterview(request));
    }

    @PutMapping("/interviews/{id}")
    public ResponseEntity<?> updateInterview(@PathVariable UUID id, @Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(service.updateInterview(id, request));
    }

    @GetMapping("/onboarding")
    public ResponseEntity<PageResponse<?>> listOnboarding(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listOnboarding(status, date, page, size)));
    }

    @PostMapping("/onboarding")
    public ResponseEntity<?> createOnboarding(@Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(service.createOnboarding(request));
    }

    @PutMapping("/onboarding/{id}")
    public ResponseEntity<?> updateOnboarding(@PathVariable UUID id, @Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(service.updateOnboarding(id, request));
    }
}
