package com.epmapat.erp_epmapat.jasperReports.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDTO;
import com.epmapat.erp_epmapat.jasperReports.services.BuildReports;

@RestController
@RequestMapping("/jasperReports")
public class BuildReportsApi {
    @Autowired
    private BuildReports buildReports;
    @Autowired
    private DataSource dataSource;

    @GetMapping("/reportes")
    public ResponseEntity<Resource> generarPdfFactura(@RequestBody JasperDTO jasperDTO) {
        try {
            JasperDTO dto = new JasperDTO();
            dto.setReportName(jasperDTO.getReportName()); // archivo ResumenEmision.jrxml
            // dto.setParameters(new HashMap<>(jasperDTO.getParameters()));
            System.out.println(jasperDTO.getReportName());

            // using for-each loop for iteration over Map.entrySet()
            for (Entry<String, Object> i : jasperDTO.getParameters().entrySet()) {
                System.out.println("Key = " + i.getKey() +
                        ", Value = " + i.getValue());
                dto.getParameters().put(i.getKey(), i.getValue());
            }
            // parameters.put(JRParameter.REPORT_CONNECTION, connection); // MUY IMPORTANTE

            // Obtén los datos para el DataSource (de tu base de datos o servicio)
            // List<DetalleFactura> detalles =
            // detalleFacturaService.obtenerDetallesPorEmisionId(idemision);

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
