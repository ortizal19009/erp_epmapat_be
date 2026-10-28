package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;

public interface CertipresuR extends JpaRepository<Certipresu, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Certipresu> {

    default List<Certipresu> findDesdeHasta(Integer tipo, Long desdeNum, Long hastaNum,
            LocalDate desdeFecha, LocalDate hastaFecha) {
        return findAll((root, query, cb) -> {
            root.fetch("intdoc", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("idbene", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("idbeneres", javax.persistence.criteria.JoinType.LEFT);
            var filtros = new java.util.ArrayList<javax.persistence.criteria.Predicate>();
            filtros.add(cb.equal(root.get("tipo"), tipo));
            if (desdeNum != null) filtros.add(cb.greaterThanOrEqualTo(root.get("numero"), desdeNum));
            if (hastaNum != null) filtros.add(cb.lessThanOrEqualTo(root.get("numero"), hastaNum));
            if (desdeFecha != null) filtros.add(cb.greaterThanOrEqualTo(root.get("fecha"), desdeFecha));
            if (hastaFecha != null) filtros.add(cb.lessThanOrEqualTo(root.get("fecha"), hastaFecha));
            return cb.and(filtros.toArray(new javax.persistence.criteria.Predicate[0]));
        }, org.springframework.data.domain.Sort.by("numero", "idcerti"));
    }

	// Ultima Certificación o Reintegrada
	@EntityGraph(attributePaths = { "intdoc", "idbene", "idbeneres" })
	Certipresu findFirstByTipoOrderByNumeroDesc(Integer tipo);

	// Busca Certificación o Reintegrada por número
	@EntityGraph(attributePaths = { "intdoc", "idbene", "idbeneres" })
	Certipresu findByNumeroAndTipo(Long numero, int tipo); // Si se usa

	@Override
	@EntityGraph(attributePaths = { "intdoc", "idbene", "idbeneres" })
	Optional<Certipresu> findById(Long idcerti);

	// Valida por Número
	boolean existsByNumeroAndTipo(Long numero, int tipo);

	// @Query("""
	// SELECT c.numero
	// FROM Certipresu c
	// WHERE c.tipo = 1
	// AND c.fecha <= :fecha
	// ORDER BY c.numero DESC
	// """)
	// Long findUltimoNumeroTipo1HastaFecha(@Param("fecha") LocalDate fecha);

	// BUsca la última certificacion hasta una fecha (para el navegador)
	@Query(value = """
			  SELECT c.numero
			  FROM certificaciones c
			  WHERE c.tipo = 1
			    AND c.fecha <= :fecha
			  ORDER BY c.numero DESC
			  LIMIT 1
			""", nativeQuery = true)
	Long findUltimoNumeroTipo1HastaFecha(@Param("fecha") LocalDate fecha);

}
