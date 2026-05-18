package com.epmapat.erp_epmapat.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FecMailQueueRequestDto {
   private List<Long> idfacturas;
   private Long usuarioSolicita;
   private Integer prioridad;
   private String ipSolicita;
}
