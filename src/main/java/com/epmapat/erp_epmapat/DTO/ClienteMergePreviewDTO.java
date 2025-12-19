package com.epmapat.erp_epmapat.DTO;

import java.util.List;

public class ClienteMergePreviewDTO {

  private Long clienteId;
  private String nombre;
  private String cedula;

  private List<AbonadoDTO> abonados;
  private List<FacturaPendienteDTO> facturasPendientes;

}
