package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Reformas;

public interface ReformasR extends JpaRepository<Reformas, Long> {

   @EntityGraph(attributePaths = { "intdoc" })
   @Query("SELECT r FROM Reformas r WHERE r.numero BETWEEN ?1 AND ?2 ORDER BY r.numero")
   List<Reformas> buscaByNumfec(Long desde, Long hasta);

   // Ultima Reforma
	@EntityGraph(attributePaths = { "intdoc" })
	Reformas findFirstByOrderByNumeroDesc();

   //Siguiente Reforma
   @EntityGraph(attributePaths = { "intdoc" })
   Reformas findTopByOrderByNumeroDesc();

   @Override
   @EntityGraph(attributePaths = { "intdoc" })
   Optional<Reformas> findById(Long id);

}
