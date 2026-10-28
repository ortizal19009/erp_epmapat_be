package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.RetiroMedidoresRequest;
import com.epmapat.erp_epmapat.modelo.AboxSuspensionM;
import com.epmapat.erp_epmapat.modelo.SuspensionesM;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.AboxSuspensionR;

@Service
public class RetiroMedidoresServicio {
    private final SuspensionesS suspensiones;
    private final AbonadosR abonados;
    private final AboxSuspensionR relaciones;
    private final AbonadoServicio abonadoServicio;

    public RetiroMedidoresServicio(SuspensionesS suspensiones, AbonadosR abonados,
            @Qualifier("aboxSuspensionR") AboxSuspensionR relaciones, AbonadoServicio abonadoServicio) {
        this.suspensiones = suspensiones; this.abonados = abonados;
        this.relaciones = relaciones; this.abonadoServicio = abonadoServicio;
    }

    @Transactional
    public SuspensionesM retirar(Long idOrigen, RetiroMedidoresRequest solicitud, Long usuario) {
        if (usuario == null || usuario <= 0 || solicitud.cuentas() == null || solicitud.cuentas().isEmpty()
                || solicitud.cuentas().stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indique el usuario y las cuentas a retirar");
        }
        SuspensionesM origen = suspensiones.findById(idOrigen).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la suspension de origen"));
        if (!Long.valueOf(2).equals(origen.getTipo()) && !Long.valueOf(3).equals(origen.getTipo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El retiro debe partir de una suspension");
        }
        List<Long> ids = solicitud.cuentas().stream().distinct().sorted().toList();
        var cuentas = abonados.lockParaRetiro(ids);
        if (cuentas.size() != ids.size() || cuentas.stream().anyMatch(a -> !Long.valueOf(2).equals(a.getEstado()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden retirar cuentas suspendidas. Recargue el detalle.");
        }
        var vinculadas = new HashSet<Long>();
        relaciones.findByIdsuspension(idOrigen).forEach(relacion -> {
            if (relacion.getIdabonado_abonados() != null) vinculadas.add(relacion.getIdabonado_abonados().getIdabonado());
        });
        if (!vinculadas.containsAll(ids)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hay cuentas que no pertenecen al registro de origen");
        }
        ZoneId zona = ZoneId.of("America/Guayaquil");
        Date fecha = Date.from(LocalDate.now(zona).atStartOfDay(zona).toInstant());
        String nota = "Retiro de medidores desde suspension #" + origen.getNumero();
        if (solicitud.observacion() != null && !solicitud.observacion().isBlank()) nota += ". " + solicitud.observacion().trim();
        SuspensionesM retiro = new SuspensionesM();
        retiro.setTipo(3L); retiro.setIdsuspension_origen(origen); retiro.setFecha(fecha);
        retiro.setFeccrea(fecha); retiro.setUsucrea(usuario); retiro.setObserva(nota);
        retiro.setIddocumento_documentos(origen.getIddocumento_documentos()); retiro.setNumdoc(origen.getNumdoc());
        retiro.setTotal((long) ids.size());
        retiro = suspensiones.save(retiro);
        for (Long id : ids) {
            var abonado = abonadoServicio.retirarMedidorConAuditoria(id, usuario, nota);
            AboxSuspensionM relacion = new AboxSuspensionM();
            relacion.setIdsuspension_suspensiones(retiro); relacion.setIdabonado_abonados(abonado);
            relaciones.save(relacion);
        }
        retiro.setCuentasAfectadas(new ArrayList<>(ids));
        return retiro;
    }
}
