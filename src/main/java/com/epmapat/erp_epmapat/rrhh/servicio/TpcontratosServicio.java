package com.epmapat.erp_epmapat.rrhh.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.rrhh.modelo.Tpcontratos;
import com.epmapat.erp_epmapat.rrhh.repositorio.TpcontratosR;

@Service("rrhhLegacyTpcontratosServicio")
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

