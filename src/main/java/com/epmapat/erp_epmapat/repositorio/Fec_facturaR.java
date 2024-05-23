package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Fec_factura;

public interface Fec_facturaR extends JpaRepository<Fec_factura, Long> {
    @Query(value = "SELECT * FROM fec_factura where estado = ?1 order by idfactura asc limit = ?2 ", nativeQuery = true)
    public List<Fec_factura> findByEstado(String estado, Long limit);

    @Query(value = "SELECT * FROM fec_factura where referencia = ?1 order by idfactura asc ", nativeQuery = true)
    public List<Fec_factura> findByCuenta(Long referencia);

    @Query(value = "SELECT * FROM fec_factura where razonsocialcomprador = ?1 order by idfactura asc ", nativeQuery = true)
    public List<Fec_factura> findByNombreCliente(String cliente);

}