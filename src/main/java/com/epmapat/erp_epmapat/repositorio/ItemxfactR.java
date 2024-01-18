package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.Itemxfact;

// @Repository
public interface ItemxfactR extends JpaRepository<Itemxfact, Long>{

   //Productos de una Facturación (itemxfac de una Facturacion)
   @Query(value = "SELECT * FROM itemxfact AS i WHERE i.idfacturacion_facturacion=?1", nativeQuery=true)
	public List<Itemxfact> findByIdfacturacion(Long idfacturacion);

   //Movimiento de un Prodcto (itemxfact de una Catalogoitems)
   @Query(value = "SELECT * FROM itemxfact where idcatalogoitems_catalogoitems=?1 order by iditemxfact DESC LIMIT 10", nativeQuery = true)
   public List<Itemxfact> findByIdcatalogoitems( Long Idcatalogoitems);

}
