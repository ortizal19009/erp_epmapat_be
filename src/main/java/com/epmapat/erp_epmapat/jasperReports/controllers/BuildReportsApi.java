package com.epmapat.erp_epmapat.jasperReports.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            JasperDTO dto = new JasperDTO();
            dto.setReportName(jasperDTO.getReportName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (Entry<String, Object> i : jasperDTO.getParameters().entrySet()) {
                String key = i.getKey();
                Object value = i.getValue();

                System.out.println("Key = " + key + ", Value = " + value);

                if ("desde".equals(key) || "hasta".equals(key)) {
                    try {
                        Date parsedDate = sdf.parse(value.toString());
                        dto.getParameters().put(key, new java.sql.Date(parsedDate.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace(); // o lanza una excepción personalizada
                    }
                } else {
                    dto.getParameters().put(key, value);
                }
            }

            ByteArrayOutputStream pdfStream = buildReports.buildReport(dto, dataSource.getConnection());
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfStream.toByteArray()));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + jasperDTO.getReportName() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfStream.size())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
