package com.epmapat.erp_epmapat.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Impuestos;
import com.epmapat.erp_epmapat.repositorio.ImpuestosR;

@Service
public class ImpuestosServicio {
    @Autowired
    private ImpuestosR dao;

    public Impuestos getCurrentImpuesto() {
        return dao.getCurrentImpuesto();
    }
}
