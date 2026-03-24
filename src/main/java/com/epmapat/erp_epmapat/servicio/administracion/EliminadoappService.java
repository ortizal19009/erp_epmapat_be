package com.epmapat.erp_epmapat.servicio.administracion;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Eliminadosapp;
import com.epmapat.erp_epmapat.repositorio.administracion.EliminadosappR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EliminadoappService {

   private final EliminadosappR dao;

   // Guardar nuevo
   public <S extends Eliminadosapp> S save(S entity) {
      return dao.save(entity);
   }

}
