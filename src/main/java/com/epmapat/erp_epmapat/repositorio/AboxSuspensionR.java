package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.AboxSuspensionM;

public interface AboxSuspensionR extends JpaRepository<AboxSuspensionM, Long> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {
        "idsuspension_suspensiones", "idsuspension_suspensiones.iddocumento_documentos",
        "idabonado_abonados", "idabonado_abonados.idcliente_clientes",
        "idabonado_abonados.idresponsable", "idabonado_abonados.idruta_rutas",
        "idabonado_abonados.idcategoria_categorias", "idabonado_abonados.idestadom_estadom"
    })
    @Query("SELECT ax FROM AboxSuspensionM ax WHERE ax.idsuspension_suspensiones.idsuspension = ?1 ORDER BY ax.idabonado_abonados.idabonado")
	public List<AboxSuspensionM> findByIdsuspension(Long idsuspension);

    @Query(value = """
        SELECT DISTINCT idsuspension_suspensiones AS idsuspension, idabonado_abonados AS cuenta
        FROM aboxsuspension
        WHERE idsuspension_suspensiones IN (:ids) AND idabonado_abonados IS NOT NULL
        ORDER BY idsuspension_suspensiones, idabonado_abonados
        """, nativeQuery = true)
    List<com.epmapat.erp_epmapat.interfaces.SuspensionCuentaResumen> findCuentasPorSuspensiones(
        @org.springframework.data.repository.query.Param("ids") List<Long> ids);

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
