package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

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

    public <S extends Fec_factura_detalles_impuestos> S _save(S entity) {
        return dao.save(entity);
    }

    public List<Fec_factura_detalles_impuestos> findByIdDetalle(Long iddetalle) {
        return dao.findByIdDetalle(iddetalle);
    }

    public void deleteById(Long idimpuesto) {
        dao.deleteById(idimpuesto);
    }
    public Fec_factura_detalles_impuestos findById(Long idimpuesto){
    return dao.findBy_id(idimpuesto);
    }
}
