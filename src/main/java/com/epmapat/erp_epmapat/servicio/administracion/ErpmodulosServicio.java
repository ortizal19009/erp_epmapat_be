package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.ErpmodulosAuditDTO;
import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulos;
import com.epmapat.erp_epmapat.repositorio.administracion.ErpmodulosR;
import com.epmapat.erp_epmapat.servicio.AuditoriaGenericaService;

@Service
public class ErpmodulosServicio {
    @Autowired
    private ErpmodulosR dao;

    @Autowired
    private AuditoriaGenericaService auditoriaService;

    public List<Erpmodulos> findAll() {
        return dao.findAll();
    }

    public List<Erpmodulos> findByPlatform(String platform) {
        return dao.findByPlatform(platform);
    }

    public Optional<Erpmodulos> findById(Long id) {
        return dao.findById(id);
    }

    public Erpmodulos save(Erpmodulos erpmodulos) {
        return dao.save(erpmodulos);
    }

    public Erpmodulos actualizarErpmoduloConAuditoria(Long iderpmodulo, Erpmodulos erpmodulos, Long usumodi,
            String observacion, String tipo) {
        Erpmodulos actual = dao.findById(iderpmodulo)
                .orElseThrow(() -> new RuntimeException("Módulo ERP no encontrado: " + iderpmodulo));

        ErpmodulosAuditDTO auditDTO = new ErpmodulosAuditDTO(
                actual.getIderpmodulo(),
                actual.getDescripcion(),
                actual.getPlatform());

        auditoriaService.saveAudit("erpmodulos", actual.getIderpmodulo(), auditDTO, usumodi, observacion, tipo);

        actual.setDescripcion(erpmodulos.getDescripcion());
        actual.setPlatform(erpmodulos.getPlatform());

        return dao.save(actual);
    }

}
