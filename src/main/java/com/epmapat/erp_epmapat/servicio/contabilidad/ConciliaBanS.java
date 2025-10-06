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

import com.epmapat.erp_epmapat.modelo.contabilidad.ConciliaBan;
import com.epmapat.erp_epmapat.repositorio.contabilidad.ConciliaBanR;

@Service
public class ConciliaBanS {
	@Autowired
	public ConciliaBanR conciliabanR;

	public List<ConciliaBan> findAll() {
		
		return conciliabanR.findAll();
	}

	public <S extends ConciliaBan> S save(S entity) {
		
		return conciliabanR.save(entity);
	}

	public Optional<ConciliaBan> findById(Long id) {
		
		return Optional.empty();
	}
	public boolean existsById(Long id) {
		
		return false;
	}
	
}
