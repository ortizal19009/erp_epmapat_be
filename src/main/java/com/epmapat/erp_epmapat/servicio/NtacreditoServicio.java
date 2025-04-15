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

    public List<Ntacredito> findAll() {
        return dao.findAll();
    }

    public Ntacredito save(Ntacredito ntacredito) {
        return dao.save(ntacredito);
    }

    public Optional<Ntacredito> findById(Long idntacredito) {
        return dao.findById(idntacredito);
    }
    public Page<Ntacredito> findAllNtaCredito(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return dao.findAllNtaCreditos(pageable);
        //return null;
    }
    public List<NtaCreditoSaldos> findSaldosByCuenta(Long cuenta){
        return dao.findSaldosByCuenta(cuenta);
    }
}
