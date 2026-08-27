package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.CondMultasIntereses;

import javax.persistence.LockModeType;

public interface CondMultasInteresesR extends JpaRepository<CondMultasIntereses, Long> {
    @Query("""
            select c
            from CondMultasIntereses c
            join fetch c.idfactura_facturas f
            left join fetch f.idcliente
            left join fetch c.usuarioAprueba
            order by c.idcondmultainteres desc
            """)
    List<CondMultasIntereses> findAllDetalleOrderByIdDesc();

    @Query("""
            select c
            from CondMultasIntereses c
            join fetch c.idfactura_facturas f
            left join fetch f.idcliente
            left join fetch c.usuarioAprueba
            where c.idcondmultainteres = :id
            """)
    Optional<CondMultasIntereses> findDetalleById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c
            from CondMultasIntereses c
            join fetch c.idfactura_facturas f
            where c.idcondmultainteres = :id
            """)
    Optional<CondMultasIntereses> findForUpdate(@Param("id") Long id);

    @Query("""
            select count(c) > 0
            from CondMultasIntereses c
            where c.idfactura_facturas.idfactura = :idfactura
              and upper(c.estado) in ('PENDIENTE', 'APROBADO')
              and ((:consideraInteres = true and c.totalinteres is not null and c.totalinteres > 0)
                   or (:consideraMulta = true and c.totalmultas is not null and c.totalmultas > 0))
            """)
    boolean existsSolicitudActiva(
            @Param("idfactura") Long idfactura,
            @Param("consideraInteres") boolean consideraInteres,
            @Param("consideraMulta") boolean consideraMulta);

}
