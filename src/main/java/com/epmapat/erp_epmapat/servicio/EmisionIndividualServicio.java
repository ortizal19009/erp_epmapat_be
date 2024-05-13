package com.epmapat.erp_epmapat.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.EmisionIndividual;
import com.epmapat.erp_epmapat.repositorio.EmisionIndividualR;

@Service
public class EmisionIndividualServicio {
    @Autowired
    private EmisionIndividualR dao;

    public <S extends EmisionIndividual> S save(S entity) {
        return dao.save(entity);
    }

}
