package com.epmapat.erp_epmapat.rrhh.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.rrhh.modelo.Detcargo;
import com.epmapat.erp_epmapat.rrhh.repositorio.DetcargoR;

@Service("rrhhLegacyDetcargoServicio")
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

