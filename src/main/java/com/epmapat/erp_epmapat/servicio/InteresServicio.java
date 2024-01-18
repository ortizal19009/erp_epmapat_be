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

import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.repositorio.InteresesR;

@Service
public class InteresServicio implements InteresesR{
	
	@Autowired
	private InteresesR dao;

	@Override
	public List<Intereses> findAll() {
		return dao.findAll();
	}

	@Override
	public List<Intereses> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	@Override
	public List<Intereses> findByAnioMes(Number anio, Number mes) {
		return dao.findByAnioMes( anio, mes);
	}

	@Override
	public List<Intereses> findUltimo() {
		return dao.findUltimo();
	}

	@Override
	public List<Intereses> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends Intereses> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends Intereses> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends Intereses> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Intereses> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public Intereses getOne(Long id) {
		return null;
	}

	@Override
	public Intereses getById(Long id) {
		return null;
	}

	@Override
	public Intereses getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends Intereses> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends Intereses> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<Intereses> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Intereses> S save(S entity) {
		return dao.save(entity);
	}

	@Override
	public Optional<Intereses> findById(Long id) {
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
	public void delete(Intereses entity) {
		dao.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends Intereses> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends Intereses> Optional<S> findOne(Example<S> example) {
		return null;
	}

	@Override
	public <S extends Intereses> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Intereses> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends Intereses> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends Intereses, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

}
