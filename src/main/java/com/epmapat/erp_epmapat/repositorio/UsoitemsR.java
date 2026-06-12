package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Usoitems;

public interface UsoitemsR extends JpaRepository<Usoitems, Long> {
   
   List<Usoitems> findByOrderByDescripcionAsc();

   @EntityGraph(attributePaths = { "idmodulo_modulos" })
   @Query("SELECT u FROM Usoitems u LEFT JOIN FETCH u.idmodulo_modulos m WHERE m.idmodulo = ?1 ORDER BY u.descripcion")
	public List<Usoitems> findByIdmodulo(Long idmodulo);
   
 	//Validar por Nombre y Sección
	@EntityGraph(attributePaths = { "idmodulo_modulos" })
	@Query("SELECT u FROM Usoitems u LEFT JOIN FETCH u.idmodulo_modulos m WHERE m.idmodulo = ?1 and LOWER(u.descripcion)=?2")
	public List<Usoitems> findByNombre(Long idmodulo, String descripcion);

}
