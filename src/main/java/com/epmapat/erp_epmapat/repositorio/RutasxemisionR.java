package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Rutasxemision;

public interface RutasxemisionR extends JpaRepository<Rutasxemision, Long> {

	// Rutas por Emision
	@Query("""
			SELECT rxe
			FROM Rutasxemision rxe
			JOIN FETCH rxe.idemision_emisiones emi
			JOIN FETCH rxe.idruta_rutas ruta
			WHERE emi.idemision = ?1
			ORDER BY ruta.codigo
			""")
	public List<Rutasxemision> findByIdemision(Long idemision);

	//Cuenta las rutas abiertas de una Emisión
	@Query(value = "SELECT COUNT(*) FROM rutasxemision r WHERE r.idemision_emisiones=?1 and r.estado = 0", nativeQuery = true)
	Long contarPorEstadoYIdemision(Long idemision_emisiones);

	@Query(value = "select * from rutasxemision where idemision_emisiones = ?1 and idruta_rutas = ?2", nativeQuery = true)
	public Rutasxemision findByEmisionRuta(Long idemision, Long idruta); 

}
