package com.epmapat.erp_epmapat.rrhh.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveBalance;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThLeaveBalanceR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveBalanceServicio {

    private final ThLeaveBalanceR dao;
    private final PersonalR personalR;
    private final ThLeaveMovementServicio movements;

    @Transactional
    public ThLeaveBalance save(ThLeaveBalance b) {
        validar(b);
        b.setIdpersonal_personal(personalR.findByIdForUpdate(b.getIdpersonal_personal().getIdpersonal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Personal no existe: " + b.getIdpersonal_personal().getIdpersonal())));
        dao.findByPersonalAndAnio(b.getIdpersonal_personal().getIdpersonal(), b.getAnio())
                .ifPresent(x -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe saldo para ese año"); });
        if (b.getDias_asignados() == null) b.setDias_asignados(BigDecimal.ZERO);
        if (b.getDias_usados() == null) b.setDias_usados(BigDecimal.ZERO);
        BigDecimal disponible = b.getDias_asignados().subtract(b.getDias_usados());
        if (b.getDias_asignados().signum() < 0 || b.getDias_usados().signum() < 0 || disponible.signum() < 0
                || (b.getDias_disponibles() != null && b.getDias_disponibles().compareTo(disponible) != 0))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo inválido: disponibles debe ser asignados menos usados y no negativo");
        b.setDias_disponibles(disponible);
        b.setFeccrea(LocalDate.now());
        if (b.getEstado() == null) b.setEstado(true);
        ThLeaveBalance saved = dao.save(b);
        movements.apertura(saved, saved.getUsucrea());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ThLeaveBalance> byPersonal(Long idpersonal) {
        return dao.findByPersonal(idpersonal);
    }

    private void validar(ThLeaveBalance b) {
        if (b == null || b.getIdpersonal_personal() == null || b.getIdpersonal_personal().getIdpersonal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idpersonal_personal es obligatorio");
        if (b.getIdbalance() != null || b.getUsumodi() != null || b.getFecmodi() != null || b.getVersion() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La creación de saldo no admite ID, versión ni campos de modificación");
        if (b.getUsucrea() == null || b.getUsucrea() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario responsable obligatorio");
        for (BigDecimal amount : new BigDecimal[] {b.getDias_asignados(), b.getDias_usados(), b.getDias_disponibles()}) {
            if (amount != null && amount.stripTrailingZeros().scale() > 2)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los saldos admiten hasta dos decimales");
        }
        if (b.getAnio() == null || b.getAnio() < 1900 || b.getAnio() > 9999)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "anio es obligatorio");
    }
}

