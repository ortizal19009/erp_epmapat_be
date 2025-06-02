package com.epmapat.erp_epmapat.jasperReports.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDTO;
import com.epmapat.erp_epmapat.jasperReports.services.BuildReports;

@RestController
@RequestMapping("/jasperReports")
@CrossOrigin("*")
public class BuildReportsApi {
    @Autowired
    private BuildReports buildReports;
    @Autowired
    private DataSource dataSource;

    @PostMapping("/reportes")
    public ResponseEntity<Resource> generarPdfFactura(@RequestBody JasperDTO jasperDTO) {
        try {
            // Creamos un nuevo DTO donde meteremos valores ya convertidos
            JasperDTO dto = new JasperDTO();
            dto.setReportName(jasperDTO.getReportName());

            // Formato para parsear cadenas “yyyy-MM-dd” a java.sql.Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // Recorremos cada par <clave, valor> que nos llegó en el JSON
            for (Entry<String, Object> entry : jasperDTO.getParameters().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // Si la clave es “desde” o “hasta”, asumimos que viene como String “yyyy-MM-dd”
                if ("desde".equals(key) || "hasta".equals(key)) {
                    try {
                        java.util.Date parsed = sdf.parse(value.toString());
                        // Lo almacenamos como java.sql.Date para Jasper
                        dto.getParameters().put(key, new java.sql.Date(parsed.getTime()));
                    } catch (ParseException e) {
                        // Si falla el parse, puedes lanzar una excepción controlada o asignar null
                        throw new IllegalArgumentException("La fecha '" + value + "' no tiene formato yyyy-MM-dd", e);
                    }

                } else {
                    /*
                     * Para cualquier otro parámetro numérico (por ejemplo un id), puede venir como:
                     * • Integer (189)
                     * • Long (189L)
                     * • String ("189")
                     *
                     * Lo normal para Jasper es que, si la consulta SQL espera un LONG,
                     * debemos convertirlo a Long en todos los casos.
                     */
                    if (value instanceof Integer) {
                        System.out.println("Integer");
                        // de Integer a Long
                        // dto.getParameters().put(key, ((Integer) value).longValue());
                        dto.getParameters().put(key, ( value));

                    } else if (value instanceof Long) {
                        System.out.println("Long");
                        dto.getParameters().put(key, (Long) value);
                    } else if (value instanceof String) {
                        // intentamos parsear el String a Long
                        try {
                            dto.getParameters().put(key, Long.valueOf((String) value));
                        } catch (NumberFormatException ex) {
                            throw new IllegalArgumentException("El parámetro '" + key +
                                    "' con valor '" + value + "' no es un Long válido", ex);
                        }
                    } else {
                        // Si fuese otro tipo (por ejemplo List<?> u Object), lo dejamos tal cual,
                        // o bien podrías lanzar un error indicando tipo no esperado.
                        dto.getParameters().put(key, value);
                    }
                }
            }

            // Ahora invocamos a buildReport pasándole la conexión y el dto ya “limpio”
            ByteArrayOutputStream pdfStream;
            try (Connection conn = dataSource.getConnection()) {
                pdfStream = buildReports.buildReport(dto, conn);
            }

            // Envolvemos el resultado en un Resource para devolverlo al cliente
            InputStreamResource resource = new InputStreamResource(
                    new ByteArrayInputStream(pdfStream.toByteArray()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + jasperDTO.getReportName() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfStream.size())
                    .body(resource);

        } catch (Exception e) {
            // Aquí podrías registrar el error con un logger y devolver 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
