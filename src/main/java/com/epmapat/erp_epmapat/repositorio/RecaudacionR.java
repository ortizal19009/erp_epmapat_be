package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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


}
