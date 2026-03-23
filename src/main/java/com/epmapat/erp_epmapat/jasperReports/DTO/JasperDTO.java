package com.epmapat.erp_epmapat.jasperReports.DTO;

import java.util.List;

import lombok.Data;

@Data
public class JasperDTO {

   private String reportName;
   private List<JasperParameterDTO> parameters;
   private String extension;

}
