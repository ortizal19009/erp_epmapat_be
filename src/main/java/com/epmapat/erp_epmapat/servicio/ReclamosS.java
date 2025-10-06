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

import com.epmapat.erp_epmapat.modelo.Reclamos;
import com.epmapat.erp_epmapat.repositorio.ReclamosR;

@Service
public class ReclamosS implements ReclamosR{
	
	@Autowired
	private ReclamosR reclamosR;

	@Override
	public List<Reclamos> findAll() {
		return reclamosR.findAll();
	}

	@Override
	public List<Reclamos> findAll(Sort sort) {
		return null;
	}

	@Override
	public List<Reclamos> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends Reclamos> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends Reclamos> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends Reclamos> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Reclamos> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public Reclamos getOne(Long id) {
		return null;
	}

	@Override
	public Reclamos getById(Long id) {
		return null;
	}

	@Override
	public Reclamos getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends Reclamos> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends Reclamos> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<Reclamos> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Reclamos> S save(S entity) {
		return reclamosR.save(entity);
	}

	@Override
	public Optional<Reclamos> findById(Long id) {
		return reclamosR.findById(id);
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
		reclamosR.deleteById(id);
	}

	@Override
	public void delete(Reclamos entity) {
		reclamosR.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends Reclamos> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends Reclamos> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends Reclamos> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Reclamos> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends Reclamos> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends Reclamos, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}
	
}
