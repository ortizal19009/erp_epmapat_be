package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Fec_factura_pagos;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_pagosR;
@Service
public class Fec_factura_pagosServicio {
    @Autowired
    private Fec_factura_pagosR dao;

    public List<Fec_factura_pagos> findAll(){
        return dao.findAll();
    }
    public <S extends Fec_factura_pagos> S save(S entity) {
        return dao.save(entity);
    }

}
