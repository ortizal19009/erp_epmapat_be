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

import com.epmapat.erp_epmapat.modelo.TramiteNuevo;
import com.epmapat.erp_epmapat.repositorio.TramiteNuevoR;

@Service
public class TramiteNuevoS implements TramiteNuevoR{

	@Autowired
	private TramiteNuevoR dao;

	@Override
	public List<TramiteNuevo> findAll() {
		return dao.findAll();
	}

	@Override
	public List<TramiteNuevo> findAll(Sort sort) {
		
		return dao.findAll(sort);
	}

	@Override
	public List<TramiteNuevo> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends TramiteNuevo> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends TramiteNuevo> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends TramiteNuevo> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<TramiteNuevo> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public TramiteNuevo getOne(Long id) {
		return null;
	}

	@Override
	public TramiteNuevo getById(Long id) {
		return null;
	}

	@Override
	public TramiteNuevo getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends TramiteNuevo> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends TramiteNuevo> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<TramiteNuevo> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends TramiteNuevo> S save(S entity) {
		return dao.save(entity);
	}

	@Override
	public Optional<TramiteNuevo> findById(Long id) {
		
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
	public void delete(TramiteNuevo entity) {
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends TramiteNuevo> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends TramiteNuevo> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends TramiteNuevo> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends TramiteNuevo> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends TramiteNuevo> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends TramiteNuevo, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public List<TramiteNuevo> findByIdAguaTramite(Long idaguatramite) {
		return dao.findByIdAguaTramite(idaguatramite);
	}
}
