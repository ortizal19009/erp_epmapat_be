package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Facturacion;

public interface FacturacionR extends JpaRepository<Facturacion, Long> {

    @Query("""
            SELECT f
            FROM Facturacion f
            LEFT JOIN FETCH f.idcliente_clientes
            WHERE f.idfacturacion BETWEEN ?1 AND ?2
              AND f.feccrea BETWEEN ?3 AND ?4
            ORDER BY f.idfacturacion
            """)
    List<Facturacion> findDesdeHasta(Long desde, Long hasta, Date del, Date al);

    @Query("""
            SELECT f
            FROM Facturacion f
            JOIN FETCH f.idcliente_clientes c
            WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', ?1, '%'))
              AND f.feccrea BETWEEN ?2 AND ?3
            ORDER BY c.nombre
            """)
    List<Facturacion> findByCliente(String cliente, Date del, Date al);

    @Query("""
            SELECT f
            FROM Facturacion f
            LEFT JOIN FETCH f.idcliente_clientes
            ORDER BY f.idfacturacion DESC
            """)
    List<Facturacion> findTopByOrderByIdfacturacionDesc();

    @Query("""
            SELECT f
            FROM Facturacion f
            LEFT JOIN FETCH f.idcliente_clientes
            ORDER BY f.idfacturacion DESC
            """)
    List<Facturacion> findAll();

    @Query("""
            SELECT f
            FROM Facturacion f
            LEFT JOIN FETCH f.idcliente_clientes
            WHERE f.idfacturacion = ?1
            """)
    Optional<Facturacion> findById(Long id);
}
