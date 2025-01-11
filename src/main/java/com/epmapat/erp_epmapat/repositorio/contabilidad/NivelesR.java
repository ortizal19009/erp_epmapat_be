package com.epmapat.erp_epmapat.repositorio.contabilidad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Niveles;

public interface NivelesR extends JpaRepository<Niveles, Long> {

   Niveles findByNivcue(Integer nivcue);

   @Query(value = "SELECT * FROM Niveles n WHERE n.nivcue >?1 ORDER BY n.nivcue ASC LIMIT 1", nativeQuery = true)
   Niveles findSiguienteByNivcue(@Param("nivcue") Integer nivcue);

}
