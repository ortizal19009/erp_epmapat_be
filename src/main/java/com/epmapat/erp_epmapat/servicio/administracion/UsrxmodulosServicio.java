package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.interfaces.ErpModulosI;
import com.epmapat.erp_epmapat.modelo.administracion.Usrxmodulos;
import com.epmapat.erp_epmapat.repositorio.administracion.UsrxmodulosR;

@Service
public class UsrxmodulosServicio {
    private static final Logger logger = LoggerFactory.getLogger(UsrxmodulosServicio.class);

    @Autowired
    private UsrxmodulosR dao;

    public List<ErpModulosI> findModulosEnabledByUser(Long idusuario) {
        return dao.findModulosEnabledByUser(idusuario);
    }

    public List<Usrxmodulos> FindByUser(Long iduser) {
        return dao.FindByUser(iduser);
    }

    @Transactional
    public Usrxmodulos save(Usrxmodulos usrxmodulos) {
        if (usrxmodulos == null || usrxmodulos.getIdusuario_usuarios() == null
                || usrxmodulos.getIderpmodulo_erpmodulos() == null) {
            throw new IllegalArgumentException("El objeto Usrxmodulos o sus relaciones no pueden ser nulos");
        }

        Long idUsuario = usrxmodulos.getIdusuario_usuarios().getIdusuario();
        Long idErpModulo = usrxmodulos.getIderpmodulo_erpmodulos().getIderpmodulo();

        Usrxmodulos um = dao.findModulos(idUsuario, idErpModulo);

        if (um == null) {
            logger.info("Datos no encontrados, creando nuevo registro para usuario {} y módulo {}", idUsuario,
                    idErpModulo);
            um = usrxmodulos;
        }
        if (um != null) {
            logger.info("Datos encontrados, actualizando registro para usuario {} y módulo {}", idUsuario, idErpModulo);
            um.setEnabled(usrxmodulos.getEnabled());
        }
        return dao.save(um);
    }
}
