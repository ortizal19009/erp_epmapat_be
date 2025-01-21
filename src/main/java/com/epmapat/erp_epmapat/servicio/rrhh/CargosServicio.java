package com.epmapat.erp_epmapat.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.rrhh.Cargos;
import com.epmapat.erp_epmapat.repositorio.rrhh.CargosR;

@Service
public class CargosServicio {
    @Autowired
    private CargosR dao;

    public List<Cargos> findAll() {
        return dao.findAll();
    }

    public Cargos save(Cargos cargos) {
        return dao.save(cargos);
    }
}
