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
    private static final Set<String> TIPOS = Set.of("VACACION", "PERMISO", "LICENCIA");

    @Transactional
    public ThLeaveRequest save(ThLeaveRequest r) {
        validar(r);
        Long idpersonal = r.getIdpersonal_personal().getIdpersonal();
        r.setIdpersonal_personal(personalR.findById(idpersonal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Personal no existe: " + idpersonal)));

        if (dao.existsSolape(idpersonal, r.getFechainicio(), r.getFechafin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una solicitud que se cruza en fechas");
        }

        if (r.getEstado() == null || r.getEstado().isBlank()) r.setEstado("SOLICITADA");
        if (r.getFeccrea() == null) r.setFeccrea(LocalDate.now());
        if (r.getActivo() == null) r.setActivo(true);
        if (r.getDias_solicitados() == null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(r.getFechainicio(), r.getFechafin()) + 1;
            r.setDias_solicitados(BigDecimal.valueOf(days));
        }
        ThLeaveRequest saved = dao.save(r);
        auditServicio.log("TH_LEAVE_REQUEST", saved.getIdrequest(), "CREATE",
                saved.getTipolicencia() + " " + saved.getFechainicio() + "-" + saved.getFechafin(), saved.getUsucrea());
        return saved;
    }

    @Transactional
    public ThLeaveRequest aprobar(Long idrequest, Long aprobadorId, String observacion) {
        ThLeaveRequest r = dao.findById(idrequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada: " + idrequest));
        if (!"SOLICITADA".equalsIgnoreCase(r.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede aprobar una solicitud en estado SOLICITADA");
        }

        if ("VACACION".equalsIgnoreCase(r.getTipolicencia())) {
            int anio = r.getFechainicio().getYear();
            Long idpersonal = r.getIdpersonal_personal().getIdpersonal();
            ThLeaveBalance b = balanceR.findByPersonalAndAnio(idpersonal, anio)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "No existe saldo de vacaciones para el año " + anio));

            BigDecimal solicitado = r.getDias_solicitados() == null ? BigDecimal.ZERO : r.getDias_solicitados();
            BigDecimal disponible = b.getDias_disponibles() == null ? BigDecimal.ZERO : b.getDias_disponibles();
            if (disponible.compareTo(solicitado) < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Saldo insuficiente. Disponible=" + disponible + ", solicitado=" + solicitado);
            }

            b.setDias_usados((b.getDias_usados() == null ? BigDecimal.ZERO : b.getDias_usados()).add(solicitado));
            b.setDias_disponibles(disponible.subtract(solicitado));
            b.setFecmodi(LocalDate.now());
            b.setUsumodi(aprobadorId);
            balanceR.save(b);
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
        ThLeaveRequest r = dao.findById(idrequest)
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

    @Transactional(readOnly = true)
    public List<ThLeaveRequest> byEstado(String estado) {
        return dao.findByEstado(estado.toUpperCase());
    }

    private void validar(ThLeaveRequest r) {
        if (r == null || r.getIdpersonal_personal() == null || r.getIdpersonal_personal().getIdpersonal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idpersonal_personal es obligatorio");
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
    }
}
