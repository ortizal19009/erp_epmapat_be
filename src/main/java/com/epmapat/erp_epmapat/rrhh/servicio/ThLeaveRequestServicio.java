package com.epmapat.erp_epmapat.rrhh.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveBalance;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveRequest;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThLeaveBalanceR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThLeaveRequestR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveRequestServicio {

    private final ThLeaveRequestR dao;
    private final PersonalR personalR;
    private final ThLeaveBalanceR balanceR;
    private final ThAuditServicio auditServicio;
    private final ThLeaveMovementServicio movements;
    private static final Set<String> TIPOS = Set.of("VACACION", "PERMISO", "LICENCIA");

    @Transactional
    public ThLeaveRequest save(ThLeaveRequest r) {
        validar(r);
        Long idpersonal = r.getIdpersonal_personal().getIdpersonal();
        r.setIdpersonal_personal(personalR.findByIdForUpdate(idpersonal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Personal no existe: " + idpersonal)));

        if (dao.existsSolape(idpersonal, r.getFechainicio(), r.getFechafin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una solicitud que se cruza en fechas");
        }

        r.setEstado("SOLICITADA");
        r.setFeccrea(LocalDate.now());
        r.setActivo(true);
        r.setDias_solicitados(calcularDias(r));
        ThLeaveRequest saved = dao.save(r);
        auditServicio.log("TH_LEAVE_REQUEST", saved.getIdrequest(), "CREATE",
                saved.getTipolicencia() + " " + saved.getFechainicio() + "-" + saved.getFechafin(), saved.getUsucrea());
        return saved;
    }

    @Transactional
    public ThLeaveRequest aprobar(Long idrequest, Long aprobadorId, String observacion) {
        validarActor(aprobadorId);
        ThLeaveRequest r = dao.findByIdForUpdate(idrequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada: " + idrequest));
        if (!"SOLICITADA".equalsIgnoreCase(r.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede aprobar una solicitud en estado SOLICITADA");
        }

        if (r.getFechainicio() == null || r.getFechafin() == null || r.getFechainicio().isAfter(r.getFechafin()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitud con fechas inválidas; requiere revisión");
        String tipo = r.getTipolicencia() == null ? "" : r.getTipolicencia().trim().toUpperCase(java.util.Locale.ROOT);
        if (!TIPOS.contains(tipo))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tipo de solicitud desconocido; requiere revisión");
        r.setTipolicencia(tipo);
        if ("VACACION".equals(tipo)) {
            if (r.getFechainicio().getYear() != r.getFechafin().getYear())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitud de vacaciones con cruce de año; requiere revisión");
            int anio = r.getFechainicio().getYear();
            Long idpersonal = r.getIdpersonal_personal().getIdpersonal();
            ThLeaveBalance b = balanceR.findByPersonalAndAnioForUpdate(idpersonal, anio)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "No existe saldo de vacaciones para el año " + anio));

            BigDecimal solicitado = calcularDias(r);
            if (r.getDias_solicitados() == null || r.getDias_solicitados().compareTo(solicitado) != 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Los días de la solicitud no coinciden con sus fechas; requiere revisión");
            }
            if (!Boolean.TRUE.equals(b.getEstado())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo de vacaciones inactivo");
            }
            BigDecimal disponible = b.getDias_disponibles() == null ? BigDecimal.ZERO : b.getDias_disponibles();
            if (disponible.compareTo(solicitado) < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Saldo insuficiente. Disponible=" + disponible + ", solicitado=" + solicitado);
            }

            movements.consumir(b, r, aprobadorId, observacion);
        }

        r.setEstado("APROBADA");
        r.setAprobador_id(aprobadorId);
        r.setFecha_aprobacion(LocalDate.now());
        r.setObservacion_aprobacion(observacion);
        r.setFecmodi(LocalDate.now());
        r.setUsumodi(aprobadorId);
        ThLeaveRequest savedA = dao.save(r);
        auditServicio.log("TH_LEAVE_REQUEST", savedA.getIdrequest(), "APPROVE", savedA.getObservacion_aprobacion(), aprobadorId);
        return savedA;
    }

    @Transactional
    public ThLeaveRequest rechazar(Long idrequest, Long aprobadorId, String observacion) {
        validarActor(aprobadorId);
        if (observacion == null || observacion.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indique el motivo del rechazo");
        ThLeaveRequest r = dao.findByIdForUpdate(idrequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada: " + idrequest));
        if (!"SOLICITADA".equalsIgnoreCase(r.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede rechazar una solicitud en estado SOLICITADA");
        }
        r.setEstado("RECHAZADA");
        r.setAprobador_id(aprobadorId);
        r.setFecha_aprobacion(LocalDate.now());
        r.setObservacion_aprobacion(observacion);
        r.setFecmodi(LocalDate.now());
        r.setUsumodi(aprobadorId);
        ThLeaveRequest savedR = dao.save(r);
        auditServicio.log("TH_LEAVE_REQUEST", savedR.getIdrequest(), "REJECT", savedR.getObservacion_aprobacion(), aprobadorId);
        return savedR;
    }

    @Transactional(readOnly = true)
    public List<ThLeaveRequest> byPersonal(Long idpersonal) {
        return dao.findByPersonal(idpersonal);
    }

    @Transactional
    public ThLeaveRequest cancelar(Long idrequest, Long actor, String motivo) {
        return resolver(idrequest, actor, motivo, false);
    }

    @Transactional
    public ThLeaveRequest revertir(Long idrequest, Long actor, String motivo) {
        return resolver(idrequest, actor, motivo, true);
    }

    private ThLeaveRequest resolver(Long idrequest, Long actor, String motivo, boolean reverse) {
        validarActor(actor);
        if (motivo == null || motivo.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indique el motivo de " + (reverse ? "reversión" : "cancelación"));
        ThLeaveRequest request = dao.findByIdForUpdate(idrequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada: " + idrequest));
        String expected = reverse ? "APROBADA" : "SOLICITADA";
        if (!expected.equalsIgnoreCase(request.getEstado()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede " + (reverse ? "revertir" : "cancelar")
                    + " una solicitud en estado " + expected);
        if (reverse) {
            if ("VACACION".equalsIgnoreCase(request.getTipolicencia())) movements.reintegrar(request, actor, motivo.trim());
            else if (!"PERMISO".equalsIgnoreCase(request.getTipolicencia()) && !"LICENCIA".equalsIgnoreCase(request.getTipolicencia()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Tipo de solicitud desconocido; requiere revisión");
        }
        request.setEstado(reverse ? "REVERTIDA" : "CANCELADA");
        request.setResuelto_por(actor);
        request.setFecha_resolucion(java.time.LocalDateTime.now());
        request.setMotivo_resolucion(motivo.trim());
        request.setUsumodi(actor); request.setFecmodi(LocalDate.now());
        ThLeaveRequest saved = dao.save(request);
        auditServicio.log("TH_LEAVE_REQUEST", saved.getIdrequest(), reverse ? "REVERSE" : "CANCEL", motivo.trim(), actor);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ThLeaveRequest> byEstado(String estado) {
        return dao.findByEstado(estado.toUpperCase());
    }

    private void validarActor(Long actor) {
        if (actor == null || actor <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario responsable obligatorio");
    }

    private BigDecimal calcularDias(ThLeaveRequest r) {
        return BigDecimal.valueOf(java.time.temporal.ChronoUnit.DAYS.between(r.getFechainicio(), r.getFechafin()) + 1);
    }

    private void validar(ThLeaveRequest r) {
        if (r == null || r.getIdpersonal_personal() == null || r.getIdpersonal_personal().getIdpersonal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idpersonal_personal es obligatorio");
        validarActor(r.getUsucrea());
        if (r.getIdrequest() != null || r.getAprobador_id() != null || r.getFecha_aprobacion() != null
                || r.getObservacion_aprobacion() != null || r.getUsumodi() != null || r.getFecmodi() != null
                || Boolean.FALSE.equals(r.getActivo()) || r.getResuelto_por() != null
                || r.getFecha_resolucion() != null || r.getMotivo_resolucion() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La creación no admite ID ni campos de aprobación/modificación");
        if (r.getEstado() != null && !r.getEstado().isBlank() && !"SOLICITADA".equalsIgnoreCase(r.getEstado().trim()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado inicial debe ser SOLICITADA");
        if (r.getTipolicencia() == null || r.getTipolicencia().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipolicencia es obligatorio");
        String tipo = r.getTipolicencia().trim().toUpperCase();
        if (!TIPOS.contains(tipo))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipolicencia inválido: " + TIPOS);
        r.setTipolicencia(tipo);
        if (r.getFechainicio() == null || r.getFechafin() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechainicio y fechafin son obligatorias");
        if (r.getFechainicio().isAfter(r.getFechafin()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechainicio no puede ser mayor a fechafin");
        if (r.getDias_solicitados() != null && r.getDias_solicitados().compareTo(calcularDias(r)) != 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los días solicitados deben coincidir con los días calendario de las fechas");
        if ("VACACION".equals(r.getTipolicencia()) && r.getFechainicio().getYear() != r.getFechafin().getYear())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registre las vacaciones en solicitudes separadas por año");
    }
}
