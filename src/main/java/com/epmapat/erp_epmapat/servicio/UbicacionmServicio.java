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

import com.epmapat.erp_epmapat.modelo.Ubicacionm;
import com.epmapat.erp_epmapat.repositorio.UbicacionmR;

@Service
public class UbicacionmServicio implements UbicacionmR{

    @Autowired
    UbicacionmR dao;

    @Override
    public <S extends Ubicacionm> S save(S entity) {
        return dao.save(entity);
    }

    @Override
    public List<Ubicacionm> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Ubicacionm> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);                
    }

// ===================================================================
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        
        
    }

    @Override
    public void deleteAllInBatch() {
        
        
    }

    @Override
    public void deleteAllInBatch(Iterable<Ubicacionm> entities) {
        
        
    }

    @Override
    public List<Ubicacionm> findAll(Sort sort) {
        
        return null;
    }

    @Override
    public <S extends Ubicacionm> List<S> findAll(Example<S> example) {
        
        return null;
    }

    @Override
    public <S extends Ubicacionm> List<S> findAll(Example<S> example, Sort sort) {
        
        return null;
    }

    @Override
    public List<Ubicacionm> findAllById(Iterable<Long> ids) {
        
        return null;
    }

    @Override
    public void flush() {
        
        
    }

    @Override
    public Ubicacionm getById(Long id) {
        
        return null;
    }

    @Override
    public Ubicacionm getOne(Long id) {
        
        return null;
    }

    @Override
    public Ubicacionm getReferenceById(Long id) {
        
        return null;
    }

    @Override
    public <S extends Ubicacionm> List<S> saveAll(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Ubicacionm> List<S> saveAllAndFlush(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Ubicacionm> S saveAndFlush(S entity) {
        
        return null;
    }

    @Override
    public Page<Ubicacionm> findAll(Pageable pageable) {
        
        return null;
    }

    @Override
    public long count() {
        
        return 0;
    }

    @Override
    public void delete(Ubicacionm entity) {
        
        
    }

    @Override
    public void deleteAll() {
        
        
    }

    @Override
    public void deleteAll(Iterable<? extends Ubicacionm> entities) {
        
        
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        
        
    }

    @Override
    public boolean existsById(Long id) {
        
        return false;
    }

    @Override
    public <S extends Ubicacionm> long count(Example<S> example) {
        
        return 0;
    }

    @Override
    public <S extends Ubicacionm> boolean exists(Example<S> example) {
        
        return false;
    }

    @Override
    public <S extends Ubicacionm> Page<S> findAll(Example<S> example, Pageable pageable) {
        
        return null;
    }

    @Override
    public <S extends Ubicacionm, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        
        return null;
    }

    @Override
    public <S extends Ubicacionm> Optional<S> findOne(Example<S> example) {
        
        return null;
    }


}
