package com.epmapat.erp_epmapat.servicio;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.AuditoriaGenerica;
import com.epmapat.erp_epmapat.repositorio.AuditoriaGenericaR;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class AuditoriaGenericaService {

    private final AuditoriaGenericaR auditoriaGenericaR;
    private final ObjectMapper objectMapper;

    public AuditoriaGenericaService(AuditoriaGenericaR auditoriaGenericaR) {
        this.auditoriaGenericaR = auditoriaGenericaR;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void saveAudit(String entidad,
                          Long entidadId,
                          Object estadoInicial,
                          Long usumodi,
                          String observacion,
                          String tipo) {

        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(estadoInicial);
        } catch (JsonProcessingException e) {
            json = "{\"serializationError\":\"" + e.getMessage().replaceAll("\"", "\\\"") + "\"}";
        }

        AuditoriaGenerica audit = new AuditoriaGenerica();
        audit.setEntidad(entidad);
        audit.setEntidadId(entidadId);
        audit.setUsumodi(usumodi);
        audit.setFecmodi(new Timestamp(System.currentTimeMillis()));
        audit.setObservacion(observacion);
        audit.setTipo(tipo == null || tipo.isBlank() ? "MODIFICACION" : tipo);
        audit.setObjectJson(json);

        auditoriaGenericaR.save(audit);
    }

    public AuditoriaGenerica saveAuditEntry(Map<String, Object> payload) {
        String entidad = text(payload.get("entidad"));
        if (entidad == null || entidad.isBlank()) {
            entidad = "EMISION";
        }

        Long entidadId = number(payload.get("idregistro"));
        Long usuario = number(payload.get("usuario"));
        String accion = text(payload.get("accion"));
        Object detalle = payload.get("detalle");

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("accion", accion);
        envelope.put("resultado", text(payload.get("resultado")));
        envelope.put("mensaje", text(payload.get("mensaje")));
        envelope.put("usuario", usuario);
        envelope.put("fecha", payload.get("fecha"));
        envelope.put("emision", payload.get("emision"));
        envelope.put("resumen", payload.get("resumen"));
        envelope.put("rutas", payload.get("rutas"));
        envelope.put("lecturas", payload.get("lecturas"));
        envelope.put("facturas", payload.get("facturas"));
        envelope.put("documentos", payload.get("documentos"));
        if (detalle != null) {
            envelope.put("detalle", detalle);
        }

        String observacion = accion == null || accion.isBlank() ? "AUDITORIA EMISION" : accion;
        saveAudit(entidad, entidadId, envelope, usuario == null ? 0L : usuario, observacion, "MODIFICACION");
        return auditoriaGenericaR.findAll().stream().reduce((first, second) -> second).orElse(null);
    }

    public List<Map<String, Object>> consultarAuditoriaEmisiones(
            Long idemision,
            String accion,
            String desde,
            String hasta) {

        Timestamp fechaDesde = parseDesde(desde);
        Timestamp fechaHasta = parseHasta(hasta);
        List<String> entidades = List.of("EMISION", "EMISIONES");
        List<AuditoriaGenerica> rows = idemision == null
                ? auditoriaGenericaR.findByEntidadInOrderByFecmodiDesc(entidades)
                : auditoriaGenericaR.findByEntidadInAndEntidadIdOrderByFecmodiDesc(entidades, idemision);

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (AuditoriaGenerica row : rows) {
            if (fechaDesde != null && row.getFecmodi() != null && row.getFecmodi().before(fechaDesde)) {
                continue;
            }
            if (fechaHasta != null && row.getFecmodi() != null && row.getFecmodi().after(fechaHasta)) {
                continue;
            }

            Object detalle = parseJson(row.getObjectJson());
            String accionRow = resolveAccion(detalle, row);
            if (accion != null && !accion.isBlank() && !accion.equalsIgnoreCase(accionRow)) {
                continue;
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idauditoria", row.getIdauditoria());
            item.put("idregistro", row.getEntidadId());
            item.put("entidad", row.getEntidad());
            item.put("usuario", row.getUsumodi());
            item.put("fecha", row.getFecmodi());
            item.put("tipo", row.getTipo());
            item.put("observacion", row.getObservacion());
            item.put("accion", accionRow);
            item.put("detalle", detalle);
            resultado.add(item);
        }

        return resultado;
    }

    private Timestamp parseDesde(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        LocalDate fecha = LocalDate.parse(valor.trim());
        return Timestamp.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Timestamp parseHasta(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        LocalDate fecha = LocalDate.parse(valor.trim());
        return Timestamp.from(fecha.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }

    private String resolveAccion(Object detalle, AuditoriaGenerica row) {
        if (detalle instanceof Map<?, ?> map) {
            Object accion = map.get("accion");
            if (accion != null && !String.valueOf(accion).isBlank()) {
                return String.valueOf(accion).trim().toUpperCase();
            }
            Object emision = map.get("emision");
            if (emision instanceof Map<?, ?> emisionMap) {
                Object accionEmision = emisionMap.get("accion");
                if (accionEmision != null && !String.valueOf(accionEmision).isBlank()) {
                    return String.valueOf(accionEmision).trim().toUpperCase();
                }
            }
        }
        if (row.getObservacion() != null && !row.getObservacion().isBlank()) {
            String obs = row.getObservacion().trim().toUpperCase();
            if ("ANULAR".equals(obs) || "REABRIR".equals(obs) || "ELIMINAR".equals(obs)) {
                return obs;
            }
        }
        return row.getEntidad() != null && row.getEntidad().equalsIgnoreCase("EMISIONES")
                ? "ANULAR"
                : "REABRIR";
    }

    private Object parseJson(String valor) {
        if (valor == null || valor.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(valor, Object.class);
        } catch (Exception e) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("texto", valor);
            return fallback;
        }
    }

    private String text(Object valor) {
        return valor == null ? null : String.valueOf(valor);
    }

    private Long number(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(valor));
        } catch (Exception e) {
            return null;
        }
    }
}
