package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Usoitems;

public interface UsoitemsR extends JpaRepository<Usoitems, Long> {
   
   List<Usoitems> findByOrderByDescripcionAsc();

   @Query(value="SELECT * FROM usoitems WHERE idmodulo_modulos=?1 order by descripcion", nativeQuery = true)
	public List<Usoitems> findByIdmodulo(Long idmodulo);
   
 	//Validar por Nombre y Sección
	@Query(value="SELECT * FROM usoitems WHERE idmodulo_modulos=?1 and LOWER(descripcion)=?2", nativeQuery = true)
	public List<Usoitems> findByNombre(Long idmodulo, String descripcion);

}
