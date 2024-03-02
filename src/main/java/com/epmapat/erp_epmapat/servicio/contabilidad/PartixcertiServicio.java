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

import com.epmapat.erp_epmapat.modelo.contabilidad.Partixcerti;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PartixcertiR;

@Service
public class PartixcertiServicio implements PartixcertiR{

	@Autowired
	private PartixcertiR dao;

	@Override
	public List<Partixcerti> findAll() {
		return dao.findAll();
	}

	@Override
	public List<Partixcerti> findAll(Sort sort) {
		return null;
	}

	@Override
	public List<Partixcerti> findAllById(Iterable<Long> ids) {
		
		return null;
	}

	@Override
	public <S extends Partixcerti> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends Partixcerti> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends Partixcerti> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Partixcerti> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public Partixcerti getOne(Long id) {
		
		return null;
	}

	@Override
	public Partixcerti getById(Long id) {
		
		return null;
	}

	@Override
	public Partixcerti getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends Partixcerti> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends Partixcerti> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<Partixcerti> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Partixcerti> S save(S entity) {
		
		return dao.save(entity);
	}

	@Override
	public Optional<Partixcerti> findById(Long id) {
		
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
		
		
	}

	@Override
	public void delete(Partixcerti entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends Partixcerti> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends Partixcerti> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends Partixcerti> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Partixcerti> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends Partixcerti> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends Partixcerti, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}

	@Override
	public List<Partixcerti> findByIdcerti(Long idcerti) {
		
		return dao.findByIdcerti(idcerti);
	}
	
}
