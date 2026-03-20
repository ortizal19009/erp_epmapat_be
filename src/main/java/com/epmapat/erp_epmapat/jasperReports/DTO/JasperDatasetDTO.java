package com.epmapat.erp_epmapat.jasperReports.DTO;

import java.util.List;

import lombok.Data;

@Data
public class JasperDatasetDTO<T> {

   private String reportName;
   private String extension;
   private List<ReportParameterDTO> parameters; // opcional
   private List<T> data;

}