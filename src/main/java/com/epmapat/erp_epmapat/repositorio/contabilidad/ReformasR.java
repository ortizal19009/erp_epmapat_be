package com.epmapat.erp_epmapat.repositorio.contabilidad;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.contabilidad.Reformas;

public interface ReformasR extends JpaRepository<Reformas, Long> {

   @Query(value = "SELECT * FROM reformas where numero BETWEEN ?1 AND ?2 order by numero", nativeQuery = true)
   List<Reformas> buscaByNumfec(Long desde, Long hasta);

   // @Query(value = "SELECT * FROM reformas ORDER BY idrefo DESC LIMIT 1", nativeQuery = true)
   // List<Reformas> buscarUltimaReforma();

   // Ultima Reforma
	Reformas findFirstByOrderByNumeroDesc();

   // Para Siguiente
   Reformas findTopByOrderByNumeroDesc();

   

}
