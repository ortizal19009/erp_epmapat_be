package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.TramiteNuevo;

public interface TramiteNuevoR extends JpaRepository<TramiteNuevo, Long> {

	@Query("SELECT t FROM TramiteNuevo t ORDER BY t.idtramitenuevo DESC")
	List<TramiteNuevo> findAll();

	@Override
	@EntityGraph(attributePaths = {
			"idcategoria_categorias",
			"idaguatramite_aguatramite",
			"idaguatramite_aguatramite.idcliente_clientes",
			"idaguatramite_aguatramite.idtipotramite_tipotramite"
	})
	Optional<TramiteNuevo> findById(Long id);

	@Query("""
			SELECT t
			FROM TramiteNuevo t
			LEFT JOIN FETCH t.idaguatramite_aguatramite a
			LEFT JOIN FETCH a.idcliente_clientes c
			LEFT JOIN FETCH t.idcategoria_categorias
			WHERE a.idaguatramite = ?1
			ORDER BY t.idtramitenuevo DESC
			""")
	List<TramiteNuevo> findByIdAguaTramite(Long idaguatramite);
}
