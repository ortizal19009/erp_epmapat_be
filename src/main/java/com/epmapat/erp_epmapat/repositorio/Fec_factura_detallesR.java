package com.epmapat.erp_epmapat.repositorio;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles;

public interface Fec_factura_detallesR extends JpaRepository<Fec_factura_detalles, Long>{
    @Query(value = "select * from fec_factura_detalles where idfactura = ?1 order by idfacturadetalle asc ", nativeQuery= true )
    public List<Fec_factura_detalles> getFecDetalleByIdFactura(Long idfactura); 
}
