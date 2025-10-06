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

import com.epmapat.erp_epmapat.modelo.RubroAdicionalM;
import com.epmapat.erp_epmapat.repositorio.RubroAdicionalR;

@Service
public class RubroAdicionalS implements RubroAdicionalR {
	
	@Autowired
	private RubroAdicionalR rubroadicionalR;

	@Override
	public List<RubroAdicionalM> findAll() {
		return rubroadicionalR.findAll();
	}

	@Override
	public List<RubroAdicionalM> findAll(Sort sort) {
		return rubroadicionalR.findAll(sort);
	}

	@Override
	public List<RubroAdicionalM> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends RubroAdicionalM> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends RubroAdicionalM> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends RubroAdicionalM> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<RubroAdicionalM> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public RubroAdicionalM getOne(Long id) {
		return null;
	}

	@Override
	public RubroAdicionalM getById(Long id) {
		return null;
	}

	@Override
	public RubroAdicionalM getReferenceById(Long id) {
		return null;
	}

	@Override
	public <S extends RubroAdicionalM> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends RubroAdicionalM> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public Page<RubroAdicionalM> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends RubroAdicionalM> S save(S entity) {
		return rubroadicionalR.save(entity);
	}

	@Override
	public Optional<RubroAdicionalM> findById(Long id) {
		return rubroadicionalR.findById(id);
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
		rubroadicionalR.deleteById(id);
	}

	@Override
	public void delete(RubroAdicionalM entity) {
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	@Override
	public void deleteAll(Iterable<? extends RubroAdicionalM> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public <S extends RubroAdicionalM> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends RubroAdicionalM> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends RubroAdicionalM> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends RubroAdicionalM> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends RubroAdicionalM, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public List<RubroAdicionalM> findByIdTpTramtie(Long idtptramite) {
		return rubroadicionalR.findByIdTpTramtie(idtptramite);
	}
}
