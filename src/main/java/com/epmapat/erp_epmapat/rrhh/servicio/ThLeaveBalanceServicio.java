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

    @Transactional
    public ThLeaveBalance save(ThLeaveBalance b) {
        validar(b);
        b.setIdpersonal_personal(personalR.findById(b.getIdpersonal_personal().getIdpersonal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Personal no existe: " + b.getIdpersonal_personal().getIdpersonal())));
        dao.findByPersonalAndAnio(b.getIdpersonal_personal().getIdpersonal(), b.getAnio())
                .ifPresent(x -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe saldo para ese año"); });
        if (b.getDias_asignados() == null) b.setDias_asignados(BigDecimal.ZERO);
        if (b.getDias_usados() == null) b.setDias_usados(BigDecimal.ZERO);
        if (b.getDias_disponibles() == null) b.setDias_disponibles(b.getDias_asignados().subtract(b.getDias_usados()));
        if (b.getFeccrea() == null) b.setFeccrea(LocalDate.now());
        if (b.getEstado() == null) b.setEstado(true);
        return dao.save(b);
    }

    @Transactional(readOnly = true)
    public List<ThLeaveBalance> byPersonal(Long idpersonal) {
        return dao.findByPersonal(idpersonal);
    }

    private void validar(ThLeaveBalance b) {
        if (b == null || b.getIdpersonal_personal() == null || b.getIdpersonal_personal().getIdpersonal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idpersonal_personal es obligatorio");
        if (b.getAnio() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "anio es obligatorio");
    }
}

