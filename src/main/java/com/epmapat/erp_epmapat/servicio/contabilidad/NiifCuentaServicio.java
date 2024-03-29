package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.NiifCuentas;
import com.epmapat.erp_epmapat.repositorio.contabilidad.NiifCuentasR;

@Service
public class NiifCuentaServicio {

   @Autowired
   private NiifCuentasR dao;

   public List<NiifCuentas> findAll() {
      return dao.findAll();
   }

   public List<NiifCuentas> findByNomcue(String nomcue) {
      return dao.findByNomcue(nomcue);
   }

   public List<NiifCuentas> findByCodcue(String codcue) {
      return dao.findByCodcue(codcue);
   }

   public <S extends NiifCuentas> S save(S entity) {
		return dao.save(entity);
	}

   public Optional<NiifCuentas> findById(Long id) {
		return dao.findById(id);
	}

}
