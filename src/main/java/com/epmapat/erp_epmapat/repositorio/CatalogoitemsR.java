package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Catalogoitems;

public interface CatalogoitemsR extends JpaRepository<Catalogoitems, Long>{

   //Productos por Seccion y/o Descripcion
   @Query(value = "SELECT * FROM catalogoitems AS c JOIN usoitems as u ON c.idusoitems_usoitems=u.idusoitems WHERE u.idmodulo_modulos>=?1 and u.idmodulo_modulos<=?2 and LOWER(c.descripcion) like %?3% ORDER by c.descripcion", nativeQuery = true)
	public List<Catalogoitems> findProductos(Long idusoitems1, Long idusoitems2, String descripcion);

   @Query(value="SELECT * FROM catalogoitems WHERE idrubro_rubros=?1 order by descripcion", nativeQuery = true)
	public List<Catalogoitems> findByIdrubro(Long idrubro);

   @Query(value="SELECT * FROM catalogoitems WHERE idusoitems_usoitems=?1 order by descripcion", nativeQuery = true)
	public List<Catalogoitems> findByIdusoitems(Long idusoitems);
   
   //Validar nombre (Por Uso)
   @Query(value = "SELECT * FROM catalogoitems where idusoitems_usoitems=?1 and LOWER(descripcion)=?2", nativeQuery=true)
	List<Catalogoitems> findByNombre(Long idusoitems, String descripcion);

}
