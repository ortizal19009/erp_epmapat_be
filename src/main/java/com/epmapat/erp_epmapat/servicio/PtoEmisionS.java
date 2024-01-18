package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import com.epmapat.erp_epmapat.modelo.PtoEmisionM;
import com.epmapat.erp_epmapat.repositorio.PtoEmisionR;

@Service
public class PtoEmisionS {

	@Autowired
	private PtoEmisionR ptoemisionR;

	public List<PtoEmisionM> findAll(Sort sort) {
		return ptoemisionR.findAll(sort);
	}

	public <S extends PtoEmisionM> S save(S entity) {
		return ptoemisionR.save(entity);
	}

	public void deleteById(Long id) {
		ptoemisionR.deleteByIdQ(id);
	}

	public void delete(PtoEmisionM entity) {
		ptoemisionR.delete(entity);
	}

	public Optional<PtoEmisionM> findById(Long id) {
		return ptoemisionR.findById(id);
	}

	public List<PtoEmisionM> used(Long id) {
		return ptoemisionR.used(id);
	}

}
