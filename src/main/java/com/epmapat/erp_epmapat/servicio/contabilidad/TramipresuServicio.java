package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TramipresuR;

@Service
public class TramipresuServicio {

   @Autowired
   private TramipresuR dao;

   public List<Tramipresu> findAll() {
      return dao.findAll();
   }

   public Tramipresu findFirstByOrderByNumeroDesc() {
      return dao.findFirstByOrderByNumeroDesc();
   }

   public <S extends Tramipresu> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Tramipresu> findById(Long id) {
      return dao.findById(id);
   }

   public List<Tramipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
		return dao.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
	}
   
}
