package com.epmapat.erp_epmapat.rrhh.servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveReconciliationResponse;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveReconciliationResponse.Ejercicio;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveMovement.Tipo;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveReconciliationServicio {
    private final PersonalR personal;
    private final ThLeaveBalanceR balances;
    private final ThLeaveMovementR movements;
    private final ThLeaveRequestR requests;

    /** All queries share one PostgreSQL snapshot, including while approvals are committing. */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ThLeaveReconciliationResponse verificar(Long idpersonal, Integer anio) {
        if (idpersonal == null || idpersonal <= 0 || (anio != null && (anio < 1900 || anio > 9999)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Personal o año inválido");
        if (!personal.existsById(idpersonal))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal inexistente");
        List<ThLeaveBalance> allBalances = balances.findByPersonal(idpersonal);
        List<ThLeaveMovement> allMovements = movements.findByPersonal(idpersonal, null);
        List<ThLeaveRequest> allRequests = requests.findByPersonal(idpersonal);
        Set<Integer> years = new HashSet<>();
        allBalances.forEach(b -> years.add(b.getAnio()));
        allMovements.forEach(m -> years.add(m.getBalance().getAnio()));
        allRequests.stream().filter(this::resueltaVacacion).forEach(r -> years.add(year(r)));
        Set<Long> consumed = allMovements.stream().filter(m -> m.getTipo() == Tipo.CONSUMO && m.getRequest() != null)
                .map(m -> m.getRequest().getIdrequest()).collect(Collectors.toSet());
        List<Ejercicio> rows = years.stream().filter(y -> anio == null || Objects.equals(anio, y))
                .sorted(Comparator.nullsLast(Comparator.reverseOrder()))
                .map(y -> ejercicio(y,
                        allBalances.stream().filter(b -> Objects.equals(y, b.getAnio())).toList(),
                        allMovements.stream().filter(m -> Objects.equals(y, m.getBalance().getAnio()))
                                .sorted(Comparator.comparing(ThLeaveMovement::getIdmovement)).toList(),
                        allRequests.stream().filter(this::resueltaVacacion).filter(r -> Objects.equals(y, year(r)))
                                .filter(r -> !consumed.contains(r.getIdrequest())).map(ThLeaveRequest::getIdrequest)
                                .sorted().toList()))
                .toList();
        return new ThLeaveReconciliationResponse(idpersonal, LocalDateTime.now(), rows);
    }

    private Ejercicio ejercicio(Integer anio, List<ThLeaveBalance> saldo, List<ThLeaveMovement> libro, List<Long> historicas) {
        List<String> alertas = new ArrayList<>();
        ThLeaveBalance b = saldo.size() == 1 ? saldo.get(0) : null;
        if (saldo.isEmpty()) alertas.add("No existe saldo para este ejercicio");
        if (saldo.size() > 1) alertas.add("Existen saldos duplicados: " + saldo.stream().map(ThLeaveBalance::getIdbalance).toList());
        if (anio == null || anio < 1900 || anio > 9999) alertas.add("Ejercicio ausente o inválido");
        boolean balanceValid = b != null && nonnegative(b.getDias_asignados(), b.getDias_usados(), b.getDias_disponibles())
                && eq(b.getDias_asignados(), b.getDias_usados().add(b.getDias_disponibles()));
        if (b != null && !balanceValid) alertas.add("Asignados debe ser usados más disponibles, sin valores nulos o negativos");
        BigDecimal opening = total(libro, Tipo.APERTURA);
        BigDecimal consumption = total(libro, Tipo.CONSUMO);
        if (consumption != null) consumption = consumption.negate();
        BigDecimal refunds = total(libro, Tipo.REINTEGRO);
        BigDecimal adjustments = total(libro, Tipo.AJUSTE);
        BigDecimal available = libro.isEmpty() || libro.stream().anyMatch(m -> m.getDias() == null) ? null
                : libro.stream().map(ThLeaveMovement::getDias).reduce(BigDecimal.ZERO, BigDecimal::add);
        ThLeaveMovement last = libro.isEmpty() ? null : libro.get(libro.size() - 1);
        BigDecimal used = last == null ? null : last.getUsados_despues();
        BigDecimal assigned = last == null ? null : last.getAsignados();
        boolean chainValid = validarLibro(libro, alertas);
        BigDecimal diffAvailable = difference(b == null ? null : b.getDias_disponibles(), available);
        BigDecimal diffUsed = difference(b == null ? null : b.getDias_usados(), used);
        BigDecimal diffAssigned = difference(b == null ? null : b.getDias_asignados(), assigned);
        boolean agrees = chainValid && zero(diffAvailable) && zero(diffUsed) && zero(diffAssigned);
        if (!libro.isEmpty() && !agrees) alertas.add("El saldo y el libro requieren revisión; no se aplican correcciones automáticas");
        if (!historicas.isEmpty()) alertas.add("Solicitudes resueltas sin consumo registrado; revisar evidencia histórica, sin inferir descuentos");
        String status = saldo.isEmpty() ? "SIN_SALDO" : !balanceValid || saldo.size() > 1
                || anio == null || anio < 1900 || anio > 9999 ? "SALDO_INVALIDO"
                : libro.isEmpty() ? "SIN_LIBRO" : agrees ? "COINCIDE" : "DIFERENCIA";
        return new Ejercicio(anio, b == null ? null : b.getIdbalance(), b == null ? null : b.getEstado(), status,
                b == null ? null : b.getDias_asignados(), b == null ? null : b.getDias_usados(),
                b == null ? null : b.getDias_disponibles(), opening, consumption, refunds, adjustments,
                available, used, assigned, diffAvailable, diffUsed, diffAssigned, libro.size(), historicas, List.copyOf(alertas));
    }

    private boolean validarLibro(List<ThLeaveMovement> libro, List<String> alertas) {
        if (libro.isEmpty()) return true;
        boolean valid = libro.get(0).getTipo() == Tipo.APERTURA
                && libro.stream().filter(m -> m.getTipo() == Tipo.APERTURA).count() == 1;
        if (!valid) alertas.add("El libro debe comenzar con una única apertura");
        Map<Long, ThLeaveMovement> debits = new HashMap<>();
        Set<Long> consumedRequests = new HashSet<>(), refundedOrigins = new HashSet<>();
        ThLeaveMovement previous = null;
        for (ThLeaveMovement m : libro) {
            boolean ok = m.getDias() != null && m.getTipo() != null
                    && nonnegative(m.getDisponibles_antes(), m.getDisponibles_despues(), m.getUsados_antes(),
                            m.getUsados_despues(), m.getAsignados());
            if (ok) {
                ok = eq(m.getDisponibles_despues(), m.getDisponibles_antes().add(m.getDias()))
                        && eq(m.getAsignados(), m.getDisponibles_despues().add(m.getUsados_despues()));
                if (previous != null) {
                    ok &= eq(m.getDisponibles_antes(), previous.getDisponibles_despues())
                            && eq(m.getUsados_antes(), previous.getUsados_despues())
                            && eq(m.getAsignados(), m.getTipo() == Tipo.AJUSTE
                                    ? add(previous.getAsignados(), m.getDias()) : previous.getAsignados());
                }
                switch (m.getTipo()) {
                    case APERTURA:
                        ok &= m.getDias().signum() >= 0 && zero(m.getDisponibles_antes())
                                && eq(m.getUsados_antes(), m.getUsados_despues()) && m.getRequest() == null && m.getOrigen() == null;
                        break;
                    case AJUSTE:
                        ok &= m.getDias().signum() != 0 && eq(m.getUsados_antes(), m.getUsados_despues())
                                && m.getRequest() == null && m.getOrigen() == null && m.getClave() != null
                                && m.getMotivo() != null && !m.getMotivo().trim().isEmpty();
                        break;
                    case CONSUMO:
                        ok &= m.getDias().signum() < 0 && eq(m.getUsados_despues(), m.getUsados_antes().subtract(m.getDias()))
                                && m.getRequest() != null && m.getOrigen() == null;
                        if (m.getRequest() != null) {
                            ok &= consumedRequests.add(m.getRequest().getIdrequest()) && resueltaVacacion(m.getRequest())
                                    && eq(m.getRequest().getDias_solicitados(), m.getDias().negate())
                                    && m.getRequest().getIdpersonal_personal() != null
                                    && Objects.equals(m.getRequest().getIdpersonal_personal().getIdpersonal(),
                                            m.getBalance().getIdpersonal_personal().getIdpersonal())
                                    && Objects.equals(year(m.getRequest()), m.getBalance().getAnio());
                            debits.put(m.getIdmovement(), m);
                        }
                        break;
                    case REINTEGRO:
                        ThLeaveMovement debit = m.getOrigen() == null ? null : debits.get(m.getOrigen().getIdmovement());
                        ok &= m.getDias().signum() > 0 && eq(m.getUsados_despues(), m.getUsados_antes().subtract(m.getDias()))
                                && debit != null && m.getRequest() != null;
                        if (debit != null && m.getRequest() != null) {
                            ok &= refundedOrigins.add(debit.getIdmovement()) && eq(m.getDias(), debit.getDias().negate())
                                    && Objects.equals(m.getRequest().getIdrequest(), debit.getRequest().getIdrequest())
                                    && "REVERTIDA".equals(normalize(m.getRequest().getEstado()));
                        }
                        break;
                }
            }
            if (!ok) { valid = false; alertas.add("Movimiento #" + m.getIdmovement() + " inconsistente con sus valores, origen o secuencia"); }
            previous = m;
        }
        for (ThLeaveMovement debit : debits.values()) {
            if ("REVERTIDA".equals(normalize(debit.getRequest().getEstado())) && !refundedOrigins.contains(debit.getIdmovement())) {
                valid = false; alertas.add("Solicitud #" + debit.getRequest().getIdrequest() + " revertida sin reintegro registrado");
            }
        }
        return valid;
    }

    private BigDecimal total(List<ThLeaveMovement> libro, Tipo tipo) {
        List<ThLeaveMovement> selected = libro.stream().filter(m -> m.getTipo() == tipo).toList();
        return selected.stream().anyMatch(m -> m.getDias() == null) ? null
                : selected.stream().map(ThLeaveMovement::getDias).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private boolean resueltaVacacion(ThLeaveRequest r) {
        return "VACACION".equals(normalize(r.getTipolicencia()))
                && Set.of("APROBADA", "REVERTIDA").contains(normalize(r.getEstado()));
    }
    private String normalize(String s) { return s == null ? "" : s.trim().toUpperCase(Locale.ROOT); }
    private Integer year(ThLeaveRequest r) { return r.getFechainicio() == null ? null : r.getFechainicio().getYear(); }
    private boolean nonnegative(BigDecimal... values) { return Arrays.stream(values).allMatch(v -> v != null && v.signum() >= 0); }
    private boolean eq(BigDecimal a, BigDecimal b) { return a != null && b != null && a.compareTo(b) == 0; }
    private boolean zero(BigDecimal n) { return n != null && n.signum() == 0; }
    private BigDecimal add(BigDecimal a, BigDecimal b) { return a == null || b == null ? null : a.add(b); }
    private BigDecimal difference(BigDecimal actual, BigDecimal book) { return actual == null || book == null ? null : actual.subtract(book); }
}
