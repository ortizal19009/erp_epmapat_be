package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Facturamodificaciones;
import com.epmapat.erp_epmapat.repositorio.FacturamodificacionesR;

@Service
public class FacturamodificacionesServicio {
    @Autowired
    private FacturamodificacionesR dao; 

    public List<Facturamodificaciones> findAll_modi() {
        return dao.findAll();
    }

    public <S extends Facturamodificaciones> S save_modi(S entity) {
        return dao.save(entity);
    }

}
