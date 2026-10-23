package com.epmapat.erp_epmapat.servicio;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.ConveniosR;

@Service
public class ValidacionTitularidadServicio {
    private final AbonadosR abonados;
    private final FacturasR facturas;
    private final ConveniosR convenios;

    public ValidacionTitularidadServicio(AbonadosR abonados, FacturasR facturas, ConveniosR convenios) {
        this.abonados = abonados;
        this.facturas = facturas;
        this.convenios = convenios;
    }

    public void validar(Long idabonado) {
        if (idabonado == null || !abonados.existsById(idabonado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona una cuenta v?lida para el tr?mite.");
        }
        if (convenios.contarConveniosActivosParaTitularidad(idabonado) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "La cuenta tiene convenios de pago activos. No se permite cambiar propietario o responsable de pago.");
        }
        if (facturas.contarDeudasParaCambioTitularidad(idabonado) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "La cuenta tiene facturas pendientes fuera de la emisi?n abierta. Debe cancelar la deuda antes de cambiar propietario o responsable de pago.");
        }
    }
}
