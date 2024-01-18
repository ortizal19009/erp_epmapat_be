package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Usoitems;
import com.epmapat.erp_epmapat.repositorio.UsoitemsR;

@Service
public class UsoitemsServicio {

   @Autowired
   UsoitemsR dao;

   public List<Usoitems> findByOrderByDescripcionAsc() {
      return dao.findByOrderByDescripcionAsc();
   }

   public List<Usoitems> findByIdmodulo(Long idmodulo) {
      return dao.findByIdmodulo(idmodulo);
   }

   public Optional<Usoitems> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends Usoitems> S save(S entity) {
      return dao.save(entity);
   }

   public List<Usoitems> findByNombre(Long idmodulo, String descripcion) {
      return dao.findByNombre(idmodulo, descripcion);
   }

}
