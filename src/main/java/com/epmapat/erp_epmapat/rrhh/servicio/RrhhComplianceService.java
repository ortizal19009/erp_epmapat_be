package com.epmapat.erp_epmapat.rrhh.servicio;

import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.applyAudit;
import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.pageable;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.rrhh.dto.AuditRequest;
import com.epmapat.erp_epmapat.rrhh.dto.AuditResponse;
import com.epmapat.erp_epmapat.rrhh.dto.PolicyRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PolicyResponse;
import com.epmapat.erp_epmapat.rrhh.dto.SafetyTrainingRequest;
import com.epmapat.erp_epmapat.rrhh.dto.SafetyTrainingResponse;
import com.epmapat.erp_epmapat.rrhh.exception.RrhhConflictException;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhAuditRecord;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhPolicy;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhSafetyTraining;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhAuditRecordRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhPolicyRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhSafetyTrainingRepository;
import com.epmapat.erp_epmapat.rrhh.spec.RrhhSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RrhhComplianceService {

    private final RrhhAuditRecordRepository auditRepository;
    private final RrhhPolicyRepository policyRepository;
    private final RrhhSafetyTrainingRepository safetyTrainingRepository;

    @Transactional(readOnly = true)
    public Page<AuditResponse> listAudits(String auditType, String status, java.time.LocalDate fromDate, Integer page,
            Integer size) {
        Specification<RrhhAuditRecord> spec = Specification.<RrhhAuditRecord>where(RrhhSpecifications.likeIgnoreCase("auditType", auditType))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("auditDate", fromDate));
        return auditRepository.findAll(spec, pageable(page, size)).map(this::toAuditResponse);
    }

    @Transactional
    public AuditResponse createAudit(AuditRequest request) {
        RrhhAuditRecord entity = new RrhhAuditRecord();
        mapAudit(entity, request);
        applyAudit(entity, request, true);
        return toAuditResponse(auditRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<PolicyResponse> listPolicies(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhPolicy> spec = Specification.<RrhhPolicy>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("effectiveDate", fromDate));
        return policyRepository.findAll(spec, pageable(page, size)).map(this::toPolicyResponse);
    }

    @Transactional
    public PolicyResponse createPolicy(PolicyRequest request) {
        validatePolicyCode(request, null);
        RrhhPolicy entity = new RrhhPolicy();
        mapPolicy(entity, request);
        applyAudit(entity, request, true);
        return toPolicyResponse(policyRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<SafetyTrainingResponse> listSafetyTrainings(String area, String status, java.time.LocalDate fromDate,
            Integer page, Integer size) {
        Specification<RrhhSafetyTraining> spec = Specification.<RrhhSafetyTraining>where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("trainingDate", fromDate));
        return safetyTrainingRepository.findAll(spec, pageable(page, size)).map(this::toSafetyResponse);
    }

    @Transactional
    public SafetyTrainingResponse createSafetyTraining(SafetyTrainingRequest request) {
        RrhhSafetyTraining entity = new RrhhSafetyTraining();
        mapSafety(entity, request);
        applyAudit(entity, request, true);
        return toSafetyResponse(safetyTrainingRepository.save(entity));
    }

    private void validatePolicyCode(PolicyRequest request, UUID id) {
        boolean duplicated = id == null ? policyRepository.existsByCode(request.getCode())
                : policyRepository.existsByCodeAndIdNot(request.getCode(), id);
        if (duplicated) {
            throw new RrhhConflictException("Ya existe una política con código " + request.getCode());
        }
    }

    private void mapAudit(RrhhAuditRecord entity, AuditRequest request) {
        entity.setAuditType(request.getAuditType());
        entity.setTitle(request.getTitle());
        entity.setFindings(request.getFindings());
        entity.setActionPlan(request.getActionPlan());
        entity.setStatus(request.getStatus());
        entity.setAuditDate(request.getAuditDate());
        entity.setResponsible(request.getResponsible());
    }

    private void mapPolicy(RrhhPolicy entity, PolicyRequest request) {
        entity.setCode(request.getCode());
        entity.setTitle(request.getTitle());
        entity.setVersion(request.getVersion());
        entity.setEffectiveDate(request.getEffectiveDate());
        entity.setStatus(request.getStatus());
        entity.setDescription(request.getDescription());
    }

    private void mapSafety(RrhhSafetyTraining entity, SafetyTrainingRequest request) {
        entity.setTitle(request.getTitle());
        entity.setArea(request.getArea());
        entity.setTrainingDate(request.getTrainingDate());
        entity.setHours(request.getHours());
        entity.setAttendees(request.getAttendees());
        entity.setStatus(request.getStatus());
    }

    private AuditResponse toAuditResponse(RrhhAuditRecord entity) {
        return AuditResponse.builder()
                .id(entity.getId())
                .auditType(entity.getAuditType())
                .title(entity.getTitle())
                .findings(entity.getFindings())
                .actionPlan(entity.getActionPlan())
                .status(entity.getStatus())
                .auditDate(entity.getAuditDate())
                .responsible(entity.getResponsible())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private PolicyResponse toPolicyResponse(RrhhPolicy entity) {
        return PolicyResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .title(entity.getTitle())
                .version(entity.getVersion())
                .effectiveDate(entity.getEffectiveDate())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private SafetyTrainingResponse toSafetyResponse(RrhhSafetyTraining entity) {
        return SafetyTrainingResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .area(entity.getArea())
                .trainingDate(entity.getTrainingDate())
                .hours(entity.getHours())
                .attendees(entity.getAttendees())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
