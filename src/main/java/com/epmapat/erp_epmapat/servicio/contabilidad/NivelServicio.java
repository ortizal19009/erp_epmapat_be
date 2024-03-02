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

import com.epmapat.erp_epmapat.modelo.contabilidad.Niveles;
import com.epmapat.erp_epmapat.repositorio.contabilidad.NivelesR;

@Service
public class NivelServicio implements NivelesR{
	
	@Autowired
	private NivelesR nivelesR;

	@Override
	public List<Niveles> findAll() {
		
		return nivelesR.findAll();
	}

	@Override
	public List<Niveles> findAll(Sort sort) {
		
		return null;
	}

	@Override
	public List<Niveles> findAllById(Iterable<Long> ids) {
		
		return null;
	}

	@Override
	public <S extends Niveles> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends Niveles> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends Niveles> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Niveles> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public Niveles getOne(Long id) {
		
		return null;
	}

	@Override
	public Niveles getById(Long id) {
		
		return null;
	}

	@Override
	public Niveles getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends Niveles> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends Niveles> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<Niveles> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Niveles> S save(S entity) {
		
		return null;
	}

	@Override
	public Optional<Niveles> findById(Long id) {
		
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
	public void delete(Niveles entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends Niveles> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends Niveles> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends Niveles> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Niveles> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends Niveles> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends Niveles, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}
}
