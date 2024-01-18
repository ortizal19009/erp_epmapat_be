package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.NiifHomologa;
import com.epmapat.erp_epmapat.repositorio.contabilidad.NiifHomologaR;

@Service
public class NiifHomologaServicio {
   
   @Autowired
	private NiifHomologaR dao;

	public List<NiifHomologa> findAll() {
		return dao.findAll();
	}

   public List<NiifHomologa> findByIdNiifCue(Long idniifcue) {
		return dao.findByIdNiifCue(idniifcue);
	}

   public List<NiifHomologa> findByIdCuenta(Long idcuenta) {
		return dao.findByIdCuenta( idcuenta );
	}

   public <S extends NiifHomologa> S save(S entity) {
		return dao.save(entity);
	}

   public Optional<NiifHomologa> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

}
