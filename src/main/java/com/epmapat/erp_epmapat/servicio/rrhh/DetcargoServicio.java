package com.epmapat.erp_epmapat.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.rrhh.Detcargo;
import com.epmapat.erp_epmapat.repositorio.rrhh.DetcargoR;

@Service
public class DetcargoServicio {
    @Autowired
    private DetcargoR dao;

    public List<Detcargo> findAll() {
        return dao.findAll();
    }

    public Detcargo save(Detcargo detcargo) {
        return dao.save(detcargo);
    }
}
