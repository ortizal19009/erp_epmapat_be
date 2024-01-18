package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import com.epmapat.erp_epmapat.repositorio.RutasxemisionR;

@Service
public class RutasxemisionServicio {

    @Autowired
    private RutasxemisionR dao;

    public List<Rutasxemision> findByIdemision(Long idemision) {
        return dao.findByIdemision(idemision);
    }

    public Optional<Rutasxemision> findById(Long idemision) {
        return dao.findById(idemision);
    }

    public <S extends Rutasxemision> S save(S entity) {
        return dao.save(entity);
    }

    public Long contarPorEstadoYEmision(Long idemision_emisiones) {
        return dao.contarPorEstadoYIdemision( idemision_emisiones );
    }

}
