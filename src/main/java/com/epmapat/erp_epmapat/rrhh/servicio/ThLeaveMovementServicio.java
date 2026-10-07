package com.epmapat.erp_epmapat.rrhh.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveMovementResponse;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveMovement.Tipo;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveMovementServicio {
    private final ThLeaveMovementR dao;
    private final ThLeaveBalanceR balances;

    /** Caller holds the balance lock or has just created this balance. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void apertura(ThLeaveBalance balance, Long actor) {
        validarSaldo(balance);
        if (!dao.existsByBalanceIdbalanceAndTipo(balance.getIdbalance(), Tipo.APERTURA)) {
            registrar(balance, null, null, Tipo.APERTURA, balance.getDias_disponibles(),
                    BigDecimal.ZERO, balance.getDias_usados(), actor, "Saldo vigente al iniciar el libro de movimientos");
        }
    }

    /** Caller also holds the request lock. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void consumir(ThLeaveBalance balance, ThLeaveRequest request, Long actor, String motivo) {
        validarSaldo(balance);
        validarContraLibro(balance);
        BigDecimal dias = request.getDias_solicitados();
        if (dias == null || dias.signum() <= 0 || balance.getDias_disponibles().compareTo(dias) < 0)
            throw conflicto("Días o saldo insuficiente para registrar el consumo");
        if (dao.findByRequestIdrequestAndTipo(request.getIdrequest(), Tipo.CONSUMO).isPresent())
            throw conflicto("La solicitud ya tiene un consumo registrado");
        apertura(balance, actor);
        BigDecimal before = balance.getDias_disponibles();
        BigDecimal used = balance.getDias_usados();
        balance.setDias_disponibles(before.subtract(dias));
        balance.setDias_usados(used.add(dias));
        guardarSaldo(balance, actor);
        registrar(balance, request, null, Tipo.CONSUMO, dias.negate(), before, used, actor, motivo);
    }

    /** Restore precisely the recorded debit; never infer historical debits from dates. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void reintegrar(ThLeaveRequest request, Long actor, String motivo) {
        ThLeaveMovement consumo = dao.findByRequestIdrequestAndTipo(request.getIdrequest(), Tipo.CONSUMO)
                .orElseThrow(() -> conflicto("La aprobación no tiene consumo registrado; requiere conciliación histórica"));
        if (dao.findByRequestIdrequestAndTipo(request.getIdrequest(), Tipo.REINTEGRO).isPresent())
            throw conflicto("La solicitud ya tiene un reintegro registrado");
        ThLeaveBalance balance = balances.findByIdForUpdate(consumo.getBalance().getIdbalance())
                .orElseThrow(() -> conflicto("No existe el saldo del consumo original"));
        validarSaldo(balance);
        validarContraLibro(balance);
        BigDecimal dias = consumo.getDias().negate();
        if (dias.signum() <= 0 || balance.getDias_usados().compareTo(dias) < 0
                || !balance.getIdpersonal_personal().getIdpersonal().equals(request.getIdpersonal_personal().getIdpersonal()))
            throw conflicto("El consumo original no es conciliable con el saldo; requiere revisión");
        BigDecimal before = balance.getDias_disponibles();
        BigDecimal used = balance.getDias_usados();
        balance.setDias_disponibles(before.add(dias));
        balance.setDias_usados(used.subtract(dias));
        guardarSaldo(balance, actor);
        registrar(balance, request, consumo, Tipo.REINTEGRO, dias, before, used, actor, motivo);
    }

    @Transactional
    public void ajustar(Long idbalance, BigDecimal dias, String motivo, String clave, Long actor) {
        if (actor == null || actor <= 0 || dias == null || dias.signum() == 0
                || dias.stripTrailingZeros().scale() > 2 || dias.abs().compareTo(new BigDecimal("99999999.99")) > 0
                || motivo == null || motivo.trim().isEmpty() || motivo.length() > 2000
                || clave == null || !clave.matches("[a-fA-F0-9]{8}(-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indique días no nulos con hasta dos decimales, motivo y clave UUID");
        String key = clave.toLowerCase(java.util.Locale.ROOT);
        ThLeaveBalance balance = balances.findByIdForUpdate(idbalance)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo inexistente"));
        var previous = dao.findByBalanceIdbalanceAndClave(idbalance, key);
        if (previous.isPresent()) {
            ThLeaveMovement old = previous.get();
            if (old.getTipo() != Tipo.AJUSTE || old.getDias().compareTo(dias) != 0
                    || !motivo.trim().equals(old.getMotivo()) || !actor.equals(old.getUsuario()))
                throw conflicto("La clave ya corresponde a otro ajuste");
            return;
        }
        validarSaldo(balance);
        validarContraLibro(balance);
        if (!Boolean.TRUE.equals(balance.getEstado())) throw conflicto("El saldo está inactivo");
        BigDecimal available = balance.getDias_disponibles().add(dias);
        BigDecimal assigned = balance.getDias_asignados().add(dias);
        if (available.signum() < 0 || assigned.compareTo(new BigDecimal("99999999.99")) > 0)
            throw conflicto("El ajuste excede el saldo disponible o el máximo permitido");
        apertura(balance, actor);
        BigDecimal before = balance.getDias_disponibles();
        BigDecimal used = balance.getDias_usados();
        balance.setDias_asignados(assigned);
        balance.setDias_disponibles(available);
        guardarSaldo(balance, actor);
        registrar(balance, null, null, Tipo.AJUSTE, dias, before, used, actor, motivo.trim(), key);
    }

    @Transactional
    public void abrirLibro(Long idbalance, Long actor) {
        if (actor == null || actor <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario obligatorio");
        ThLeaveBalance balance = balances.findByIdForUpdate(idbalance)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo inexistente"));
        validarSaldo(balance);
        validarContraLibro(balance);
        apertura(balance, actor);
    }

    @Transactional(readOnly = true)
    public List<ThLeaveMovementResponse> byPersonal(Long personal, Integer anio) {
        return dao.findByPersonal(personal, anio).stream().map(ThLeaveMovementResponse::from).toList();
    }

    private void validarSaldo(ThLeaveBalance b) {
        if (b.getDias_asignados() == null || b.getDias_usados() == null || b.getDias_disponibles() == null
                || b.getDias_asignados().signum() < 0 || b.getDias_usados().signum() < 0 || b.getDias_disponibles().signum() < 0
                || b.getDias_asignados().compareTo(b.getDias_usados().add(b.getDias_disponibles())) != 0)
            throw conflicto("El saldo no es conciliable: asignados debe ser usados más disponibles");
    }

    private void guardarSaldo(ThLeaveBalance balance, Long actor) {
        balance.setFecmodi(LocalDate.now()); balance.setUsumodi(actor);
        balances.save(balance);
    }

    private void validarContraLibro(ThLeaveBalance balance) {
        dao.findFirstByBalanceIdbalanceOrderByIdmovementDesc(balance.getIdbalance()).ifPresent(last -> {
            if (last.getDisponibles_despues().compareTo(balance.getDias_disponibles()) != 0
                    || last.getUsados_despues().compareTo(balance.getDias_usados()) != 0
                    || last.getAsignados().compareTo(balance.getDias_asignados()) != 0)
                throw conflicto("El saldo no coincide con el último movimiento; requiere conciliación");
        });
    }

    private ThLeaveMovement registrar(ThLeaveBalance b, ThLeaveRequest r, ThLeaveMovement origen, Tipo tipo,
            BigDecimal dias, BigDecimal before, BigDecimal used, Long actor, String motivo) {
        return registrar(b, r, origen, tipo, dias, before, used, actor, motivo, null);
    }

    private ThLeaveMovement registrar(ThLeaveBalance b, ThLeaveRequest r, ThLeaveMovement origen, Tipo tipo,
            BigDecimal dias, BigDecimal before, BigDecimal used, Long actor, String motivo, String clave) {
        ThLeaveMovement movement = new ThLeaveMovement();
        movement.setClave(clave);
        movement.setBalance(b); movement.setRequest(r); movement.setOrigen(origen); movement.setTipo(tipo);
        movement.setDias(dias); movement.setDisponibles_antes(before); movement.setDisponibles_despues(b.getDias_disponibles());
        movement.setUsados_antes(used); movement.setUsados_despues(b.getDias_usados()); movement.setAsignados(b.getDias_asignados());
        movement.setUsuario(actor); movement.setFecha(LocalDateTime.now()); movement.setMotivo(motivo);
        return dao.save(movement);
    }

    private ResponseStatusException conflicto(String message) { return new ResponseStatusException(HttpStatus.CONFLICT, message); }
}
