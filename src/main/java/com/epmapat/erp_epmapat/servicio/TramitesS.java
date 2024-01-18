package com.epmapat.erp_epmapat.servicio;

import java.util.Date;
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

import com.epmapat.erp_epmapat.modelo.TramitesM;
import com.epmapat.erp_epmapat.repositorio.TramitesR;

@Service
public class TramitesS implements TramitesR{
	
	@Autowired
	private TramitesR tramitesR;

	@Override
	public List<TramitesM> findAll() {
		return tramitesR.findAll();
	}

	@Override
	public List<TramitesM> findByTpTramite(Long idTpTramite) {
		return tramitesR.findByTpTramite(idTpTramite);
	}

	@Override
	public List<TramitesM> findByDescripcion(String descripcion) {
		return tramitesR.findByDescripcion(descripcion);
	}

	@Override
	public List<TramitesM> findByfeccrea(Date feccrea) {
		return tramitesR.findByfeccrea(feccrea);
	}

	@Override
	public List<TramitesM> findAll(Sort sort) {
		return tramitesR.findAll(sort);
	}
	//Trámites por Cliente
	@Override
	public List<TramitesM> findByIdcliente(Long idcliente) {
		return tramitesR.findByIdcliente(idcliente);
	}

	@Override
	public List<TramitesM> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends TramitesM> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends TramitesM> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends TramitesM> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<TramitesM> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public TramitesM getOne(Long id) {
		return null;
	}

	@Override
	public TramitesM getById(Long id) {
		return null;
	}

	@Override
	public TramitesM getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends TramitesM> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends TramitesM> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<TramitesM> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends TramitesM> S save(S entity) {
		return tramitesR.save(entity);
	}

	@Override
	public Optional<TramitesM> findById(Long id) {
		return tramitesR.findById(id);
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
		tramitesR.deleteById(id);
	}

	@Override
	public void delete(TramitesM entity) {
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends TramitesM> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends TramitesM> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends TramitesM> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends TramitesM> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends TramitesM> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends TramitesM, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}
	
}
