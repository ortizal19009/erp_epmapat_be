package com.epmapat.erp_epmapat.sri.services;

import org.springframework.stereotype.Component;

@Component
public class Modulo11Calculator {

   private static final int[] PESOS = { 2, 3, 4, 5, 6, 7 };

   public char calcularDigitoVerificador(String base48) {
      if (base48 == null || !base48.matches("\\d{48}")) {
         throw new IllegalArgumentException("La base de la clave de acceso debe tener 48 digitos numericos");
      }
      int suma = 0;
      int indicePeso = 0;
      for (int i = base48.length() - 1; i >= 0; i--) {
         int digito = base48.charAt(i) - '0';
         suma += digito * PESOS[indicePeso];
         indicePeso = (indicePeso + 1) % PESOS.length;
      }
      int digitoVerificador = 11 - (suma % 11);
      if (digitoVerificador == 11) {
         digitoVerificador = 0;
      } else if (digitoVerificador == 10) {
         digitoVerificador = 1;
      }
      return (char) ('0' + digitoVerificador);
   }

   public boolean esValido(String claveAcceso) {
      if (claveAcceso == null || !claveAcceso.matches("\\d{49}")) {
         return false;
      }
      String base48 = claveAcceso.substring(0, 48);
      return claveAcceso.charAt(48) == calcularDigitoVerificador(base48);
   }
}
