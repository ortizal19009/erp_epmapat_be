package com.epmapat.erp_epmapat.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.rrhh.Personal;
import com.epmapat.erp_epmapat.repositorio.rrhh.PersonalR;

@Service
public class PersonalServicio {
    @Autowired
    private PersonalR dao;

    public List<Personal> findAll() {
        return dao.findAll();
    }

    public Personal save(Personal p) {
        return dao.save(p);
    }
}
