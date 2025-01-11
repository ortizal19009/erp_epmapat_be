package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla15;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla15R;

@Service
public class Tabla15Servicio {

    @Autowired
    private Tabla15R dao;

    public List<Tabla15> findAll() {
        return dao.findAll();
    }

    public Optional<Tabla15> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Tabla15> S save(S entity) {
        return dao.save(entity);
    }

    public void deleteById(Long id) {
		dao.deleteById(id);
	}

}

