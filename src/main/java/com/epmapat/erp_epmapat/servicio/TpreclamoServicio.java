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

import com.epmapat.erp_epmapat.modelo.Tpreclamo;
import com.epmapat.erp_epmapat.repositorio.TpreclamoR;

@Service
public class TpreclamoServicio implements TpreclamoR{
    
    @Autowired
    TpreclamoR dao;

    @Override
    public <S extends Tpreclamo> S save(S entity) {
        return dao.save(entity);
    }

    @Override
    public List<Tpreclamo> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Tpreclamo> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);        
    }

// ==================================================================
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        
        
    }

    @Override
    public void deleteAllInBatch() {
        
        
    }

    @Override
    public void deleteAllInBatch(Iterable<Tpreclamo> entities) {
        
        
    }

    @Override
    public List<Tpreclamo> findAll(Sort sort) {
        
        return null;
    }

    @Override
    public <S extends Tpreclamo> List<S> findAll(Example<S> example) {
        
        return null;
    }

    @Override
    public <S extends Tpreclamo> List<S> findAll(Example<S> example, Sort sort) {
        
        return null;
    }

    @Override
    public List<Tpreclamo> findAllById(Iterable<Long> ids) {
        
        return null;
    }

    @Override
    public void flush() {
        
        
    }

    @Override
    public Tpreclamo getById(Long id) {
        
        return null;
    }

    @Override
    public Tpreclamo getOne(Long id) {
        
        return null;
    }

    @Override
    public Tpreclamo getReferenceById(Long id) {
        
        return null;
    }

    @Override
    public <S extends Tpreclamo> List<S> saveAll(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Tpreclamo> List<S> saveAllAndFlush(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Tpreclamo> S saveAndFlush(S entity) {
        
        return null;
    }

    @Override
    public Page<Tpreclamo> findAll(Pageable pageable) {
        
        return null;
    }

    @Override
    public long count() {
        
        return 0;
    }

    @Override
    public void delete(Tpreclamo entity) {
        
        
    }

    @Override
    public void deleteAll() {
        
        
    }

    @Override
    public void deleteAll(Iterable<? extends Tpreclamo> entities) {
        
        
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        
        
    }

    @Override
    public boolean existsById(Long id) {
        
        return false;
    }

    @Override
    public <S extends Tpreclamo> long count(Example<S> example) {
        
        return 0;
    }

    @Override
    public <S extends Tpreclamo> boolean exists(Example<S> example) {
        
        return false;
    }

    @Override
    public <S extends Tpreclamo> Page<S> findAll(Example<S> example, Pageable pageable) {
        
        return null;
    }

    @Override
    public <S extends Tpreclamo, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        
        return null;
    }

    @Override
    public <S extends Tpreclamo> Optional<S> findOne(Example<S> example) {
        
        return null;
    }

}
