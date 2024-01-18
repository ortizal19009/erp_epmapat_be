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

import com.epmapat.erp_epmapat.modelo.TipoTramite;
import com.epmapat.erp_epmapat.repositorio.TipoTramiteR;

@Service
public class TipoTramiteS implements TipoTramiteR{
	@Autowired
	private TipoTramiteR dao;

	@Override
	public List<TipoTramite> findAll() {
		return dao.findAll();
	}

	@Override
	public List<TipoTramite> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	@Override
	public List<TipoTramite> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends TipoTramite> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends TipoTramite> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends TipoTramite> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<TipoTramite> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public TipoTramite getOne(Long id) {
		
		return null;
	}

	@Override
	public TipoTramite getById(Long id) {
		
		return null;
	}

	@Override
	public TipoTramite getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends TipoTramite> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends TipoTramite> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<TipoTramite> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends TipoTramite> S save(S entity) {
		
		return dao.save(entity);
	}

	@Override
	public Optional<TipoTramite> findById(Long id) {
		
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
	public void delete(TipoTramite entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends TipoTramite> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends TipoTramite> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends TipoTramite> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends TipoTramite> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends TipoTramite> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends TipoTramite, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}
	
}
