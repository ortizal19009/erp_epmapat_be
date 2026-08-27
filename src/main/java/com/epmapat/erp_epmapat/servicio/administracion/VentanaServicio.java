package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Ventanas;
import com.epmapat.erp_epmapat.repositorio.administracion.VentanasR;

@Service
public class VentanaServicio {
   private static final long PERMISO_ADMIN = 3L;

   @Autowired
   VentanasR dao;

   public Ventanas findVentana(Long idusuario, String nombre) {
      return dao.findByIdusuarioAndNombre(idusuario, nombre);
   }

   public <S extends Ventanas> S save(S x) {
      return dao.save( x );
   }

   public Optional<Ventanas> findById(Long id) {
      return dao.findById(id);
   }

   public boolean hasPermission(Long idusuario, String nombre, long minimo) {
      if (idusuario == null || nombre == null || nombre.isBlank()) {
         return false;
      }
      if (idusuario == 1L) {
         return true;
      }

      Ventanas ventana = dao.findByIdusuarioAndNombre(idusuario, nombre);
      if (ventana == null || ventana.getPermissions() == null) {
         return false;
      }
      return ventana.getPermissions() >= minimo;
   }

   public boolean canApproveCondonaciones(Long idusuario) {
      return hasPermission(idusuario, "condonaciones-pendientes", PERMISO_ADMIN);
   }

}
