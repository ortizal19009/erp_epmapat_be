package com.epmapat.erp_epmapat.sri.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.sql.Date;
import java.util.Optional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_retencionesR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;

public class RetencionClaveAccesoServiceTest {
   private final RetencionesR repo = mock(RetencionesR.class);
   private final Fec_retencionesR electronicas = mock(Fec_retencionesR.class);
   private final DefinirR configuracion = mock(DefinirR.class);
   private final Modulo11Calculator modulo = new Modulo11Calculator();
   private final RetencionClaveAccesoService service = new RetencionClaveAccesoService(repo, electronicas, configuracion, modulo);
   private Retenciones retencion;
   private Definir definir;
   private static final String CLAVE = "2508202607046002881000120010010000069355396000017";

   @BeforeMethod public void preparar() {
      reset(repo, electronicas, configuracion);
      org.springframework.test.util.ReflectionTestUtils.setField(service, "serieEmisor", "001001");
      definir = new Definir(); definir.setRuc("0460028810001"); definir.setTipoambiente((byte) 2);
      when(configuracion.findTopByOrderByIddefinirDesc()).thenReturn(definir);
      retencion = new Retenciones(); retencion.setIdrete(182L); retencion.setEstado(0);
      retencion.setNumserie("001001"); retencion.setSecretencion1("6935");
      retencion.setFechaemiret1(Date.valueOf("2026-08-25"));
      retencion.setFechaemision(Date.valueOf("2026-08-19"));
      when(repo.findById(182L)).thenReturn(Optional.of(retencion));
      when(repo.save(retencion)).thenReturn(retencion);
   }

   @Test public void generaLosNueveBloquesConLongitudesExactas() {
      String clave = service.asegurarClaveAcceso(retencion).getClaveacceso();
      assertTrue(clave.matches("[0-9]{49}"));
      assertEquals("25082026", clave.substring(0, 8));
      assertEquals("07", clave.substring(8, 10));
      assertEquals("0460028810001", clave.substring(10, 23));
      assertEquals("2", clave.substring(23, 24));
      assertEquals("001001", clave.substring(24, 30));
      assertEquals("000006935", clave.substring(30, 39));
      assertTrue(clave.substring(39, 47).matches("[0-9]{8}"));
      assertEquals("1", clave.substring(47, 48));
      assertEquals(digitoIndependiente(clave.substring(0, 48)), clave.charAt(48));
      verify(repo).save(retencion);
   }

   @Test public void elXmlContieneLaMismaClaveYLosMismosDatosDeEmision() {
      String clave = service.asegurarClaveAcceso(retencion).getClaveacceso();
      var sri = new RetencionSRIService();
      var impuestos = mock(com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_reteimpuR.class);
      when(impuestos.findByIdretencionOrderByIdretencionesimpuestosAsc(182L)).thenReturn(java.util.List.of());
      org.springframework.test.util.ReflectionTestUtils.setField(sri, "retencionesR", repo);
      org.springframework.test.util.ReflectionTestUtils.setField(sri, "definirR", configuracion);
      org.springframework.test.util.ReflectionTestUtils.setField(sri, "fecReteimpuR", impuestos);
      String xml = sri.generarXml(182L);
      assertTrue(xml.contains("<claveAcceso>" + clave + "</claveAcceso>"));
      assertTrue(xml.contains("<ruc>" + clave.substring(10, 23) + "</ruc>"));
      assertTrue(xml.contains("<ambiente>" + clave.substring(23, 24) + "</ambiente>"));
      assertTrue(xml.contains("<estab>" + clave.substring(24, 27) + "</estab>"));
      assertTrue(xml.contains("<ptoEmi>" + clave.substring(27, 30) + "</ptoEmi>"));
      assertTrue(xml.contains("<secuencial>" + clave.substring(30, 39) + "</secuencial>"));
      assertTrue(xml.contains("<fechaEmision>25/08/2026</fechaEmision>"));
   }

   @Test public void serieDelProveedorNoCambiaLaSerieDelEmisor() {
      retencion.setNumserie("003002");
      String clave = service.asegurarClaveAcceso(retencion).getClaveacceso();
      assertEquals("001001", clave.substring(24, 30));
      assertEquals("003002", retencion.getNumserie());
      assertTrue(service.validarClaveAcceso(clave, retencion, definir).isValida());
   }

   @Test public void validaLasSeisClavesDelSistemaAnterior() {
      String[] claves = {
         "2201202607046002881000120010010000067877876000018",
         "2704202607046002881000120010010000068544586000010",
         "0605202607046002881000120010010000068599586000014",
         "0605202607046002881000120010010000068600686000011",
         "2508202607046002881000120010010000069355396000017",
         "1011202607046002881000120010010000069622696000011"
      };
      for (String clave : claves) {
         assertEquals(digitoIndependiente(clave.substring(0, 48)), clave.charAt(48));
         assertTrue(service.validarClaveAcceso(clave).isValida());
      }
   }

   @Test public void conservaUnaClaveValidaEnReintentos() {
      retencion.setClaveacceso(CLAVE);
      assertEquals(CLAVE, service.asegurarClaveAcceso(retencion).getClaveacceso());
      assertEquals(CLAVE, service.asegurarClaveAcceso(retencion).getClaveacceso());
      verify(repo, never()).save(any());
   }

   @Test public void modulo11IncluyeCasosEspecialesCeroYUno() {
      assertEquals('7', modulo.calcularDigitoVerificador(CLAVE.substring(0, 48)));
      assertEquals('0', modulo.calcularDigitoVerificador("0".repeat(48)));
      assertEquals('1', modulo.calcularDigitoVerificador("0".repeat(47) + "6"));
      for (int i = 0; i < 1000; i++) {
         String base = "250820260704600288100012001001000006935" + String.format(java.util.Locale.ROOT, "%08d", i) + "1";
         assertEquals(digitoIndependiente(base), modulo.calcularDigitoVerificador(base));
      }
   }

   @Test public void rechazaCaracteresAdicionalesSinOcultarlos() {
      assertFalse(service.validarClaveAcceso(CLAVE + ".").isValida());
      assertFalse(service.validarClaveAcceso("X" + CLAVE).isValida());
      assertFalse(service.validarClaveAcceso(CLAVE.substring(0, 20) + " " + CLAVE.substring(20)).isValida());
      retencion.setClaveacceso(CLAVE + ".");
      assertTrue(service.asegurarClaveAcceso(retencion).getClaveacceso().matches("[0-9]{49}"));
      verify(repo).save(retencion);
   }

   @Test public void detectaCambioDeFechaSerieSecuencialRucYAmbiente() {
      assertTrue(service.validarClaveAcceso(CLAVE, retencion, definir).isValida());
      retencion.setFechaemiret1(Date.valueOf("2026-08-26"));
      assertFalse(service.validarClaveAcceso(CLAVE, retencion, definir).isValida());
      retencion.setFechaemiret1(Date.valueOf("2026-08-25"));
      org.springframework.test.util.ReflectionTestUtils.setField(service, "serieEmisor", "001002");
      assertFalse(service.validarClaveAcceso(CLAVE, retencion, definir).isValida());
      org.springframework.test.util.ReflectionTestUtils.setField(service, "serieEmisor", "001001"); retencion.setSecretencion1("6936");
      assertFalse(service.validarClaveAcceso(CLAVE, retencion, definir).isValida());
      retencion.setSecretencion1("6935"); definir.setTipoambiente((byte) 1);
      assertFalse(service.validarClaveAcceso(CLAVE, retencion, definir).isValida());
      definir.setTipoambiente((byte) 2); definir.setRuc("0990000000001");
      assertFalse(service.validarClaveAcceso(CLAVE, retencion, definir).isValida());
   }

   @Test public void rechazaDigitoVerificadorIncorrecto() {
      assertFalse(service.validarClaveAcceso(CLAVE.substring(0, 48) + "8").isValida());
   }

   @Test public void noGeneraSiLaConfiguracionEsInvalida() {
      definir.setRuc("123");
      assertThrows(IllegalStateException.class, () -> service.asegurarClaveAcceso(retencion));
      verify(repo, never()).save(any());
   }

   @Test public void noPersisteDuplicadosSiSeAgotanLosReintentos() {
      when(electronicas.existsByClaveaccesoAndIdretencionNot(anyString(), eq(182L))).thenReturn(true);
      assertThrows(IllegalStateException.class, () -> service.asegurarClaveAcceso(retencion));
      verify(repo, never()).save(any());
   }

   @Test public void noRegeneraUnaRetencionYaAutorizada() {
      retencion.setEstado(1); retencion.setClaveacceso(CLAVE);
      assertThrows(IllegalStateException.class, () -> service.regenerarClaveAcceso(182L));
      verify(repo, never()).save(any());
   }

   private char digitoIndependiente(String base) {
      int suma = 0;
      for (int i = 0; i < 48; i++) suma += (base.charAt(i) - '0') * (2 + (47 - i) % 6);
      int resultado = (11 - suma % 11) % 11;
      return (char) ('0' + (resultado == 10 ? 1 : resultado));
   }
}
