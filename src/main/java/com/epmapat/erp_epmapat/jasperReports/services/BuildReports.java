package com.epmapat.erp_epmapat.jasperReports.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDTO;


import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class BuildReports {


    public ByteArrayOutputStream buildReport(JasperDTO jasperDTO, Connection conn) {
        Map<String, Object> parameters = jasperDTO.getParameters();

        try (
            //Connection conn = dataSource.getConnection(); // Se cierra automáticamente
                InputStream reportStream = getClass()
                        .getResourceAsStream("/reports/" + jasperDTO.getReportName() + ".jrxml")) {
            if (reportStream == null) {
                throw new RuntimeException("Plantilla " + jasperDTO.getReportName() + ".jrxml no encontrada");
            }

            parameters.put(JRParameter.REPORT_CONNECTION, conn); // Pasamos la conexión a los subreportes

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Usa JREmptyDataSource si el reporte principal no usa datos directamente
            //JRDataSource emptyDataSource = new JREmptyDataSource();
            // Configurar tema del gráfico

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);
            return pdfStream;

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

}
