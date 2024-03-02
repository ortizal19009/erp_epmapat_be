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

import com.epmapat.erp_epmapat.modelo.AboxSuspensionM;
import com.epmapat.erp_epmapat.repositorio.AboxSuspensionR;

@Service
public class AboxSuspensionS implements AboxSuspensionR{
	@Autowired
	private AboxSuspensionR aboxsuspensionR;

	@Override
	public List<AboxSuspensionM> findByIdsuspension(Long idsuspension) {
		return aboxsuspensionR.findByIdsuspension(idsuspension);
	}
	
	@SuppressWarnings("null")
	@Override
	public List<AboxSuspensionM> findAll() {
		return aboxsuspensionR.findAll();
	}

	@Override
	public List<AboxSuspensionM> findAll(Sort sort) {
		return null;
	}

	@Override
	public List<AboxSuspensionM> findAllById(Iterable<Long> ids) {
		return null;
	}

	@Override
	public <S extends AboxSuspensionM> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends AboxSuspensionM> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends AboxSuspensionM> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<AboxSuspensionM> entities) {
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public AboxSuspensionM getOne(Long id) {
		
		return null;
	}

	@Override
	public AboxSuspensionM getById(Long id) {
		
		return null;
	}

	@Override
	public AboxSuspensionM getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends AboxSuspensionM> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends AboxSuspensionM> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}

	@Override
	public Page<AboxSuspensionM> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends AboxSuspensionM> S save(S entity) {
		
		return aboxsuspensionR.save(entity);
	}

	@Override
	public Optional<AboxSuspensionM> findById(Long id) {
		
		return aboxsuspensionR.findById(id);
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
		
		aboxsuspensionR.deleteById(id);
	}

	@Override
	public void delete(AboxSuspensionM entity) {
		
		aboxsuspensionR.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends AboxSuspensionM> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends AboxSuspensionM> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends AboxSuspensionM> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends AboxSuspensionM> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends AboxSuspensionM> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends AboxSuspensionM, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}

}
