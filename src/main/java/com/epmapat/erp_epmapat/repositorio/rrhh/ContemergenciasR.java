package com.epmapat.erp_epmapat.repositorio.rrhh;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.rrhh.Contemergencia;

public interface ContemergenciasR extends JpaRepository<Contemergencia, Long> {
    @Query(value = "SELECT * FROM contemergencia ce WHERE LOWER(ce.nombre) LIKE %?1%", nativeQuery = true)
    public List<Contemergencia> findByContEmergencia(String nombre);

}
