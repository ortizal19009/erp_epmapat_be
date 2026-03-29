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

import com.epmapat.erp_epmapat.rrhh.dto.CareerPlanRequest;
import com.epmapat.erp_epmapat.rrhh.dto.CareerPlanResponse;
import com.epmapat.erp_epmapat.rrhh.dto.MentoringRequest;
import com.epmapat.erp_epmapat.rrhh.dto.MentoringResponse;
import com.epmapat.erp_epmapat.rrhh.dto.PerformanceReviewRequest;
import com.epmapat.erp_epmapat.rrhh.dto.PerformanceReviewResponse;
import com.epmapat.erp_epmapat.rrhh.dto.TrainingPlanRequest;
import com.epmapat.erp_epmapat.rrhh.dto.TrainingPlanResponse;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhCareerPlan;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhMentoring;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhPerformanceReview;
import com.epmapat.erp_epmapat.rrhh.modelo.RrhhTrainingPlan;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhCareerPlanRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhMentoringRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhPerformanceReviewRepository;
import com.epmapat.erp_epmapat.rrhh.repositorio.RrhhTrainingPlanRepository;
import com.epmapat.erp_epmapat.rrhh.spec.RrhhSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RrhhDevelopmentService {

    private final RrhhTrainingPlanRepository trainingRepository;
    private final RrhhPerformanceReviewRepository reviewRepository;
    private final RrhhCareerPlanRepository careerRepository;
    private final RrhhMentoringRepository mentoringRepository;
    private final RrhhEmployeeService employeeService;

    @Transactional(readOnly = true)
    public Page<TrainingPlanResponse> listTrainings(String area, String status, java.time.LocalDate fromDate,
            Integer page, Integer size) {
        Specification<RrhhTrainingPlan> spec = Specification.<RrhhTrainingPlan>where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("startDate", fromDate));
        return trainingRepository.findAll(spec, pageable(page, size)).map(this::toTrainingResponse);
    }

    @Transactional
    public TrainingPlanResponse createTraining(TrainingPlanRequest request) {
        RrhhTrainingPlan entity = new RrhhTrainingPlan();
        mapTraining(entity, request);
        applyAudit(entity, request, true);
        return toTrainingResponse(trainingRepository.save(entity));
    }

    @Transactional
    public TrainingPlanResponse updateTraining(UUID id, TrainingPlanRequest request) {
        RrhhTrainingPlan entity = getOrThrow(trainingRepository.findById(id), "Capacitación no encontrada: " + id);
        mapTraining(entity, request);
        applyAudit(entity, request, false);
        return toTrainingResponse(trainingRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<PerformanceReviewResponse> listReviews(String period, String status, java.time.LocalDate fromDate,
            Integer page, Integer size) {
        Specification<RrhhPerformanceReview> spec = Specification.<RrhhPerformanceReview>where(RrhhSpecifications.likeIgnoreCase("period", period))
                .and(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("reviewDate", fromDate));
        return reviewRepository.findAll(spec, pageable(page, size)).map(this::toReviewResponse);
    }

    @Transactional
    public PerformanceReviewResponse createReview(PerformanceReviewRequest request) {
        RrhhPerformanceReview entity = new RrhhPerformanceReview();
        mapReview(entity, request);
        applyAudit(entity, request, true);
        return toReviewResponse(reviewRepository.save(entity));
    }

    @Transactional
    public PerformanceReviewResponse updateReview(UUID id, PerformanceReviewRequest request) {
        RrhhPerformanceReview entity = getOrThrow(reviewRepository.findById(id), "Evaluación no encontrada: " + id);
        mapReview(entity, request);
        applyAudit(entity, request, false);
        return toReviewResponse(reviewRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<CareerPlanResponse> listCareerPlans(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhCareerPlan> spec = Specification.<RrhhCareerPlan>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("startDate", fromDate));
        return careerRepository.findAll(spec, pageable(page, size)).map(this::toCareerResponse);
    }

    @Transactional
    public CareerPlanResponse createCareerPlan(CareerPlanRequest request) {
        RrhhCareerPlan entity = new RrhhCareerPlan();
        mapCareer(entity, request);
        applyAudit(entity, request, true);
        return toCareerResponse(careerRepository.save(entity));
    }

    @Transactional
    public CareerPlanResponse updateCareerPlan(UUID id, CareerPlanRequest request) {
        RrhhCareerPlan entity = getOrThrow(careerRepository.findById(id), "Plan de carrera no encontrado: " + id);
        mapCareer(entity, request);
        applyAudit(entity, request, false);
        return toCareerResponse(careerRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<MentoringResponse> listMentoring(String status, java.time.LocalDate fromDate, Integer page, Integer size) {
        Specification<RrhhMentoring> spec = Specification.<RrhhMentoring>where(RrhhSpecifications.likeIgnoreCase("status", status))
                .and(RrhhSpecifications.dateGreaterOrEqual("startDate", fromDate));
        return mentoringRepository.findAll(spec, pageable(page, size)).map(this::toMentoringResponse);
    }

    @Transactional
    public MentoringResponse createMentoring(MentoringRequest request) {
        RrhhMentoring entity = new RrhhMentoring();
        mapMentoring(entity, request);
        applyAudit(entity, request, true);
        return toMentoringResponse(mentoringRepository.save(entity));
    }

    @Transactional
    public MentoringResponse updateMentoring(UUID id, MentoringRequest request) {
        RrhhMentoring entity = getOrThrow(mentoringRepository.findById(id), "Mentoría no encontrada: " + id);
        mapMentoring(entity, request);
        applyAudit(entity, request, false);
        return toMentoringResponse(mentoringRepository.save(entity));
    }

    private void mapTraining(RrhhTrainingPlan entity, TrainingPlanRequest request) {
        entity.setEmployee(request.getEmployeeId() == null ? null : employeeService.findEntity(request.getEmployeeId()));
        entity.setArea(request.getArea());
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setHours(request.getHours());
        entity.setCost(request.getCost());
        entity.setStatus(request.getStatus());
    }

    private void mapReview(RrhhPerformanceReview entity, PerformanceReviewRequest request) {
        entity.setEmployee(employeeService.findEntity(request.getEmployeeId()));
        entity.setPeriod(request.getPeriod());
        entity.setScore(request.getScore());
        entity.setReviewer(request.getReviewer());
        entity.setReviewDate(request.getReviewDate());
        entity.setStatus(request.getStatus());
        entity.setComments(request.getComments());
    }

    private void mapCareer(RrhhCareerPlan entity, CareerPlanRequest request) {
        entity.setEmployee(employeeService.findEntity(request.getEmployeeId()));
        entity.setGoal(request.getGoal());
        entity.setStatus(request.getStatus());
        entity.setStartDate(request.getStartDate());
        entity.setTargetDate(request.getTargetDate());
        entity.setMilestones(request.getMilestones());
    }

    private void mapMentoring(RrhhMentoring entity, MentoringRequest request) {
        entity.setEmployee(employeeService.findEntity(request.getEmployeeId()));
        entity.setMentorName(request.getMentorName());
        entity.setCoachType(request.getCoachType());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());
        entity.setNotes(request.getNotes());
    }

    private TrainingPlanResponse toTrainingResponse(RrhhTrainingPlan entity) {
        return TrainingPlanResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee() == null ? null : entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .area(entity.getArea())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .hours(entity.getHours())
                .cost(entity.getCost())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private PerformanceReviewResponse toReviewResponse(RrhhPerformanceReview entity) {
        return PerformanceReviewResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .period(entity.getPeriod())
                .score(entity.getScore())
                .reviewer(entity.getReviewer())
                .reviewDate(entity.getReviewDate())
                .status(entity.getStatus())
                .comments(entity.getComments())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private CareerPlanResponse toCareerResponse(RrhhCareerPlan entity) {
        return CareerPlanResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .goal(entity.getGoal())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .targetDate(entity.getTargetDate())
                .milestones(entity.getMilestones())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private MentoringResponse toMentoringResponse(RrhhMentoring entity) {
        return MentoringResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee().getId())
                .employeeName(employeeFullName(entity.getEmployee()))
                .mentorName(entity.getMentorName())
                .coachType(entity.getCoachType())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
