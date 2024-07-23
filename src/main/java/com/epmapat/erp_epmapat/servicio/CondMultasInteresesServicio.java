package com.epmapat.erp_epmapat.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.CondMultasIntereses;
import com.epmapat.erp_epmapat.repositorio.CondMultasInteresesR;

@Service
public class CondMultasInteresesServicio {
    @Autowired
    CondMultasInteresesR dao;

    public <S extends CondMultasIntereses> S save(S entity) {
        return dao.save(entity);
    }
}
