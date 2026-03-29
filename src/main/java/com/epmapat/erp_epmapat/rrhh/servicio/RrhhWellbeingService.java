package com.epmapat.erp_epmapat.rrhh.servicio;

import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.applyAudit;
import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.employeeFullName;
import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.getOrThrow;
import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.pageable;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.rrhh.dto.ClimateSurveyRequest;
import com.epmapat.erp_epmapat.rrhh.dto.ClimateSurveyResponse;
import com.epmapat.erp_epmapat.rrhh.dto.ConflictCaseRequest;
import com.epmapat.erp_epmapat.rrhh.dto.ConflictCaseResponse;
import com.epmapat.erp_epmapat.rrhh.dto.WellbeingProgramRequest;
import com.epmapat.erp_epmapat.rrhh.dto.WellbeingProgramResponse;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhClimateSurvey;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhConflictCase;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhWellbeingProgram;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhClimateSurveyRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhConflictCaseRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhWellbeingProgramRepository;
import com.epmapat.erp_epmapat.rrhh.spec.RrhhSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RrhhWellbeingService {

    private final RrhhClimateSurveyRepository climateRepository;
    private final RrhhWellbeingProgramRepository wellbeingRepository;
    private final RrhhConflictCaseRepository conflictRepository;
    private final RrhhEmployeeService employeeService;

    @Transactional(readOnly = true)
    public Page<ClimateSurveyResponse> listClimateSurveys(String area, String status, java.time.LocalDate fromDate,
            Integer page, Integer size) {
        Specification<RrhhClimateSurvey> spec = Specification.<RrhhClimateSurvey>where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("startDate", fromDate));
        return climateRepository.findAll(spec, pageable(page, size)).map(this::toClimateResponse);
    }

    @Transactional
    public ClimateSurveyResponse createClimateSurvey(ClimateSurveyRequest request) {
        RrhhClimateSurvey entity = new RrhhClimateSurvey();
        mapClimate(entity, request);
        applyAudit(entity, request, true);
        return toClimateResponse(climateRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<ClimateSurveyResponse> listClimateResults(String area, Integer page, Integer size) {
        Specification<RrhhClimateSurvey> spec = Specification.<RrhhClimateSurvey>where(RrhhSpecifications.likeIgnoreCase("area", area));
        return climateRepository.findAll(spec, pageable(page, size)).map(this::toClimateResponse);
    }

    @Transactional(readOnly = true)
    public Page<WellbeingProgramResponse> listWellbeingPrograms(String area, String status, java.time.LocalDate fromDate,
            Integer page, Integer size) {
        Specification<RrhhWellbeingProgram> spec = Specification.<RrhhWellbeingProgram>where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("startDate", fromDate));
        return wellbeingRepository.findAll(spec, pageable(page, size)).map(this::toWellbeingResponse);
    }

    @Transactional
    public WellbeingProgramResponse createWellbeingProgram(WellbeingProgramRequest request) {
        RrhhWellbeingProgram entity = new RrhhWellbeingProgram();
        mapWellbeing(entity, request);
        applyAudit(entity, request, true);
        return toWellbeingResponse(wellbeingRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<ConflictCaseResponse> listConflicts(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhConflictCase> spec = Specification.<RrhhConflictCase>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("openedAt", fromDate));
        return conflictRepository.findAll(spec, pageable(page, size)).map(this::toConflictResponse);
    }

    @Transactional
    public ConflictCaseResponse createConflict(ConflictCaseRequest request) {
        RrhhConflictCase entity = new RrhhConflictCase();
        mapConflict(entity, request);
        applyAudit(entity, request, true);
        return toConflictResponse(conflictRepository.save(entity));
    }

    @Transactional
    public ConflictCaseResponse updateConflict(UUID id, ConflictCaseRequest request) {
        RrhhConflictCase entity = getOrThrow(conflictRepository.findById(id), "Conflicto no encontrado: " + id);
        mapConflict(entity, request);
        applyAudit(entity, request, false);
        return toConflictResponse(conflictRepository.save(entity));
    }

    private void mapClimate(RrhhClimateSurvey entity, ClimateSurveyRequest request) {
        entity.setTitle(request.getTitle());
        entity.setArea(request.getArea());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());
        entity.setParticipationRate(request.getParticipationRate());
        entity.setSatisfactionScore(request.getSatisfactionScore());
    }

    private void mapWellbeing(RrhhWellbeingProgram entity, WellbeingProgramRequest request) {
        entity.setName(request.getName());
        entity.setArea(request.getArea());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());
        entity.setParticipationRate(request.getParticipationRate());
        entity.setCost(request.getCost());
    }

    private void mapConflict(RrhhConflictCase entity, ConflictCaseRequest request) {
        entity.setEmployee(request.getEmployeeId() == null ? null : employeeService.findEntity(request.getEmployeeId()));
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setResponsible(request.getResponsible());
        entity.setOpenedAt(request.getOpenedAt());
        entity.setResolvedAt(request.getResolvedAt());
        entity.setResolution(request.getResolution());
    }

    private ClimateSurveyResponse toClimateResponse(RrhhClimateSurvey entity) {
        return ClimateSurveyResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .area(entity.getArea())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .participationRate(entity.getParticipationRate())
                .satisfactionScore(entity.getSatisfactionScore())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private WellbeingProgramResponse toWellbeingResponse(RrhhWellbeingProgram entity) {
        return WellbeingProgramResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .area(entity.getArea())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .participationRate(entity.getParticipationRate())
                .cost(entity.getCost())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private ConflictCaseResponse toConflictResponse(RrhhConflictCase entity) {
        return ConflictCaseResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee() == null ? null : entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .responsible(entity.getResponsible())
                .openedAt(entity.getOpenedAt())
                .resolvedAt(entity.getResolvedAt())
                .resolution(entity.getResolution())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
