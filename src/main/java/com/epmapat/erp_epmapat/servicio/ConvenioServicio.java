package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Convenios;
import com.epmapat.erp_epmapat.repositorio.ConveniosR;

@Service
public class ConvenioServicio {

    @Autowired
    private ConveniosR dao;

    public List<Convenios> findAll(Long dnro, Long hnro) {
        if (dnro != null || hnro != null) {
            return dao.findAll(dnro, hnro);
        } else {
            return dao.findAll();
        }
    }

    public List<Convenios> findNroconvenio(Long nroconvenio) {
        return dao.findNroconvenio(nroconvenio);
    }

    public <S extends Convenios> boolean existsByNroconvenio() {
		return dao.exists(null);
	}

    public <S extends Convenios> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Convenios> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

}
