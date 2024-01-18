package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;

@Service
public class UsuarioServicio {

   @Autowired
   UsuariosR dao;

   // Busca Todos (Excepto el Administrador)
   public List<Usuarios> findAll() {
      return dao.findAll();
   }

   // Busca un Usuario
   public Usuarios findUsuario(String a, String b) {
      return dao.findUsuario(a, b);
   }

   // Busca un Usuario por Identificacion
   public Usuarios findByIdentificausu(String identificausu) {
      return dao.findByIdentificausu(identificausu);
   }

   public Optional<Usuarios> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends Usuarios> S save(S entity) {
      return dao.save(entity);
   }

   // Verifica
   // public boolean existe(String a, String b) {
   // return dao.existe(a, b);
   // }

}
