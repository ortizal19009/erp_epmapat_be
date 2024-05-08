package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles_impuestos;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_detalles_impuestosR;


@Service
public class Fec_factura_detalles_impuestosService {
    @Autowired
    private Fec_factura_detalles_impuestosR dao;

    public List<Fec_factura_detalles_impuestos> findAll() {
        return dao.findAll();
    }

    public <S extends Fec_factura_detalles_impuestos> S save(S entity) {
        return dao.save(entity);
    }
}
