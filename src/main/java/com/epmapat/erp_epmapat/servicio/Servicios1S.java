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
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends Servicios1M> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Servicios1M> List<S> saveAllAndFlush(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Servicios1M> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Servicios1M getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servicios1M getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servicios1M getReferenceById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Servicios1M> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Servicios1M> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Servicios1M> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Servicios1M> S save(S entity) {
		// TODO Auto-generated method stub
		return servicios1R.save(entity);
	}

	@Override
	public Optional<Servicios1M> findById(Long id) {
		// TODO Auto-generated method stub
		return servicios1R.findById(id);
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		servicios1R.deleteById(id);
	}

	@Override
	public void delete(Servicios1M entity) {
		// TODO Auto-generated method stub
		servicios1R.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends Servicios1M> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends Servicios1M> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public <S extends Servicios1M> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Servicios1M> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends Servicios1M> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <S extends Servicios1M, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Servicios1M> findByIdModulos(Long idmodulo) {
		// TODO Auto-generated method stub
		return servicios1R.findByIdModulos(idmodulo);
	}
}
