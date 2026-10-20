package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.NtaCreditoSaldos;
import com.epmapat.erp_epmapat.modelo.Ntacredito;

public interface NtacreditoR extends JpaRepository<Ntacredito, Long> {
    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "idabonado_abonados.idresponsable",
            "idabonado_abonados.idcliente_clientes",
            "iddocumento_documentos"
    })
    @Override
    Page<Ntacredito> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "idabonado_abonados.idresponsable",
            "idabonado_abonados.idcliente_clientes",
            "iddocumento_documentos"
    })
    @Override
    List<Ntacredito> findAll();

    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "idabonado_abonados.idresponsable",
            "idabonado_abonados.idcliente_clientes",
            "iddocumento_documentos"
    })
    @Override
    Optional<Ntacredito> findById(Long id);

    @Query(value = """
            select
                n.idntacredito,
                n.idabonado_abonados as cuenta,
                c.nombre as nombre,
                a.idcategoria_categorias as categoria,
                (n.valor - coalesce(n.devengado,0)) as saldo,
                n.devengado
            from ntacredito n
            left join abonados a on n.idabonado_abonados = a.idabonado
            left join clientes c on a.idcliente_clientes = c.idcliente
            where n.idabonado_abonados = ?1 AND n.estado=1 AND n.fechaeliminacion IS NULL ORDER BY n.idntacredito
            """, nativeQuery = true)
    public List<NtaCreditoSaldos> findSaldosByCuenta(Long cuenta);
    @Query(value = "SELECT n.idntacredito, n.idabonado_abonados cuenta, c.nombre, a.idcategoria_categorias categoria, n.disponible saldo, coalesce(n.devengado,0) devengado FROM nc_saldos n JOIN abonados a ON a.idabonado=n.idabonado_abonados JOIN clientes c ON c.idcliente=a.idresponsable WHERE n.idabonado_abonados=?1 AND n.estado=1 AND n.fechaeliminacion IS NULL ORDER BY n.idntacredito", nativeQuery=true)
    List<NtaCreditoSaldos> findSaldosConEndosos(Long cuenta);
/*     @Query(value = "select * from ntacredito where idntacredito =?1", nativeQuery = true)
    public List<Ntacredito> find  */
}
