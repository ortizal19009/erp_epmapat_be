package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.ThPerformanceRequest;
import com.epmapat.erp_epmapat.rrhh.modelo.ThPerformanceReview;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThPerformanceReviewR;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class ThPerformanceServicio {
    private final ThPerformanceReviewR repository;
    private final PersonalR personal;
    private final ThAuditServicio audit;

    private void requirePersonal(Long id) {
        if (id == null || id <= 0 || !personal.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado");
    }

    @Transactional(readOnly = true)
    public Page<ThPerformanceReview> listar(Long id, int page, int size) {
        requirePersonal(id);
        if (page < 0 || size < 1 || size > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginación inválida");
        return repository.findByIdpersonalOrderByFechaDescIdreviewDesc(id, PageRequest.of(page, size));
    }

    @Transactional
    public ThPerformanceReview registrar(ThPerformanceRequest request, Long actor) {
        requirePersonal(request.getIdpersonal());
        ThPerformanceReview review = new ThPerformanceReview();
        review.setIdpersonal(request.getIdpersonal());
        review.setPeriodo(request.getPeriodo().trim());
        review.setFecha(request.getFecha());
        review.setPuntaje(request.getPuntaje());
        review.setEvaluador(request.getEvaluador().trim());
        review.setObservaciones(request.getObservaciones().trim());
        review.setPlanMejora(request.getPlanMejora() == null ? null : request.getPlanMejora().trim());
        review.setUsuario(actor);
        review.setCreado(LocalDateTime.now());
        ThPerformanceReview saved = repository.saveAndFlush(review);
        audit.log("PERSONAL", saved.getIdpersonal(), "EVALUACION_REGISTRADA",
                "Evaluación #" + saved.getIdreview() + ", período " + saved.getPeriodo(), actor);
        return saved;
    }
}
