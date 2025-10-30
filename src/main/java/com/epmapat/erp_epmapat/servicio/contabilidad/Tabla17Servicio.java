package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla17;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla17R;

@Service
public class Tabla17Servicio {

    @Autowired
    private Tabla17R dao;

    public List<Tabla17> findAll() {
        return dao.findAll();
    }

    public List<Tabla17> obtenerRegistrosConPorcivaMayorACero() {
        return dao.findByPorcivaGreaterThanOrderByPorcivaAsc();
    }

    public Optional<Tabla17> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Tabla17> S save(S entity) {
        return dao.save(entity);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

}
