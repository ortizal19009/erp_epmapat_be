package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

@Service
public class DefinirServicio {

    @Autowired
    DefinirR dao;

    @SuppressWarnings("null")
    public Optional<Definir> findById(Long id) {
        return dao.findById(id);
    }

    @SuppressWarnings("null")
    public <S extends Definir> S save(S entity) {
        return dao.save(entity);
    }
}