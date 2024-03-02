package com.epmapat.erp_epmapat.servicio.contabilidad;

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

import com.epmapat.erp_epmapat.modelo.contabilidad.ConciliaBan;
import com.epmapat.erp_epmapat.repositorio.contabilidad.ConciliaBanR;

@Service
public class ConciliaBanS implements ConciliaBanR{
	@Autowired
	public ConciliaBanR conciliabanR;

	@Override
	public List<ConciliaBan> findAll() {
		
		return conciliabanR.findAll();
	}

	@Override
	public List<ConciliaBan> findAll(Sort sort) {
		
		return null;
	}

	@Override
	public List<ConciliaBan> findAllById(Iterable<Long> ids) {
		
		return null;
	}

	@Override
	public <S extends ConciliaBan> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends ConciliaBan> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends ConciliaBan> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<ConciliaBan> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public ConciliaBan getOne(Long id) {
		
		return null;
	}

	@Override
	public ConciliaBan getById(Long id) {
		
		return null;
	}

	@Override
	public ConciliaBan getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends ConciliaBan> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends ConciliaBan> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<ConciliaBan> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends ConciliaBan> S save(S entity) {
		
		return conciliabanR.save(entity);
	}

	@Override
	public Optional<ConciliaBan> findById(Long id) {
		
		return Optional.empty();
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
		
		
	}

	@Override
	public void delete(ConciliaBan entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends ConciliaBan> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends ConciliaBan> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends ConciliaBan> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends ConciliaBan> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends ConciliaBan> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends ConciliaBan, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	} 
	
	
}
