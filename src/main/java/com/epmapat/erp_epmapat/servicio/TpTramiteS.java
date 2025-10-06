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

import com.epmapat.erp_epmapat.modelo.TpTramiteM;
import com.epmapat.erp_epmapat.repositorio.TpTramiteR;

@Service
public class TpTramiteS implements TpTramiteR{
	
	@Autowired
	private TpTramiteR dao;

	@Override
	public List<TpTramiteM> findAll() {
		return dao.findAll();
	}

	@Override
	public List<TpTramiteM> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	@Override
	public List<TpTramiteM> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends TpTramiteM> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends TpTramiteM> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends TpTramiteM> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<TpTramiteM> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public TpTramiteM getOne(Long id) {
		
		return null;
	}

	@Override
	public TpTramiteM getById(Long id) {
		
		return null;
	}

	@Override
	public TpTramiteM getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends TpTramiteM> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends TpTramiteM> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<TpTramiteM> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends TpTramiteM> S save(S entity) {
		
		return dao.save(entity);
	}

	@Override
	public Optional<TpTramiteM> findById(Long id) {
		
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
	public void delete(TpTramiteM entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends TpTramiteM> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends TpTramiteM> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends TpTramiteM> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends TpTramiteM> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends TpTramiteM> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends TpTramiteM, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}
	

}
