package com.epmapat.erp_epmapat.rrhh.servicio;

import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.applyAudit;
import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.getOrThrow;
import static com.epmapat.erp_epmapat.rrhh.servicio.RrhhSupport.pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.rrhh.dto.ActionResponse;
import com.epmapat.erp_epmapat.rrhh.dto.EmployeeContractResponse;
import com.epmapat.erp_epmapat.rrhh.dto.EmployeeFileResponse;
import com.epmapat.erp_epmapat.rrhh.dto.EmployeeRequest;
import com.epmapat.erp_epmapat.rrhh.dto.EmployeeResponse;
import com.epmapat.erp_epmapat.rrhh.dto.LeaveResponse;
import com.epmapat.erp_epmapat.rrhh.dto.StatusUpdateRequest;
import com.epmapat.erp_epmapat.rrhh.exception.RrhhConflictException;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployee;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployeeAction;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployeeContract;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployeeFileRecord;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployeeLeave;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhEmployeeActionRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhEmployeeContractRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhEmployeeFileRecordRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhEmployeeLeaveRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhEmployeeRepository;
import com.epmapat.erp_epmapat.rrhh.spec.RrhhSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RrhhEmployeeService {

    private final RrhhEmployeeRepository employeeRepository;
    private final RrhhEmployeeContractRepository contractRepository;
    private final RrhhEmployeeFileRecordRepository fileRepository;
    private final RrhhEmployeeLeaveRepository leaveRepository;
    private final RrhhEmployeeActionRepository actionRepository;

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> list(String area, String dependency, String contractType, String status,
            java.time.LocalDate hireDateFrom, Integer page, Integer size) {
        Specification<RrhhEmployee> spec = Specification.where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("dependency", dependency))
                .and(RrhhSpecifications.likeIgnoreCase("contractType", contractType))
                .and(RrhhSpecifications.likeIgnoreCase("employmentStatus", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("hireDate", hireDateFrom));
        return employeeRepository.findAll(spec, pageable(page, size)).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        validateEmployeeUniqueness(request, null);
        validateEmploymentDates(request);

        RrhhEmployee employee = new RrhhEmployee();
        mapEmployee(employee, request);
        applyAudit(employee, request, true);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        validateEmployeeUniqueness(request, id);
        validateEmploymentDates(request);

        RrhhEmployee employee = findEntity(id);
        mapEmployee(employee, request);
        applyAudit(employee, request, false);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse updateStatus(Long id, StatusUpdateRequest request) {
        RrhhEmployee employee = findEntity(id);
        employee.setEmploymentStatus(request.getStatus());
        employee.setActive(!"INACTIVO".equalsIgnoreCase(request.getStatus()));
        employee.setUpdatedBy(request.getUpdatedBy());
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeContractResponse> contracts(Long employeeId) {
        ensureEmployeeExists(employeeId);
        return contractRepository.findByEmployeeIdOrderByStartDateDesc(employeeId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeFileResponse> files(Long employeeId) {
        ensureEmployeeExists(employeeId);
        return fileRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> leaves(Long employeeId) {
        ensureEmployeeExists(employeeId);
        return leaveRepository.findByEmployeeIdOrderByStartDateDesc(employeeId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ActionResponse> actions(Long employeeId) {
        ensureEmployeeExists(employeeId);
        return actionRepository.findByEmployeeIdOrderByEffectiveDateDesc(employeeId).stream().map(this::toResponse).toList();
    }

    public RrhhEmployee findEntity(Long id) {
        return getOrThrow(employeeRepository.findById(id), "Empleado no encontrado: " + id);
    }

    private void ensureEmployeeExists(Long employeeId) {
        findEntity(employeeId);
    }

    private void validateEmployeeUniqueness(EmployeeRequest request, Long id) {
        boolean duplicatedCode = id == null ? employeeRepository.existsByCode(request.getCode())
                : employeeRepository.existsByCodeAndIdNot(request.getCode(), id);
        if (duplicatedCode) {
            throw new RrhhConflictException("Ya existe un empleado con código " + request.getCode());
        }

        boolean duplicatedIdentification = id == null
                ? employeeRepository.existsByIdentification(request.getIdentification())
                : employeeRepository.existsByIdentificationAndIdNot(request.getIdentification(), id);
        if (duplicatedIdentification) {
            throw new RrhhConflictException("Ya existe un empleado con identificación " + request.getIdentification());
        }

        boolean duplicatedEmail = id == null ? employeeRepository.existsByEmail(request.getEmail())
                : employeeRepository.existsByEmailAndIdNot(request.getEmail(), id);
        if (duplicatedEmail) {
            throw new RrhhConflictException("Ya existe un empleado con email " + request.getEmail());
        }
    }

    private void validateEmploymentDates(EmployeeRequest request) {
        if (request.getTerminationDate() != null && request.getTerminationDate().isBefore(request.getHireDate())) {
            throw new IllegalArgumentException("terminationDate no puede ser menor a hireDate");
        }
    }

    private void mapEmployee(RrhhEmployee employee, EmployeeRequest request) {
        employee.setCode(request.getCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setIdentification(request.getIdentification());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setArea(request.getArea());
        employee.setDependency(request.getDependency());
        employee.setJobTitle(request.getJobTitle());
        employee.setContractType(request.getContractType());
        employee.setEmploymentStatus(request.getEmploymentStatus());
        employee.setHireDate(request.getHireDate());
        employee.setTerminationDate(request.getTerminationDate());
        employee.setBirthDate(request.getBirthDate());
        employee.setProfessionalTitle(request.getProfessionalTitle());
        employee.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
    }

    private EmployeeResponse toResponse(RrhhEmployee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .code(employee.getCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .identification(employee.getIdentification())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .area(employee.getArea())
                .dependency(employee.getDependency())
                .jobTitle(employee.getJobTitle())
                .contractType(employee.getContractType())
                .employmentStatus(employee.getEmploymentStatus())
                .hireDate(employee.getHireDate())
                .terminationDate(employee.getTerminationDate())
                .birthDate(employee.getBirthDate())
                .professionalTitle(employee.getProfessionalTitle())
                .active(employee.getActive())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .createdBy(employee.getCreatedBy())
                .updatedBy(employee.getUpdatedBy())
                .build();
    }

    private EmployeeContractResponse toResponse(RrhhEmployeeContract contract) {
        return EmployeeContractResponse.builder()
                .id(contract.getId())
                .employeeId(contract.getEmployee().getId())
                .contractNumber(contract.getContractNumber())
                .contractType(contract.getContractType())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .currentContract(contract.getCurrentContract())
                .salary(contract.getSalary())
                .status(contract.getStatus())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .createdBy(contract.getCreatedBy())
                .updatedBy(contract.getUpdatedBy())
                .build();
    }

    private EmployeeFileResponse toResponse(RrhhEmployeeFileRecord file) {
        return EmployeeFileResponse.builder()
                .id(file.getId())
                .employeeId(file.getEmployee().getId())
                .fileType(file.getFileType())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .issueDate(file.getIssueDate())
                .expiryDate(file.getExpiryDate())
                .status(file.getStatus())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .createdBy(file.getCreatedBy())
                .updatedBy(file.getUpdatedBy())
                .build();
    }

    private LeaveResponse toResponse(RrhhEmployeeLeave leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .leaveType(leave.getLeaveType())
                .absenceType(leave.getAbsenceType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .days(leave.getDays())
                .status(leave.getStatus())
                .reason(leave.getReason())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .createdBy(leave.getCreatedBy())
                .updatedBy(leave.getUpdatedBy())
                .build();
    }

    private ActionResponse toResponse(RrhhEmployeeAction action) {
        return ActionResponse.builder()
                .id(action.getId())
                .employeeId(action.getEmployee().getId())
                .actionType(action.getActionType())
                .effectiveDate(action.getEffectiveDate())
                .status(action.getStatus())
                .description(action.getDescription())
                .createdAt(action.getCreatedAt())
                .updatedAt(action.getUpdatedAt())
                .createdBy(action.getCreatedBy())
                .updatedBy(action.getUpdatedBy())
                .build();
    }
}
