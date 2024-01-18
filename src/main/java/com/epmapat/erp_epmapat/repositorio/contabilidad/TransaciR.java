package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;

public interface TransaciR extends JpaRepository<Transaci, Long> {

	@Query(value = "SELECT * FROM transaci t INNER JOIN asientos a ON t.idasiento = a.idasiento WHERE t.idcuenta=?1 AND t.mesconcili = ?2 ORDER BY a.fecha ASC", nativeQuery = true)
	public List<Transaci> findControlBancos(Long idcuenta, Long mes);

	// Verifica si una Cuenta tiene Transacciones
	@Query("SELECT COUNT(t) > 0 FROM Transaci t WHERE t.codcue = :codcue")
	boolean tieneTransaci(@Param("codcue") String codcue);

	@Query(value = "SELECT * FROM transaci t INNER JOIN asientos a ON t.idasiento = a.idasiento WHERE t.idasiento=?1 ORDER BY t.orden ASC", nativeQuery = true)
	public List<Transaci> findTransaci(Long idasiento);

	//Asiento tiene transacciones
	@Query(value = "SELECT EXISTS (SELECT 1 FROM Transaci WHERE idasiento = ?1)", nativeQuery = true)
	boolean existsByIdasiento(Long idasiento);

}
