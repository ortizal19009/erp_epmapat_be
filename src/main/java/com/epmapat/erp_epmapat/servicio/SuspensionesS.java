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

import com.epmapat.erp_epmapat.modelo.SuspensionesM;
import com.epmapat.erp_epmapat.repositorio.SuspensionesR;

@Service
public class SuspensionesS implements SuspensionesR{
	@Autowired
	private SuspensionesR suspensionesR;

	@Override
	public List<SuspensionesM> findAll() {
		
		return suspensionesR.findAll();
	}

	@Override
	public List<SuspensionesM> findAll(Sort sort) {
		
		return null;
	}

	@Override
	public List<SuspensionesM> findAllById(Iterable<Long> ids) {
		
		return null;
	}

	@Override
	public <S extends SuspensionesM> List<S> saveAll(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void flush() {
		
		
	}

	@Override
	public <S extends SuspensionesM> S saveAndFlush(S entity) {
		
		return null;
	}

	@Override
	public <S extends SuspensionesM> List<S> saveAllAndFlush(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<SuspensionesM> entities) {
		
		
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		
		
	}

	@Override
	public void deleteAllInBatch() {
		
		
	}

	@Override
	public SuspensionesM getOne(Long id) {
		
		return null;
	}

	@Override
	public SuspensionesM getById(Long id) {
		
		return null;
	}

	@Override
	public SuspensionesM getReferenceById(Long id) {
		
		return null;
	}

	@Override
	public <S extends SuspensionesM> List<S> findAll(Example<S> example) {
		
		return null;
	}

	@Override
	public <S extends SuspensionesM> List<S> findAll(Example<S> example, Sort sort) {
		
		return null;
	}
	
	@Override
	public Page<SuspensionesM> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends SuspensionesM> S save(S entity) {
		
		return suspensionesR.save(entity);
	}

	@Override
	public Optional<SuspensionesM> findById(Long id) {
		
		return suspensionesR.findById(id);
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
		
		suspensionesR.deleteById(id);
	}
	
	@Override
	public void delete(SuspensionesM entity) {
		
		
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		
		
	}

	@Override
	public void deleteAll(Iterable<? extends SuspensionesM> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public <S extends SuspensionesM> Optional<S> findOne(Example<S> example) {
		
		return Optional.empty();
	}

	@Override
	public <S extends SuspensionesM> Page<S> findAll(Example<S> example, Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends SuspensionesM> long count(Example<S> example) {
		
		return 0;
	}

	@Override
	public <S extends SuspensionesM> boolean exists(Example<S> example) {
		
		return false;
	}

	@Override
	public <S extends SuspensionesM, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {
		
		return null;
	}
	@Override
	public SuspensionesM findFirstByOrderByIdsuspensionDesc() {
		
		return suspensionesR.findFirstByOrderByIdsuspensionDesc();
	}
	@Override
	public List<SuspensionesM> findHabilitaciones(){
		return suspensionesR.findHabilitaciones();
	}

	@Override
	public List<SuspensionesM> findByFecha(Date desde, Date hasta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SuspensionesM> findByFechaHabilitaciones(Date desde, Date hasta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SuspensionesM> findLastTen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SuspensionesM> findByNumero(Long numero) {
		// TODO Auto-generated method stub
		return null;
	}

}
