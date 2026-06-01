package com.epmapat.erp_epmapat.servicio;

import com.epmapat.erp_epmapat.DTO.AnularEmisionRequest;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import com.epmapat.erp_epmapat.modelo.administracion.Documentos;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.administracion.DocumentosR;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmisionMantenimientoServicio {

    private final EmisionServicio emisionServicio;
    private final RutasxemisionServicio rutasxemisionServicio;
    private final LecturaServicio lecturaServicio;
    private final FacturasR facturasR;
    private final RubroxfacR rubroxfacR;
    private final DocumentosR documentosR;
    private final AuditoriaGenericaService auditoriaGenericaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmisionMantenimientoServicio(EmisionServicio emisionServicio,
                                        RutasxemisionServicio rutasxemisionServicio,
                                        LecturaServicio lecturaServicio,
                                        FacturasR facturasR,
                                        RubroxfacR rubroxfacR,
                                        DocumentosR documentosR,
                                        AuditoriaGenericaService auditoriaGenericaService) {
        this.emisionServicio = emisionServicio;
        this.rutasxemisionServicio = rutasxemisionServicio;
        this.lecturaServicio = lecturaServicio;
        this.facturasR = facturasR;
        this.rubroxfacR = rubroxfacR;
        this.documentosR = documentosR;
        this.auditoriaGenericaService = auditoriaGenericaService;
    }

    @Transactional
    public Map<String, Object> reabrirEmision(Long idemision, Long usumodi) {
        Emisiones emision = emisionServicio.findById(idemision)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe la Emision Id: " + idemision));

        validarEstadoReapertura(emision);
        validarSinFacturasCobradas(idemision);
        validarSinEmisionPosteriorGenerada(emision);

        List<Rutasxemision> rutas = rutasxemisionServicio.findByIdemision(idemision);
        int rutasActualizadas = 0;

        emision.setEstado(0);
        emision.setM3(0L);
        emision.setUsuariocierre(null);
        emision.setFechacierre(null);
        emision.setUsumodi(usumodi);
        emision.setFecmodi(java.util.Date.from(ZonedDateTime.now(ZoneId.systemDefault()).toInstant()));
        emisionServicio.save(emision);

        for (Rutasxemision ruta : rutas) {
            boolean cambio = ruta.getEstado() == null || ruta.getEstado() != 0
                    || ruta.getUsuariocierre() != null
                    || ruta.getFechacierre() != null
                    || ruta.getM3() == null || ruta.getM3() != 0L
                    || ruta.getTotal() != null && ruta.getTotal().compareTo(BigDecimal.ZERO) != 0;

            ruta.setEstado(0);
            ruta.setUsuariocierre(null);
            ruta.setFechacierre(null);
            ruta.setM3(0L);
            ruta.setTotal(BigDecimal.ZERO);
            rutasxemisionServicio.save(ruta);
            if (cambio) {
                rutasActualizadas++;
            }
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idemision", idemision);
        respuesta.put("estado", 0);
        respuesta.put("rutasActualizadas", rutasActualizadas);
        respuesta.put("totalRutas", rutas.size());
        return respuesta;
    }

    @Transactional
    public Map<String, Object> anularEmision(Long idemision,
                                             AnularEmisionRequest request,
                                             Long usumodi,
                                             HttpServletRequest httpRequest) {
        Emisiones emision = emisionServicio.findById(idemision)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe la Emision Id: " + idemision));

        validarEstadoAnulacion(emision);
        validarRequestAnulacion(request);
        Documentos documento = documentosR.findById(request.getIddocumento())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Debe seleccionar un documento de respaldo válido para continuar."));
        validarSinFacturasCobradas(idemision);

        List<Facturas> facturas = facturasR.findFacturasByEmision(idemision);
        Set<Long> facturaIds = facturas.stream()
                .map(Facturas::getIdfactura)
                .collect(Collectors.toSet());

        Map<String, Object> estadoAnterior = construirEstadoAnterior(emision, facturas);

        int rubrosActualizados = 0;
        if (!facturaIds.isEmpty()) {
            rubrosActualizados = rubroxfacR.desactivarRubrosByFacturaIds(facturaIds);
        }

        int facturasActualizadas = 0;
        for (Facturas factura : facturas) {
            factura.setTotaltarifa(BigDecimal.ZERO);
            factura.setValorbase(BigDecimal.ZERO);
            factura.setRazonanulacion(request.getMotivo().trim());
            factura.setUsuarioanulacion(normalizarUsuario(usumodi));
            factura.setFechaanulacion(java.time.LocalDate.now());
            facturasR.save(factura);
            facturasActualizadas++;
        }

        emision.setEstado(3);
        emision.setMotivoAnulacion(request.getMotivo().trim());
        emision.setIddocumentoAnulacion(documento.getIntdoc());
        emision.setDocumentoAnulacion(documento.getNomdoc());
        emision.setReferenciaDocumentoAnulacion(normalizarTextoOpcional(request.getReferenciaDocumento(), 250));
        emision.setUsuarioAnulacion(normalizarUsuario(usumodi));
        emision.setFechaanulacion(ZonedDateTime.now(ZoneId.systemDefault()));
        emision.setUsumodi(normalizarUsuario(usumodi));
        emision.setFecmodi(java.util.Date.from(ZonedDateTime.now(ZoneId.systemDefault()).toInstant()));
        emisionServicio.save(emision);

        Map<String, Object> estadoNuevo = construirEstadoNuevo(emision, facturasActualizadas, rubrosActualizados);
        registrarAuditoriaAnulacion(idemision, usumodi, request, documento, httpRequest, estadoAnterior, estadoNuevo);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("success", true);
        respuesta.put("message", "Emisión anulada correctamente.");
        respuesta.put("idemision", idemision);
        respuesta.put("estado", 3);
        respuesta.put("facturasActualizadas", facturasActualizadas);
        respuesta.put("rubrosActualizados", rubrosActualizados);
        respuesta.put("documento", documento.getNomdoc());
        return respuesta;
    }

    @Transactional
    public Map<String, Object> eliminarEmision(Long idemision, Long usumodi) {
        Map<String, Object> respuesta = reabrirEmision(idemision, usumodi);
        lecturaServicio.deleteRubrosByIdEmisin(idemision);
        respuesta.put("rubroxfacActualizado", true);
        respuesta.put("accion", "ELIMINADA");
        return respuesta;
    }

    private void validarEstadoReapertura(Emisiones emision) {
        Integer estado = emision.getEstado();
        if (estado == null || !(estado == 1 || estado == 2)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden reabrir emisiones cerradas o reabiertas.");
        }
    }

    private void validarEstadoAnulacion(Emisiones emision) {
        Integer estado = emision.getEstado();
        if (estado == null || !(estado == 1 || estado == 2)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden anular emisiones cerradas o reabiertas.");
        }
    }

    private void validarRequestAnulacion(AnularEmisionRequest request) {
        if (request == null || request.getIddocumento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un documento de respaldo para continuar.");
        }

        String motivo = request.getMotivo() == null ? "" : request.getMotivo().trim();
        if (motivo.length() < 20 || motivo.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El motivo de anulación debe tener entre 20 y 500 caracteres.");
        }
    }

    private void validarSinFacturasCobradas(Long idemision) {
        long cobradas = facturasR.countFacturasCobradasByEmision(idemision);
        if (cobradas > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Existen movimientos financieros asociados a esta emisión. No es posible realizar la operación.");
        }
    }

    private void validarSinEmisionPosteriorGenerada(Emisiones emision) {
        List<Emisiones> posteriores = emisionServicio.findPosterioresByEmisionAndEstadoIn(
                emision.getEmision(), List.of(0, 1, 2));
        if (!posteriores.isEmpty()) {
            Emisiones siguiente = posteriores.get(0);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No es posible reabrir la emisión porque existe una emisión posterior generada: "
                            + siguiente.getEmision() + ".");
        }
    }

    private Long normalizarUsuario(Long usumodi) {
        return usumodi == null ? 0L : usumodi;
    }

    private String normalizarTextoOpcional(String valor, int max) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        return limpio.length() > max ? limpio.substring(0, max) : limpio;
    }

    private Map<String, Object> construirEstadoAnterior(Emisiones emision, List<Facturas> facturas) {
        Map<String, Object> estadoAnterior = new LinkedHashMap<>();
        estadoAnterior.put("emision", emision);
        estadoAnterior.put("facturas", facturas.stream().map(f -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("idfactura", f.getIdfactura());
            row.put("estado", f.getEstado());
            row.put("totaltarifa", f.getTotaltarifa());
            row.put("valorbase", f.getValorbase());
            row.put("pagado", f.getPagado());
            row.put("idabonado", f.getIdabonado());
            return row;
        }).collect(Collectors.toList()));
        return estadoAnterior;
    }

    private Map<String, Object> construirEstadoNuevo(Emisiones emision, int facturasActualizadas, int rubrosActualizados) {
        Map<String, Object> estadoNuevo = new LinkedHashMap<>();
        estadoNuevo.put("idemision", emision.getIdemision());
        estadoNuevo.put("estado", emision.getEstado());
        estadoNuevo.put("motivoAnulacion", emision.getMotivoAnulacion());
        estadoNuevo.put("iddocumentoAnulacion", emision.getIddocumentoAnulacion());
        estadoNuevo.put("documentoAnulacion", emision.getDocumentoAnulacion());
        estadoNuevo.put("referenciaDocumentoAnulacion", emision.getReferenciaDocumentoAnulacion());
        estadoNuevo.put("usuarioAnulacion", emision.getUsuarioAnulacion());
        estadoNuevo.put("fechaanulacion", emision.getFechaanulacion());
        estadoNuevo.put("facturasActualizadas", facturasActualizadas);
        estadoNuevo.put("rubrosActualizados", rubrosActualizados);
        return estadoNuevo;
    }

    private void registrarAuditoriaAnulacion(Long idemision,
                                             Long usumodi,
                                             AnularEmisionRequest request,
                                             Documentos documento,
                                             HttpServletRequest httpRequest,
                                             Map<String, Object> estadoAnterior,
                                             Map<String, Object> estadoNuevo) {
        Map<String, Object> auditPayload = new LinkedHashMap<>();
        auditPayload.put("modulo", "EMISIONES");
        auditPayload.put("accion", "ANULAR");
        auditPayload.put("tabla_afectada", "emisiones");
        auditPayload.put("idregistro", idemision);
        auditPayload.put("estado_anterior", ((Emisiones) estadoAnterior.get("emision")).getEstado());
        auditPayload.put("estado_nuevo", "ANULADA");
        auditPayload.put("observacion", request.getMotivo().trim());
        auditPayload.put("iddocumento", documento.getIntdoc());
        auditPayload.put("documento", documento.getNomdoc());
        auditPayload.put("referencia_documento", normalizarTextoOpcional(request.getReferenciaDocumento(), 250));
        auditPayload.put("usuario", normalizarUsuario(usumodi));
        auditPayload.put("fecha", ZonedDateTime.now(ZoneId.systemDefault()));
        auditPayload.put("ip", extraerIp(httpRequest));
        auditPayload.put("equipo", extraerEquipo());
        auditPayload.put("json_anterior", estadoAnterior);
        auditPayload.put("json_nuevo", estadoNuevo);

        auditoriaGenericaService.saveAudit(
                "EMISIONES",
                idemision,
                auditPayload,
                normalizarUsuario(usumodi),
                request.getMotivo().trim(),
                "MODIFICACION");
    }

    private String extraerIp(HttpServletRequest request) {
        if (request == null) {
            return "DESCONOCIDA";
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extraerEquipo() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "DESCONOCIDO";
        }
    }
}
