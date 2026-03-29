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

import com.epmapat.erp_epmapat.rrhh.dto.BenefitRequest;
import com.epmapat.erp_epmapat.rrhh.dto.BenefitResponse;
import com.epmapat.erp_epmapat.rrhh.dto.IncentiveRequest;
import com.epmapat.erp_epmapat.rrhh.dto.IncentiveResponse;
import com.epmapat.erp_epmapat.rrhh.dto.PayrollRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PayrollResponse;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhBenefit;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhIncentive;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhPayroll;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhBenefitRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhIncentiveRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhPayrollRepository;
import com.epmapat.erp_epmapat.rrhh.spec.RrhhSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RrhhCompensationService {

    private final RrhhPayrollRepository payrollRepository;
    private final RrhhBenefitRepository benefitRepository;
    private final RrhhIncentiveRepository incentiveRepository;
    private final RrhhEmployeeService employeeService;

    @Transactional(readOnly = true)
    public Page<PayrollResponse> listPayrolls(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhPayroll> spec = Specification.<RrhhPayroll>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("periodStart", fromDate));
        return payrollRepository.findAll(spec, pageable(page, size)).map(this::toPayrollResponse);
    }

    @Transactional(readOnly = true)
    public PayrollResponse getPayroll(UUID id) {
        return toPayrollResponse(getOrThrow(payrollRepository.findById(id), "Nómina no encontrada: " + id));
    }

    @Transactional
    public PayrollResponse createPayroll(PayrollRequest request) {
        RrhhPayroll entity = new RrhhPayroll();
        mapPayroll(entity, request);
        applyAudit(entity, request, true);
        return toPayrollResponse(payrollRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<BenefitResponse> listBenefits(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhBenefit> spec = Specification.<RrhhBenefit>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("startDate", fromDate));
        return benefitRepository.findAll(spec, pageable(page, size)).map(this::toBenefitResponse);
    }

    @Transactional
    public BenefitResponse createBenefit(BenefitRequest request) {
        RrhhBenefit entity = new RrhhBenefit();
        mapBenefit(entity, request);
        applyAudit(entity, request, true);
        return toBenefitResponse(benefitRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<IncentiveResponse> listIncentives(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhIncentive> spec = Specification.<RrhhIncentive>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("grantedDate", fromDate));
        return incentiveRepository.findAll(spec, pageable(page, size)).map(this::toIncentiveResponse);
    }

    @Transactional
    public IncentiveResponse createIncentive(IncentiveRequest request) {
        RrhhIncentive entity = new RrhhIncentive();
        mapIncentive(entity, request);
        applyAudit(entity, request, true);
        return toIncentiveResponse(incentiveRepository.save(entity));
    }

    private void mapPayroll(RrhhPayroll entity, PayrollRequest request) {
        entity.setEmployee(employeeService.findEntity(request.getEmployeeId()));
        entity.setPeriodStart(request.getPeriodStart());
        entity.setPeriodEnd(request.getPeriodEnd());
        entity.setGrossAmount(request.getGrossAmount());
        entity.setTotalBenefits(request.getTotalBenefits());
        entity.setTotalDeductions(request.getTotalDeductions());
        entity.setNetAmount(request.getNetAmount());
        entity.setOvertimeHours(request.getOvertimeHours());
        entity.setStatus(request.getStatus());
    }

    private void mapBenefit(RrhhBenefit entity, BenefitRequest request) {
        entity.setEmployee(request.getEmployeeId() == null ? null : employeeService.findEntity(request.getEmployeeId()));
        entity.setBenefitType(request.getBenefitType());
        entity.setName(request.getName());
        entity.setCost(request.getCost());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());
    }

    private void mapIncentive(RrhhIncentive entity, IncentiveRequest request) {
        entity.setEmployee(request.getEmployeeId() == null ? null : employeeService.findEntity(request.getEmployeeId()));
        entity.setIncentiveType(request.getIncentiveType());
        entity.setTitle(request.getTitle());
        entity.setAmount(request.getAmount());
        entity.setGrantedDate(request.getGrantedDate());
        entity.setStatus(request.getStatus());
    }

    private PayrollResponse toPayrollResponse(RrhhPayroll entity) {
        return PayrollResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .grossAmount(entity.getGrossAmount())
                .totalBenefits(entity.getTotalBenefits())
                .totalDeductions(entity.getTotalDeductions())
                .netAmount(entity.getNetAmount())
                .overtimeHours(entity.getOvertimeHours())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private BenefitResponse toBenefitResponse(RrhhBenefit entity) {
        return BenefitResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee() == null ? null : entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .benefitType(entity.getBenefitType())
                .name(entity.getName())
                .cost(entity.getCost())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private IncentiveResponse toIncentiveResponse(RrhhIncentive entity) {
        return IncentiveResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee() == null ? null : entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .incentiveType(entity.getIncentiveType())
                .title(entity.getTitle())
                .amount(entity.getAmount())
                .grantedDate(entity.getGrantedDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
