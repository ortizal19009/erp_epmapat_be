package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
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

	@Override
	@EntityGraph(attributePaths = { "idemision_emisiones", "idruta_rutas" })
	java.util.Optional<Rutasxemision> findById(Long id);

	//Cuenta las rutas abiertas de una Emisión
	@Query(value = "SELECT COUNT(*) FROM rutasxemision r WHERE r.idemision_emisiones=?1 and r.estado = 0", nativeQuery = true)
	Long contarPorEstadoYIdemision(Long idemision_emisiones);

	@Query("""
			SELECT rxe
			FROM Rutasxemision rxe
			JOIN FETCH rxe.idemision_emisiones emi
			JOIN FETCH rxe.idruta_rutas ruta
			WHERE emi.idemision = ?1
			  AND ruta.idruta = ?2
			""")
	public Rutasxemision findByEmisionRuta(Long idemision, Long idruta); 

	@Query("""
			SELECT rxe
			FROM Rutasxemision rxe
			JOIN FETCH rxe.idemision_emisiones emi
			JOIN FETCH rxe.idruta_rutas ruta
			WHERE emi.idemision = ?1
			  AND ruta.idruta = ?2
			""")
	Optional<Rutasxemision> findOptionalByEmisionRuta(Long idemision, Long idruta);

}
