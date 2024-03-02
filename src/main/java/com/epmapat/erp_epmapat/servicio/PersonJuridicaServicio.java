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

import com.epmapat.erp_epmapat.modelo.PersonJuridica;
import com.epmapat.erp_epmapat.repositorio.PersonJuridicaR;

@Service
public class PersonJuridicaServicio implements PersonJuridicaR{
	
	@Autowired
	PersonJuridicaR dao;

	@Override
	public List<PersonJuridica> findAll() {
		return dao.findAll();
	}
	
    @Override
    public <S extends PersonJuridica> S save(S entity) {
        return dao.save(entity);
    }

	@Override
	public List<PersonJuridica> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	@Override
	public List<PersonJuridica> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends PersonJuridica> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends PersonJuridica> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends PersonJuridica> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<PersonJuridica> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public PersonJuridica getOne(Long id) {
		
		return null;
	}

	@Override
	public PersonJuridica getById(Long id) {
		
		return null;
	}

	@Override
	public PersonJuridica getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends PersonJuridica> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends PersonJuridica> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Optional<PersonJuridica> findById(Long id) {
		
		return null;
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
	public void delete(PersonJuridica entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends PersonJuridica> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends PersonJuridica> Optional<S> findOne(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends PersonJuridica> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends PersonJuridica> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends PersonJuridica> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends PersonJuridica, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}

	@Override
	public Page<PersonJuridica> findAll(Pageable pageable) {
		
		return null;
	}
}
