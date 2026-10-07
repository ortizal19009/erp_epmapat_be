package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDate;
import java.util.*;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveRequest;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThLeaveRequestR;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveInboxServicio {
    private final ThLeaveRequestR requests;
    private static final Set<String> ESTADOS = Set.of("SOLICITADA", "APROBADA", "RECHAZADA", "CANCELADA", "REVERTIDA");
    private static final Set<String> TIPOS = Set.of("VACACION", "PERMISO", "LICENCIA");

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ThLeaveInboxResponse calendario(String tipo, Long idpersonal, LocalDate desde, LocalDate hasta, int pagina) {
        if (desde == null || hasta == null || desde.isAfter(hasta)
                || java.time.temporal.ChronoUnit.DAYS.between(desde, hasta) > 41
                || desde.getYear() < 1900 || hasta.getYear() > 9999)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El calendario requiere un rango válido de hasta 42 días entre 1900 y 9999");
        return consultar("APROBADA", tipo, idpersonal, desde, hasta, pagina, 100);
    }

    /** Content and count use one snapshot; only a bounded page is materialized. */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ThLeaveInboxResponse consultar(String estado, String tipo, Long idpersonal, LocalDate desde, LocalDate hasta,
            int pagina, int tamano) {
        String state = filtro(estado, ESTADOS, "estado");
        String kind = filtro(tipo, TIPOS, "tipo");
        if (pagina < 0 || pagina > 100000 || tamano < 1 || tamano > 100
                || (idpersonal != null && idpersonal <= 0) || (desde != null && hasta != null && desde.isAfter(hasta)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginación, personal o rango de fechas inválido");
        Specification<ThLeaveRequest> filter = (root, query, cb) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (state != null) conditions.add(cb.equal(cb.upper(cb.trim(root.get("estado"))), state));
            if (kind != null) conditions.add(cb.equal(cb.upper(cb.trim(root.get("tipolicencia"))), kind));
            if (idpersonal != null) conditions.add(cb.equal(root.get("idpersonal_personal").get("idpersonal"), idpersonal));
            // Inclusive intersection: a request starting before the filter can still overlap it.
            if (desde != null) conditions.add(cb.greaterThanOrEqualTo(root.get("fechafin"), desde));
            if (hasta != null) conditions.add(cb.lessThanOrEqualTo(root.get("fechainicio"), hasta));
            return cb.and(conditions.toArray(new Predicate[0]));
        };
        Page<ThLeaveRequest> page = requests.findAll(filter, PageRequest.of(pagina, tamano, Sort.by("idrequest").ascending()));
        return new ThLeaveInboxResponse(page.getContent().stream().map(ThLeaveInboxResponse.Solicitud::from).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private String filtro(String value, Set<String> allowed, String field) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("TODAS") || normalized.equals("TODOS")) return null;
        if (!allowed.contains(normalized)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filtro de " + field + " inválido");
        return normalized;
    }
}
