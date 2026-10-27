package com.epmapat.erp_epmapat.sri.services;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.testng.annotations.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.ArgumentCaptor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.epmapat.erp_epmapat.commons.JasperReportManager;
import com.epmapat.erp_epmapat.modelo.contabilidad.*;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.contabilidad.*;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.servicio.administracion.DefinirServicio;
import com.epmapat.erp_epmapat.servicio.administracion.CorreosEnviadosServicio;
import com.epmapat.erp_epmapat.emails.model.EmailAccount;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.service.EmailAccountService;

public class RetencionDocumentosTest {
   private Retenciones retencion() {
      Retenciones r = new Retenciones();
      r.setIdrete(1L); r.setSecretencion1("17"); r.setEstado(1);
      r.setClaveacceso("2508202607046002881000120010010000069355396000017");
      r.setNumautoriza_e(r.getClaveacceso()); r.setFecautoriza("2026-10-27");
      r.setFechaemision(java.sql.Date.valueOf("2026-10-27")); r.setAmbiente("PRUEBAS");
      r.setNumdoc("001001000000123"); r.setNumserie("001001");
      r.setCodretair("312"); r.setBaseimpair(new BigDecimal("100.00"));
      r.setPorcentajeair(new BigDecimal("1.00")); r.setValretair(new BigDecimal("1.00"));
      Beneficiarios b = new Beneficiarios(); b.setNomben("PROVEEDOR PRUEBA");
      b.setTpidben("04"); b.setRucben("0990000000001"); b.setMailben("original@example.invalid");
      r.setIdbene(b);
      return r;
   }

   @Test
   public void generaRideRealConPlantillaYMetadatos() throws Exception {
      RetencionPdfService pdf = new RetencionPdfService();
      RetencionesR repo = mock(RetencionesR.class);
      Fec_reteimpuR impuestos = mock(Fec_reteimpuR.class);
      DefinirR definir = mock(DefinirR.class);
      ReflectionTestUtils.setField(pdf, "retencionesR", repo);
      ReflectionTestUtils.setField(pdf, "fecReteimpuR", impuestos);
      ReflectionTestUtils.setField(pdf, "definirR", definir);
      ReflectionTestUtils.setField(pdf, "reportManager", new JasperReportManager());
      when(repo.findById(1L)).thenReturn(Optional.of(retencion()));
      when(impuestos.findByIdretencionOrderByIdretencionesimpuestosAsc(1L)).thenReturn(List.of());
      Definir empresa = new Definir(); empresa.setRazonsocial("EMPRESA PRUEBA"); empresa.setRuc("0460028810001");
      when(definir.findTopByOrderByIddefinirDesc()).thenReturn(empresa);
      byte[] bytes = pdf.generarPdf(1L).toByteArray();
      assertTrue(new String(bytes, 0, 5, StandardCharsets.US_ASCII).equals("%PDF-"));
      try (PDDocument doc = PDDocument.load(bytes)) {
         assertTrue(doc.getNumberOfPages() >= 1);
         String texto = new PDFTextStripper().getText(doc);
         assertTrue(texto.contains("PROVEEDOR PRUEBA"));
         assertTrue(texto.replaceAll("\\s+", "").contains(retencion().getNumautoriza_e()), texto);
         assertTrue(texto.contains("RENTA"));
         assertTrue(texto.contains("100"));
      }
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void correoAdjuntaRideYXmlYRespetaDestinatarioExplicito() {
      RetencionEmailService service = new RetencionEmailService();
      RetencionesR repo = mock(RetencionesR.class);
      Fec_retencionesR documentos = mock(Fec_retencionesR.class);
      RetencionSRIService sri = mock(RetencionSRIService.class);
      RetencionPdfService pdf = mock(RetencionPdfService.class);
      EmailService mail = mock(EmailService.class);
      EmailAccountService cuentas = mock(EmailAccountService.class);
      DefinirServicio definir = mock(DefinirServicio.class);
      ReflectionTestUtils.setField(service, "retencionesR", repo);
      ReflectionTestUtils.setField(service, "fecRetencionesR", documentos);
      ReflectionTestUtils.setField(service, "retencionSRIService", sri);
      ReflectionTestUtils.setField(service, "retencionPdfService", pdf);
      ReflectionTestUtils.setField(service, "emailService", mail);
      ReflectionTestUtils.setField(service, "emailAccountService", cuentas);
      ReflectionTestUtils.setField(service, "definirServicio", definir);
      ReflectionTestUtils.setField(service, "correosEnviadosServicio", mock(CorreosEnviadosServicio.class));
      when(repo.findById(1L)).thenReturn(Optional.of(retencion()));
      Fec_retenciones fec = new Fec_retenciones(); fec.setIdretencion(1L); fec.setEstado("AUTORIZADA");
      String xml = "<autorizacion><numeroAutorizacion>123</numeroAutorizacion><comprobante>PRUEBA</comprobante></autorizacion>";
      fec.setXmlautorizado(xml); fec.setEmailsujetoretenido("registrado@example.invalid");
      when(documentos.findById(1L)).thenReturn(Optional.of(fec));
      when(definir.findById(1L)).thenReturn(Optional.of(new Definir()));
      when(pdf.generarPdf(1L)).thenReturn(new java.io.ByteArrayOutputStream());
      when(cuentas.resolveAccount(null, EmailType.DOC_ELECTRONICO)).thenReturn(new EmailAccount());
      when(mail.envioEmail(anyString(), anyString(), anyList(), anyString(), anyString(), anyList())).thenReturn(true);
      fec.setEstado("ENVIADA");
      when(sri.actualizarEstado(1L, "ENVIADA", null, "destino@example.invalid")).thenReturn(fec);
      service.enviarRetencion(1L, "destino@example.invalid", "Prueba", "Prueba");
      ArgumentCaptor<List> adjuntos = ArgumentCaptor.forClass(List.class);
      verify(mail).envioEmail(anyString(), anyString(), eq(List.of("destino@example.invalid")),
            eq("Prueba"), eq("Prueba"), adjuntos.capture());
      List<MultipartFile> archivos = adjuntos.getValue();
      assertEquals(archivos.size(), 2);
      assertEquals(archivos.get(0).getContentType(), "application/pdf");
      assertEquals(archivos.get(1).getContentType(), "application/xml");
      try { assertEquals(new String(archivos.get(1).getBytes(), StandardCharsets.UTF_8), xml); }
      catch (Exception error) { throw new AssertionError(error); }
      verify(sri, never()).generarYGuardar(anyLong());
   }
}

