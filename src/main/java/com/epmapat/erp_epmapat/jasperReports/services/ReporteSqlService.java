package com.epmapat.erp_epmapat.jasperReports.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDTO;
import com.epmapat.erp_epmapat.jasperReports.DTO.JasperParameterDTO;
import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;
import com.epmapat.erp_epmapat.servicio.administracion.ReportejrService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

@RequiredArgsConstructor
@Service
public class ReporteSqlService {

   private final ReportejrService repojrService; // Servicio para buscar Reportesjr por nombre
   private final DataSource dataSource;

   public JasperPrint fillSqlReport(JasperDTO dto) throws Exception {

      Reportesjr reporte = repojrService.findByNomrep(dto.getReportName());

      Map<String, Object> params = convertParams(dto.getParameters());

      JasperReport jasperReport = (JasperReport) JRLoader.loadObject(
            new ByteArrayInputStream(reporte.getJasper()));
      params.forEach((k, v) -> {
         System.out.println(k + " => " + v + " (" + v.getClass().getName() + ")");
      });

      try (Connection conn = dataSource.getConnection()) {
         return JasperFillManager.fillReport(jasperReport, params, conn);
      }
   }

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
      }
      return out;
   }

   private Map<String, Object> convertParams(List<JasperParameterDTO> parametros) throws JRException {
      Map<String, Object> params = new HashMap<>();
      // Map<String, Object> params = new LinkedHashMap<>();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

      if (parametros != null) {
         for (JasperParameterDTO p : parametros) {
            if (p.getValue() == null)
               continue;

            Object valorConvertido;
            String valorTexto = p.getValue().toString();

            switch (p.getType()) {
               case "java.lang.String":
                  valorConvertido = p.getValue().toString();
                  break;
               case "java.lang.Integer":
                  valorConvertido = Integer.valueOf(p.getValue().toString());
                  break;
               case "java.lang.Long":
                  valorConvertido = Long.valueOf(p.getValue().toString());
                  break;
               case "java.lang.Boolean":
                  valorConvertido = Boolean.valueOf(p.getValue().toString());
                  break;
               case "java.util.Date":
                  try {
                     valorConvertido = sdf.parse(valorTexto);
                  } catch (ParseException e) {
                     throw new JRException("Error parseando fecha para parámetro " + p.getName(), e);
                  }
                  break;
               default:
                  valorConvertido = p.getValue();
            }
            params.put(p.getName(), valorConvertido);
         }
      }
      return params;
   }

}
