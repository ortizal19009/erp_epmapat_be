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

import com.epmapat.erp_epmapat.rrhh.dto.CareerPlanRequest;
import com.epmapat.erp_epmapat.rrhh.dto.MentoringRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PageResponse;
import com.epmapat.erp_epmapat.rrhh.dto.PerformanceReviewRequest;
import com.epmapat.erp_epmapat.rrhh.dto.TrainingPlanRequest;
import com.epmapat.erp_epmapat.rrhh.servicio.RrhhDevelopmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rrhh")
public class RrhhDevelopmentController {

    private final RrhhDevelopmentService service;

    @GetMapping("/trainings")
    public ResponseEntity<PageResponse<?>> listTrainings(@RequestParam(required = false) String area,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listTrainings(area, status, date, page, size)));
    }

    @PostMapping("/trainings")
    public ResponseEntity<?> createTraining(@Valid @RequestBody TrainingPlanRequest request) {
        return ResponseEntity.ok(service.createTraining(request));
    }

    @PutMapping("/trainings/{id}")
    public ResponseEntity<?> updateTraining(@PathVariable UUID id, @Valid @RequestBody TrainingPlanRequest request) {
        return ResponseEntity.ok(service.updateTraining(id, request));
    }

    @GetMapping("/performance-reviews")
    public ResponseEntity<PageResponse<?>> listReviews(@RequestParam(required = false) String period,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listReviews(period, status, date, page, size)));
    }

    @PostMapping("/performance-reviews")
    public ResponseEntity<?> createReview(@Valid @RequestBody PerformanceReviewRequest request) {
        return ResponseEntity.ok(service.createReview(request));
    }

    @PutMapping("/performance-reviews/{id}")
    public ResponseEntity<?> updateReview(@PathVariable UUID id, @Valid @RequestBody PerformanceReviewRequest request) {
        return ResponseEntity.ok(service.updateReview(id, request));
    }

    @GetMapping("/career-plans")
    public ResponseEntity<PageResponse<?>> listCareerPlans(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listCareerPlans(status, date, page, size)));
    }

    @PostMapping("/career-plans")
    public ResponseEntity<?> createCareerPlan(@Valid @RequestBody CareerPlanRequest request) {
        return ResponseEntity.ok(service.createCareerPlan(request));
    }

    @PutMapping("/career-plans/{id}")
    public ResponseEntity<?> updateCareerPlan(@PathVariable UUID id, @Valid @RequestBody CareerPlanRequest request) {
        return ResponseEntity.ok(service.updateCareerPlan(id, request));
    }

    @GetMapping("/mentoring")
    public ResponseEntity<PageResponse<?>> listMentoring(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listMentoring(status, date, page, size)));
    }

    @PostMapping("/mentoring")
    public ResponseEntity<?> createMentoring(@Valid @RequestBody MentoringRequest request) {
        return ResponseEntity.ok(service.createMentoring(request));
    }

    @PutMapping("/mentoring/{id}")
    public ResponseEntity<?> updateMentoring(@PathVariable UUID id, @Valid @RequestBody MentoringRequest request) {
        return ResponseEntity.ok(service.updateMentoring(id, request));
    }
}
