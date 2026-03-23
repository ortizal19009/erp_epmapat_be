package com.epmapat.erp_epmapat.DTO.contabilidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CuentasReporte {

   private String codcue;
   private String nomcue;
   private String grucue;
   private Integer movcue;
   private Integer sigef;
   private Integer tiptran;
   private String asodebe;
   private String asohaber;

}
