package com.epmapat.erp_epmapat.jasperReports.DTO;

import java.util.List;

import lombok.Data;

@Data
public class JasperBeanDTO {

   private String reportName; // Nombre del archivo jasper sin extensión
   private String extension; // pdf, xlsx, csv
   private List<ReportParameterDTO> parameters; // Parámetros del reporte
   private List<?> beanCollection;

   public void setParameter(String name, Object value) {
      if (parameters == null)
         return;

      for (ReportParameterDTO p : parameters) {
         if (p.getName().equals(name)) {
            p.setValue(value);
            return;
         }
      }
      // Si no existe, agrega
      parameters.add(new ReportParameterDTO(name, value));
   }

}