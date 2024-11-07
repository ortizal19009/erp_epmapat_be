package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;

@Service
public class EmisionServicio {

	@Autowired
	private EmisionesR dao;

	public List<Emisiones> findByDesdeHasta(String desde, String hasta) {
		return dao.findByDesdeHasta(desde, hasta);
	}

	public <S extends Emisiones> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Emisiones> findById(Long id) {
		return dao.findById(id);
	}

	// Busca la última Emisión
	public Emisiones findFirstByOrderByEmisionDesc() {
		return dao.findFirstByOrderByEmisionDesc();
	}

	public List<Emisiones> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	public List<Emisiones> findByIdEmisiones(Long idemision) {
		return dao.findByIdEmisiones(idemision);
	}



}
