package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Estadom;
import com.epmapat.erp_epmapat.repositorio.EstadomR;

@Service
public class EstadomServicio implements EstadomR {

    @Autowired
    EstadomR dao;

    @Override
    public <S extends Estadom> S save(S entity) {
        return dao.save(entity);
    }

    @Override
    public List<Estadom> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Estadom> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);        
    }

    // =====================================================================
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        
        
    }

    @Override
    public void deleteAllInBatch() {
        
        
    }

    @Override
    public void deleteAllInBatch(Iterable<Estadom> entities) {
        
        
    }

    @Override
    public List<Estadom> findAll(Sort sort) {
        
        return null;
    }

    @Override
    public <S extends Estadom> List<S> findAll(Example<S> example) {
        
        return null;
    }

    @Override
    public <S extends Estadom> List<S> findAll(Example<S> example, Sort sort) {
        
        return null;
    }

    @Override
    public List<Estadom> findAllById(Iterable<Long> ids) {
        
        return null;
    }

    @Override
    public void flush() {
        
        
    }

    @Override
    public Estadom getById(Long id) {
        
        return null;
    }

    @Override
    public Estadom getOne(Long id) {
        
        return null;
    }

    @Override
    public Estadom getReferenceById(Long id) {
        
        return null;
    }

    @Override
    public <S extends Estadom> List<S> saveAll(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Estadom> List<S> saveAllAndFlush(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Estadom> S saveAndFlush(S entity) {
        
        return null;
    }

    @Override
    public Page<Estadom> findAll(Pageable pageable) {
        
        return null;
    }

    @Override
    public long count() {
        
        return 0;
    }

    @Override
    public void delete(Estadom entity) {
        
        
    }

    @Override
    public void deleteAll() {
        
        
    }

    @Override
    public void deleteAll(Iterable<? extends Estadom> entities) {
        
        
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        
        
    }

    @Override
    public boolean existsById(Long id) {
        
        return false;
    }

    @Override
    public <S extends Estadom> long count(Example<S> example) {
        
        return 0;
    }

    @Override
    public <S extends Estadom> boolean exists(Example<S> example) {
        
        return false;
    }

    @Override
    public <S extends Estadom> Page<S> findAll(Example<S> example, Pageable pageable) {
        
        return null;
    }

    @Override
    public <S extends Estadom, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        
        return null;
    }

    @Override
    public <S extends Estadom> Optional<S> findOne(Example<S> example) {
        
        return null;
    }

    
}
