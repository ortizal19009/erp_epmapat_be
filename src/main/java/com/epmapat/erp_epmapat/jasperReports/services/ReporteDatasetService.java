package com.epmapat.erp_epmapat.jasperReports.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDatasetDTO;
import com.epmapat.erp_epmapat.jasperReports.DTO.ReportParameterDTO;
import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;
import com.epmapat.erp_epmapat.servicio.administracion.ReportejrService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@RequiredArgsConstructor
@Service

public class ReporteDatasetService {
   
   private final ReporteExportService exportService;

   private final ReportejrService repojrService; // para recuperar el jasper desde la BD
   // Servicio para Dataset
   // private final ReporteDatasetService reporteDatasetService;

   public <T> byte[] generarReporte(JasperDatasetDTO<T> dto) throws JRException, IOException {

      // Recupera el reporte compilado desde la tabla
      Reportesjr reporte = repojrService.findByNomrep(dto.getReportName());
      byte[] jasperBytes = reporte.getJasper();

      // Cargar JasperReport desde bytes
      ByteArrayInputStream jasperStream = new ByteArrayInputStream(jasperBytes);
      JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

      // Parámetros opcionales
      Map<String, Object> jasperParams = new HashMap<>();
      if (dto.getParameters() != null) {
         for (ReportParameterDTO p : dto.getParameters()) {
            jasperParams.put(p.getName(), p.getValue());
         }
      }

      // Dataset enviado desde el frontend
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dto.getData());

      // Llenar reporte
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParams, dataSource);

      // Exportar
      ByteArrayOutputStream out = exportService.export(dto.getExtension(), jasperPrint);

      return out.toByteArray();
   }

   // public byte[] generaPreingresos(JasperDatasetDTO<PreingresosReporte> dto)
   // throws JRException, IOException {

   // // Recupera el reporte compilado desde la tabla
   // Reportesjr reporte = repojrService.findByNomrep(dto.getReportName());
   // byte[] jasperBytes = reporte.getJasper();

   // // Cargar JasperReport desde bytes
   // ByteArrayInputStream jasperStream = new ByteArrayInputStream(jasperBytes);
   // JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

   // // Parámetros opcionales
   // Map<String, Object> jasperParams = new HashMap<>();
   // if (dto.getParameters() != null) {
   // for (ReportParameterDTO p : dto.getParameters()) {
   // jasperParams.put(p.getName(), p.getValue());
   // }
   // }
   // // Dataset enviado desde el frontend
   // JRBeanCollectionDataSource dataSource = new
   // JRBeanCollectionDataSource(dto.getData());
   // // Llenar reporte
   // JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
   // jasperParams, dataSource);
   // // Exportar
   // ByteArrayOutputStream out = exportService.export(dto.getExtension(),
   // jasperPrint);

   // return out.toByteArray();
   // }

   // public byte[] generaAuxingreso(JasperDatasetDTO<AuxingresoReporte> dto)
   // throws JRException, IOException {

   // // Recupera el reporte compilado desde la tabla
   // Reportesjr reporte = repojrService.findByNomrep(dto.getReportName());
   // byte[] jasperBytes = reporte.getJasper();

   // // Cargar JasperReport desde bytes
   // ByteArrayInputStream jasperStream = new ByteArrayInputStream(jasperBytes);
   // JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

   // // Parámetros opcionales
   // Map<String, Object> jasperParams = new HashMap<>();
   // if (dto.getParameters() != null) {
   // for (ReportParameterDTO p : dto.getParameters()) {
   // jasperParams.put(p.getName(), p.getValue());
   // }
   // }
   // // Dataset enviado desde el frontend
   // JRBeanCollectionDataSource dataSource = new
   // JRBeanCollectionDataSource(dto.getData());
   // // Llenar reporte
   // JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
   // jasperParams, dataSource);
   // // Exportar
   // ByteArrayOutputStream out = exportService.export(dto.getExtension(),
   // jasperPrint);

   // return out.toByteArray();
   // }

   

   // private MediaType getMediaType(String extension) {
   //    if (extension == null)
   //       return MediaType.APPLICATION_OCTET_STREAM;

   //    switch (extension.toLowerCase()) {
   //       case "pdf":
   //          return MediaType.APPLICATION_PDF;
   //       case "xlsx":
   //          // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
   //          return MediaType.parseMediaType(
   //                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
   //       case "xls":
   //          return MediaType.parseMediaType("application/vnd.ms-excel");
   //       case "docx":
   //          // application/vnd.openxmlformats-officedocument.wordprocessingml.document
   //          return MediaType.parseMediaType(
   //                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
   //       case "csv":
   //          return MediaType.parseMediaType("text/csv");
   //       case "html":
   //          return MediaType.TEXT_HTML;
   //       case "rtf":
   //          return MediaType.parseMediaType("application/rtf");
   //       case "xml":
   //          return MediaType.APPLICATION_XML;
   //       case "json":
   //          return MediaType.APPLICATION_JSON;
   //       default:
   //          // Fallback genérico
   //          return MediaType.APPLICATION_OCTET_STREAM;
   //    }
   // }

}
