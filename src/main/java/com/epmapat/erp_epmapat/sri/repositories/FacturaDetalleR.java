package com.epmapat.erp_epmapat.sri.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.sri.interfaces.TotalSinImpuestos;
import com.epmapat.erp_epmapat.sri.models.FacturaDetalle;

public interface FacturaDetalleR extends JpaRepository<FacturaDetalle, Long> {
    @Query(value = "select sum(cantidad * preciounitario) as totalsinimpuestos, sum (descuento) as descuento from fec_factura_detalles where idfactura = ?1", nativeQuery = true)
    TotalSinImpuestos getTotalSinImpuestos(Long idfactura);
}