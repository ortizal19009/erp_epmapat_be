package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.CtramitesM;

public interface CtramitesR extends JpaRepository<CtramitesM, Long> {

	@Override
	@EntityGraph(attributePaths = { "idcliente_clientes", "idtptramite_tptramite" })
	List<CtramitesM> findAll();

	@Query("""
			SELECT c
			FROM CtramitesM c
			LEFT JOIN FETCH c.idcliente_clientes
			LEFT JOIN FETCH c.idtptramite_tptramite
			WHERE c.idtptramite_tptramite.idtptramite = ?1
			ORDER BY c.idctramite DESC
			""")
	List<CtramitesM> findByTpTramite(Long idTpTramite);

	@Query("""
			SELECT c
			FROM CtramitesM c
			LEFT JOIN FETCH c.idcliente_clientes
			LEFT JOIN FETCH c.idtptramite_tptramite
			WHERE LOWER(c.descripcion) LIKE CONCAT('%', ?1, '%')
			ORDER BY c.idctramite DESC
			""")
	List<CtramitesM> findByDescripcion(String descripcion);

	@Query("""
			SELECT c
			FROM CtramitesM c
			LEFT JOIN FETCH c.idcliente_clientes
			LEFT JOIN FETCH c.idtptramite_tptramite
			WHERE c.feccrea = DATE(?1)
			ORDER BY c.idctramite DESC
			""")
	List<CtramitesM> findByfeccrea(Date feccrea);

	@Query("""
			SELECT c
			FROM CtramitesM c
			LEFT JOIN FETCH c.idcliente_clientes
			LEFT JOIN FETCH c.idtptramite_tptramite
			WHERE c.idcliente_clientes.idcliente = ?1
			ORDER BY c.idctramite DESC
			""")
	List<CtramitesM> findByIdcliente(Long idcliente);

	@Override
	@EntityGraph(attributePaths = { "idcliente_clientes", "idtptramite_tptramite" })
	Optional<CtramitesM> findById(Long id);
}
