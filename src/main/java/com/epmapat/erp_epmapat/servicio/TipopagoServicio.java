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

import com.epmapat.erp_epmapat.modelo.Tipopago;
import com.epmapat.erp_epmapat.repositorio.TipopagoR;

@Service
public class TipopagoServicio implements TipopagoR {
    
    @Autowired
    TipopagoR dao;

    @Override
    public <S extends Tipopago> S save(S entity) {
        return dao.save(entity);
    }

    @Override
    public List<Tipopago> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Tipopago> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    // ======================================================================

    @Override
    public void deleteAllInBatch() {
        
        
    }

    @Override
    public void deleteAllInBatch(Iterable<Tipopago> entities) {
        
        
    }

    @Override
    public List<Tipopago> findAll(Sort sort) {
        
        return null;
    }

    @Override
    public <S extends Tipopago> List<S> findAll(Example<S> example) {
        
        return null;
    }

    @Override
    public <S extends Tipopago> List<S> findAll(Example<S> example, Sort sort) {
        
        return null;
    }

    @Override
    public List<Tipopago> findAllById(Iterable<Long> ids) {
        
        return null;
    }

    @Override
    public void flush() {
        
        
    }

    @Override
    public Tipopago getById(Long id) {
        
        return null;
    }

    @Override
    public Tipopago getOne(Long id) {
        
        return null;
    }

    @Override
    public Tipopago getReferenceById(Long id) {
        
        return null;
    }

    @Override
    public <S extends Tipopago> List<S> saveAll(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Tipopago> List<S> saveAllAndFlush(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Tipopago> S saveAndFlush(S entity) {
        
        return null;
    }

    @Override
    public Page<Tipopago> findAll(Pageable pageable) {
        
        return null;
    }

    @Override
    public long count() {
        
        return 0;
    }

    @Override
    public void delete(Tipopago entity) {
        
        
    }

    @Override
    public void deleteAll() {
        
        
    }

    @Override
    public void deleteAll(Iterable<? extends Tipopago> entities) {
        
        
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        
        
    }

    @Override
    public boolean existsById(Long id) {
        
        return false;
    }

    @Override
    public <S extends Tipopago> long count(Example<S> example) {
        
        return 0;
    }

    @Override
    public <S extends Tipopago> boolean exists(Example<S> example) {
        
        return false;
    }

    @Override
    public <S extends Tipopago> Page<S> findAll(Example<S> example, Pageable pageable) {
        
        return null;
    }

    @Override
    public <S extends Tipopago, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        
        return null;
    }

    @Override
    public <S extends Tipopago> Optional<S> findOne(Example<S> example) {
        
        return null;
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        
        
    }



}
