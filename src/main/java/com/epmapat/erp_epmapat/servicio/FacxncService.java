package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.NtaCreditoCompPago;
import com.epmapat.erp_epmapat.modelo.Facxnc;
import com.epmapat.erp_epmapat.repositorio.FacxncR;

@Service
public class FacxncService {
    @Autowired
    private FacxncR dao;

    public Facxnc save(Facxnc fxnc) {
        return dao.save(fxnc);
    }

    public List<Facxnc> findByNc(Long idvalcn) {
        return null;
    }

    public List<NtaCreditoCompPago> findByIdfactura(Long idfactura) {
        return dao.findByIdfactura(idfactura);
    }
}
