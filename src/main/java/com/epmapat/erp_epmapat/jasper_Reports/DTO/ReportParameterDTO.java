package com.epmapat.erp_epmapat.jasper_Reports.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportParameterDTO {

   private String name; // nombre del parámetro
   private String type; // "String", "Integer", "Date", etc.
   private Object value; // valor

   public ReportParameterDTO(String name, Object value) {
      this.name = name;
      this.value = value;
      if (value != null) {
         this.type = value.getClass().getSimpleName();
      } else {
         this.type = null;
      }
   }

}
