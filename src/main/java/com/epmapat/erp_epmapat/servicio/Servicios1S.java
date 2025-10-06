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

import com.epmapat.erp_epmapat.modelo.Servicios1M;
import com.epmapat.erp_epmapat.repositorio.Servicios1R;

@Service
public class Servicios1S implements Servicios1R{

	@Autowired
	private Servicios1R servicios1R;

	@Override
	public List<Servicios1M> findAll() {
		return servicios1R.findAll();
	}

	@Override
	public List<Servicios1M> findAll(Sort sort) {
		return servicios1R.findAll(sort);
	}

	@Override
	public List<Servicios1M> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends Servicios1M> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends Servicios1M> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends Servicios1M> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Servicios1M> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public Servicios1M getOne(Long id) {
		
		return null;
	}

	@Override
	public Servicios1M getById(Long id) {
		
		return null;
	}

	@Override
	public Servicios1M getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends Servicios1M> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends Servicios1M> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<Servicios1M> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Servicios1M> S save(S entity) {
		
		return servicios1R.save(entity);
	}

	@Override
	public Optional<Servicios1M> findById(Long id) {
		
		return servicios1R.findById(id);
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
		
		servicios1R.deleteById(id);
	}

	@Override
	public void delete(Servicios1M entity) {
		
		servicios1R.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends Servicios1M> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends Servicios1M> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends Servicios1M> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Servicios1M> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends Servicios1M> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends Servicios1M, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}

	@Override
	public List<Servicios1M> findByIdModulos(Long idmodulo) {
		
		return servicios1R.findByIdModulos(idmodulo);
	}
}
