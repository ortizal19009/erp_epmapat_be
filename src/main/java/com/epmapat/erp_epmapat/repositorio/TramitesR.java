package com.epmapat.erp_epmapat.repositorio;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.TramitesM;

public interface TramitesR extends JpaRepository<TramitesM, Long>{
	//Trámites por Tipo de Trámite
	@Query(value = "SELECT * FROM tramites WHERE idtptramite_tptramite=?1 ORDER BY idtramite DESC LIMIT 20", nativeQuery = true)
	public List<TramitesM> findByTpTramite(Long idTpTramite);
	
	@Query(value = "SELECT * FROM tramites WHERE LOWER(descripcion) LIKE %?1%", nativeQuery = true)
	public List<TramitesM> findByDescripcion(String descripcion);
	
	@Query(value = "SELECT * FROM tramites WHERE feccrea=DATE(?1)", nativeQuery = true)
	public List<TramitesM> findByfeccrea(Date feccrea);
	//Trámites por Cliente
	@Query(value = "SELECT * FROM tramites WHERE idcliente_clientes=?1 ORDER BY idtramite DESC", nativeQuery = true)
	public List<TramitesM> findByIdcliente(Long idcliente);
	
}
