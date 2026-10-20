package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.NtaCreditoSaldos;
import com.epmapat.erp_epmapat.modelo.Ntacredito;
import com.epmapat.erp_epmapat.repositorio.NtacreditoR;

@Service
public class NtacreditoServicio {
    @Autowired
    private NtacreditoR dao;

    @org.springframework.beans.factory.annotation.Value("${erp.nc-endosos.enabled:false}")
    private boolean endososEnabled;
    @Autowired private com.epmapat.erp_epmapat.nc.NcSchemaState ncSchema;
    @Autowired private org.springframework.jdbc.core.JdbcTemplate jdbc;
    public void bloquearCuenta(Long cuenta) {
        jdbc.queryForList("SELECT idntacredito FROM ntacredito WHERE idabonado_abonados=? ORDER BY idntacredito FOR UPDATE", cuenta);
    }
    public List<Ntacredito> findAll() {
        var result=dao.findAll(); result.forEach(this::enriquecer); return result;
    }

    public Ntacredito save(Ntacredito ntacredito) {
        return dao.save(ntacredito);
    }

    public Optional<Ntacredito> findById(Long idntacredito) {
        return dao.findById(idntacredito).map(this::enriquecer);
    }
    public Page<Ntacredito> findAllNtaCredito(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return dao.findAll(pageable).map(this::enriquecer);
    }
    public List<NtaCreditoSaldos> findSaldosByCuenta(Long cuenta){
        return (endososEnabled || ncSchema.migrated()) ? dao.findSaldosConEndosos(cuenta) : dao.findSaldosByCuenta(cuenta);
    }
    private Ntacredito enriquecer(Ntacredito n) {
        if(endososEnabled || ncSchema.migrated()) {
            var rows=jdbc.queryForList("SELECT disponible,endosado FROM nc_saldos WHERE idntacredito=?",n.getIdntacredito());
            if(!rows.isEmpty()){ n.setSaldoDisponible((java.math.BigDecimal)rows.get(0).get("disponible")); n.setTotalEndosado((java.math.BigDecimal)rows.get(0).get("endosado")); }
        }
        return n;
    }
}
