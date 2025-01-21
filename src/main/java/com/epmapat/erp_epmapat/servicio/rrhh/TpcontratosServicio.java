package com.epmapat.erp_epmapat.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.rrhh.Tpcontratos;
import com.epmapat.erp_epmapat.repositorio.rrhh.TpcontratosR;

@Service
public class TpcontratosServicio {
    @Autowired
    private TpcontratosR dao;

    public List<Tpcontratos> findAll() {
        return dao.findAll();
    }

    public Tpcontratos save(Tpcontratos tpcontratos) {
        return dao.save(tpcontratos);
    }
}
