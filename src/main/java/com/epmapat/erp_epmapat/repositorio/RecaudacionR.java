package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.RecaudaFacturasI;
import com.epmapat.erp_epmapat.interfaces.RecaudadorI;
import com.epmapat.erp_epmapat.modelo.Recaudacion;

public interface RecaudacionR extends JpaRepository<Recaudacion, Long> {
        @Query(value = "SELECT SUM(valor) FROM recaudacion WHERE recaudador = ?1 and fechacobro = ?2 ", nativeQuery = true)
        Double totalRecaudado(Long idrecaudador, Date fechacobro);

        @Query(value = "select * from recaudacion where recaudador = ?1 and (Date(fechacobro) between ?2 and ?3)", nativeQuery = true)
        public List<Recaudacion> findByRecFec(Long idrecauddor, Date d, Date h);

        @Query(value = "select * from recaudacion where Date(fechacobro) between ?1 and ?2 order by recaudador", nativeQuery = true)
        public List<Recaudacion> findByFecha(Date d, Date h);

        /* encontrar el listado de los recaudadores de una fecha */
        @Query(value = "select r.recaudador from recaudacion r where Date(fechacobro) between ?1 and ?2 group by r.recaudador order by recaudador", nativeQuery = true)
        public List<RecaudadorI> findListRecaudador(Date d, Date h);

        //
        @Query(value = "select f.idfactura,c.nombre,f.nrofactura,f.estado,f.formapago, f.fechacobro, u.nomusu, f.idabonado," +
                        "sum((rf.cantidad * rf.valorunitario)+f.swiva ) as valor " +
                        "from recaudacion r " +
                        "join facxrecauda fr on r.idrecaudacion = fr.idrecaudacion " +
                        "join facturas f on fr.idfactura = f.idfactura " +
                        "join usuarios u on f.usuariocobro = u.idusuario "+
                        "join rubroxfac rf on rf.idfactura_facturas = f.idfactura " +
                        "join clientes c on c.idcliente = f.idcliente " +
                        "where r.fechacobro between ?1 and ?2 and not rf.idrubro_rubros = 165 " +
                        "group by f.idfactura, c.nombre, f.nrofactura, f.estado, f.formapago, f.fechacobro, u.nomusu " +
                        "order by f.nrofactura asc", nativeQuery = true)
        public List<RecaudaFacturasI> findFacturasToReport(LocalDateTime d, LocalDateTime h);

        @Query(value = "select rf.idrubro_rubros, rb.descripcion, COUNT(f.idfactura) as nfacturas, sum((rf.cantidad * rf.valorunitario)+f.swiva) as valor "
                        +
                        "from recaudacion r join facxrecauda fr on r.idrecaudacion = fr.idrecaudacion " +
                        "join facturas f on fr.idfactura = f.idfactura " +
                        "join rubroxfac rf on rf.idfactura_facturas = f.idfactura " +
                        "join rubros rb on rf.idrubro_rubros = rb.idrubro " +
                        "where r.fechacobro between ?1 and ?2 and f.feccrea <= ?3and not rf.idrubro_rubros = 165  " +
                        "group by rf.idrubro_rubros, rb.descripcion", nativeQuery = true)
        public Object[] findRubrosAnterioresToReport(LocalDateTime d, LocalDateTime h, LocalDate t);

        @Query(value = "select rf.idrubro_rubros, rb.descripcion, COUNT(f.idfactura) as nfacturas, sum((rf.cantidad * rf.valorunitario)+f.swiva ) as valor "
                        +
                        "from recaudacion r join facxrecauda fr on r.idrecaudacion = fr.idrecaudacion " +
                        "join facturas f on fr.idfactura = f.idfactura " +
                        "join rubroxfac rf on rf.idfactura_facturas = f.idfactura " +
                        "join rubros rb on rf.idrubro_rubros = rb.idrubro " +
                        "where r.fechacobro between ?1 and ?2 and f.feccrea > ?3 and rf.idrubro_rubros = 165 " +
                        "group by rf.idrubro_rubros, rb.descripcion", nativeQuery = true)
        public Object[] findRubrosActualesToReport(LocalDateTime d, LocalDateTime h, LocalDate t);

}
