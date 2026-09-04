package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRI;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRia;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRin;
import com.epmapat.erp_epmapat.interfaces.FacEliminadas;
import com.epmapat.erp_epmapat.interfaces.IemiIndividual;
import com.epmapat.erp_epmapat.interfaces.R_refacturacion_int;
import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.EmisionIndividual;

public interface EmisionIndividualR extends JpaRepository<EmisionIndividual, Long> {
        @EntityGraph(attributePaths = {
                        "idemision",
                        "idlecturanueva",
                        "idlecturanueva.idabonado_abonados",
                        "idlecturanueva.idabonado_abonados.idresponsable",
                        "idlecturaanterior",
                        "idlecturaanterior.idabonado_abonados",
                        "idlecturaanterior.idabonado_abonados.idresponsable"
        })
        @Query("select ei from EmisionIndividual ei where ei.idemision.idemision = :idemision")
        List<EmisionIndividual> findByIdEmision(@Param("idemision") Long idemision);

        /* REPORTE DE LECTURAS NUEVAS */
        @Query(value = "select r.idrubro_rubros as rubro, rs.descripcion as descripcion,  count(*) as nrofacturas, sum(ROUND(CAST(r.valorunitario * r.cantidad AS numeric), 2)) as sumaTotal from emisionindividual ei join lecturas l on ei.idlecturanueva = l.idlectura join rubroxfac r on l.idfactura = r.idfactura_facturas and (r.estado <> 0 or r.estado is null) join rubros rs on r.idrubro_rubros = rs.idrubro where ei.idemision = ?1 and not r.idrubro_rubros = 5 group by r.idrubro_rubros, rs.descripcion ", nativeQuery = true)
        public List<IemiIndividual> findLecturasNuevas(Long idemision);

        /* REPORTE DE LECTURAS ANTERIORES */
        @Query(value = "select r.idrubro_rubros as rubro, rs.descripcion as descripcion,  count(*) as nrofacturas, sum(ROUND(CAST(r.valorunitario * r.cantidad AS numeric), 2))as sumaTotal from emisionindividual ei join lecturas l on ei.idlecturaanterior = l.idlectura join rubroxfac r on l.idfactura = r.idfactura_facturas and (r.estado <> 0 or r.estado is null) join rubros rs on r.idrubro_rubros = rs.idrubro where ei.idemision = ?1 and not r.idrubro_rubros = 5 group by r.idrubro_rubros, rs.descripcion ", nativeQuery = true)
        public List<IemiIndividual> findLecturasAnteriores(Long idemision);

        @Query(value = "select la.idfactura as facturaa, ea.emision as emisiona, ln.idfactura as facturan, en.emision as emisionn, fa.idabonado as cuenta, sum(rfa.valorunitario) as tanterior , sum(rfn.valorunitario) as tnuevo "
                        + "from emisionindividual ei "
                        + "join lecturas la on ei.idlecturaanterior = la.idlectura "
                        + "join facturas fa on la.idfactura = fa.idfactura "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = la.idfactura and (rfa.estado <> 0 or rfa.estado is null) "
                        + "join emisiones ea on ea.idemision = la.idemision "
                        + "join lecturas ln on ei.idlecturanueva = ln.idlectura "
                        + "join facturas fn on ln.idfactura = fn.idfactura "
                        + "join rubroxfac rfn on rfn.idfactura_facturas = ln.idfactura and (rfn.estado <> 0 or rfn.estado is null) "
                        + "join emisiones en on en.idemision = ln.idemision "
                        + "where ei.idemision = ?1 "
                        + "group by rfa.idfactura_facturas, fa.idabonado, la.idfactura, ea.emision, ln.idfactura, en.emision, rfn.idfactura_facturas "
                        + "order by fa.idabonado asc", nativeQuery = true)
        public List<EmisionIndividualRI> getLecReport(Integer idemision);

        @Query(value = "select fa.idabonado as cuenta, la.idfactura as facturaa, ea.emision as emisiona, sum(ROUND(CAST(rfa.cantidad * rfa.valorunitario AS numeric), 2)) as tanterior "
                        + "from emisionindividual ei "
                        + "join lecturas la on ei.idlecturaanterior = la.idlectura "
                        + "join facturas fa on la.idfactura = fa.idfactura "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = la.idfactura and (rfa.estado <> 0 or rfa.estado is null) "
                        + "join emisiones ea on ea.idemision = la.idemision "
                        + "where ei.idemision = ?1 and not rfa.idrubro_rubros = 5 "
                        + "group by rfa.idfactura_facturas, fa.idabonado, la.idfactura, ea.emision  "
                        + "order by fa.idabonado asc;", nativeQuery = true)
        public List<EmisionIndividualRia> _emisionIndividualAnterior(Integer idemision);

        @Query(value = "select fa.idabonado as cuenta, c.nombre as nombrecliente, to_char(fa.fechaeliminacion, 'DD/MM/YYYY') as fechaeliminacion, l.idfactura as facturaa, ea.emision as emisiona, sum(ROUND(CAST(rfa.cantidad * rfa.valorunitario AS numeric), 2)) as tanterior "
                        + "from lecturas l "
                        + "join facturas fa on l.idfactura = fa.idfactura "
                        + "join abonados a on fa.idabonado = a.idabonado "
                        + "join clientes c on a.idcliente_clientes = c.idcliente "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = l.idfactura and (rfa.estado <> 0 or rfa.estado is null) "
                        + "join emisiones ea on ea.idemision = l.idemision "
                        + "where l.idemision = ?1 and rfa.idrubro_rubros <> 5 and fa.fechaeliminacion is not null "
                        + "group by rfa.idfactura_facturas, fa.idabonado, c.nombre, fa.fechaeliminacion, l.idfactura, ea.emision  "
                        + "order by fa.idabonado asc;", nativeQuery = true)
        public List<EmisionIndividualRia> emisionIndividualAnterior(Integer idemision);

        @Query(value = "select fn.idabonado as cuenta, c.nombre as nombrecliente, ln.idfactura as facturan, en.emision as emisionn, sum(ROUND(CAST(rfn.cantidad * rfn.valorunitario AS numeric), 2)) as tnuevo "
                        + "from emisionindividual ei "
                        + "join lecturas ln on ei.idlecturanueva = ln.idlectura "
                        + "join facturas fn on ln.idfactura = fn.idfactura "
                        + "join abonados a on fn.idabonado = a.idabonado "
                        + "join clientes c on a.idcliente_clientes = c.idcliente "
                        + "join rubroxfac rfn on rfn.idfactura_facturas = ln.idfactura and (rfn.estado <> 0 or rfn.estado is null) "
                        + "join emisiones en on en.idemision = ln.idemision "
                        + "where ei.idemision = ?1 and not rfn.idrubro_rubros = 5 "
                        + "group by fn.idabonado, c.nombre, ln.idfactura, en.emision ,rfn.idfactura_facturas "
                        + "order by fn.idabonado asc;", nativeQuery = true)
        public List<EmisionIndividualRin> emisionIndividualNueva(Integer idemision);

        /* REPORTE REFACTURACION X EMISION */@Query(value = """
                        WITH facturas_objetivo AS (
                            SELECT ln.idfactura
                            FROM emisionindividual e
                            JOIN lecturas ln ON e.idlecturanueva = ln.idlectura
                            WHERE e.idemision = :idEmision
                            UNION
                            SELECT la.idfactura
                            FROM emisionindividual e
                            JOIN lecturas la ON e.idlecturaanterior = la.idlectura
                            WHERE e.idemision = :idEmision
                        ), rubros_redondeados AS (
                            SELECT rf.idfactura_facturas,
                                SUM(ROUND(CAST(rf.valorunitario * rf.cantidad AS numeric), 2)) AS total
                            FROM rubroxfac rf
                            JOIN facturas_objetivo fo ON fo.idfactura = rf.idfactura_facturas
                            WHERE (rf.estado <> 0 OR rf.estado IS NULL)
                                AND rf.idrubro_rubros <> 5
                            GROUP BY rf.idfactura_facturas
                        )
                        SELECT
                            ln.idabonado_abonados AS cuenta,
                            c.nombre,
                            fa.fechaeliminacion AS fecelimina,
                            ln.idfactura AS nuevaplanilla,
                            COALESCE(rn.total, 0) AS valornuevo,
                            la.idfactura AS anteriorplanilla,
                            COALESCE(ra.total, 0) AS valoranterior,
                            la.observaciones
                        FROM emisionindividual e
                        JOIN lecturas ln ON e.idlecturanueva = ln.idlectura
                        JOIN abonados a ON ln.idabonado_abonados = a.idabonado
                        JOIN clientes c ON a.idresponsable = c.idcliente
                        JOIN lecturas la ON e.idlecturaanterior = la.idlectura
                        JOIN facturas fa ON la.idfactura = fa.idfactura
                        LEFT JOIN rubros_redondeados rn ON rn.idfactura_facturas = ln.idfactura
                        LEFT JOIN rubros_redondeados ra ON ra.idfactura_facturas = la.idfactura
                        WHERE e.idemision = :idEmision
                        ORDER BY ln.idabonado_abonados ASC
                        """, nativeQuery = true)
        List<R_refacturacion_int> getRefacturacionxEmision(@Param("idEmision") Long idEmision);

        @Query(value = "select ra.idrubro as idrubro_rubros, ra.descripcion, sum(ROUND(CAST(rfa.cantidad * rfa.valorunitario AS numeric), 2)) "
                        + " from emisionindividual e "
                        + " join lecturas ln on e.idlecturaanterior = ln.idlectura "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = ln.idfactura and (rfa.estado <> 0 or rfa.estado is null) "
                        + "join rubros ra on rfa.idrubro_rubros = ra.idrubro "
                        + " where e.idemision = ?1 and not rfa.idrubro_rubros = 5 "
                        + "group by ra.idrubro, ra.descripcion;", nativeQuery = true)
        public List<RubroxfacI> getRefacturacionxEmisionRubrosAnteriores(Long idemision);

        @Query(value = "select rn.idrubro  as idrubro_rubros, rn.descripcion, sum(ROUND(CAST(rfn.cantidad * rfn.valorunitario AS numeric), 2)) "
                        + "from emisionindividual e "
                        + "join lecturas ln on e.idlecturanueva = ln.idlectura "
                        + "join rubroxfac rfn on rfn.idfactura_facturas = ln.idfactura and (rfn.estado <> 0 or rfn.estado is null) "
                        + "join rubros rn on rfn.idrubro_rubros = rn.idrubro "
                        + "where e.idemision = ?1 and not rfn.idrubro_rubros = 5 "
                        + "group by rn.idrubro, rn.descripcion;", nativeQuery = true)
        public List<RubroxfacI> getRefacturacionxEmisionRubrosNuevos(Long idemision);

        @Query(value = """
                            SELECT
                            ln.idabonado_abonados AS cuenta,
                            c.nombre,
                            fa.fechaeliminacion AS fecelimina,
                            ln.idfactura AS nuevaplanilla,
                            ea.emision as emisionanterior,
                            en.emision as emisionnueva,
                            (SELECT SUM(ROUND(CAST(rfn.valorunitario * rfn.cantidad AS numeric), 2))
                            FROM rubroxfac rfn
                            WHERE (rfn.estado <> 0 or rfn.estado is null) and rfn.idfactura_facturas = ln.idfactura and not rfn.idrubro_rubros = 5) AS valornuevo,
                            la.idfactura AS anteriorplanilla,
                            (SELECT SUM(ROUND(CAST(rfa.valorunitario * rfa.cantidad AS numeric), 2))
                            FROM rubroxfac rfa
                            WHERE (rfa.estado <> 0 or rfa.estado is null) and rfa.idfactura_facturas = la.idfactura and not rfa.idrubro_rubros = 5) AS valoranterior,
                            la.observaciones
                        FROM emisionindividual e
                        INNER JOIN lecturas ln ON e.idlecturanueva = ln.idlectura
                        INNER JOIN emisiones en on en.idemision = ln.idemision
                        INNER JOIN abonados a ON ln.idabonado_abonados = a.idabonado
                        INNER JOIN clientes c ON a.idresponsable = c.idcliente
                        INNER JOIN facturas fn ON ln.idfactura = fn.idfactura
                        INNER JOIN lecturas la ON e.idlecturaanterior = la.idlectura
                        INNER JOIN emisiones ea on ea.idemision = ln.idemision
                        INNER JOIN facturas fa ON la.idfactura = fa.idfactura
                        WHERE fa.fechaeliminacion BETWEEN :fechaInicio AND :fechaFin

                        GROUP BY
                            ln.idabonado_abonados,
                            c.nombre,
                            fa.fechaeliminacion,
                            ln.idfactura,
                            la.idfactura,
                            la.observaciones,
                            ea.emision,
                            en.emision
                        ORDER BY ln.idabonado_abonados ASC
                        """, nativeQuery = true)
        List<R_refacturacion_int> getRefacturacionxFecha(
                        @Param("fechaInicio") Date fechaInicio,
                        @Param("fechaFin") Date fechaFin);

        @Query(value = """
                                    select
                        	ra.idrubro as idrubro_rubros,
                        	ra.descripcion,
                        sum(ROUND(CAST(rfa.cantidad * rfa.valorunitario AS numeric), 2))
                        from
                        	emisionindividual e
                        join lecturas la on
                        	e.idlecturaanterior = la.idlectura
                        join facturas fa on la.idfactura = fa.idfactura
                        join rubroxfac rfa on
                        	rfa.idfactura_facturas = la.idfactura and (rfa.estado <> 0 or rfa.estado is null)
                        join rubros ra on
                        	rfa.idrubro_rubros = ra.idrubro
                        where
                        	fa.fechaeliminacion BETWEEN :fechaInicio AND :fechaFin
                        	and rfa.idrubro_rubros not in (5, 165)
                        group by
                        	ra.idrubro,
                        	ra.descripcion;
                                    """, nativeQuery = true)
        public List<RubroxfacI> getRefacturacionxFechaRubrosAnteriores(
                        @Param("fechaInicio") Date fechaInicio,
                        @Param("fechaFin") Date fechaFin);

        @Query(value = """
                            select
                        	ra.idrubro as idrubro_rubros,
                        	ra.descripcion,
                        sum(ROUND(CAST(rfa.cantidad * rfa.valorunitario AS numeric), 2))
                        from
                        	emisionindividual e
                        join lecturas la on
                        	e.idlecturaanterior = la.idlectura
                        join lecturas ln on
                        	e.idlecturanueva = ln.idlectura
                        join facturas fa on la.idfactura = fa.idfactura
                        join rubroxfac rfa on
                        	rfa.idfactura_facturas = ln.idfactura and (rfa.estado <> 0 or rfa.estado is null)
                        join rubros ra on
                        	rfa.idrubro_rubros = ra.idrubro
                        where
                        	fa.fechaeliminacion BETWEEN :fechaInicio AND :fechaFin
                        	and rfa.idrubro_rubros not in (5, 165)
                        group by
                        	ra.idrubro,
                        	ra.descripcion;
                            """, nativeQuery = true)
        public List<RubroxfacI> getRefacturacionxFechaRubrosNuevos(
                        @Param("fechaInicio") Date fechaInicio,
                        @Param("fechaFin") Date fechaFin);

        @Query(value = """
                        select
                        	l.idfactura,
                        	l.idabonado_abonados as cuenta,
                        sum(ROUND(CAST(rf.cantidad * rf.valorunitario AS numeric), 2)) as total,
                        	c.nombre,
                        	f.fechaeliminacion,
                        	f.razoneliminacion,
                                u.nomusu as usuario
                        from
                        	lecturas l
                        join facturas f on
                        	f.idfactura = l.idfactura
                        join rubroxfac rf on
                        	f.idfactura = rf.idfactura_facturas and (rf.estado <> 0 or rf.estado is null)
                        join emisiones e on
                        	l.idemision = e.idemision
                        join clientes c on
                        	f.idcliente = c.idcliente
                        join usuarios u on
                                f.usuarioeliminacion = u.idusuario
                        where
                        	l.idemision = ?1
                        	and not f.fechaeliminacion is null
                                and rf.idrubro_rubros not in (5,165)

                        group by
                        	l.idfactura, c.nombre, f.fechaeliminacion, f.razoneliminacion, l.idabonado_abonados,u.nomusu
                        """, nativeQuery = true)
        public List<FacEliminadas> getFacElimByEmision(Long idemision);

        @Query(value = """
                        select
                                l.idfactura,
                                l.idabonado_abonados as cuenta,
                                sum(ROUND(CAST(rf.cantidad * rf.valorunitario AS numeric), 2)) as total,
                                c.nombre,
                                f.fechaeliminacion,
                                f.razoneliminacion,
                                u.nomusu as usuario
                        from
                                lecturas l
                        join facturas f on
                                f.idfactura = l.idfactura
                        join rubroxfac rf on
                                f.idfactura = rf.idfactura_facturas and (rf.estado <> 0 or rf.estado is null)
                        join emisiones e on
                                l.idemision = e.idemision
                        join clientes c on
                                f.idcliente = c.idcliente
                        join usuarios u on
                                f.usuarioeliminacion = u.idusuario
                        where
                                f.fechaeliminacion between ?1 and ?2
                                and not f.fechaeliminacion is null
                                and rf.idrubro_rubros not in (5,165)
                        group by
                                l.idfactura, c.nombre, f.fechaeliminacion, f.razoneliminacion, l.idabonado_abonados,u.nomusu
                        """, nativeQuery = true)
        public List<FacEliminadas> getFacElimByFechaElimina(LocalDate d, LocalDate h);
}


