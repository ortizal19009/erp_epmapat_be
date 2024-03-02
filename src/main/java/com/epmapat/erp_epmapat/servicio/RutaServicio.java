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

import com.epmapat.erp_epmapat.modelo.Rutas;
import com.epmapat.erp_epmapat.repositorio.RutasR;

@Service
public class RutaServicio implements RutasR {
	@Autowired
	private RutasR dao;

	@Override
	public List<Rutas> findAll() {
		return dao.findAll();
	}

	@Override
	public List<Rutas> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	// @Override
	// public List<Rutas> OrderByOrdenAsc() {
	// return dao.findAll();
	// }

	// ============================================================================

	@Override
	public List<Rutas> findAllById(Iterable<Long> ids) {
		
		return null;
	}

	@Override
	public <S extends Rutas> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		

	}

	@Override
	public <S extends Rutas> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends Rutas> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Rutas> entities) {
		

	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		

	}

	@Override
	public void deleteAllInBatch() {
		

	}

	@Override
	public Rutas getOne(Long id) {
		
		return null;
	}

	@Override
	public Rutas getById(Long id) {
		
		return null;
	}

	@Override
	public Rutas getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends Rutas> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends Rutas> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<Rutas> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Rutas> S save(S entity) {
		
		return dao.save(entity);
	}

	@Override
	public Optional<Rutas> findById(Long id) {
		
		return dao.findById(id);
	}

	@Override
	public boolean existsById(Long id) {
		
		return false;
	}

	@Override
	public long count() {
		
		return 0;
	}

	@Override
	public void deleteById(Long id) {
		
		dao.deleteById(id);
	}

	@Override
	public void delete(Rutas entity) {
		
		dao.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
	}

	@Override
	public void deleteAll(Iterable<? extends Rutas> entities) {
		
	}

	@Override
	public void deleteAll() {
		
	}

	@Override
	public <S extends Rutas> Optional<S> findOne(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends Rutas> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Rutas> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends Rutas> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends Rutas, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}

	// Valida Identificacion
	@Override
	public boolean valCodigo(String codigo) {
		return dao.valCodigo(codigo);
	}
	
}
