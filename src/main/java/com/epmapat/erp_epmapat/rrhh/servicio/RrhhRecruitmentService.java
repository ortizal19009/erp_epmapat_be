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

import com.epmapat.erp_epmapat.rrhh.dto.CandidateRequest;
import com.epmapat.erp_epmapat.rrhh.dto.CandidateResponse;
import com.epmapat.erp_epmapat.rrhh.dto.InterviewRequest;
import com.epmapat.erp_epmapat.rrhh.dto.InterviewResponse;
import com.epmapat.erp_epmapat.rrhh.dto.OnboardingRequest;
import com.epmapat.erp_epmapat.rrhh.dto.OnboardingResponse;
import com.epmapat.erp_epmapat.rrhh.dto.VacancyRequest;
import com.epmapat.erp_epmapat.rrhh.dto.VacancyResponse;
import com.epmapat.erp_epmapat.rrhh.exception.RrhhConflictException;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhCandidate;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhInterview;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhOnboarding;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhVacancy;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhCandidateRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhInterviewRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhOnboardingRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhVacancyRepository;
import com.epmapat.erp_epmapat.rrhh.spec.RrhhSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RrhhRecruitmentService {

    private final RrhhVacancyRepository vacancyRepository;
    private final RrhhCandidateRepository candidateRepository;
    private final RrhhInterviewRepository interviewRepository;
    private final RrhhOnboardingRepository onboardingRepository;
    private final RrhhEmployeeService employeeService;

    @Transactional(readOnly = true)
    public Page<VacancyResponse> listVacancies(String area, String stage, String status, java.time.LocalDate fromDate,
            Integer page, Integer size) {
        Specification<RrhhVacancy> spec = Specification.<RrhhVacancy>where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("stage", stage))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("openDate", fromDate));
        return vacancyRepository.findAll(spec, pageable(page, size)).map(this::toVacancyResponse);
    }

    @Transactional
    public VacancyResponse createVacancy(VacancyRequest request) {
        validateVacancyCode(request, null);
        RrhhVacancy vacancy = new RrhhVacancy();
        mapVacancy(vacancy, request);
        applyAudit(vacancy, request, true);
        return toVacancyResponse(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyResponse updateVacancy(UUID id, VacancyRequest request) {
        validateVacancyCode(request, id);
        RrhhVacancy vacancy = findVacancy(id);
        mapVacancy(vacancy, request);
        applyAudit(vacancy, request, false);
        return toVacancyResponse(vacancyRepository.save(vacancy));
    }

    @Transactional(readOnly = true)
    public Page<CandidateResponse> listCandidates(UUID vacancyId, String area, String stage, String status,
            java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhCandidate> spec = Specification.<RrhhCandidate>where(RrhhSpecifications.equalsNested("vacancy", "id", vacancyId))
                .and(RrhhSpecifications.likeNestedIgnoreCase("vacancy", "area", area))
                .and(RrhhSpecifications.likeIgnoreCase("stage", stage))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("appliedAt", fromDate));
        return candidateRepository.findAll(spec, pageable(page, size)).map(this::toCandidateResponse);
    }

    @Transactional
    public CandidateResponse createCandidate(CandidateRequest request) {
        validateCandidateUniqueness(request, null);
        RrhhCandidate candidate = new RrhhCandidate();
        mapCandidate(candidate, request);
        applyAudit(candidate, request, true);
        return toCandidateResponse(candidateRepository.save(candidate));
    }

    @Transactional
    public CandidateResponse updateCandidate(UUID id, CandidateRequest request) {
        validateCandidateUniqueness(request, id);
        RrhhCandidate candidate = findCandidate(id);
        mapCandidate(candidate, request);
        applyAudit(candidate, request, false);
        return toCandidateResponse(candidateRepository.save(candidate));
    }

    @Transactional(readOnly = true)
    public Page<InterviewResponse> listInterviews(UUID vacancyId, String stage, String status,
            java.time.LocalDate date, Integer page, Integer size) {
        Specification<RrhhInterview> spec = Specification.<RrhhInterview>where(RrhhSpecifications.equalsNested("vacancy", "id", vacancyId))
                .and(RrhhSpecifications.likeIgnoreCase("stage", stage))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and((root, query, cb) -> date == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("scheduledAt"), date.atStartOfDay()));
        return interviewRepository.findAll(spec, pageable(page, size)).map(this::toInterviewResponse);
    }

    @Transactional
    public InterviewResponse createInterview(InterviewRequest request) {
        RrhhInterview interview = new RrhhInterview();
        mapInterview(interview, request);
        applyAudit(interview, request, true);
        return toInterviewResponse(interviewRepository.save(interview));
    }

    @Transactional
    public InterviewResponse updateInterview(UUID id, InterviewRequest request) {
        RrhhInterview interview = findInterview(id);
        mapInterview(interview, request);
        applyAudit(interview, request, false);
        return toInterviewResponse(interviewRepository.save(interview));
    }

    @Transactional(readOnly = true)
    public Page<OnboardingResponse> listOnboarding(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhOnboarding> spec = Specification.<RrhhOnboarding>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("startDate", fromDate));
        return onboardingRepository.findAll(spec, pageable(page, size)).map(this::toOnboardingResponse);
    }

    @Transactional
    public OnboardingResponse createOnboarding(OnboardingRequest request) {
        validateOnboarding(request);
        RrhhOnboarding onboarding = new RrhhOnboarding();
        mapOnboarding(onboarding, request);
        applyAudit(onboarding, request, true);
        return toOnboardingResponse(onboardingRepository.save(onboarding));
    }

    @Transactional
    public OnboardingResponse updateOnboarding(UUID id, OnboardingRequest request) {
        validateOnboarding(request);
        RrhhOnboarding onboarding = findOnboarding(id);
        mapOnboarding(onboarding, request);
        applyAudit(onboarding, request, false);
        return toOnboardingResponse(onboardingRepository.save(onboarding));
    }

    public RrhhVacancy findVacancy(UUID id) {
        return getOrThrow(vacancyRepository.findById(id), "Vacante no encontrada: " + id);
    }

    public RrhhCandidate findCandidate(UUID id) {
        return getOrThrow(candidateRepository.findById(id), "Candidato no encontrado: " + id);
    }

    private RrhhInterview findInterview(UUID id) {
        return getOrThrow(interviewRepository.findById(id), "Entrevista no encontrada: " + id);
    }

    private RrhhOnboarding findOnboarding(UUID id) {
        return getOrThrow(onboardingRepository.findById(id), "Onboarding no encontrado: " + id);
    }

    private void validateVacancyCode(VacancyRequest request, UUID id) {
        boolean duplicated = id == null ? vacancyRepository.existsByCode(request.getCode())
                : vacancyRepository.existsByCodeAndIdNot(request.getCode(), id);
        if (duplicated) {
            throw new RrhhConflictException("Ya existe una vacante con código " + request.getCode());
        }
    }

    private void validateCandidateUniqueness(CandidateRequest request, UUID id) {
        boolean duplicated = id == null ? candidateRepository.existsByIdentification(request.getIdentification())
                : candidateRepository.existsByIdentificationAndIdNot(request.getIdentification(), id);
        if (duplicated) {
            throw new RrhhConflictException("Ya existe un candidato con identificación " + request.getIdentification());
        }
    }

    private void validateOnboarding(OnboardingRequest request) {
        if (request.getEmployeeId() == null && request.getCandidateId() == null) {
            throw new IllegalArgumentException("Debe informar employeeId o candidateId");
        }
    }

    private void mapVacancy(RrhhVacancy vacancy, VacancyRequest request) {
        vacancy.setCode(request.getCode());
        vacancy.setTitle(request.getTitle());
        vacancy.setArea(request.getArea());
        vacancy.setDependency(request.getDependency());
        vacancy.setStage(request.getStage());
        vacancy.setStatus(request.getStatus());
        vacancy.setOpenDate(request.getOpenDate());
        vacancy.setCloseDate(request.getCloseDate());
        vacancy.setBudgetedSalary(request.getBudgetedSalary());
    }

    private void mapCandidate(RrhhCandidate candidate, CandidateRequest request) {
        candidate.setVacancy(findVacancy(request.getVacancyId()));
        candidate.setFirstName(request.getFirstName());
        candidate.setLastName(request.getLastName());
        candidate.setIdentification(request.getIdentification());
        candidate.setEmail(request.getEmail());
        candidate.setPhone(request.getPhone());
        candidate.setStage(request.getStage());
        candidate.setStatus(request.getStatus());
        candidate.setAppliedAt(request.getAppliedAt());
        candidate.setScore(request.getScore());
    }

    private void mapInterview(RrhhInterview interview, InterviewRequest request) {
        interview.setVacancy(findVacancy(request.getVacancyId()));
        interview.setCandidate(findCandidate(request.getCandidateId()));
        interview.setStage(request.getStage());
        interview.setStatus(request.getStatus());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setInterviewer(request.getInterviewer());
        interview.setNotes(request.getNotes());
        interview.setScore(request.getScore());
    }

    private void mapOnboarding(RrhhOnboarding onboarding, OnboardingRequest request) {
        onboarding.setEmployee(request.getEmployeeId() == null ? null : employeeService.findEntity(request.getEmployeeId()));
        onboarding.setCandidate(request.getCandidateId() == null ? null : findCandidate(request.getCandidateId()));
        onboarding.setStartDate(request.getStartDate());
        onboarding.setEndDate(request.getEndDate());
        onboarding.setStatus(request.getStatus());
        onboarding.setOwner(request.getOwner());
        onboarding.setNotes(request.getNotes());
    }

    private VacancyResponse toVacancyResponse(RrhhVacancy vacancy) {
        return VacancyResponse.builder()
                .id(vacancy.getId())
                .code(vacancy.getCode())
                .title(vacancy.getTitle())
                .area(vacancy.getArea())
                .dependency(vacancy.getDependency())
                .stage(vacancy.getStage())
                .status(vacancy.getStatus())
                .openDate(vacancy.getOpenDate())
                .closeDate(vacancy.getCloseDate())
                .budgetedSalary(vacancy.getBudgetedSalary())
                .createdAt(vacancy.getCreatedAt())
                .updatedAt(vacancy.getUpdatedAt())
                .createdBy(vacancy.getCreatedBy())
                .updatedBy(vacancy.getUpdatedBy())
                .build();
    }

    private CandidateResponse toCandidateResponse(RrhhCandidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .vacancyId(candidate.getVacancy().getId())
                .vacancyCode(candidate.getVacancy().getCode())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .identification(candidate.getIdentification())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .stage(candidate.getStage())
                .status(candidate.getStatus())
                .appliedAt(candidate.getAppliedAt())
                .score(candidate.getScore())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .createdBy(candidate.getCreatedBy())
                .updatedBy(candidate.getUpdatedBy())
                .build();
    }

    private InterviewResponse toInterviewResponse(RrhhInterview interview) {
        return InterviewResponse.builder()
                .id(interview.getId())
                .vacancyId(interview.getVacancy().getId())
                .vacancyCode(interview.getVacancy().getCode())
                .candidateId(interview.getCandidate().getId())
                .candidateName(interview.getCandidate().getFirstName() + " " + interview.getCandidate().getLastName())
                .stage(interview.getStage())
                .status(interview.getStatus())
                .scheduledAt(interview.getScheduledAt())
                .interviewer(interview.getInterviewer())
                .notes(interview.getNotes())
                .score(interview.getScore())
                .createdAt(interview.getCreatedAt())
                .updatedAt(interview.getUpdatedAt())
                .createdBy(interview.getCreatedBy())
                .updatedBy(interview.getUpdatedBy())
                .build();
    }

    private OnboardingResponse toOnboardingResponse(RrhhOnboarding onboarding) {
        return OnboardingResponse.builder()
                .id(onboarding.getId())
                .employeeId(onboarding.getEmployee() == null ? null : onboarding.getEmployee().getId())
                .candidateId(onboarding.getCandidate() == null ? null : onboarding.getCandidate().getId())
                .employeeName(employeeFullName(onboarding.getEmployee()))
                .candidateName(onboarding.getCandidate() == null ? null
                        : onboarding.getCandidate().getFirstName() + " " + onboarding.getCandidate().getLastName())
                .startDate(onboarding.getStartDate())
                .endDate(onboarding.getEndDate())
                .status(onboarding.getStatus())
                .owner(onboarding.getOwner())
                .notes(onboarding.getNotes())
                .createdAt(onboarding.getCreatedAt())
                .updatedAt(onboarding.getUpdatedAt())
                .createdBy(onboarding.getCreatedBy())
                .updatedBy(onboarding.getUpdatedBy())
                .build();
    }
}
