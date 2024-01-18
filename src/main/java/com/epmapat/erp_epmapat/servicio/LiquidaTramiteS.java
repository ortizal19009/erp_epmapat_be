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

import com.epmapat.erp_epmapat.modelo.LiquidaTramite;
import com.epmapat.erp_epmapat.repositorio.LiquidaTramiteR;

@Service
public class LiquidaTramiteS implements LiquidaTramiteR{

	@Autowired
	private LiquidaTramiteR liquidatramiteR;

	@Override
	public List<LiquidaTramite> findAll() {
		return liquidatramiteR.findAll();
	}

	@Override
	public List<LiquidaTramite> findAll(Sort sort) {
		return liquidatramiteR.findAll(sort);
	}

	@Override
	public List<LiquidaTramite> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends LiquidaTramite> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends LiquidaTramite> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends LiquidaTramite> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<LiquidaTramite> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public LiquidaTramite getOne(Long id) {
		return null;
	}

	@Override
	public LiquidaTramite getById(Long id) {
		return null;
	}

	@Override
	public LiquidaTramite getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends LiquidaTramite> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends LiquidaTramite> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<LiquidaTramite> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends LiquidaTramite> S save(S entity) {
		return liquidatramiteR.save(entity);
	}

	@Override
	public Optional<LiquidaTramite> findById(Long id) {
		
		return liquidatramiteR.findById(id);
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
		liquidatramiteR.deleteById(id);
	}

	@Override
	public void delete(LiquidaTramite entity) {
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends LiquidaTramite> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends LiquidaTramite> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends LiquidaTramite> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends LiquidaTramite> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends LiquidaTramite> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends LiquidaTramite, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public List<LiquidaTramite> findByIdTramite(Long idtramite) {
		return liquidatramiteR.findByIdTramite(idtramite);
	}

}
