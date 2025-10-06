package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
        @Query(value = "select * from emisionindividual where idemision = ?1", nativeQuery = true)
        List<EmisionIndividual> findByIdEmision(Long idemision);

        /* REPORTE DE LECTURAS NUEVAS */
        @Query(value = "select r.idrubro_rubros as rubro, rs.descripcion as descripcion,  count(*) as nrofacturas, sum(r.valorunitario * r.cantidad) as sumaTotal from emisionindividual ei join lecturas l on ei.idlecturanueva = l.idlectura join rubroxfac r on l.idfactura = r.idfactura_facturas join rubros rs on r.idrubro_rubros = rs.idrubro where ei.idemision = ?1 and not r.idrubro_rubros = 5 group by r.idrubro_rubros, rs.descripcion ", nativeQuery = true)
        public List<IemiIndividual> findLecturasNuevas(Long idemision);

        /* REPORTE DE LECTURAS ANTERIORES */
        @Query(value = "select r.idrubro_rubros as rubro, rs.descripcion as descripcion,  count(*) as nrofacturas, sum(r.valorunitario * r.cantidad)as sumaTotal from emisionindividual ei join lecturas l on ei.idlecturaanterior = l.idlectura join rubroxfac r on l.idfactura = r.idfactura_facturas join rubros rs on r.idrubro_rubros = rs.idrubro where ei.idemision = ?1 and not r.idrubro_rubros = 5 group by r.idrubro_rubros, rs.descripcion ", nativeQuery = true)
        public List<IemiIndividual> findLecturasAnteriores(Long idemision);

        @Query(value = "select la.idfactura as facturaa, ea.emision as emisiona, ln.idfactura as facturan, en.emision as emisionn, fa.idabonado as cuenta, sum(rfa.valorunitario) as tanterior , sum(rfn.valorunitario) as tnuevo "
                        + "from emisionindividual ei "
                        + "join lecturas la on ei.idlecturaanterior = la.idlectura "
                        + "join facturas fa on la.idfactura = fa.idfactura "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = la.idfactura "
                        + "join emisiones ea on ea.idemision = la.idemision "
                        + "join lecturas ln on ei.idlecturanueva = ln.idlectura "
                        + "join facturas fn on ln.idfactura = fn.idfactura "
                        + "join rubroxfac rfn on rfn.idfactura_facturas = ln.idfactura "
                        + "join emisiones en on en.idemision = ln.idemision "
                        + "where ei.idemision = ?1 and fn.fechaeliminacion is null "
                        + "group by rfa.idfactura_facturas, fa.idabonado, la.idfactura, ea.emision, ln.idfactura, en.emision, rfn.idfactura_facturas "
                        + "order by fa.idabonado asc", nativeQuery = true)
        public List<EmisionIndividualRI> getLecReport(Integer idemision);

        @Query(value = "select fa.idabonado as cuenta, la.idfactura as facturaa, ea.emision as emisiona, sum(rfa.cantidad * rfa.valorunitario) as tanterior "
                        + "from emisionindividual ei "
                        + "join lecturas la on ei.idlecturaanterior = la.idlectura "
                        + "join facturas fa on la.idfactura = fa.idfactura "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = la.idfactura "
                        + "join emisiones ea on ea.idemision = la.idemision "
                        + "where ei.idemision = ?1 and not rfa.idrubro_rubros = 5 "
                        + "group by rfa.idfactura_facturas, fa.idabonado, la.idfactura, ea.emision  "
                        + "order by fa.idabonado asc;", nativeQuery = true)
        public List<EmisionIndividualRia> _emisionIndividualAnterior(Integer idemision);

        @Query(value = "select fa.idabonado as cuenta, l.idfactura as facturaa, ea.emision as emisiona, sum(rfa.cantidad * rfa.valorunitario) as tanterior "
                        + "from lecturas l "
                        + "join facturas fa on l.idfactura = fa.idfactura "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = l.idfactura "
                        + "join emisiones ea on ea.idemision = l.idemision "
                        + "where l.idemision = ?1 and not rfa.idrubro_rubros = 5 and not l.observaciones is null "
                        + "group by rfa.idfactura_facturas, fa.idabonado, l.idfactura, ea.emision  "
                        + "order by fa.idabonado asc;", nativeQuery = true)
        public List<EmisionIndividualRia> emisionIndividualAnterior(Integer idemision);

        @Query(value = "select fn.idabonado  as cuenta, ln.idfactura as facturan, en.emision as emisionn, sum(rfn.cantidad * rfn.valorunitario) as tnuevo "
                        + "from emisionindividual ei "
                        + "join lecturas ln on ei.idlecturanueva = ln.idlectura "
                        + "join facturas fn on ln.idfactura = fn.idfactura "
                        + "join rubroxfac rfn on rfn.idfactura_facturas = ln.idfactura "
                        + "join emisiones en on en.idemision = ln.idemision "
                        + "where ei.idemision = ?1 and fn.fechaeliminacion is null and not rfn.idrubro_rubros = 5 "
                        + "group by fn.idabonado, ln.idfactura, en.emision ,rfn.idfactura_facturas "
                        + "order by fn.idabonado asc;", nativeQuery = true)
        public List<EmisionIndividualRin> emisionIndividualNueva(Integer idemision);

        /* REPORTE REFACTURACION X EMISION */@Query(value = """
                            SELECT
                            ln.idabonado_abonados AS cuenta,
                            c.nombre,
                            fa.fechaeliminacion AS fecelimina,
                            ln.idfactura AS nuevaplanilla,
                            (SELECT SUM(rfn.valorunitario * rfn.cantidad)
                            FROM rubroxfac rfn
                            WHERE rfn.idfactura_facturas = ln.idfactura and not rfn.idrubro_rubros = 5) AS valornuevo,
                            la.idfactura AS anteriorplanilla,
                            (SELECT SUM(rfa.valorunitario * rfa.cantidad)
                            FROM rubroxfac rfa
                            WHERE rfa.idfactura_facturas = la.idfactura and not rfa.idrubro_rubros = 5) AS valoranterior,
                            la.observaciones
                        FROM emisionindividual e
                        INNER JOIN lecturas ln ON e.idlecturanueva = ln.idlectura
                        INNER JOIN abonados a ON ln.idabonado_abonados = a.idabonado
                        INNER JOIN clientes c ON a.idresponsable = c.idcliente
                        INNER JOIN facturas fn ON ln.idfactura = fn.idfactura
                        INNER JOIN lecturas la ON e.idlecturaanterior = la.idlectura
                        INNER JOIN facturas fa ON la.idfactura = fa.idfactura
                        WHERE e.idemision = :idEmision
                        GROUP BY
                            ln.idabonado_abonados,
                            c.nombre,
                            fa.fechaeliminacion,
                            ln.idfactura,
                            la.idfactura,
                            la.observaciones
                        ORDER BY ln.idabonado_abonados ASC
                        """, nativeQuery = true)
        List<R_refacturacion_int> getRefacturacionxEmision(@Param("idEmision") Long idEmision);

        @Query(value = "select ra.idrubro as idrubro_rubros, ra.descripcion, sum(rfa.cantidad * rfa.valorunitario) "
                        + " from emisionindividual e "
                        + " join lecturas ln on e.idlecturaanterior = ln.idlectura "
                        + "join rubroxfac rfa on rfa.idfactura_facturas = ln.idfactura "
                        + "join rubros ra on rfa.idrubro_rubros = ra.idrubro "
                        + " where e.idemision = ?1 and not rfa.idrubro_rubros = 5 "
                        + "group by ra.idrubro, ra.descripcion;", nativeQuery = true)
        public List<RubroxfacI> getRefacturacionxEmisionRubrosAnteriores(Long idemision);

        @Query(value = "select rn.idrubro  as idrubro_rubros, rn.descripcion, sum(rfn.cantidad * rfn.valorunitario) "
                        + "from emisionindividual e "
                        + "join lecturas ln on e.idlecturanueva = ln.idlectura "
                        + "join rubroxfac rfn on rfn.idfactura_facturas = ln.idfactura "
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
                            (SELECT SUM(rfn.valorunitario * rfn.cantidad)
                            FROM rubroxfac rfn
                            WHERE rfn.idfactura_facturas = ln.idfactura and not rfn.idrubro_rubros = 5) AS valornuevo,
                            la.idfactura AS anteriorplanilla,
                            (SELECT SUM(rfa.valorunitario * rfa.cantidad)
                            FROM rubroxfac rfa
                            WHERE rfa.idfactura_facturas = la.idfactura and not rfa.idrubro_rubros = 5) AS valoranterior,
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
                        	sum(rfa.cantidad * rfa.valorunitario)
                        from
                        	emisionindividual e
                        join lecturas la on
                        	e.idlecturaanterior = la.idlectura
                        join facturas fa on la.idfactura = fa.idfactura
                        join rubroxfac rfa on
                        	rfa.idfactura_facturas = la.idfactura
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
                        	sum(rfa.cantidad * rfa.valorunitario)
                        from
                        	emisionindividual e
                        join lecturas la on
                        	e.idlecturaanterior = la.idlectura
                        join lecturas ln on
                        	e.idlecturanueva = ln.idlectura
                        join facturas fa on la.idfactura = fa.idfactura
                        join rubroxfac rfa on
                        	rfa.idfactura_facturas = ln.idfactura
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
                        	sum(rf.cantidad * rf.valorunitario) as total,
                        	c.nombre,
                        	f.fechaeliminacion,
                        	f.razoneliminacion,
                                u.nomusu as usuario
                        from
                        	lecturas l
                        join facturas f on
                        	f.idfactura = l.idfactura
                        join rubroxfac rf on
                        	f.idfactura = rf.idfactura_facturas
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
                                sum(rf.cantidad * rf.valorunitario) as total,
                                c.nombre,
                                f.fechaeliminacion,
                                f.razoneliminacion,
                                u.nomusu as usuario
                        from
                                lecturas l
                        join facturas f on
                                f.idfactura = l.idfactura
                        join rubroxfac rf on
                                f.idfactura = rf.idfactura_facturas
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
