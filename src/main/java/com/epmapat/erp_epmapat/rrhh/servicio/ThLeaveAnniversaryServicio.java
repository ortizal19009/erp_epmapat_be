package com.epmapat.erp_epmapat.rrhh.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveBalance;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @lombok.extern.slf4j.Slf4j
public class ThLeaveAnniversaryServicio {
    private final PersonalR personal;
    private final ThLeaveBalanceR balances;
    private final ThLeaveMovementServicio movements;

    /** Personal lock serializes annual credits with manual openings/reposition assignments. */
    @Transactional
    public void acreditar(Long id, LocalDate activacion, LocalDate hoy) {
        var empleado = personal.findByIdForUpdate(id).orElseThrow();
        var inicio = empleado.getFecinicio();
        if (!Boolean.TRUE.equals(empleado.getEstado()) || inicio == null || !inicio.isBefore(hoy)) return;
        LocalDate limite = empleado.getFecfin() != null && empleado.getFecfin().isBefore(hoy) ? empleado.getFecfin() : hoy;
        for (int year = Math.max(inicio.getYear() + 1, activacion.getYear()); year <= limite.getYear(); year++) {
            // LocalDate.plusYears maps a February 29 anniversary to February 28 in a non-leap year.
            LocalDate aniversario = inicio.plusYears(year - inicio.getYear());
            if (!aniversario.isAfter(activacion) || aniversario.isAfter(limite)) continue;
            var balance = balances.findByPersonalAndAnioForUpdate(id, year).orElse(null);
            if (balance == null) {
                balance = new ThLeaveBalance(); balance.setIdpersonal_personal(empleado); balance.setAnio(year);
                balance.setDias_asignados(BigDecimal.ZERO); balance.setDias_usados(BigDecimal.ZERO);
                balance.setDias_disponibles(BigDecimal.ZERO); balance.setEstado(true);
                balance.setFeccrea(hoy); balance.setUsucrea(0L);
                balance = balances.saveAndFlush(balance);
                movements.apertura(balance, 0L);
            }
            if (!Boolean.TRUE.equals(balance.getEstado())) {
                log.warn("Aniversario pendiente: saldo inactivo personal={} anio={}", id, year);
                continue;
            }
            movements.acreditarAniversario(balance.getIdbalance(), id, aniversario);
        }
    }
}
