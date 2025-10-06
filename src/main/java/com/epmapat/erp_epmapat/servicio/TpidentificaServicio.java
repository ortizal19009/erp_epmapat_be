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

import com.epmapat.erp_epmapat.modelo.Tpidentifica;
import com.epmapat.erp_epmapat.repositorio.TpidentificaR;

@Service
public class TpidentificaServicio implements TpidentificaR {

    @Autowired
    TpidentificaR dao;

    @Override
    public <S extends Tpidentifica> S save(S entity) {
        return dao.save(entity);
    }

    @Override
    public List<Tpidentifica> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Tpidentifica> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    // ======================================================================================
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        

    }

    @Override
    public void deleteAllInBatch() {
        

    }

    @Override
    public void deleteAllInBatch(Iterable<Tpidentifica> entities) {
        

    }

    @Override
    public List<Tpidentifica> findAll(Sort sort) {
        
        return null;
    }

    @Override
    public <S extends Tpidentifica> List<S> findAll(Example<S> example) {
        
        return null;
    }

    @Override
    public <S extends Tpidentifica> List<S> findAll(Example<S> example, Sort sort) {
        
        return null;
    }

    @Override
    public List<Tpidentifica> findAllById(Iterable<Long> ids) {
        
        return null;
    }

    @Override
    public void flush() {
        

    }

    @Override
    public Tpidentifica getById(Long id) {
        
        return null;
    }

    @Override
    public Tpidentifica getOne(Long id) {
        
        return null;
    }

    @Override
    public Tpidentifica getReferenceById(Long id) {
        
        return null;
    }

    @Override
    public <S extends Tpidentifica> List<S> saveAll(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Tpidentifica> List<S> saveAllAndFlush(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Tpidentifica> S saveAndFlush(S entity) {
        
        return null;
    }

    @Override
    public Page<Tpidentifica> findAll(Pageable pageable) {
        
        return null;
    }

    @Override
    public long count() {
        
        return 0;
    }

    @Override
    public void deleteAll() {
        

    }

    @Override
    public void deleteAll(Iterable<? extends Tpidentifica> entities) {
        

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        

    }

    @Override
    public boolean existsById(Long id) {
        
        return false;
    }

    @Override
    public <S extends Tpidentifica> long count(Example<S> example) {
        
        return 0;
    }

    @Override
    public <S extends Tpidentifica> boolean exists(Example<S> example) {
        
        return false;
    }

    @Override
    public <S extends Tpidentifica> Page<S> findAll(Example<S> example, Pageable pageable) {
        
        return null;
    }

    @Override
    public <S extends Tpidentifica, R> R findBy(Example<S> example,
            Function<FetchableFluentQuery<S>, R> queryFunction) {
        
        return null;
    }

    @Override
    public <S extends Tpidentifica> Optional<S> findOne(Example<S> example) {
        
        return null;
    }

    @Override
    public void delete(Tpidentifica entity) {
        
        
    }

}
