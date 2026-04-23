package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;

public interface Fec_retencionesR extends JpaRepository<Fec_retenciones, Long> {

   List<Fec_retenciones> findByEstadoOrderByIdretencionDesc(String estado);

   List<Fec_retenciones> findAllByOrderByIdretencionDesc();

   Fec_retenciones findFirstByClaveaccesoOrderByIdretencionDesc(String claveacceso);

   boolean existsByClaveaccesoAndEstadoIn(String claveacceso, List<String> estados);

}
