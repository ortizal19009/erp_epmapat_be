package com.epmapat.erp_epmapat.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.rrhh.Contemergencia;
import com.epmapat.erp_epmapat.repositorio.rrhh.ContemergenciasR;

@Service
public class ContemergenciaServicio {
    @Autowired
    private ContemergenciasR dao;

    public List<Contemergencia> findAll() {
        return dao.findAll();
    }
}
