package com.epmapat.erp_epmapat.config.jasperConfig;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class ReportCompiler {

    // Method to compile and export a report
    public void compileAndExportReport(String jrxmlResourcePath, String outputPath, Connection con) throws JRException {
        // 1. Get the JRXML file as an InputStream from resources
        InputStream jrxmlStream = getClass().getResourceAsStream(jrxmlResourcePath);

        /*
         * File jrxmlFile = new File("path/to/report.jrxml");
         * JasperReport report =
         * JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
         * // Assuming you have the JRXML content stored as text/clob in your database
         * String jrxmlContent = reportRepository.findJrxmlContentById(reportId);
         * JasperReport report = JasperCompileManager.compileReport(new
         * ByteArrayInputStream(jrxmlContent.getBytes()));
         */

        if (jrxmlStream == null) {
            throw new JRException("Report file not found: " + jrxmlResourcePath);
        }

        // 2. Compile the JRXML file
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        // 3. Create parameters map (empty if no parameters needed)
        Map<String, Object> parameters = new HashMap<>();

        // 4. Fill the report with data
        // Using JREmptyDataSource for demonstration - replace with your actual data
        // source
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);

        // 5. Export to PDF
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
    }

    // Example usage
    /*
     * public static void main(String[] args) {
     * ReportCompiler compiler = new ReportCompiler();
     * try {
     * // Path is relative to your resources folder
     * // Example: if your report is in src/main/resources/reports/sample.jrxml
     * compiler.compileAndExportReport("/reports/sample.jrxml", "output/report.pdf",
     * null);
     * } catch (JRException e) {
     * System.err.println("Error generating report: " + e.getMessage());
     * e.printStackTrace();
     * }
     * }
     */
}