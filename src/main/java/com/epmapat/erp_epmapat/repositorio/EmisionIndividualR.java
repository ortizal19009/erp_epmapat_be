package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRI;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRia;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRin;
import com.epmapat.erp_epmapat.interfaces.IemiIndividual;
import com.epmapat.erp_epmapat.interfaces.R_refacturacion_int;
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

        /* REPORTE REFACTURACION X EMISION */
        @Query(value = "select ln.idabonado_abonados as cuenta, c.nombre, fa.fechaeliminacion as fecelimina ,  ln.idfactura as nuevaplanilla, fn.totaltarifa as valornuevo,  la.idfactura as anteriorplanilla, fa.totaltarifa as valoranterior, la.observaciones "
                        + " from emisionindividual e "
                        + " join lecturas ln on e.idlecturanueva = ln.idlectura"
                        + " join abonados a on ln.idabonado_abonados = a.idabonado "
                        + " join clientes c on a.idresponsable = c.idcliente "
                        + " join facturas fn on ln.idfactura = fn.idfactura "
                        + " join lecturas la on e.idlecturaanterior = la.idlectura "
                        + " join facturas fa on la.idfactura = fa.idfactura "
                        + " where e.idemision = ?1 "
                        + " order by ln.idabonado_abonados asc", nativeQuery = true)
        public List<R_refacturacion_int> getRefacturacionxEmision(Long idemision);

        @Query(value = "select ln.idabonado_abonados as cuenta, c.nombre ,  fa.fechaeliminacion as fecelimina , ln.idfactura as nuevaplanilla, fn.totaltarifa as valornuevo,  la.idfactura as anteriorplanilla, fa.totaltarifa as valoranterior, la.observaciones "
                        + " from emisionindividual e "
                        + " join lecturas ln on e.idlecturanueva = ln.idlectura"
                        + " join abonados a on ln.idabonado_abonados = a.idabonado "
                        + " join clientes c on a.idresponsable = c.idcliente "
                        + " join facturas fn on ln.idfactura = fn.idfactura "
                        + " join lecturas la on e.idlecturaanterior = la.idlectura "
                        + " join facturas fa on la.idfactura = fa.idfactura "
                        + " where fa.fechaeliminacion between ?1 and ?2 "
                        + " order by ln.idabonado_abonados asc", nativeQuery = true)
        public List<R_refacturacion_int> getRefacturacionxFecha(Date d, Date h);
}
