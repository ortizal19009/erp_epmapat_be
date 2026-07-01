package com.epmapat.erp_epmapat.repositorio;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Liquidafac;

public interface LiquidafacR extends JpaRepository<Liquidafac, Serializable> {

   @Query("""
         SELECT l
         FROM Liquidafac l
         JOIN FETCH l.idfacturacion_facturacion f
         LEFT JOIN FETCH f.idcliente_clientes
         LEFT JOIN FETCH l.idfactura_facturas fac
         LEFT JOIN FETCH fac.idcliente
         LEFT JOIN FETCH fac.idmodulo
         WHERE f.idfacturacion = ?1
         ORDER BY l.idliquidafac
         """)
   public List<Liquidafac> findByIdfacturacion(Long idfacturacion);

   @Query("""
         SELECT l
         FROM Liquidafac l
         JOIN FETCH l.idfacturacion_facturacion f
         LEFT JOIN FETCH f.idcliente_clientes c
         LEFT JOIN FETCH l.idfactura_facturas fac
         LEFT JOIN FETCH fac.idcliente
         LEFT JOIN FETCH fac.idmodulo
         WHERE COALESCE(f.cuotas, 0) > 0
         ORDER BY f.idfacturacion DESC, l.cuota ASC, l.idliquidafac ASC
         """)
   List<Liquidafac> findPendientesFacturacionConDetalle();
        
}
