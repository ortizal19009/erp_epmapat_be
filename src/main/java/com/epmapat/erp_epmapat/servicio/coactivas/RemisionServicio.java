package com.epmapat.erp_epmapat.servicio.coactivas;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.coactivas.Remision;
import com.epmapat.erp_epmapat.repositorio.coactivas.RemisionR;

@Service
public class RemisionServicio {
    @Autowired
    private RemisionR dao;

    public Page<Remision> findAllPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return dao.findAll(pageable);
    }

    @Transactional
    public Remision saveRemision(Remision remision) {
        return dao.save(remision);
    }

    public List<Remision> findRemisionesByFeccrea(LocalDate d, LocalDate h) {
        return dao.findRemisionesByFeccrea(d, h);
    }
    public Optional<Remision> findRemisionById(Long idremision){
        return dao.findById(idremision);
    }
}
