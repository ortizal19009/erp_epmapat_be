package com.epmapat.erp_epmapat.jasperReports.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDTO;
import com.epmapat.erp_epmapat.jasperReports.utils.JasperReportLoader;
import com.epmapat.erp_epmapat.jasperReports.utils.ReportCache;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import java.io.File;

@RequiredArgsConstructor
@Service
public class BuildReports {
    private final ReportCache reportCache;
    private final JasperReportLoader loader;

    public ByteArrayOutputStream buildReport(JasperDTO jasperDTO, Connection conn) {
        Map<String, Object> parameters = jasperDTO.getParameters();

        try (
                // Connection conn = dataSource.getConnection(); // Se cierra automáticamente
                InputStream reportStream = getClass()
                        .getResourceAsStream("/reports/" + jasperDTO.getReportName() + ".jrxml")) {
            if (reportStream == null) {
                throw new RuntimeException("Plantilla " + jasperDTO.getReportName() + ".jrxml no encontrada");
            }

            parameters.put(JRParameter.REPORT_CONNECTION, conn); // Pasamos la conexión a los subreportes

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Usa JREmptyDataSource si el reporte principal no usa datos directamente
            // JRDataSource emptyDataSource = new JREmptyDataSource();
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

    public JasperPrint buildPrint(String reportName, Map<String, Object> params, Connection conn) throws JRException {
        JasperReport jr = reportCache.getCompiled(reportName);
        return JasperFillManager.fillReport(jr, params, conn);
    }

    public JasperPrint fillFromCompiled(String reportName, Map<String, Object> params, Connection conn)
            throws JRException {
        // Asegura SUBREPORT_DIR si lo usas en jrxml
        try {
            if (params != null && !params.containsKey("SUBREPORT_DIR")) {
                // Resuelve el directorio "reports/" en el classpath (terminado en "/")
                String subDir = new ClassPathResource("reports/").getURL().getPath();
                if (!subDir.endsWith(File.separator))
                    subDir += File.separator;
                params.put("SUBREPORT_DIR", subDir);
            }
        } catch (Exception ignore) {
        }

        JasperReport jr = loader.load(reportName);
        return JasperFillManager.fillReport(jr, params, conn);
    }
}
