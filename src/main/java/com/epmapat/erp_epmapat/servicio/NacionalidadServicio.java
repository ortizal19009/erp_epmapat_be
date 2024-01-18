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

import com.epmapat.erp_epmapat.modelo.Nacionalidad;
import com.epmapat.erp_epmapat.repositorio.NacionalidadR;

@Service
public class NacionalidadServicio implements NacionalidadR{
	
    @Autowired
    private NacionalidadR dao;

	@Override
	public List<Nacionalidad> findAll() {
		return dao.findAll();
	}

	@Override
	public List<Nacionalidad> findAll(Sort sort) {
		return dao.findAll(sort);
	}

    @Override
	public <S extends Nacionalidad> S save(S entity) {
		return dao.save(entity);
	}

	@Override
	public Optional<Nacionalidad> findById(Long id) {
		return dao.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Nacionalidad> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Nacionalidad> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> saveAllAndFlush(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Nacionalidad> entities) {
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
	public Nacionalidad getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Nacionalidad getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Nacionalidad getReferenceById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Nacionalidad> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
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
	public void delete(Nacionalidad entity) {
		// TODO Auto-generated method stub
		dao.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends Nacionalidad> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends Nacionalidad> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Nacionalidad> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Nacionalidad> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends Nacionalidad> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <S extends Nacionalidad, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public void deleteByIdQ(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Nacionalidad> used(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	// public List<NacionalidadM> findByDescription(String descripcion) {
	// 	// TODO Auto-generated method stub
	// 	return null;
	// }
	@Override
	public List<Nacionalidad> findByDescription(String nombre){
		return dao.findByDescription(nombre);
	}

}
