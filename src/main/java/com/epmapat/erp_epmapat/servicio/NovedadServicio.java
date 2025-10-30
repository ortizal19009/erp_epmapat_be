package com.epmapat.erp_epmapat.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Novedad;
import com.epmapat.erp_epmapat.repositorio.NovedadR;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class NovedadServicio implements NovedadR {

    @Autowired
    NovedadR dao;

    @Override
    public List<Novedad> findByDescri(String descripcion) {
        return dao.findByDescri(descripcion) ;
    }

    @Override
    public List<Novedad> findAll() {
        return dao.findAll();
    }
    
    @Override
    public <S extends Novedad> S save(S entity) {
        return dao.save(entity);
    }

    @Override
    public Optional<Novedad> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
// =======================================================================================
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        
    }

    @Override
    public void deleteAllInBatch() {
        
    }

    @Override
    public void deleteAllInBatch(Iterable<Novedad> entities) {
        
    }

    @Override
    public List<Novedad> findAll(Sort sort) {
        
        return null;
    }

    @Override
    public <S extends Novedad> List<S> findAll(Example<S> example) {
        
        return null;
    }

    @Override
    public <S extends Novedad> List<S> findAll(Example<S> example, Sort sort) {
        
        return null;
    }

    @Override
    public List<Novedad> findAllById(Iterable<Long> ids) {
        
        return null;
    }

    @Override
    public void flush() {
        
    }

    @Override
    public Novedad getById(Long id) {
        
        return null;
    }

    @Override
    public Novedad getOne(Long id) {
        
        return null;
    }

    @Override
    public Novedad getReferenceById(Long id) {
        
        return null;
    }

    @Override
    public <S extends Novedad> List<S> saveAll(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Novedad> List<S> saveAllAndFlush(Iterable<S> entities) {
        
        return null;
    }

    @Override
    public <S extends Novedad> S saveAndFlush(S entity) {
        
        return null;
    }

    @Override
    public Page<Novedad> findAll(Pageable pageable) {
        
        return null;
    }

    @Override
    public long count() {
        
        return 0;
    }

    @Override
    public void delete(Novedad entity) {
        
    }

    @Override
    public void deleteAll() {
        
    }

    @Override
    public void deleteAll(Iterable<? extends Novedad> entities) {
        
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        
    }

    @Override
    public boolean existsById(Long id) {
        
        return false;
    }

    @Override
    public <S extends Novedad> long count(Example<S> example) {
        
        return 0;
    }

    @Override
    public <S extends Novedad> boolean exists(Example<S> example) {
        
        return false;
    }

    @Override
    public <S extends Novedad> Page<S> findAll(Example<S> example, Pageable pageable) {
        
        return null;
    }

    @Override
    public <S extends Novedad, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        
        return null;
    }

    @Override
    public <S extends Novedad> Optional<S> findOne(Example<S> example) {
        
        return null;
    }

	@Override
	public List<Novedad> findByEstado(Long estado) {
		
		return dao.findByEstado(estado);
	}

}