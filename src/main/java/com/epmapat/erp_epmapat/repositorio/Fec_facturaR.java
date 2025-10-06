package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.sri.interfaces.fecFacturaDatos;

public interface Fec_facturaR extends JpaRepository<Fec_factura, Long> {
    @Query(value = "SELECT * FROM fec_factura where estado like ?1 order by idfactura asc limit ?2 ", nativeQuery = true)
    public List<Fec_factura> findByEstado(String estado, Long limit);

    @Query(value = "SELECT * FROM fec_factura where referencia = ?1 order by idfactura asc ", nativeQuery = true)
    public List<Fec_factura> findByCuenta(String referencia);

    @Query(value = "SELECT * FROM fec_factura where LOWER(razonsocialcomprador) like %?1% order by idfactura asc ", nativeQuery = true)
    public List<Fec_factura> findByNombreCliente(String cliente);

    @Query(value = "SELECT * FROM fec_factura where idfactura = ?1 ", nativeQuery = true)
    public List<Fec_factura> findByNroFactura(Long idfactura);

    @Query(value = "select xmlautorizado, fechaemision from fec_factura where idfactura = ?1", nativeQuery = true)
    public fecFacturaDatos getNroFactura(Long idfactura);
    

}