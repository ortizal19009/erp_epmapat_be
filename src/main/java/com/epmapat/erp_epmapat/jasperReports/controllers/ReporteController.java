package com.epmapat.erp_epmapat.jasperReports.controllers;

import java.io.ByteArrayOutputStream;

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
import com.epmapat.erp_epmapat.jasperReports.services.ReporteExportService;
import com.epmapat.erp_epmapat.jasperReports.services.ReporteSqlService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperPrint;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reporteJasper")
@CrossOrigin("*")

public class ReporteController {

   private final ReporteSqlService reporteSqlService; // Servicio para SQL Directo
   private final ReporteExportService exportService; // Servicio para Exportar

   // ========== SQL DIRECTO ==========
   @PostMapping("/sql")
   public ResponseEntity<byte[]> descargarSql(@RequestBody JasperDTO dto) {
      try {
         JasperPrint print = reporteSqlService.fillSqlReport(dto);
         ByteArrayOutputStream stream = exportService.export(dto.getExtension(), print);
         return buildResponse(dto.getReportName(), dto.getExtension(), stream);
      } catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
   }

   private ResponseEntity<byte[]> buildResponse(String name, String ext, ByteArrayOutputStream stream) {

      MediaType mediaType;

      switch (ext.toLowerCase()) {
         case "pdf":
            mediaType = MediaType.APPLICATION_PDF;
            break;
         case "xlsx":
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            break;
         case "csv":
            mediaType = MediaType.TEXT_PLAIN;
            break;
         default:
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
      }

      return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name + "." + ext)
            .contentType(mediaType)
            .body(stream.toByteArray());
   }

}
