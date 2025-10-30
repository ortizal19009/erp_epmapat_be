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

import com.epmapat.erp_epmapat.modelo.CtramitesM;
import com.epmapat.erp_epmapat.repositorio.CtramitesR;

@Service
public class CtramitesS implements CtramitesR{
	
	@Autowired
	private CtramitesR tramitesR;

	@Override
	public List<CtramitesM> findAll() {
		return tramitesR.findAll();
	}

	@Override
	public List<CtramitesM> findByTpTramite(Long idTpTramite) {
		return tramitesR.findByTpTramite(idTpTramite);
	}

	@Override
	public List<CtramitesM> findByDescripcion(String descripcion) {
		return tramitesR.findByDescripcion(descripcion);
	}

	@Override
	public List<CtramitesM> findByfeccrea(Date feccrea) {
		return tramitesR.findByfeccrea(feccrea);
	}

	@Override
	public List<CtramitesM> findAll(Sort sort) {
		return tramitesR.findAll(sort);
	}
	//Trámites por Cliente
	@Override
	public List<CtramitesM> findByIdcliente(Long idcliente) {
		return tramitesR.findByIdcliente(idcliente);
	}

	@Override
	public List<CtramitesM> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends CtramitesM> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends CtramitesM> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends CtramitesM> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<CtramitesM> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public CtramitesM getOne(Long id) {
		return null;
	}

	@Override
	public CtramitesM getById(Long id) {
		return null;
	}

	@Override
	public CtramitesM getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends CtramitesM> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends CtramitesM> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<CtramitesM> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends CtramitesM> S save(S entity) {
		return tramitesR.save(entity);
	}

	@Override
	public Optional<CtramitesM> findById(Long id) {
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
	public void delete(CtramitesM entity) {
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends CtramitesM> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends CtramitesM> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends CtramitesM> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends CtramitesM> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends CtramitesM> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends CtramitesM, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}
	
}
