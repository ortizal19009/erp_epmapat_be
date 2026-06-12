package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.modelo.Fec_factura_pagos;

public interface Fec_factura_pagosR extends JpaRepository<Fec_factura_pagos, Long> {
    @Query(value = "select * from fec_factura_pagos where idfactura = ?1", nativeQuery = true)
    public List<Fec_factura_pagos> getByIdfactura(Long idfactura);

    @Transactional
    @Modifying
    @Query("delete from Fec_factura_pagos p where p.idfactura = :idfactura")
    void deleteByIdfactura(@Param("idfactura") Long idfactura);

}
