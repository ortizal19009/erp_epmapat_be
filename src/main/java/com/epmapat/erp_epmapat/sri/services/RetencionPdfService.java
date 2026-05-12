package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.epmapat.erp_epmapat.commons.JasperReportManager;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_reteimpu;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_reteimpuR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class RetencionPdfService {

   @Autowired
   private RetencionesR retencionesR;
   @Autowired
   private Fec_reteimpuR fecReteimpuR;
   @Autowired
   private DefinirR definirR;
   @Autowired
   private JasperReportManager reportManager;

   public ByteArrayOutputStream generarPdf(Long idretencion) {
      try {
         Retenciones retencion = retencionesR.findById(idretencion)
               .orElseThrow(() -> new IllegalArgumentException("No existe la retención " + idretencion));
         if (!estaAutorizada(retencion)) {
            throw new IllegalStateException("La retención " + idretencion + " no está autorizada");
         }
         Definir definir = definirR.findTopByOrderByIddefinirDesc();
         List<Fec_reteimpu> impuestos = cargarImpuestosSeguro(idretencion);

         Map<String, Object> parameters = construirParametros(retencion, definir);
         List<LineaRetencionPdf> lineas = construirLineas(retencion, impuestos);

         JasperReport report = reportManager.getCompiledReport("retencion_template");
         JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(lineas));

         ByteArrayOutputStream pdf = new ByteArrayOutputStream();
         JasperExportManager.exportReportToPdfStream(print, pdf);
         return pdf;
      } catch (Exception e) {
         throw new RuntimeException("No se pudo generar el PDF de la retención " + idretencion + ": " + e.getMessage(), e);
      }
   }

   private List<Fec_reteimpu> cargarImpuestosSeguro(Long idretencion) {
      try {
         return fecReteimpuR.findByIdretencionOrderByIdretencionesimpuestosAsc(idretencion);
      } catch (DataAccessException ex) {
         return List.of();
      }
   }

   private Map<String, Object> construirParametros(Retenciones retencion, Definir definir) {
      Map<String, Object> parameters = new LinkedHashMap<>();
      Beneficiarios beneficiario = retencion.getIdbene();

      parameters.put("estado", estadoEtiqueta(retencion.getEstado()));
      parameters.put("numeroAutorizacion", firstNonBlank(retencion.getNumautoriza_e(), retencion.getNumautoriza(), retencion.getClaveacceso()));
      parameters.put("fechaAutorizacion", valueOf(retencion.getFecautoriza()));
      parameters.put("ambienteAutorizacion", valueOf(retencion.getAmbiente()));
      parameters.put("razonSocial", valueOf(definir != null ? definir.getRazonsocial() : null));
      parameters.put("nombreComercial", valueOf(definir != null ? definir.getNombrecomercial() : null));
      parameters.put("ruc", valueOf(definir != null ? definir.getRuc() : null));
      parameters.put("claveAcceso", valueOf(retencion.getClaveacceso(), retencion.getNumautoriza()));
      parameters.put("estab", obtenerEstablecimiento(retencion));
      parameters.put("ptoEmi", obtenerPuntoEmision(retencion));
      parameters.put("secuencial", formatearSecuencial(retencion.getSecretencion1(), retencion.getIdrete()));
      parameters.put("dirMatriz", valueOf(definir != null ? firstNonBlank(definir.getDirmatriz(), definir.getDireccion()) : null));
      parameters.put("fechaEmision", formatearFecha(retencion.getFechaemision()));
      parameters.put("obligadoContabilidad", "SI");
      parameters.put("tipoIdentificacionSujetoRetenido", beneficiario != null && beneficiario.getTpidben() != null && !beneficiario.getTpidben().isBlank()
            ? beneficiario.getTpidben().trim()
            : "04");
      parameters.put("razonSocialSujetoRetenido", valueOf(beneficiario != null ? beneficiario.getNomben() : null));
      parameters.put("identificacionSujetoRetenido", firstNonBlank(
            beneficiario != null ? beneficiario.getRucben() : null,
            beneficiario != null ? beneficiario.getCiben() : null));
      parameters.put("periodoFiscal", formatearPeriodoFiscal(retencion.getFechaemision()));
      parameters.put("direccionSujetoRetenido", valueOf(beneficiario != null ? beneficiario.getDirben() : null));
      parameters.put("telefonoSujetoRetenido", valueOf(beneficiario != null ? beneficiario.getTlfben() : null));
      parameters.put("emailSujetoRetenido", valueOf(beneficiario != null ? beneficiario.getMailben() : null));
      return parameters;
   }

   private List<LineaRetencionPdf> construirLineas(Retenciones retencion, List<Fec_reteimpu> impuestos) {
      Map<String, List<Fec_reteimpu>> agrupadas = new LinkedHashMap<>();
      for (Fec_reteimpu item : impuestos) {
         if (item == null) {
            continue;
         }
         String clave = valueOf(item.getCodigo()) + "|" + valueOf(item.getCodigoporcentaje());
         agrupadas.computeIfAbsent(clave, key -> new ArrayList<>()).add(item);
      }

      List<LineaRetencionPdf> lineas = new ArrayList<>();
      if (!agrupadas.isEmpty()) {
         for (List<Fec_reteimpu> grupo : agrupadas.values()) {
            Fec_reteimpu referencia = grupo.get(0);
            LineaRetencionPdf linea = new LineaRetencionPdf();
            linea.codigo = valueOf(referencia.getCodigo(), "2");
            linea.codigoRetencion = valueOf(referencia.getCodigoporcentaje());
            linea.baseImponible = grupo.stream()
                  .map(Fec_reteimpu::getBaseimponible)
                  .filter(Objects::nonNull)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (linea.baseImponible.compareTo(BigDecimal.ZERO) <= 0) {
               linea.baseImponible = resolverBaseDesdeRetencion(retencion, linea.codigoRetencion);
            }
            linea.porcentajeRetener = resolverPorcentaje(retencion, linea.codigoRetencion, linea.baseImponible);
            linea.valorRetenido = resolverValorRetenido(retencion, linea.codigoRetencion, linea.baseImponible, linea.porcentajeRetener);
            linea.codDocSustento = valueOf(referencia.getCodigodocumentosustento());
            linea.numDocSustento = valueOf(referencia.getNumerodocumentosustento());
            linea.fechaEmisionDocSustento = formatearFecha(referencia.getFechaemisiondocumentosustento());
            lineas.add(linea);
         }
         return lineas;
      }

      agregarLinea(lineas, retencion.getCodretair(), retencion.getBaseimpair(), retencion.getPorcentajeair(),
            retencion.getValretair(), "1", retencion.getNumdoc(), retencion.getFechaemision());
      agregarLinea(lineas, retencion.getCodretbienes(), retencion.getMontoivabienes(), retencion.getPorretbienes(),
            retencion.getValorretbienes(), "2", retencion.getNumdoc(), retencion.getFechaemision());
      agregarLinea(lineas, retencion.getCodretservicios(), retencion.getMontoivaservicios(), retencion.getPorretservicios(),
            retencion.getValorretservicios(), "2", retencion.getNumdoc(), retencion.getFechaemision());
      agregarLinea(lineas, retencion.getCodretserv100(), retencion.getMontoivaserv100(), retencion.getPorretserv100(),
            retencion.getValretserv100(), "2", retencion.getNumdoc(), retencion.getFechaemision());
      return lineas;
   }

   private void agregarLinea(List<LineaRetencionPdf> lineas, String codigoRetencion, BigDecimal baseImponible,
         Number porcentajeRetener, BigDecimal valorRetenido, String codigo, String numDocSustento, Date fechaEmision) {
      if (codigoRetencion == null && baseImponible == null && valorRetenido == null) {
         return;
      }
      LineaRetencionPdf linea = new LineaRetencionPdf();
      linea.codigo = valueOf(codigo, "2");
      linea.codigoRetencion = valueOf(codigoRetencion);
      linea.baseImponible = baseImponible != null ? baseImponible : BigDecimal.ZERO;
      linea.porcentajeRetener = porcentajeRetener != null ? new BigDecimal(String.valueOf(porcentajeRetener)) : BigDecimal.ZERO;
      linea.valorRetenido = valorRetenido != null ? valorRetenido : resolverValorRetenido(null, codigoRetencion, linea.baseImponible, linea.porcentajeRetener);
      linea.codDocSustento = "01";
      linea.numDocSustento = valueOf(numDocSustento);
      linea.fechaEmisionDocSustento = formatearFecha(fechaEmision);
      lineas.add(linea);
   }

   private BigDecimal resolverBaseDesdeRetencion(Retenciones retencion, String codigoRetencion) {
      if (retencion == null || codigoRetencion == null) {
         return BigDecimal.ZERO;
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretair()))) {
         return valueOf(retencion.getBaseimpair());
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretbienes()))) {
         return valueOf(retencion.getMontoivabienes());
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretservicios()))) {
         return valueOf(retencion.getMontoivaservicios());
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretserv100()))) {
         return valueOf(retencion.getMontoivaserv100());
      }
      return valueOf(retencion.getBaseimponible());
   }

   private BigDecimal resolverPorcentaje(Retenciones retencion, String codigoRetencion, BigDecimal baseImponible) {
      if (retencion == null || codigoRetencion == null) {
         return BigDecimal.ZERO;
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretair())) && retencion.getPorcentajeair() != null) {
         return retencion.getPorcentajeair().setScale(2, RoundingMode.HALF_UP);
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretbienes())) && retencion.getPorretbienes() != null) {
         return BigDecimal.valueOf(retencion.getPorretbienes()).setScale(2, RoundingMode.HALF_UP);
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretservicios())) && retencion.getPorretservicios() != null) {
         return retencion.getPorretservicios().setScale(2, RoundingMode.HALF_UP);
      }
      if (codigoRetencion.equals(valueOf(retencion.getCodretserv100())) && retencion.getPorretserv100() != null) {
         return retencion.getPorretserv100().setScale(2, RoundingMode.HALF_UP);
      }
      if (baseImponible != null && baseImponible.compareTo(BigDecimal.ZERO) > 0) {
         return BigDecimal.ZERO;
      }
      return BigDecimal.ZERO;
   }

   private BigDecimal resolverValorRetenido(Retenciones retencion, String codigoRetencion, BigDecimal baseImponible,
         BigDecimal porcentajeRetener) {
      if (retencion != null && codigoRetencion != null) {
         if (codigoRetencion.equals(valueOf(retencion.getCodretair())) && retencion.getValretair() != null) {
            return retencion.getValretair().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(valueOf(retencion.getCodretbienes())) && retencion.getValorretbienes() != null) {
            return retencion.getValorretbienes().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(valueOf(retencion.getCodretservicios())) && retencion.getValorretservicios() != null) {
            return retencion.getValorretservicios().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(valueOf(retencion.getCodretserv100())) && retencion.getValretserv100() != null) {
            return retencion.getValretserv100().setScale(2, RoundingMode.HALF_UP);
         }
      }
      if (baseImponible == null || porcentajeRetener == null || baseImponible.compareTo(BigDecimal.ZERO) <= 0
            || porcentajeRetener.compareTo(BigDecimal.ZERO) <= 0) {
         return BigDecimal.ZERO;
      }
      BigDecimal tasa = porcentajeRetener;
      if (tasa.compareTo(BigDecimal.ONE) > 0) {
         tasa = tasa.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
      }
      return baseImponible.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
   }

   private String obtenerEstablecimiento(Retenciones retencion) {
      String serie = normalizarSerie(retencion != null ? retencion.getNumserie() : null);
      if (serie.length() >= 3) {
         return serie.substring(0, 3);
      }
      return "000";
   }

   private String obtenerPuntoEmision(Retenciones retencion) {
      String serie = normalizarSerie(retencion != null ? retencion.getNumserie() : null);
      if (serie.length() >= 6) {
         return serie.substring(3, 6);
      }
      return "000";
   }

   private String normalizarSerie(String serie) {
      if (serie == null) {
         return "";
      }
      return serie.replaceAll("[^0-9]", "").trim();
   }

   private String formatearSecuencial(String secuencial, Long fallback) {
      String soloDigitos = secuencial == null ? "" : secuencial.replaceAll("[^0-9]", "").trim();
      if (!soloDigitos.isBlank()) {
         return String.format("%09d", Long.parseLong(soloDigitos));
      }
      if (fallback != null) {
         return String.format("%09d", fallback);
      }
      return "000000000";
   }

   private String formatearFecha(Date date) {
      if (date == null) {
         return "";
      }
      return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date);
   }

   private String formatearPeriodoFiscal(Date date) {
      if (date == null) {
         return "";
      }
      return new SimpleDateFormat("MM/yyyy", Locale.US).format(date);
   }

   private BigDecimal valueOf(BigDecimal value) {
      return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
   }

   private String valueOf(Object value) {
      return value == null ? "" : String.valueOf(value);
   }

   private String valueOf(Object value, String fallback) {
      String str = valueOf(value);
      return str.isBlank() ? fallback : str;
   }

   private boolean estaAutorizada(Retenciones retencion) {
      if (retencion == null) {
         return false;
      }
      if (StringUtils.hasText(retencion.getNumautoriza_e())) {
         return true;
      }
      if (StringUtils.hasText(retencion.getAutorizacion())) {
         return true;
      }
      return StringUtils.hasText(retencion.getFecautoriza());
   }

   private String firstNonBlank(String... values) {
      if (values == null) {
         return "";
      }
      for (String value : values) {
         if (value != null && !value.trim().isEmpty()) {
            return value.trim();
         }
      }
      return "";
   }

   private String estadoEtiqueta(Integer estado) {
      if (estado == null) {
         return "GENERADA";
      }
      if (estado == 1) {
         return "AUTORIZADA";
      }
      return "GENERADA";
   }

   public static class LineaRetencionPdf {
      public String codigo;
      public String codigoRetencion;
      public BigDecimal baseImponible;
      public BigDecimal porcentajeRetener;
      public BigDecimal valorRetenido;
      public String codDocSustento;
      public String numDocSustento;
      public String fechaEmisionDocSustento;

      public String getCodigo() {
         return codigo;
      }

      public String getCodigoRetencion() {
         return codigoRetencion;
      }

      public BigDecimal getBaseImponible() {
         return baseImponible;
      }

      public BigDecimal getPorcentajeRetener() {
         return porcentajeRetener;
      }

      public BigDecimal getValorRetenido() {
         return valorRetenido;
      }

      public String getCodDocSustento() {
         return codDocSustento;
      }

      public String getNumDocSustento() {
         return numDocSustento;
      }

      public String getFechaEmisionDocSustento() {
         return fechaEmisionDocSustento;
      }
   }
}
