package com.epmapat.erp_epmapat.servicio;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.ConvenioAuditDTO;
import com.epmapat.erp_epmapat.interfaces.ConvenioOneData;
import com.epmapat.erp_epmapat.interfaces.ConvenioDetalle;
import com.epmapat.erp_epmapat.interfaces.EstadoConvenios;
import com.epmapat.erp_epmapat.modelo.Convenios;
import com.epmapat.erp_epmapat.repositorio.ConveniosR;

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
            aplicarMetadatosEstado(convenioOriginal, convenioM.getEstado(), convenioM.getUsuarioeliminacion());
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
        aplicarMetadatosEstado(convenioOriginal, estado, usumodi);

        convenioOriginal.setUsumodi(usumodi);
        convenioOriginal.setFecmodi(new Timestamp(System.currentTimeMillis()));

        return dao.save(convenioOriginal);
    }

    public Page<ConvenioDetalle> buscarConvenios(
            Integer nroDesde,
            Integer nroHasta,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String nombre,
            Integer estado,
            Long minPendientes,
            Long maxPendientes,
            Long idabonado,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        String nombreNormalizado = (nombre == null || nombre.isBlank()) ? null : nombre.trim().toLowerCase();
        return dao.buscarConvenios(nroDesde, nroHasta, fechaDesde, fechaHasta, nombreNormalizado, estado, minPendientes, maxPendientes, idabonado, pageable);
    }

    public List<ConvenioDetalle> getConveniosSinPendientes(Integer estado) {
        return dao.findConveniosSinPendientes(estado);
    }

    public List<Convenios> marcarConveniosPagados(Long usumodi, String observacion, String tipo) {
        List<ConvenioDetalle> conveniosSinPendientes = dao.findConveniosSinPendientes(1);

        return conveniosSinPendientes.stream()
                .map(item -> actualizarEstadoConvenioConAuditoria(item.getIdconvenio(), 3, usumodi, observacion, tipo))
                .collect(Collectors.toList());
    }

    public List<ConvenioDetalle> getConveniosConPendientes(Integer estado) {
        return dao.findConveniosConPendientes(estado);
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

    private void aplicarMetadatosEstado(Convenios convenio, Integer estado, Long usuarioAccion) {
        if (estado == null) {
            return;
        }

        if (estado == 0 || estado == 2) {
            convenio.setUsuarioeliminacion(usuarioAccion);
            convenio.setFechaeliminacion(LocalDate.now());
            convenio.setRazoneliminacion(estado == 2 ? "ANULADO" : "ELIMINADO");
            return;
        }

        if (estado == 3) {
            convenio.setRazoneliminacion("PAGADO");
            return;
        }

        convenio.setUsuarioeliminacion(null);
        convenio.setFechaeliminacion(null);
        convenio.setRazoneliminacion(null);
    }

}
