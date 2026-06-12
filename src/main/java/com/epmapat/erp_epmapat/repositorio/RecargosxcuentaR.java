package com.epmapat.erp_epmapat.repositorio;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.Recargosxcuenta;

public interface RecargosxcuentaR extends JpaRepository<Recargosxcuenta, Long> {

    @Query("""
                SELECT r
                FROM Recargosxcuenta r
                WHERE r.idemision_emisiones.idemision = :idemision
            """)
    List<Recargosxcuenta> findByIdEmision(@Param("idemision") Long idemision);

    @Query("""
                SELECT r
                FROM Recargosxcuenta r
                LEFT JOIN FETCH r.idemision_emisiones e
                LEFT JOIN FETCH r.idrubro_rubros ru
                WHERE r.idabonado_abonados.idabonado = :idabonado
                ORDER BY e.idemision DESC, r.fecha DESC, r.feccrea DESC, r.idrecargoxcuenta DESC
            """)
    List<Recargosxcuenta> findByIdAbonado(@Param("idabonado") Long idabonado);

    // ✅ REGLA #2: tipo 1 NO repetible en la misma emisión (cuenta + emisión + tipo)
    @Query("""
                SELECT (count(r) > 0)
                FROM Recargosxcuenta r
                WHERE r.idabonado_abonados.idabonado = :idabonado
                  AND r.idemision_emisiones.idemision = :idemision
                  AND r.tipo = :tipo
            """)
    boolean existsEnEmisionYTipo(@Param("idabonado") Long idabonado,
            @Param("idemision") Long idemision,
            @Param("tipo") int tipo);

    // ✅ tipo 1 solo 1 por mes
    @Query(value = """
            select exists(
              select 1
              from recargosxcuenta r
              where r.idabonado_abonados = :idabonado
                and r.tipo = 1
                and r.fecha >= date_trunc('month', cast(:fecha as timestamp))
                and r.fecha <  (date_trunc('month', cast(:fecha as timestamp)) + interval '1 month')
            )
            """, nativeQuery = true)
    boolean existsTipo1EnMes(@Param("idabonado") Long idabonado,
            @Param("fecha") Timestamp fecha);

    // ✅ tipo 2 solo 1 por año
    @Query(value = """
            select exists(
              select 1
              from recargosxcuenta r
              where r.idabonado_abonados = :idabonado
                and r.tipo = 2
                and r.fecha >= date_trunc('year', cast(:fecha as timestamp))
                and r.fecha <  (date_trunc('year', cast(:fecha as timestamp)) + interval '1 year')
            )
            """, nativeQuery = true)
    boolean existsTipo2EnAnio(@Param("idabonado") Long idabonado,
            @Param("fecha") Timestamp fecha);

    @Query(value = """
                select *
                from recargosxcuenta
                where idemision_emisiones = :idemision
                  and idabonado_abonados = :cuenta
            """, nativeQuery = true)
    List<Recargosxcuenta> findByEmisionAndAbonado(
            @Param("idemision") Long idemision,
            @Param("cuenta") Long cuenta);

}
