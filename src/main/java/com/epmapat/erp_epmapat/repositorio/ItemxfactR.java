package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Itemxfact;

public interface ItemxfactR extends JpaRepository<Itemxfact, Long> {

   @Query("SELECT i FROM Itemxfact i LEFT JOIN FETCH i.idcatalogoitems_catalogoitems c LEFT JOIN FETCH c.idrubro_rubros LEFT JOIN FETCH i.idfacturacion_facturacion f LEFT JOIN FETCH f.idcliente_clientes WHERE i.idfacturacion_facturacion.idfacturacion = ?1")
   List<Itemxfact> findByIdfacturacion(Long idfacturacion);

   @Query("SELECT i FROM Itemxfact i LEFT JOIN FETCH i.idcatalogoitems_catalogoitems c LEFT JOIN FETCH c.idrubro_rubros LEFT JOIN FETCH i.idfacturacion_facturacion f LEFT JOIN FETCH f.idcliente_clientes WHERE i.idcatalogoitems_catalogoitems.idcatalogoitems = ?1 ORDER BY i.iditemxfact DESC")
   List<Itemxfact> findByIdcatalogoitems(Long idcatalogoitems);
}
