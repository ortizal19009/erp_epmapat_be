package com.epmapat.erp_epmapat.sri.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import org.testng.annotations.Test;

public class Modulo11CalculatorTest {

   private final Modulo11Calculator calculator = new Modulo11Calculator();

   @Test
   public void calculaDigitoVerificadorParaBase48() {
      String base48 = "070520260712345678900121001001000000123123456781";

      char digito = calculator.calcularDigitoVerificador(base48);

      assertEquals('3', digito);
      assertTrue(calculator.esValido(base48 + digito));
   }

   @Test
   public void rechazaClaveConDigitoVerificadorIncorrecto() {
      String clave = "07052026071234567890012100100100000012312345610";

      assertFalse(calculator.esValido(clave));
   }

   @Test
   public void exigeBaseDe48Digitos() {
      expectThrows(IllegalArgumentException.class, () -> calculator.calcularDigitoVerificador("123"));
   }
}
