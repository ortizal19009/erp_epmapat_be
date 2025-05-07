package com.epmapat.erp_epmapat.config.jasperConfig;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;


@Service
public class ReportService {
      private final Map<String, JasperReport> reportCache = new ConcurrentHashMap<>();
    
    public byte[] getReport(String jrxmlPath, Map<String, Object> params, Connection con) throws JRException {
        JasperReport report = reportCache.computeIfAbsent(jrxmlPath, path -> {
            try {
                return JasperCompileManager.compileReport(
                    getClass().getResourceAsStream(path));
            } catch (JRException e) {
                throw new RuntimeException("Failed to compile report", e);
            }
        });
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, con);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
