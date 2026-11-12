package com.epmapat.erp_epmapat.sri.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;

import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_reteimpu;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_reteimpuR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;

public class RetencionSRIServiceTest {

   @Test
   public void generaXmlConsistenteParaRetencionIvaCienPorciento() throws Exception {
      RetencionSRIService service = new RetencionSRIService();

      RetencionesR retencionesR = mock(RetencionesR.class);
      Fec_reteimpuR fecReteimpuR = mock(Fec_reteimpuR.class);
      DefinirR definirR = mock(DefinirR.class);

      ReflectionTestUtils.setField(service, "retencionesR", retencionesR);
      ReflectionTestUtils.setField(service, "fecReteimpuR", fecReteimpuR);
      ReflectionTestUtils.setField(service, "definirR", definirR);

      Retenciones retencion = buildRetencionCasoReferencia();
      Definir definir = buildDefinir();

      when(retencionesR.findById(6935L)).thenReturn(Optional.of(retencion));
      when(definirR.findTopByOrderByIddefinirDesc()).thenReturn(definir);
      when(fecReteimpuR.findByIdretencionOrderByIdretencionesimpuestosAsc(6935L))
            .thenReturn(List.of(
                  buildImpuesto(1L, "1", "312", "54999.53", "001003000876481", "19/08/2026"),
                  buildImpuesto(2L, "2", "802", "8249.93", "876481", "19/08/2026")));

      String xml = service.generarXml(6935L);

      assertTrue(xml.contains("<razonSocial>EPMAPA-T</razonSocial>"));
      assertTrue(xml.contains("<nombreComercial>EPMAPA-T</nombreComercial>"));
      assertTrue(xml.contains("<fechaEmision>25/08/2026</fechaEmision>"));
      assertTrue(xml.contains("<fechaEmisionDocSustento>19/08/2026</fechaEmisionDocSustento>"));
      assertTrue(xml.contains("<estab>001</estab>"));
      assertTrue(xml.contains("<ptoEmi>001</ptoEmi>"));
      assertTrue(xml.contains("<numDocSustento>001003000876481</numDocSustento>"));
      assertTrue(xml.contains("<codigoRetencion>3</codigoRetencion>"));
      assertTrue(xml.contains("<porcentajeRetener>100.00</porcentajeRetener>"));
      assertTrue(xml.contains("<valorRetenido>8249.93</valorRetenido>"));
   }

   @Test
   public void revierteMetadatosSiFallaElGuardadoDelXmlAutorizado() throws Exception {
      var service = org.mockito.Mockito.spy(new RetencionSRIService());
      var metadatos = mock(com.epmapat.erp_epmapat.servicio.contabilidad.RetencionesServicio.class);
      ReflectionTestUtils.setField(service, "retencionesServicio", metadatos);
      var retencion = new Retenciones();
      when(metadatos.guardarAutorizacionSri(1L, retencion)).thenAnswer(invocation -> {
         assertTrue(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());
         return retencion;
      });
      org.mockito.Mockito.doAnswer(invocation -> {
         assertTrue(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());
         throw new IllegalStateException("Fallo al guardar XML");
      }).when(service).actualizarXmlAutorizado(1L, "xml", "AUTORIZADA", null);
      var dataSource = mock(javax.sql.DataSource.class);
      var connection = mock(java.sql.Connection.class);
      when(dataSource.getConnection()).thenReturn(connection);
      when(connection.getAutoCommit()).thenReturn(true);
      var manager = new org.springframework.jdbc.datasource.DataSourceTransactionManager(dataSource);
      var interceptor = new org.springframework.transaction.interceptor.TransactionInterceptor(manager,
            new org.springframework.transaction.annotation.AnnotationTransactionAttributeSource());
      var factory = new org.springframework.aop.framework.ProxyFactory(service);
      factory.setProxyTargetClass(true);
      factory.addAdvice(interceptor);
      var proxy = (RetencionSRIService) factory.getProxy();
      org.testng.Assert.expectThrows(IllegalStateException.class,
            () -> proxy.guardarAutorizacion(1L, retencion, "xml"));
      org.mockito.Mockito.verify(metadatos).guardarAutorizacionSri(1L, retencion);
      org.mockito.Mockito.verify(connection).rollback();
      org.mockito.Mockito.verify(connection, org.mockito.Mockito.never()).commit();
   }

   private Retenciones buildRetencionCasoReferencia() throws Exception {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

      Beneficiarios beneficiario = new Beneficiarios();
      beneficiario.setTpidben("04");
      beneficiario.setNomben("MEXICHEM ECUADOR S.A");
      beneficiario.setRucben("0990003769001");
      beneficiario.setDirben("PARQUE INDUSTRIAL");
      beneficiario.setTlfben("062987000");
      beneficiario.setMailben("tributos@mexichem.ec");

      Retenciones retencion = new Retenciones();
      retencion.setIdrete(6935L);
      retencion.setSecretencion1("6935");
      retencion.setFechaemision(df.parse("19/08/2026"));
      retencion.setFechaemiret1(df.parse("25/08/2026"));
      retencion.setNumdoc("876481");
      retencion.setNumserie("001003");
      retencion.setClaveacceso("2508202607046002881000120010010000069355396000017");
      retencion.setCodretair("312");
      retencion.setBaseimpair(new BigDecimal("54999.53"));
      retencion.setPorcentajeair(new BigDecimal("2.00"));
      retencion.setValretair(new BigDecimal("1099.99"));
      retencion.setCodretserv100("3");
      retencion.setMontoivaserv100(new BigDecimal("8249.93"));
      retencion.setPorretserv100(new BigDecimal("100"));
      retencion.setValretserv100(new BigDecimal("8249.93"));
      retencion.setIdbene(beneficiario);
      return retencion;
   }

   private Definir buildDefinir() {
      Definir definir = new Definir();
      definir.setTipoambiente((byte) 2);
      definir.setRazonsocial("EMPRESA PUBLICA MUNICIPAL DE AGUA POTABLE Y ALCANTARILLADO DE TULCAN");
      definir.setNombrecomercial("EPMAPA-T");
      definir.setRuc("0460028810001");
      definir.setDirmatriz("JUAN RAMON ARELLANO Y BOLIVAR");
      definir.setDireccion("JUAN RAMON ARELLANO Y BOLIVAR");
      return definir;
   }

   private Fec_reteimpu buildImpuesto(Long id, String codigo, String codigoRetencion, String base,
         String numeroDocumento, String fechaDocumento) throws Exception {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
      Fec_reteimpu impuesto = new Fec_reteimpu();
      impuesto.setIdretencionesimpuestos(id);
      impuesto.setIdretencion(6935L);
      impuesto.setCodigo(codigo);
      impuesto.setCodigoporcentaje(codigoRetencion);
      impuesto.setBaseimponible(new BigDecimal(base));
      impuesto.setCodigodocumentosustento("01");
      impuesto.setNumerodocumentosustento(numeroDocumento);
      impuesto.setFechaemisiondocumentosustento(df.parse(fechaDocumento));
      return impuesto;
   }
}
