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

import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TransaciR;

@Service
public class TransaciServicio implements TransaciR {

	@Autowired
	private TransaciR dao;

	@Override
	public List<Transaci> findAll() {
		return dao.findAll();
	}

	//Cuenta tiene Transacciones
	@Override
	public boolean tieneTransaci(String codcue) {
		return dao.tieneTransaci(codcue);
	}

	//Asiento tiene Transacciones
	@Override
	public boolean existsByIdasiento(Long idasiento) {
		return dao.existsByIdasiento( idasiento );
	}

	@Override
	public List<Transaci> findAll(Sort sort) {
		
		return null;
	}

	@Override
	public List<Transaci> findAllById(Iterable<Long> ids) {
		
		return null;
	}

	@Override
	public <S extends Transaci> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		

	}

	@Override
	public <S extends Transaci> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends Transaci> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Transaci> entities) {
		

	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		

	}

	@Override
	public void deleteAllInBatch() {
		

	}

	@Override
	public Transaci getOne(Long id) {
		
		return null;
	}

	@Override
	public Transaci getById(Long id) {
		
		return null;
	}

	@Override
	public Transaci getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends Transaci> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends Transaci> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<Transaci> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Transaci> S save(S entity) {
		return dao.save(entity);
	}

	@Override
	public Optional<Transaci> findById(Long id) {
		
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
	public void delete(Transaci entity) {

	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {

	}

	@Override
	public void deleteAll(Iterable<? extends Transaci> entities) {

	}

	@Override
	public void deleteAll() {

	}

	@Override
	public <S extends Transaci> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends Transaci> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Transaci> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends Transaci> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends Transaci, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public List<Transaci> findControlBancos(Long idcuenta, Long mes) {
		return dao.findControlBancos(idcuenta, mes);
	}

	@Override
	public List<Transaci> findTransaci(Long idasiento) {
		return dao.findTransaci(idasiento);
	}

}
