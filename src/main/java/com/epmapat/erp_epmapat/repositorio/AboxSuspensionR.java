package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.AboxSuspensionM;

public interface AboxSuspensionR extends JpaRepository<AboxSuspensionM, Long> {

	@Query(value = "SELECT * FROM aboxsuspension WHERE idsuspension_suspensiones=?1", nativeQuery = true)
	public List<AboxSuspensionM> findByIdsuspension(Long idsuspension);

	@Query(value = """
			SELECT ax.*
			FROM aboxsuspension ax
			JOIN suspensiones s ON s.idsuspension = ax.idsuspension_suspensiones
			WHERE ax.idabonado_abonados = ?1
			  AND s.tipo IN (2, 3)
			ORDER BY s.fecha DESC, s.idsuspension DESC, ax.idaboxsuspen DESC
			LIMIT 1
			""", nativeQuery = true)
	Optional<AboxSuspensionM> findUltimaSuspensionActivaByAbonado(Long idabonado);

}
