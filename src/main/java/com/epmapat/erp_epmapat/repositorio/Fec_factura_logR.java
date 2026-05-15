package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.Fec_factura_log;

public interface Fec_factura_logR extends JpaRepository<Fec_factura_log, Long> {
   List<Fec_factura_log> findByIdfacturaOrderByFechaAsc(Long idfactura);
}
