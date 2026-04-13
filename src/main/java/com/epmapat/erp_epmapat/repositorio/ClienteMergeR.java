package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.ClienteMergeMonitorView;
import com.epmapat.erp_epmapat.modelo.ClienteMerge;

public interface ClienteMergeR extends JpaRepository<ClienteMerge, Long> {

    @Query(value = """
            SELECT
                cm.id_merge AS "idMerge",
                CAST(NULLIF(CAST(cm.master_id AS TEXT), '') AS BIGINT) AS "masterId",
                c.nombre AS "masterNombre",
                c.cedula AS "masterCedula",
                cm.fecha_merge AS "fechaMerge",
                CAST(NULLIF(CAST(cm.usuario_merge AS TEXT), '') AS BIGINT) AS "usuarioMerge",
                cm.observacion AS observacion,
                (SELECT COUNT(*) FROM cliente_merge_clientes cmc WHERE CAST(cmc.id_merge AS TEXT) = CAST(cm.id_merge AS TEXT)) AS "clientesCount",
                (SELECT COUNT(*) FROM cliente_merge_abonados cma WHERE CAST(cma.id_merge AS TEXT) = CAST(cm.id_merge AS TEXT)) AS "abonadosCount",
                (SELECT COUNT(*) FROM cliente_merge_facturas cmf WHERE CAST(cmf.id_merge AS TEXT) = CAST(cm.id_merge AS TEXT)) AS "facturasCount",
                (SELECT COUNT(*) FROM cliente_merge_lecturas cml WHERE CAST(cml.id_merge AS TEXT) = CAST(cm.id_merge AS TEXT)) AS "lecturasCount"
            FROM cliente_merge cm
            LEFT JOIN clientes c ON CAST(c.idcliente AS TEXT) = CAST(cm.master_id AS TEXT)
            WHERE (:q IS NULL OR :q = ''
                    OR CAST(cm.id_merge AS TEXT) LIKE CONCAT('%', :q, '%')
                    OR CAST(cm.master_id AS TEXT) LIKE CONCAT('%', :q, '%')
                    OR LOWER(COALESCE(c.nombre, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                    OR COALESCE(c.cedula, '') LIKE CONCAT('%', :q, '%'))
              AND (:masterId IS NULL OR CAST(cm.master_id AS TEXT) = CAST(:masterId AS TEXT))
              AND (:usuario IS NULL OR CAST(cm.usuario_merge AS TEXT) = CAST(:usuario AS TEXT))
              AND (CAST(:desde AS TIMESTAMP) IS NULL OR cm.fecha_merge >= CAST(:desde AS TIMESTAMP))
              AND (CAST(:hasta AS TIMESTAMP) IS NULL OR cm.fecha_merge <= CAST(:hasta AS TIMESTAMP))
            ORDER BY cm.fecha_merge DESC, cm.id_merge DESC
            """, countQuery = """
            SELECT COUNT(*)
            FROM cliente_merge cm
            LEFT JOIN clientes c ON CAST(c.idcliente AS TEXT) = CAST(cm.master_id AS TEXT)
            WHERE (:q IS NULL OR :q = ''
                    OR CAST(cm.id_merge AS TEXT) LIKE CONCAT('%', :q, '%')
                    OR CAST(cm.master_id AS TEXT) LIKE CONCAT('%', :q, '%')
                    OR LOWER(COALESCE(c.nombre, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                    OR COALESCE(c.cedula, '') LIKE CONCAT('%', :q, '%'))
              AND (:masterId IS NULL OR CAST(cm.master_id AS TEXT) = CAST(:masterId AS TEXT))
              AND (:usuario IS NULL OR CAST(cm.usuario_merge AS TEXT) = CAST(:usuario AS TEXT))
              AND (CAST(:desde AS TIMESTAMP) IS NULL OR cm.fecha_merge >= CAST(:desde AS TIMESTAMP))
              AND (CAST(:hasta AS TIMESTAMP) IS NULL OR cm.fecha_merge <= CAST(:hasta AS TIMESTAMP))
            """, nativeQuery = true)
    Page<ClienteMergeMonitorView> findMonitor(
            @Param("q") String q,
            @Param("masterId") Long masterId,
            @Param("usuario") Long usuario,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);
}
