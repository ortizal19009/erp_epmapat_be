package com.epmapat.erp_epmapat.servicio.contabilidad;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.epmapat.erp_epmapat.config.JacksonHibernateConfig;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;

class PresupuestoJsonTest {
   @Test void moduloHibernateIncluyeLaEjecucionSinPersistirColumnasNuevas() throws Exception {
      var p = new Presupue(); p.setIntpre(7L); p.setInicia(100.0);
      p.setEjecCompromiso(new BigDecimal("80.50"));
      p.setEjecDevengado(new BigDecimal("60.25")); p.setEjecCobpagado(BigDecimal.ZERO);
      var mapper = new ObjectMapper().registerModule(new JacksonHibernateConfig().hibernate5Module());
      var json = mapper.readTree(mapper.writeValueAsString(p));
      assertEquals(new BigDecimal("80.5"), json.get("ejecCompromiso").decimalValue());
      assertEquals(new BigDecimal("60.25"), json.get("ejecDevengado").decimalValue());
      assertEquals(0, json.get("ejecCobpagado").decimalValue().signum());
      assertEquals(100.0, json.get("inicia").asDouble());
      for (String campo : new String[]{"ejecCompromiso", "ejecDevengado", "ejecCobpagado"}) {
         assertTrue(java.lang.reflect.Modifier.isTransient(Presupue.class.getDeclaredField(campo).getModifiers()));
      }
   }
}
