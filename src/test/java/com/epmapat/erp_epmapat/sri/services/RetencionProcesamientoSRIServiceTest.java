package com.epmapat.erp_epmapat.sri.services;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.Map;
import java.util.Optional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;
import com.epmapat.erp_epmapat.servicio.contabilidad.RetencionesServicio;
import com.epmapat.erp_epmapat.servicio.contabilidad.Fec_retencionesServicio;
import com.epmapat.erp_epmapat.servicio.administracion.CorreosEnviadosServicio;

public class RetencionProcesamientoSRIServiceTest {
   private static final String CLAVE = "2508202607046002881000120010010000069355396000017";
   private RetencionProcesamientoSRIService service;
   private MockRestServiceServer server;
   private RetencionSRIService documentos;
   private RetencionEmailService correo;
   private Retenciones retencion;
   private ObjectMapper mapper = new ObjectMapper();

   @BeforeMethod
   public void setup() {
      RestTemplate rest = new RestTemplate();
      server = MockRestServiceServer.createServer(rest);
      documentos = mock(RetencionSRIService.class);
      correo = mock(RetencionEmailService.class);
      RetencionesR repo = mock(RetencionesR.class);
      retencion = new Retenciones();
      retencion.setIdrete(1L);
      retencion.setEstado(0);
      retencion.setClaveacceso(CLAVE);
      when(repo.findById(1L)).thenReturn(Optional.of(retencion));
      Fec_retenciones generado = new Fec_retenciones();
      generado.setXmlautorizado(comprobante(CLAVE));
      when(documentos.generarYGuardar(1L)).thenReturn(generado);
      service = new RetencionProcesamientoSRIService(documentos, mock(RetencionPdfService.class), correo,
            mock(RetencionesServicio.class), mock(Fec_retencionesServicio.class), repo, rest,
            mock(CorreosEnviadosServicio.class), mapper, mock(RetencionSseService.class));
      ReflectionTestUtils.setField(service, "sriMicroserviceBaseUrl", "http://sri");
   }

   private String comprobante(String clave) {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><comprobanteRetencion><infoTributaria><claveAcceso>"
            + clave + "</claveAcceso></infoTributaria><infoAdicional>Retencion con \u00f1</infoAdicional></comprobanteRetencion>";
   }

   private void autorizacion(String estado, String clave) throws Exception {
      String json = mapper.writeValueAsString(Map.of("autorizacion", Map.of("estado", estado,
            "numeroAutorizacion", CLAVE, "fechaAutorizacion", "2026-10-27T10:15:00-05:00",
            "ambiente", "PRUEBAS", "comprobante", comprobante(clave))));
      server.expect(requestTo("http://sri/api/v1/autorizacion/" + CLAVE + "?includeXml=true"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Accept", "application/json"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
   }

   @Test
   public void procesaPorRutaV1YConservaAutorizacionSiFallaCorreo() throws Exception {
      server.expect(requestTo("http://sri/api/v1/retenciones")).andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andRespond(withSuccess("{\"estado\":\"AUTORIZADO\"}", MediaType.APPLICATION_JSON));
      autorizacion("AUTORIZADO", CLAVE);
      when(correo.enviarRetencion(1L, null, null, null, true)).thenThrow(new IllegalStateException("SMTP no disponible"));
      Map<String, Object> resultado = service.procesar(1L, null, null, null);
      assertEquals(resultado.get("estado"), "AUTORIZADA");
      assertEquals(resultado.get("emailEnviado"), false);
      assertEquals(retencion.getFecautoriza(), "2026-10-27");
      String xml = (String) resultado.get("xmlautorizado");
      assertTrue(xml.contains("<comprobante><![CDATA[<?xml"));
      assertTrue(xml.contains("Retencion con \u00f1"));
      verify(documentos).actualizarXmlAutorizado(1L, xml, "AUTORIZADA", null);
      server.verify();
   }

   @Test
   public void pendienteNoEnviaCorreo() {
      server.expect(requestTo("http://sri/api/v1/retenciones"))
            .andRespond(withStatus(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON)
                  .body("{\"estado\":\"PENDIENTE_AUTORIZACION\"}"));
      assertEquals(service.procesar(1L, null, null, null).get("estado"), "PENDIENTE_AUTORIZACION");
      verifyNoInteractions(correo);
      server.verify();
   }

   @Test
   public void consultaSinEmitirYRechazoNoProducePdfNiCorreo() throws Exception {
      autorizacion("NO AUTORIZADO", CLAVE);
      assertEquals(service.consultarEstadoPendiente(1L).get("estado"), "NO_AUTORIZADO");
      verifyNoInteractions(correo);
      verify(documentos, never()).generarYGuardar(anyLong());
      server.verify();
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void rechazaXmlDeOtraClave() throws Exception {
      autorizacion("AUTORIZADO", "1".repeat(49));
      service.consultarEstadoPendiente(1L);
   }

   @Test
   public void autorizadaNoSeVuelveAEmitir() {
      retencion.setEstado(1);
      retencion.setNumautoriza_e(CLAVE);
      assertEquals(service.procesar(1L, null, null, null).get("estado"), "YA_AUTORIZADA");
      verifyNoInteractions(correo, documentos);
      server.verify();
   }
}
