package com.epmapat.erp_epmapat.servicio.coactivas;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.coactivas.Facxremi;
import com.epmapat.erp_epmapat.repositorio.coactivas.FacxremiR;

@Service
public class FacxremiServicio {
    @Autowired
    private FacxremiR dao;

    @Transactional
    public Facxremi savefacxremi(Facxremi facxremi) {
        return dao.save(facxremi);
    }

    public List<Facxremi> findByRemision(Long idremision, Long tipfac) {
        return dao.findByRemision(idremision, tipfac);
    }

}
