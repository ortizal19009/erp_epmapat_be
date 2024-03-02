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

import com.epmapat.erp_epmapat.modelo.Tramites1M;
import com.epmapat.erp_epmapat.repositorio.Tramites1R;

@Service
public class Tramites1S implements Tramites1R{
	
	@Autowired
	private Tramites1R tramites1R;

	@Override
	public List<Tramites1M> findAll() {
		
		return tramites1R.findAll();
	}

	@Override
	public List<Tramites1M> findAll(Sort sort) {
		
		return tramites1R.findAll(sort);
	}

	@Override
	public List<Tramites1M> findAllById(Iterable<Long> ids) {
		
		return null;
	}

	@Override
	public <S extends Tramites1M> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends Tramites1M> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends Tramites1M> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Tramites1M> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public Tramites1M getOne(Long id) {
		
		return null;
	}

	@Override
	public Tramites1M getById(Long id) {
		
		return null;
	}

	@Override
	public Tramites1M getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends Tramites1M> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends Tramites1M> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<Tramites1M> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Tramites1M> S save(S entity) {
		
		return tramites1R.save(entity);
	}

	@Override
	public Optional<Tramites1M> findById(Long id) {
		
		return tramites1R.findById(id);
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
		
		tramites1R.deleteById(id);
	}

	@Override
	public void delete(Tramites1M entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends Tramites1M> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends Tramites1M> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends Tramites1M> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Tramites1M> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends Tramites1M> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends Tramites1M, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}

}
