package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.NiifCuentas;

public interface NiifCuentasR extends JpaRepository<NiifCuentas, Long> {

	@Query(value = "SELECT * FROM niifcuentas LIMIT 20", nativeQuery = true)
	public List<NiifCuentas> findAll();

   @Query(value = "SELECT * FROM niifcuentas WHERE LOWER(nomcue) LIKE %?1%", nativeQuery = true)
	public List<NiifCuentas> findByNomcue(String nomcue);

	@Query(value = "SELECT * FROM niifcuentas WHERE codcue = ?1 ", nativeQuery = true)
	public List<NiifCuentas> findByCodcue(String codcue);

}
