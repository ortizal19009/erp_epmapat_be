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

import com.epmapat.erp_epmapat.modelo.Cajas;
import com.epmapat.erp_epmapat.repositorio.CajasR;

@Service
public class CajaServicio implements CajasR{
	
	@Autowired
	private CajasR dao;

	@Override
	public List<Cajas> findAll() {
		return dao.findAll();
	}

	@Override
	public Optional<Cajas> findById(Long id) {
		return dao.findById(id);
	}

	@Override
	public List<Cajas> findByCodigos(Long idptoemision, String codigo) {
		return dao.findByCodigos(idptoemision, codigo);
	}

	@Override
	public List<Cajas> findByDescri(String descripcion) {
		return dao.findByDescri( descripcion );
	}

	@Override
	public List<Cajas> findByIdptoemision(Long idptoemision) {
		return dao.findByIdptoemision(idptoemision);
	}

	//==========================================================
	@Override
	public List<Cajas> findAll(Sort sort) {
		return null;
	}

	@Override
	public List<Cajas> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends Cajas> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends Cajas> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends Cajas> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Cajas> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public Cajas getOne(Long id) {
		return null;
	}

	@Override
	public Cajas getById(Long id) {
		return null;
	}

	@Override
	public Cajas getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends Cajas> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends Cajas> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<Cajas> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Cajas> S save(S entity) {
		return dao.save(entity);
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
	public void delete(Cajas entity) {
		dao.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends Cajas> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends Cajas> Optional<S> findOne(Example<S> example) {
		return null;
	}

	@Override
	public <S extends Cajas> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Cajas> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends Cajas> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends Cajas, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public Cajas findCajaByIdUsuario(Long idusuario) {
		// TODO Auto-generated method stub
		return dao.findCajaByIdUsuario(idusuario);
	}

}
