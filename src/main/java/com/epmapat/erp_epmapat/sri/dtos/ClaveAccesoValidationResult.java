package com.epmapat.erp_epmapat.sri.dtos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClaveAccesoValidationResult {

   private final String claveAcceso;
   private final boolean valida;
   private final List<String> errores;

   private ClaveAccesoValidationResult(String claveAcceso, boolean valida, List<String> errores) {
      this.claveAcceso = claveAcceso;
      this.valida = valida;
      this.errores = errores == null ? List.of() : Collections.unmodifiableList(errores);
   }

   public static ClaveAccesoValidationResult valida(String claveAcceso) {
      return new ClaveAccesoValidationResult(claveAcceso, true, List.of());
   }

   public static ClaveAccesoValidationResult invalida(String claveAcceso, List<String> errores) {
      return new ClaveAccesoValidationResult(claveAcceso, false, new ArrayList<>(errores));
   }

   public String getClaveAcceso() {
      return claveAcceso;
   }

   public boolean isValida() {
      return valida;
   }

   public List<String> getErrores() {
      return errores;
   }
}
