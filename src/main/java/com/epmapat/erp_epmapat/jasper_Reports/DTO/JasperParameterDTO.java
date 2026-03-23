package com.epmapat.erp_epmapat.jasper_Reports.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JasperParameterDTO {

   private String name; // nombre del parámetro
   private String type; // tipo de dato (String, Integer, Date, etc.)
   private Object value; // valor del parámetro

}
