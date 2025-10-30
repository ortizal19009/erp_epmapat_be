package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Acceso;
import com.epmapat.erp_epmapat.repositorio.administracion.AccesoR;

@Service
public class AccesoServicio {

   @Autowired
   AccesoR dao;

   // Todos
   public List<Acceso> findAll() {
      return dao.findAll();
   }

}
