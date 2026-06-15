package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Catalogoitems;

public interface CatalogoitemsR extends JpaRepository<Catalogoitems, Long>{

   //Productos por Seccion y/o Descripcion
   @Query("""
         SELECT c
         FROM Catalogoitems c
         JOIN FETCH c.idusoitems_usoitems u
         LEFT JOIN FETCH u.idmodulo_modulos
         LEFT JOIN FETCH c.idrubro_rubros
         WHERE u.idmodulo_modulos.idmodulo >= ?1
           AND u.idmodulo_modulos.idmodulo <= ?2
           AND (?3 = '' OR LOWER(c.descripcion) LIKE CONCAT('%', LOWER(?3), '%'))
         ORDER BY c.descripcion
         """)
	public List<Catalogoitems> findProductos(Long idusoitems1, Long idusoitems2, String descripcion);

   @Query("SELECT c FROM Catalogoitems c LEFT JOIN FETCH c.idrubro_rubros LEFT JOIN FETCH c.idusoitems_usoitems u LEFT JOIN FETCH u.idmodulo_modulos WHERE c.idrubro_rubros.idrubro = ?1 ORDER BY c.descripcion")
	public List<Catalogoitems> findByIdrubro(Long idrubro);

   @Query("SELECT c FROM Catalogoitems c LEFT JOIN FETCH c.idusoitems_usoitems u LEFT JOIN FETCH u.idmodulo_modulos LEFT JOIN FETCH c.idrubro_rubros WHERE c.idusoitems_usoitems.idusoitems = ?1 ORDER BY c.descripcion")
	public List<Catalogoitems> findByIdusoitems(Long idusoitems);
   
   //Validar nombre (Por Uso)
   @Query("SELECT c FROM Catalogoitems c LEFT JOIN FETCH c.idusoitems_usoitems u LEFT JOIN FETCH u.idmodulo_modulos LEFT JOIN FETCH c.idrubro_rubros WHERE c.idusoitems_usoitems.idusoitems = ?1 AND LOWER(c.descripcion) = ?2")
	List<Catalogoitems> findByNombre(Long idusoitems, String descripcion);

}
