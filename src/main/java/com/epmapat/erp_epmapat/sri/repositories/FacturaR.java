package com.epmapat.erp_epmapat.sri.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.sri.models.Factura;

public interface FacturaR extends JpaRepository<Factura, Long>{

    @Query("""
            select distinct f
            from Factura f
            left join fetch f.detalles d
            where f.idfactura = :idfactura
            """)
    Optional<Factura> findByIdWithDetalles(@Param("idfactura") Long idfactura);
}
