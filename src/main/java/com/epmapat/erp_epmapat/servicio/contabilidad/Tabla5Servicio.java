package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla5;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla5R;

@Service
public class Tabla5Servicio {

    @Autowired
    private Tabla5R dao;

    public List<Tabla5> findAll() {
        return dao.findAll();
    }

    public Optional<Tabla5> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Tabla5> S save(S entity) {
        return dao.save(entity);
    }

    public void deleteById(Long id) {
		dao.deleteById(id);
	}

}
