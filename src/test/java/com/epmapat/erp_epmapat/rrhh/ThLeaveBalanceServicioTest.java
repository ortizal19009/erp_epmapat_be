package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveBalanceServicio;

class ThLeaveBalanceServicioTest {
    @Test void creationCannotSupplyPersistenceVersion() {
        var b = new ThLeaveBalance();
        var p = new Personal(); p.setIdpersonal(7L); b.setIdpersonal_personal(p);
        b.setAnio(2026); b.setUsucrea(8L); b.setVersion(10L);
        assertThrows(ResponseStatusException.class, () -> service.save(b));
    }
    ThLeaveBalanceR balances = mock(ThLeaveBalanceR.class);
    PersonalR people = mock(PersonalR.class);
    ThLeaveBalanceServicio service = new ThLeaveBalanceServicio(balances, people,
            mock(com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveMovementServicio.class));

    ThLeaveBalance balance() {
        Personal employee = new Personal(); employee.setIdpersonal(7L);
        when(people.findByIdForUpdate(7L)).thenReturn(Optional.of(employee));
        when(balances.save(any())).thenAnswer(i -> i.getArgument(0));
        ThLeaveBalance balance = new ThLeaveBalance();
        balance.setIdpersonal_personal(employee); balance.setAnio(2026);
        balance.setDias_asignados(new BigDecimal("20"));
        balance.setUsucrea(8L);
        return balance;
    }

    @Test void derivesAvailableDaysFromAssignedAndUsed() {
        ThLeaveBalance balance = balance(); balance.setDias_usados(new BigDecimal("3"));
        service.save(balance);
        assertEquals(new BigDecimal("17"), balance.getDias_disponibles());
    }

    @Test void rejectsStaleAvailableValue() {
        ThLeaveBalance balance = balance(); balance.setDias_disponibles(new BigDecimal("15"));
        assertThrows(ResponseStatusException.class, () -> service.save(balance));
        verify(balances, never()).save(any());
    }

    @Test void rejectsNegativeOrOverdrawnBalance() {
        ThLeaveBalance balance = balance(); balance.setDias_asignados(new BigDecimal("-1"));
        assertThrows(ResponseStatusException.class, () -> service.save(balance));
        balance.setDias_asignados(BigDecimal.ONE); balance.setDias_usados(BigDecimal.TEN);
        assertThrows(ResponseStatusException.class, () -> service.save(balance));
    }

    @Test void duplicateAnnualBalanceIsRejected() {
        ThLeaveBalance balance = balance();
        when(balances.findByPersonalAndAnio(7L, 2026)).thenReturn(Optional.of(balance));
        assertThrows(ResponseStatusException.class, () -> service.save(balance));
        verify(balances, never()).save(any());
    }
}
