package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Ccertificaciones;

public interface CcertificacionesR extends JpaRepository<Ccertificaciones, Long> {

   @Query("""
         SELECT c
         FROM Ccertificaciones c
         LEFT JOIN FETCH c.idfactura_facturas f
         LEFT JOIN FETCH f.idcliente
         LEFT JOIN FETCH c.idtpcertifica_tpcertifica
         WHERE c.numero >= ?1 AND c.numero <= ?2
         ORDER BY c.numero
         """)
   List<Ccertificaciones> findDesdeHasta(Long desde, Long hasta);

   @Query("""
         SELECT c
         FROM Ccertificaciones c
         JOIN FETCH c.idfactura_facturas f
         JOIN FETCH f.idcliente cl
         LEFT JOIN FETCH c.idtpcertifica_tpcertifica
         WHERE LOWER(cl.nombre) LIKE CONCAT('%', ?1, '%')
         ORDER BY cl.nombre
         """)
   List<Ccertificaciones> findByCliente(String cliente);

   @EntityGraph(attributePaths = { "idfactura_facturas", "idfactura_facturas.idcliente",
         "idtpcertifica_tpcertifica" })
   Ccertificaciones findFirstByOrderByIdccertificacionDesc();
}
