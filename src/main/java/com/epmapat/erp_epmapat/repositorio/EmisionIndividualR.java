package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.IemiIndividual;
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
}
