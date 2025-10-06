package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Pagoscobros;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PagoscobrosR;

@Service
public class PagoscobroServicio {

   @Autowired
   private PagoscobrosR dao;

   public List<Pagoscobros> findByIdbenxtra(Long idbenxtra) {
      return dao.getByIdbenxtra(idbenxtra);
   }

}
