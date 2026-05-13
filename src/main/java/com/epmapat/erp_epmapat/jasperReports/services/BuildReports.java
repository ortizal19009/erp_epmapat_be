package com.epmapat.erp_epmapat.jasperReports.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.jasperReports.DTO.Jasper_DTO;
import com.epmapat.erp_epmapat.jasperReports.utils.JasperReportLoader;
import com.epmapat.erp_epmapat.jasperReports.utils.ReportCache;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import java.io.File;

@RequiredArgsConstructor
@Service
public class BuildReports {
    private static final int RECEIPT_CUT_PADDING_POINTS = 2;
    private static final int MAX_RECEIPT_HEIGHT_POINTS = 567;

    private final ReportCache reportCache;
    private final JasperReportLoader loader;

    public ByteArrayOutputStream buildReport(Jasper_DTO jasperDTO, Connection conn) {
        Map<String, Object> parameters = new HashMap<>(jasperDTO.getParameters());

        try (
                // Connection conn = dataSource.getConnection(); // Se cierra automáticamente
                InputStream reportStream = getClass()
                        .getResourceAsStream("/reports/" + jasperDTO.getReportName() + ".jrxml")) {
            if (reportStream == null) {
                throw new RuntimeException("Plantilla " + jasperDTO.getReportName() + ".jrxml no encontrada");
            }

            parameters.put(JRParameter.REPORT_CONNECTION, conn); // Pasamos la conexión a los subreportes

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            parameters = normalizeParametersForReport(jasperReport, parameters);

            // Usa JREmptyDataSource si el reporte principal no usa datos directamente
            // JRDataSource emptyDataSource = new JREmptyDataSource();
            // Configurar tema del gráfico

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
            trimReceiptPaper(jasperDTO.getReportName(), jasperPrint);

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
        JasperPrint jasperPrint = JasperFillManager.fillReport(jr, normalizeParametersForReport(jr, params), conn);
        trimReceiptPaper(reportName, jasperPrint);
        return jasperPrint;
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
        JasperPrint jasperPrint = JasperFillManager.fillReport(jr, normalizeParametersForReport(jr, params), conn);
        trimReceiptPaper(reportName, jasperPrint);
        return jasperPrint;
    }

    private Map<String, Object> normalizeParametersForReport(JasperReport report, Map<String, Object> inputParams) {
        Map<String, Object> normalized = new HashMap<>();
        if (inputParams != null) {
            normalized.putAll(inputParams);
        }

        if (report == null || report.getParameters() == null) {
            return normalized;
        }

        for (JRParameter parameter : report.getParameters()) {
            if (parameter == null || parameter.isSystemDefined()) {
                continue;
            }

            String name = parameter.getName();
            if (!normalized.containsKey(name)) {
                continue;
            }

            Object value = normalized.get(name);
            Object converted = convertValue(value, parameter.getValueClass());
            normalized.put(name, converted);
        }

        return normalized;
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null || targetType == null || targetType.isInstance(value)) {
            return value;
        }

        if (targetType == Integer.class) {
            if (value instanceof Number number) {
                return number.intValue();
            }
            return Integer.valueOf(value.toString().trim());
        }

        if (targetType == Long.class) {
            if (value instanceof Number number) {
                return number.longValue();
            }
            return Long.valueOf(value.toString().trim());
        }

        if (targetType == Double.class) {
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            return Double.valueOf(value.toString().trim());
        }

        if (targetType == Float.class) {
            if (value instanceof Number number) {
                return number.floatValue();
            }
            return Float.valueOf(value.toString().trim());
        }

        if (targetType == String.class) {
            return value.toString();
        }

        if (targetType == Timestamp.class && value instanceof Date date) {
            return new Timestamp(date.getTime());
        }

        if (targetType == Time.class && value instanceof Date date) {
            return new Time(date.getTime());
        }

        return value;
    }

    private void trimReceiptPaper(String reportName, JasperPrint jasperPrint) {
        if (!isReceiptReport(reportName) || jasperPrint == null || jasperPrint.getPages() == null
                || jasperPrint.getPages().isEmpty()) {
            return;
        }

        int usedHeight = 0;
        for (JRPrintPage page : jasperPrint.getPages()) {
            if (page.getElements() == null) {
                continue;
            }
            for (JRPrintElement element : page.getElements()) {
                usedHeight = Math.max(usedHeight, element.getY() + element.getHeight());
            }
        }

        if (usedHeight <= 0) {
            return;
        }

        int currentHeight = jasperPrint.getPageHeight();
        int trimmedHeight = Math.min(currentHeight, usedHeight + RECEIPT_CUT_PADDING_POINTS);
        trimmedHeight = Math.min(trimmedHeight, MAX_RECEIPT_HEIGHT_POINTS);
        jasperPrint.setPageHeight(trimmedHeight);
        jasperPrint.setTopMargin(0);
        jasperPrint.setBottomMargin(0);
        jasperPrint.setLeftMargin(0);
        jasperPrint.setRightMargin(0);
    }

    private boolean isReceiptReport(String reportName) {
        return reportName != null && reportName.startsWith("CompPago");
    }
}
