package com.epmapat.erp_epmapat.rrhh.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.*;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveBalanceStateServicio {
    private final ThLeaveBalanceR balances;
    private final ThLeaveMovementR movements;
    private final ThAuditServicio audit;
    private final ThAuditLogR logs;

    @Transactional
    public void cambiar(Long idbalance, Boolean activo, Long version, String motivo, Long actor) {
        if (idbalance == null || idbalance <= 0 || activo == null || version == null || version < 0
                || actor == null || actor <= 0 || motivo == null || motivo.isBlank() || motivo.length() > 2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indique saldo, estado, versión consultada y motivo obligatorio");
        ThLeaveBalance balance = balances.findByIdForUpdate(idbalance)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo inexistente"));
        if (!version.equals(balance.getVersion()))
            throw conflicto("El saldo cambió desde la consulta; actualice antes de cambiar su estado");
        if (activo.equals(balance.getEstado())) return;
        if (activo) validarActivacion(balance);
        Boolean before = balance.getEstado();
        balance.setEstado(activo); balance.setUsumodi(actor); balance.setFecmodi(LocalDate.now());
        balances.saveAndFlush(balance);
        audit.log("TH_LEAVE_BALANCE", idbalance, activo ? "ACTIVATE" : "DEACTIVATE",
                "Estado " + stateLabel(before) + " -> " + stateLabel(activo) + ". Versión " + version + " -> " + balance.getVersion()
                        + ". Motivo: " + motivo.trim(), actor);
    }

    private void validarActivacion(ThLeaveBalance b) {
        if (b.getAnio() == null || b.getAnio() < 1900 || b.getAnio() > 9999
                || !nonnegative(b.getDias_asignados(), b.getDias_usados(), b.getDias_disponibles())
                || b.getDias_asignados().compareTo(b.getDias_usados().add(b.getDias_disponibles())) != 0)
            throw conflicto("El saldo requiere conciliación antes de activarse");
        if (balances.findByPersonal(b.getIdpersonal_personal().getIdpersonal()).stream()
                .filter(other -> Objects.equals(b.getAnio(), other.getAnio())).count() != 1)
            throw conflicto("Existen saldos duplicados para este ejercicio; requiere conciliación");
        movements.findFirstByBalanceIdbalanceOrderByIdmovementDesc(b.getIdbalance()).ifPresent(last -> {
            if (!same(last.getAsignados(), b.getDias_asignados()) || !same(last.getUsados_despues(), b.getDias_usados())
                    || !same(last.getDisponibles_despues(), b.getDias_disponibles()))
                throw conflicto("El saldo no coincide con el libro; requiere conciliación antes de activarse");
        });
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ThLeaveBalanceStateResponse historial(Long idbalance) {
        if (idbalance == null || idbalance <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo inválido");
        ThLeaveBalance b = balances.findById(idbalance)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo inexistente"));
        var events = logs.findByEntidadAndIdregistroOrderByFechaDesc("TH_LEAVE_BALANCE", idbalance).stream()
                .sorted(Comparator.comparing(ThAuditLog::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ThAuditLog::getIdaudit))
                .map(a -> new ThLeaveHistoryResponse.Evento(a.getIdaudit(), a.getAccion(), a.getDetalle(), a.getUsuario(), a.getFecha())).toList();
        return new ThLeaveBalanceStateResponse(b.getIdbalance(), b.getIdpersonal_personal().getIdpersonal(), b.getAnio(),
                b.getEstado(), b.getVersion(), events);
    }

    private boolean nonnegative(BigDecimal... amounts) { return Arrays.stream(amounts).allMatch(n -> n != null && n.signum() >= 0); }
    private String stateLabel(Boolean value) { return value == null ? "Sin estado" : value ? "Activo" : "Inactivo"; }
    private boolean same(BigDecimal a, BigDecimal b) { return a != null && b != null && a.compareTo(b) == 0; }
    private ResponseStatusException conflicto(String message) { return new ResponseStatusException(HttpStatus.CONFLICT, message); }
}
