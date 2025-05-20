package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.NtaCreditoSaldos;
import com.epmapat.erp_epmapat.modelo.Ntacredito;

public interface NtacreditoR extends JpaRepository<Ntacredito, Long> {
    @Query(value = "SELECT * FROM ntacredito", nativeQuery = true)
    public Page<Ntacredito> findAllNtaCreditos(Pageable pageable);
    @Query(value = "select n.idntacredito, n.idabonado_abonados as cuenta, (n.valor - n.devengado) as saldo, n.devengado from ntacredito n where n.idabonado_abonados = ?1", nativeQuery = true)
    public List<NtaCreditoSaldos> findSaldosByCuenta(Long cuenta);
/*     @Query(value = "select * from ntacredito where idntacredito =?1", nativeQuery = true)
    public List<Ntacredito> find  */
}