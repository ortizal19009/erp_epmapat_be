package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.TramiteNuevo;

public interface TramiteNuevoR extends JpaRepository<TramiteNuevo, Long> {

	@Query("SELECT t FROM TramiteNuevo t ORDER BY t.idtramitenuevo DESC")
	List<TramiteNuevo> findAll();

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
