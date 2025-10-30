package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles_impuestos;

public interface Fec_factura_detalles_impuestosR extends JpaRepository<Fec_factura_detalles_impuestos, Long> {
    @Query(value = "select * from fec_factura_detalles_impuestos where idfacturadetalle = ?1 order by idfacturadetalle asc", nativeQuery = true)
    public List<Fec_factura_detalles_impuestos> findByIdDetalle(Long iddetalle);

    @Query(value = "delete from fec_factura_detalles_impuestos where idfacturadetalleimpuestos = ?1", nativeQuery = true)
    public void deleteByIdDetalle(Long idimpuesto);
    @Query(value = "select * from fec_factura_detalles_impuestos where idfacturadetalleimpuestos = ?1", nativeQuery = true)
    public Fec_factura_detalles_impuestos findBy_id(Long idimpuesto);
}
