package com.epmapat.erp_epmapat.servicio;

import java.sql.Timestamp;

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
}