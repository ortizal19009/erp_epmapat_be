package com.epmapat.erp_epmapat.servicio;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.ConvenioAuditDTO;
import com.epmapat.erp_epmapat.interfaces.ConvenioOneData;
import com.epmapat.erp_epmapat.interfaces.EstadoConvenios;
import com.epmapat.erp_epmapat.modelo.Convenios;
import com.epmapat.erp_epmapat.repositorio.ConveniosR;
import com.epmapat.erp_epmapat.servicio.AuditoriaGenericaService;

@Service
public class ConvenioServicio {

    @Autowired
    private ConveniosR dao;

    @Autowired
    private AuditoriaGenericaService auditoriaService;

    public List<Convenios> conveniosDesdeHasta(Integer desde, Integer hasta) {
        return dao.findByNroconvenioBetweenOrderByNroconvenioAsc(desde, hasta);
    }

    public List<Convenios> findNroconvenio(Long nroconvenio) {
        return dao.findNroconvenio(nroconvenio);
    }

    // Último Nroconvenio
    public Convenios ultimoNroconvenio() {
        return dao.findFirstByOrderByNroconvenioDesc();
    }

    // Siguiente Nroconvenio
    public Integer siguienteNroconvenio() {
        Convenios x = dao.findTopByOrderByNroconvenioDesc();
        if (x != null) {
            Integer ultConvenio = x.getNroconvenio();
            return ultConvenio + 1;
        } else {
            return 1;
        }
    }

    // Valida Nroconvenio
    public boolean valNroconvenio(Integer nroconvenio) {
        return dao.valNroconvenio(nroconvenio);
    }

    @SuppressWarnings("null")
    public <S extends Convenios> boolean existsByNroconvenio() {
        return dao.exists(null);
    }

    @SuppressWarnings("null")
    public <S extends Convenios> S save(S entity) {
        return dao.save(entity);
    }

    @SuppressWarnings("null")
    public Optional<Convenios> findById(Long id) {
        return dao.findById(id);
    }

    public Convenios actualizarConvenioConAuditoria(Long idconvenio, Convenios convenioM, Long usumodi, String observacion, String tipo) {
        Convenios convenioOriginal = dao.findById(idconvenio)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + idconvenio));

        auditoriaService.saveAudit("convenios", convenioOriginal.getIdconvenio(), buildAuditDTO(convenioOriginal), usumodi, observacion, tipo);

        if (convenioM.getReferencia() != null) {
            convenioOriginal.setReferencia(convenioM.getReferencia());
        }
        if (convenioM.getNroautorizacion() != null) {
            convenioOriginal.setNroautorizacion(convenioM.getNroautorizacion());
        }
        if (convenioM.getEstado() != null) {
            convenioOriginal.setEstado(convenioM.getEstado());

            if (convenioM.getEstado() == 2 || convenioM.getEstado() == 3) {
                // estado 2 = ANULADO, estado 3 = ELIMINADO
                convenioOriginal.setUsuarioeliminacion(convenioM.getUsuarioeliminacion());
                convenioOriginal.setFechaeliminacion(convenioM.getFechaeliminacion() != null ? convenioM.getFechaeliminacion() : java.time.LocalDate.now());
                convenioOriginal.setRazoneliminacion(convenioM.getRazoneliminacion());
            }
        }

        if (convenioM.getObservaciones() != null) {
            convenioOriginal.setObservaciones(convenioM.getObservaciones());
        }
        if (convenioM.getTotalconvenio() != null) {
            convenioOriginal.setTotalconvenio(convenioM.getTotalconvenio());
        }
        if (convenioM.getCuotainicial() != null) {
            convenioOriginal.setCuotainicial(convenioM.getCuotainicial());
        }
        if (convenioM.getPagomensual() != null) {
            convenioOriginal.setPagomensual(convenioM.getPagomensual());
        }
        if (convenioM.getCuotafinal() != null) {
            convenioOriginal.setCuotafinal(convenioM.getCuotafinal());
        }

        convenioOriginal.setUsumodi(usumodi);
        convenioOriginal.setFecmodi(new Timestamp(System.currentTimeMillis()));

        return dao.save(convenioOriginal);
    }

    public Convenios actualizarEstadoConvenioConAuditoria(Long idconvenio, Integer estado, Long usumodi, String observacion, String tipo) {
        Convenios convenioOriginal = dao.findById(idconvenio)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + idconvenio));

        auditoriaService.saveAudit("convenios", convenioOriginal.getIdconvenio(), buildAuditDTO(convenioOriginal), usumodi, observacion, tipo);

        convenioOriginal.setEstado(estado);

        if (estado == 2 || estado == 3) {
            convenioOriginal.setFechaeliminacion(java.time.LocalDate.now());
            convenioOriginal.setRazoneliminacion(estado == 2 ? "ANULADO" : "ELIMINADO");
        }

        convenioOriginal.setUsumodi(usumodi);
        convenioOriginal.setFecmodi(new Timestamp(System.currentTimeMillis()));

        return dao.save(convenioOriginal);
    }

    private ConvenioAuditDTO buildAuditDTO(Convenios convenioOriginal) {
        ConvenioAuditDTO dto = new ConvenioAuditDTO();
        dto.setIdconvenio(convenioOriginal.getIdconvenio());
        dto.setNroautorizacion(convenioOriginal.getNroautorizacion());
        dto.setReferencia(convenioOriginal.getReferencia());
        dto.setEstado(convenioOriginal.getEstado());
        dto.setNroconvenio(convenioOriginal.getNroconvenio());
        dto.setTotalconvenio(convenioOriginal.getTotalconvenio());
        dto.setCuotas(convenioOriginal.getCuotas());
        dto.setCuotainicial(convenioOriginal.getCuotainicial());
        dto.setPagomensual(convenioOriginal.getPagomensual());
        dto.setCuotafinal(convenioOriginal.getCuotafinal());
        dto.setObservaciones(convenioOriginal.getObservaciones());
        dto.setUsuarioeliminacion(convenioOriginal.getUsuarioeliminacion());
        dto.setFechaeliminacion(convenioOriginal.getFechaeliminacion());
        dto.setRazoneliminacion(convenioOriginal.getRazoneliminacion());
        dto.setUsucrea(convenioOriginal.getUsucrea());
        dto.setFeccrea(convenioOriginal.getFeccrea());
        dto.setUsumodi(convenioOriginal.getUsumodi());
        dto.setFecmodi(convenioOriginal.getFecmodi());
        dto.setIdabonado(convenioOriginal.getIdabonado() != null ? convenioOriginal.getIdabonado().getIdabonado() : null);
        return dto;
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public List<Convenios> findByReferencia(Long referencia) {
        return dao.findByReferencia(referencia);
    }

    public List<EstadoConvenios> getEstadoByConvenios() {
        return dao.getEstadoByConvenios();
    }

    public Page<EstadoConvenios> getByFacPendientes(Long d, Long h, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return dao.getByFacPendientes(d, h, pageable);
    }

    public List<EstadoConvenios> gePendienteByConvenio(Long idconvenio) {
        return dao.gePendienteByConvenio(idconvenio);
    }

    public List<ConvenioOneData> findDatosConvenio(Long idconvenio) {
        return dao.findDatosConvenio(idconvenio);
    }

}