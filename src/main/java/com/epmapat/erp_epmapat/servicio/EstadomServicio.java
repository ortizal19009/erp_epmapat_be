package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.mobile.EstadomMobile;
import com.epmapat.erp_epmapat.modelo.Estadom;
import com.epmapat.erp_epmapat.repositorio.EstadomR;

@Service
public class EstadomServicio {

    @Autowired
    EstadomR dao;

    public <S extends Estadom> S save(S entity) {
        return dao.save(entity);
    }

    public List<Estadom> findAll() {
        return dao.findAll();
    }

    public Optional<Estadom> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    /*
     * =============================================================
     * SERVICIOS PARA MOBILE
     * =============================================================
     */
    public List<EstadomMobile> findAllEstadom() {
        return dao.findAllEstadom();
    }
}