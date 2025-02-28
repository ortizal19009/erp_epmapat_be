package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Ntacredito;
import com.epmapat.erp_epmapat.repositorio.NtacreditoR;

@Service
public class NtacreditoServicio {
    @Autowired
    private NtacreditoR dao;

    public List<Ntacredito> findAll(){
        return dao.findAll();
    }
}
