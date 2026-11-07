package com.epmapat.erp_epmapat.DTO.contabilidad;

import java.util.List;

/** Las filas contienen exactamente los campos del archivo, sin cabeceras. */
public record SinafipResultado(String archivo, int anio, int periodo, String corte,
      List<Bloque> bloques, List<String> avisos) {
   public record Bloque(String nombre, List<String> columnas, List<List<String>> filas) {}
}
