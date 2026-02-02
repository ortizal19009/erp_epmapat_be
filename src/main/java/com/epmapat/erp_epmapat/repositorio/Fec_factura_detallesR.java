package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles;

public interface Fec_factura_detallesR extends JpaRepository<Fec_factura_detalles, Long> {
    @Query(value = "select * from fec_factura_detalles where idfactura = ?1 order by idfacturadetalle asc ", nativeQuery = true)
    public List<Fec_factura_detalles> getFecDetalleByIdFactura(Long idfactura);

    List<Fec_factura_detalles> findByIdfactura(Long idfactura);

    @Modifying
    @Query("delete from Fec_factura_detalles d where d.idfactura = :idfactura")
    void deleteByIdfactura(@Param("idfactura") Long idfactura);
}
