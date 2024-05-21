package com.epmapat.erp_epmapat.repositorio;

import java.io.Serializable;
import java.util.List;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Facturacion;

public interface FacturacionR extends JpaRepository<Facturacion, Serializable> {

    // @Query(value = "SELECT * FROM facturacion order by idfacturacion DESC LIMIT
    // 10", nativeQuery = true)
    // public List<Facturacion> findAll();

    @Query(value = "SELECT * FROM facturacion AS f WHERE f.idfacturacion >= ?1 and f.idfacturacion <= ?2 and f.feccrea BETWEEN (?3) AND (?4) ORDER BY idfacturacion", nativeQuery = true)
    public List<Facturacion> findDesdeHasta(Long desde, Long hasta, Date del, Date al);

    @Query(value = "SELECT * FROM facturacion AS f JOIN clientes as c ON f.idcliente_clientes=c.idcliente WHERE (c.nombre like %?1% OR LOWER(c.nombre) like %?1% or UPPER(c.nombre) like %?1% OR INITCAP(c.nombre) like %?1%) and f.feccrea BETWEEN (?2) AND (?3) ORDER by c.nombre", nativeQuery = true)
    public List<Facturacion> findByCliente(String cliente, Date del, Date al);

    // Ultima Facturación
    Facturacion findFirstByOrderByIdfacturacionDesc();

}
