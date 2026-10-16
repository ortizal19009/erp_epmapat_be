package com.epmapat.erp_epmapat.rrhh;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThLeaveAnniversaryTest {
    final PersonalR people = mock(PersonalR.class);
    final ThLeaveBalanceR balances = mock(ThLeaveBalanceR.class);
    final ThLeaveMovementServicio movements = mock(ThLeaveMovementServicio.class);
    final ThLeaveAnniversaryServicio worker = new ThLeaveAnniversaryServicio(people, balances, movements);
    final Personal person = new Personal();
    ThLeaveAnniversaryTest() {
        person.setIdpersonal(8L); person.setEstado(true); person.setFecinicio(LocalDate.of(2018,10,20));
        when(people.findByIdForUpdate(8L)).thenReturn(Optional.of(person));
        when(balances.findByPersonalAndAnioForUpdate(anyLong(),anyInt())).thenReturn(Optional.empty());
        when(balances.saveAndFlush(any())).thenAnswer(call -> { ThLeaveBalance b=call.getArgument(0); b.setIdbalance((long)b.getAnio()); return b; });
    }
    @Test void activationPreservesPastYearsAndDoesNotCreditOnActivationDay() {
        worker.acreditar(8L, LocalDate.of(2026,10,20), LocalDate.of(2026,10,20));
        verifyNoInteractions(balances,movements);
        worker.acreditar(8L, LocalDate.of(2026,10,16), LocalDate.of(2026,10,19));
        verifyNoInteractions(balances,movements);
    }
    @Test void creditsExactlyThirtyAtNextAnniversary() {
        worker.acreditar(8L, LocalDate.of(2026,10,16), LocalDate.of(2026,10,20));
        verify(movements).acreditarAniversario(2026L,8L,LocalDate.of(2026,10,20));
        verify(balances,never()).findByPersonalAndAnioForUpdate(8L,2025);
    }
    @Test void catchesMissedYearsOnlyAfterActivation() {
        worker.acreditar(8L,LocalDate.of(2026,10,16),LocalDate.of(2028,10,21));
        verify(movements,times(3)).acreditarAniversario(anyLong(),eq(8L),any());
    }
    @Test void leapDayUsesFebruary28AndNeverCreditsBeforeFirstYear() {
        person.setFecinicio(LocalDate.of(2024,2,29));
        worker.acreditar(8L,LocalDate.of(2024,3,1),LocalDate.of(2025,2,28));
        verify(movements).acreditarAniversario(2025L,8L,LocalDate.of(2025,2,28));
    }
    @Test void inactiveOrMissingStartDateNeverGetsCredit() {
        person.setEstado(false); worker.acreditar(8L,LocalDate.of(2026,1,1),LocalDate.of(2026,12,31));
        person.setEstado(true); person.setFecinicio(null); worker.acreditar(8L,LocalDate.of(2026,1,1),LocalDate.of(2026,12,31));
        verifyNoInteractions(balances,movements);
    }
    @Test void endOfContractPreventsLaterAnniversaries() {
        person.setFecfin(LocalDate.of(2026,10,19));
        worker.acreditar(8L,LocalDate.of(2026,1,1),LocalDate.of(2027,12,31));
        verifyNoInteractions(balances,movements);
    }
    @Test void existingBalanceAndUsedDaysAreNeverOverwritten() {
        var b=new ThLeaveBalance(); b.setIdbalance(88L); b.setEstado(true); b.setDias_usados(new BigDecimal("12"));
        when(balances.findByPersonalAndAnioForUpdate(8L,2026)).thenReturn(Optional.of(b));
        worker.acreditar(8L,LocalDate.of(2026,10,16),LocalDate.of(2026,10,20));
        verify(balances,never()).saveAndFlush(any());
        assertEquals(new BigDecimal("12"),b.getDias_usados());
        verify(movements).acreditarAniversario(88L,8L,LocalDate.of(2026,10,20));
    }
    @Test void inactiveBalanceIsNotCredited() {
        var b=new ThLeaveBalance(); b.setIdbalance(88L); b.setEstado(false);
        when(balances.findByPersonalAndAnioForUpdate(8L,2026)).thenReturn(Optional.of(b));
        worker.acreditar(8L,LocalDate.of(2026,10,16),LocalDate.of(2026,10,20));
        verifyNoInteractions(movements);
    }
}
