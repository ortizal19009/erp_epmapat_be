package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla10;

public interface Tabla10R extends JpaRepository<Tabla10, Long> {
  
   @Query(value = "SELECT * FROM tabla10 WHERE codretair like (?1)", nativeQuery = true)
    public List<Tabla10> findByCodretair(String codretair);

  // Tabla10 findByCodretair(String codretair);
}
