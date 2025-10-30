package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulos;
import com.epmapat.erp_epmapat.repositorio.administracion.ErpmodulosR;

@Service
public class ErpmodulosServicio {
    @Autowired
    private ErpmodulosR dao;

    public List<Erpmodulos> findAll() {
        return dao.findAll();
    }
}
