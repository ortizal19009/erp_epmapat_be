package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;

public interface UsuariosR extends JpaRepository<Usuarios, Long> {

   // Todos (Excepto el Administrador)
   @SuppressWarnings("null")
   @Query(value = "SELECT * FROM usuarios order by identificausu", nativeQuery = true)
   List<Usuarios> findAll();

   // Busca un usuario por Identificación
   @Query(value = "SELECT * FROM usuarios where identificausu=?1", nativeQuery = true)
   Usuarios findByIdentificausu(String identificausu);

   @Query(value = "SELECT * FROM usuarios where identificausu=?1 AND codusu=?2", nativeQuery = true)
   Usuarios findUsuario(String a, String b);

}
