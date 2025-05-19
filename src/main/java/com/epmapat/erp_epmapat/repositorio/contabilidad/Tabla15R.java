package com.epmapat.erp_epmapat.repositorio.contabilidad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla15;

public interface Tabla15R extends JpaRepository<Tabla15, Long> {
    @Query(value ="select nomtabla15 from tabla15 where codtabla15 = ?1", nativeQuery = true)
    String getNombre(String cod);
}
