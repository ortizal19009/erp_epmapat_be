package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.ErpModulosI;
import com.epmapat.erp_epmapat.repositorio.administracion.UsrxmodulosR;

@Service
public class UsrxmodulosServicio {
    @Autowired
    private UsrxmodulosR dao;

    public List<ErpModulosI> findModulosEnabledByUser(Long idusuario) {
        return dao.findModulosEnabledByUser(idusuario);
    }

}
