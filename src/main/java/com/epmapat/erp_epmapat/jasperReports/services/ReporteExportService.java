package com.epmapat.erp_epmapat.jasperReports.services;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JsonExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleJsonExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

@Service
public class ReporteExportService {

   public ByteArrayOutputStream export(String ext, JasperPrint print) throws JRException {

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      switch (ext.toLowerCase()) {
         case "pdf":
            JasperExportManager.exportReportToPdfStream(print, out);
            break;
         case "xlsx":
            JRXlsxExporter xlsx = new JRXlsxExporter();
            xlsx.setExporterInput(new SimpleExporterInput(print));
            xlsx.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            xlsx.exportReport();
            break;
         case "csv":
            JRCsvExporter csv = new JRCsvExporter();
            csv.setExporterInput(new SimpleExporterInput(print));
            csv.setExporterOutput(new SimpleWriterExporterOutput(out));
            csv.exportReport();
            break;
         case "xls":
            JRXlsExporter xls = new JRXlsExporter();
            xls.setExporterInput(new SimpleExporterInput(print));
            xls.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            xls.exportReport();
            break;
         case "docx":
            JRDocxExporter docx = new JRDocxExporter();
            docx.setExporterInput(new SimpleExporterInput(print));
            docx.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            docx.exportReport();
            break;
         case "rtf":
            JRRtfExporter rtf = new JRRtfExporter();
            rtf.setExporterInput(new SimpleExporterInput(print));
            rtf.setExporterOutput(new SimpleWriterExporterOutput(out));
            rtf.exportReport();
            break;
         case "html":
            HtmlExporter html = new HtmlExporter();
            html.setExporterInput(new SimpleExporterInput(print));
            html.setExporterOutput(new SimpleHtmlExporterOutput(out));
            html.exportReport();
            break;
         case "xml":
            JasperExportManager.exportReportToXmlStream(print, out);
            break;
         case "json":
            JsonExporter json = new JsonExporter();
            json.setExporterInput(new SimpleExporterInput(print));
            json.setExporterOutput(new SimpleJsonExporterOutput(out));
            json.exportReport();
            break;
         default:
            throw new IllegalArgumentException("Formato no soportado: " + ext);
      }
      return out;
   }

}
