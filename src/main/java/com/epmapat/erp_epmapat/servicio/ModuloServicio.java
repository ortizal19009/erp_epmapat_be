package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Modulos;
import com.epmapat.erp_epmapat.repositorio.ModulosR;

@Service
public class ModuloServicio {

	@Autowired
	private ModulosR dao;

	public List<Modulos> findAll() {
		return dao.findByOrderByDescripcionAsc();
	}

	public List<Modulos> getTwoModulos() {
		return dao.getTwoModulos();
	}

	// @Override
   public Optional<Modulos> findById(Long id) {
      return dao.findById(id);
   }

}
