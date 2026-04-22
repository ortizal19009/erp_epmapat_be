package com.epmapat.erp_epmapat.sri.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_reteimpu;
import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_reteimpuR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_retencionesR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;

@Service
public class RetencionSRIService {

   private static final String VERSION = "1.0.0";
   private static final String COD_DOC_RETENCION = "07";

   @Autowired
   private RetencionesR retencionesR;
   @Autowired
   private Fec_reteimpuR fecReteimpuR;
   @Autowired
   private Fec_retencionesR fecRetencionesR;
   @Autowired
   private DefinirR definirR;

   public String generarXml(Long idretencion) {
      Retenciones retencion = cargarRetencion(idretencion);
      Definir definir = getDefinir();
      List<Fec_reteimpu> impuestos = cargarImpuestosSeguro(idretencion);
      List<ImpuestoRetencionXml> lineas = construirLineasImpuesto(retencion, impuestos);
      return construirXml(retencion, definir, lineas);
   }

   @Transactional
   public Fec_retenciones generarYGuardar(Long idretencion) {
      Retenciones retencion = cargarRetencion(idretencion);
      String xml = generarXml(idretencion);

      Fec_retenciones fec = fecRetencionesR.findById(idretencion).orElseGet(Fec_retenciones::new);
      fec.setIdretencion(idretencion);
      fec.setClaveacceso(retencion.getClaveacceso());
      fec.setSecuencial(formatearSecuencial(retencion.getSecretencion1(), idretencion));
      fec.setXmlautorizado(xml);
      fec.setErrores(null);
      fec.setEstado("GENERADA");
      fec.setEstablecimiento(obtenerEstablecimiento(retencion));
      fec.setPuntoemision(obtenerPuntoEmision(retencion));
      fec.setDireccionestablecimiento(obtenerDireccionEstablecimiento());
      fec.setFechaemision(toLocalDate(retencion.getFechaemision()));
      fec.setTipoidentificacionsujetoretenid(obtenerTipoIdentificacion(retencion));
      fec.setRazonsocialsujetoretenido(obtenerRazonSocial(retencion));
      fec.setIdentificacionsujetoretenido(obtenerIdentificacion(retencion));
      fec.setPeriodofiscal(formatearPeriodoFiscal(retencion.getFechaemision()));
      fec.setTelefonosujetoretenido(obtenerTelefono(retencion));
      fec.setEmailsujetoretenido(obtenerEmail(retencion));
      fecRetencionesR.save(fec);
      return fec;
   }

   @Transactional
   public Fec_retenciones actualizarXmlAutorizado(Long idretencion, String xmlAutorizado, String estado, String errores) {
      Fec_retenciones fec = fecRetencionesR.findById(idretencion).orElseGet(Fec_retenciones::new);
      fec.setIdretencion(idretencion);
      if (xmlAutorizado != null) {
         fec.setXmlautorizado(xmlAutorizado);
      }
      fec.setEstado(estado);
      fec.setErrores(errores);
      return fecRetencionesR.save(fec);
   }

   public List<Fec_retenciones> listarTodas() {
      return fecRetencionesR.findAllByOrderByIdretencionDesc();
   }

   public List<Fec_retenciones> listarPorEstado(String estado) {
      if (estado == null || estado.isBlank()) {
         return listarTodas();
      }
      return fecRetencionesR.findByEstadoOrderByIdretencionDesc(estado.trim());
   }

   @Transactional
   public Fec_retenciones actualizarEstado(Long idretencion, String estado, String errores) {
      return actualizarEstado(idretencion, estado, errores, null);
   }

   @Transactional
   public Fec_retenciones actualizarEstado(Long idretencion, String estado, String errores, String email) {
      Fec_retenciones fec = fecRetencionesR.findById(idretencion)
            .orElseThrow(() -> new IllegalArgumentException("No existe la retención generada " + idretencion));
      fec.setEstado(estado);
      fec.setErrores(errores);
      if (email != null && !email.isBlank()) {
         fec.setEmailsujetoretenido(email.trim());
      }
      return fecRetencionesR.save(fec);
   }

   private Retenciones cargarRetencion(Long idretencion) {
      if (idretencion == null) {
         throw new IllegalArgumentException("Debe indicar el ID de la retención.");
      }
      return retencionesR.findById(idretencion)
            .orElseThrow(() -> new IllegalArgumentException("No existe la retención " + idretencion));
   }

   private List<Fec_reteimpu> cargarImpuestosSeguro(Long idretencion) {
      try {
         return fecReteimpuR.findByIdretencionOrderByIdretencionesimpuestosAsc(idretencion);
      } catch (DataAccessException ex) {
         return List.of();
      }
   }

   private Definir getDefinir() {
      return definirR.findTopByOrderByIddefinirDesc();
   }

   private List<ImpuestoRetencionXml> construirLineasImpuesto(Retenciones retencion, List<Fec_reteimpu> impuestos) {
      Map<String, List<Fec_reteimpu>> agrupadas = new LinkedHashMap<>();
      if (impuestos != null) {
         for (Fec_reteimpu item : impuestos) {
            if (item == null) {
               continue;
            }
            String clave = normalizar(item.getCodigo()) + "|" + normalizar(item.getCodigoporcentaje());
            agrupadas.computeIfAbsent(clave, key -> new ArrayList<>()).add(item);
         }
      }

      List<ImpuestoRetencionXml> resultado = new ArrayList<>();
      if (!agrupadas.isEmpty()) {
         for (List<Fec_reteimpu> grupo : agrupadas.values()) {
            Fec_reteimpu referencia = grupo.get(0);
            ImpuestoRetencionXml linea = new ImpuestoRetencionXml();
            linea.codigo = firstNonBlank(referencia.getCodigo(), "2");
            linea.codigoRetencion = firstNonBlank(referencia.getCodigoporcentaje(), obtenerCodigoRetencionDesdeRetencion(retencion, referencia));
            linea.baseImponible = sumarBaseImponible(grupo, retencion, referencia);
            linea.porcentajeRetener = resolverPorcentajeRetener(retencion, referencia, linea.codigoRetencion, linea.baseImponible);
            linea.valorRetenido = resolverValorRetenido(retencion, referencia, linea.codigoRetencion, linea.baseImponible, linea.porcentajeRetener);
            linea.codDocSustento = firstNonBlank(referencia.getCodigodocumentosustento(), obtenerCodigoSustento(retencion));
            linea.numDocSustento = firstNonBlank(referencia.getNumerodocumentosustento(), retencion.getNumdoc());
            linea.fechaEmisionDocSustento = referencia.getFechaemisiondocumentosustento() != null
                  ? referencia.getFechaemisiondocumentosustento()
                  : retencion.getFechaemision();
            resultado.add(linea);
         }
         return resultado;
      }

      agregarLineaDesdeRetencion(resultado, retencion.getCodretair(), retencion.getBaseimpair(), retencion.getPorcentajeair(),
            retencion.getValretair(), "1", obtenerCodigoSustento(retencion), retencion.getNumdoc(), retencion.getFechaemision());
      agregarLineaDesdeRetencion(resultado, retencion.getCodretbienes(), retencion.getMontoivabienes(), retencion.getPorretbienes(),
            retencion.getValorretbienes(), "2", obtenerCodigoSustento(retencion), retencion.getNumdoc(), retencion.getFechaemision());
      agregarLineaDesdeRetencion(resultado, retencion.getCodretservicios(), retencion.getMontoivaservicios(), retencion.getPorretservicios(),
            retencion.getValorretservicios(), "2", obtenerCodigoSustento(retencion), retencion.getNumdoc(), retencion.getFechaemision());
      agregarLineaDesdeRetencion(resultado, retencion.getCodretserv100(), retencion.getMontoivaserv100(), retencion.getPorretserv100(),
            retencion.getValretserv100(), "2", obtenerCodigoSustento(retencion), retencion.getNumdoc(), retencion.getFechaemision());
      return resultado;
   }

   private void agregarLineaDesdeRetencion(List<ImpuestoRetencionXml> resultado, String codigoRetencion,
         BigDecimal baseImponible, Number porcentajeRetener, BigDecimal valorRetenido, String codigo,
         String codDocSustento, String numDocSustento, Date fechaEmisionDocSustento) {
      if (codigoRetencion == null && baseImponible == null && valorRetenido == null) {
         return;
      }
      ImpuestoRetencionXml linea = new ImpuestoRetencionXml();
      linea.codigo = firstNonBlank(codigo, "2");
      linea.codigoRetencion = codigoRetencion;
      linea.baseImponible = baseImponible != null ? baseImponible : BigDecimal.ZERO;
      linea.porcentajeRetener = porcentajeRetener != null ? new BigDecimal(String.valueOf(porcentajeRetener)) : BigDecimal.ZERO;
      linea.valorRetenido = valorRetenido != null ? valorRetenido : calcularValorRetenido(linea.baseImponible, linea.porcentajeRetener);
      linea.codDocSustento = codDocSustento;
      linea.numDocSustento = numDocSustento;
      linea.fechaEmisionDocSustento = fechaEmisionDocSustento;
      resultado.add(linea);
   }

   private String construirXml(Retenciones retencion, Definir definir, List<ImpuestoRetencionXml> lineas) {
      StringBuilder xml = new StringBuilder();
      xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      xml.append("<comprobanteRetencion id=\"comprobante\" version=\"").append(VERSION).append("\">\n");
      xml.append("  <infoTributaria>\n");
      appendTag(xml, "    ", "ambiente", valueOf(definir != null ? definir.getTipoambiente() : null, "1"));
      appendTag(xml, "    ", "tipoEmision", "1");
      appendTag(xml, "    ", "razonSocial", valueOf(definir != null ? definir.getRazonsocial() : null));
      appendTag(xml, "    ", "nombreComercial", valueOf(definir != null ? definir.getNombrecomercial() : null));
      appendTag(xml, "    ", "ruc", valueOf(definir != null ? definir.getRuc() : null));
      appendTag(xml, "    ", "claveAcceso", valueOf(retencion.getClaveacceso() != null ? retencion.getClaveacceso() : retencion.getNumautoriza()));
      appendTag(xml, "    ", "codDoc", COD_DOC_RETENCION);
      appendTag(xml, "    ", "estab", obtenerEstablecimiento(retencion));
      appendTag(xml, "    ", "ptoEmi", obtenerPuntoEmision(retencion));
      appendTag(xml, "    ", "secuencial", formatearSecuencial(retencion.getSecretencion1(), retencion.getIdrete()));
      appendTag(xml, "    ", "dirMatriz", valueOf(definir != null ? definir.getDirmatriz() : null));
      xml.append("  </infoTributaria>\n");

      xml.append("  <infoCompRetencion>\n");
      appendTag(xml, "    ", "fechaEmision", formatearFecha(retencion.getFechaemision()));
      appendTag(xml, "    ", "dirEstablecimiento", obtenerDireccionEstablecimiento(definir));
      appendTag(xml, "    ", "obligadoContabilidad", "SI");
      appendTag(xml, "    ", "tipoIdentificacionSujetoRetenido", obtenerTipoIdentificacion(retencion));
      appendTag(xml, "    ", "razonSocialSujetoRetenido", obtenerRazonSocial(retencion));
      appendTag(xml, "    ", "identificacionSujetoRetenido", obtenerIdentificacion(retencion));
      appendTag(xml, "    ", "periodoFiscal", formatearPeriodoFiscal(retencion.getFechaemision()));
      xml.append("  </infoCompRetencion>\n");

      xml.append("  <impuestos>\n");
      for (ImpuestoRetencionXml linea : lineas) {
         xml.append("    <impuesto>\n");
         appendTag(xml, "      ", "codigo", valueOf(linea.codigo));
         appendTag(xml, "      ", "codigoRetencion", valueOf(linea.codigoRetencion));
         appendTag(xml, "      ", "baseImponible", formatDecimal(linea.baseImponible));
         appendTag(xml, "      ", "porcentajeRetener", formatDecimal(linea.porcentajeRetener));
         appendTag(xml, "      ", "valorRetenido", formatDecimal(linea.valorRetenido));
         appendTag(xml, "      ", "codDocSustento", valueOf(linea.codDocSustento));
         appendTag(xml, "      ", "numDocSustento", valueOf(linea.numDocSustento));
         appendTag(xml, "      ", "fechaEmisionDocSustento", formatearFecha(linea.fechaEmisionDocSustento));
         xml.append("    </impuesto>\n");
      }
      xml.append("  </impuestos>\n");
      xml.append("</comprobanteRetencion>\n");
      return xml.toString();
   }

   private String obtenerDireccionEstablecimiento(Definir definir) {
      if (definir == null) {
         return "";
      }
      return valueOf(firstNonBlank(definir.getDireccion(), definir.getDirmatriz(), definir.getCiudad()));
   }

   private String obtenerDireccionEstablecimiento() {
      Definir definir = getDefinir();
      return obtenerDireccionEstablecimiento(definir);
   }

   private String obtenerTipoIdentificacion(Retenciones retencion) {
      Beneficiarios beneficiario = retencion.getIdbene();
      if (beneficiario == null || beneficiario.getTpidben() == null || beneficiario.getTpidben().isBlank()) {
         return "04";
      }
      return beneficiario.getTpidben().trim();
   }

   private String obtenerRazonSocial(Retenciones retencion) {
      Beneficiarios beneficiario = retencion.getIdbene();
      if (beneficiario == null) {
         return "";
      }
      return valueOf(beneficiario.getNomben());
   }

   private String obtenerIdentificacion(Retenciones retencion) {
      Beneficiarios beneficiario = retencion.getIdbene();
      if (beneficiario == null) {
         return "";
      }
      return firstNonBlank(beneficiario.getRucben(), beneficiario.getCiben());
   }

   private String obtenerTelefono(Retenciones retencion) {
      Beneficiarios beneficiario = retencion.getIdbene();
      if (beneficiario == null) {
         return "";
      }
      return valueOf(beneficiario.getTlfben());
   }

   private String obtenerEmail(Retenciones retencion) {
      Beneficiarios beneficiario = retencion.getIdbene();
      if (beneficiario == null) {
         return "";
      }
      return valueOf(beneficiario.getMailben());
   }

   private String obtenerCodigoSustento(Retenciones retencion) {
      if (retencion.getIdtabla01() != null && retencion.getIdtabla01().getCodsustento() != null) {
         return retencion.getIdtabla01().getCodsustento().trim();
      }
      return "01";
   }

   private String obtenerCodigoRetencionDesdeRetencion(Retenciones retencion, Fec_reteimpu detalle) {
      String codigo = normalizar(detalle != null ? detalle.getCodigoporcentaje() : null);
      if (codigo.isBlank()) {
         return firstNonBlank(retencion.getCodretair(), retencion.getCodretbienes(), retencion.getCodretservicios(), retencion.getCodretserv100());
      }
      if (matchesAny(codigo, retencion.getCodretair(), retencion.getCodretbienes(), retencion.getCodretservicios(), retencion.getCodretserv100())) {
         return codigo;
      }
      return firstNonBlank(retencion.getCodretair(), retencion.getCodretbienes(), retencion.getCodretservicios(), retencion.getCodretserv100(), codigo);
   }

   private BigDecimal sumarBaseImponible(List<Fec_reteimpu> grupo, Retenciones retencion, Fec_reteimpu referencia) {
      BigDecimal desdeGrupo = grupo.stream()
            .map(Fec_reteimpu::getBaseimponible)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
      if (desdeGrupo.compareTo(BigDecimal.ZERO) > 0) {
         return desdeGrupo;
      }
      String codigo = obtenerCodigoRetencionDesdeRetencion(retencion, referencia);
      if (codigo != null && codigo.equals(retencion.getCodretair())) {
         return retencion.getBaseimpair() != null ? retencion.getBaseimpair() : BigDecimal.ZERO;
      }
      if (codigo != null && codigo.equals(retencion.getCodretbienes())) {
         return retencion.getMontoivabienes() != null ? retencion.getMontoivabienes() : BigDecimal.ZERO;
      }
      if (codigo != null && codigo.equals(retencion.getCodretservicios())) {
         return retencion.getMontoivaservicios() != null ? retencion.getMontoivaservicios() : BigDecimal.ZERO;
      }
      if (codigo != null && codigo.equals(retencion.getCodretserv100())) {
         return retencion.getMontoivaserv100() != null ? retencion.getMontoivaserv100() : BigDecimal.ZERO;
      }
      return retencion.getBaseimponible() != null ? retencion.getBaseimponible() : BigDecimal.ZERO;
   }

   private BigDecimal resolverPorcentajeRetener(Retenciones retencion, Fec_reteimpu detalle, String codigoRetencion, BigDecimal baseImponible) {
      if (codigoRetencion != null) {
         if (codigoRetencion.equals(retencion.getCodretair()) && retencion.getPorcentajeair() != null) {
            return retencion.getPorcentajeair().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(retencion.getCodretbienes()) && retencion.getPorretbienes() != null) {
            return BigDecimal.valueOf(retencion.getPorretbienes()).setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(retencion.getCodretservicios()) && retencion.getPorretservicios() != null) {
            return retencion.getPorretservicios().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(retencion.getCodretserv100()) && retencion.getPorretserv100() != null) {
            return retencion.getPorretserv100().setScale(2, RoundingMode.HALF_UP);
         }
      }

      if (retencion.getIdtabla17() != null && retencion.getIdtabla17().getPorciva() != null) {
         return BigDecimal.valueOf(retencion.getIdtabla17().getPorciva()).setScale(2, RoundingMode.HALF_UP);
      }

      if (baseImponible != null && baseImponible.compareTo(BigDecimal.ZERO) > 0) {
         BigDecimal valorRet = resolverValorRetenido(retencion, detalle, codigoRetencion, baseImponible, BigDecimal.ZERO);
         if (valorRet.compareTo(BigDecimal.ZERO) > 0) {
            return valorRet.multiply(BigDecimal.valueOf(100)).divide(baseImponible, 2, RoundingMode.HALF_UP);
         }
      }
      return BigDecimal.ZERO;
   }

   private BigDecimal resolverValorRetenido(Retenciones retencion, Fec_reteimpu detalle, String codigoRetencion,
         BigDecimal baseImponible, BigDecimal porcentajeRetener) {
      if (codigoRetencion != null) {
         if (codigoRetencion.equals(retencion.getCodretair()) && retencion.getValretair() != null) {
            return retencion.getValretair().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(retencion.getCodretbienes()) && retencion.getValorretbienes() != null) {
            return retencion.getValorretbienes().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(retencion.getCodretservicios()) && retencion.getValorretservicios() != null) {
            return retencion.getValorretservicios().setScale(2, RoundingMode.HALF_UP);
         }
         if (codigoRetencion.equals(retencion.getCodretserv100()) && retencion.getValretserv100() != null) {
            return retencion.getValretserv100().setScale(2, RoundingMode.HALF_UP);
         }
      }

      if (baseImponible == null) {
         baseImponible = BigDecimal.ZERO;
      }
      if (porcentajeRetener == null) {
         porcentajeRetener = BigDecimal.ZERO;
      }
      if (baseImponible.compareTo(BigDecimal.ZERO) <= 0 || porcentajeRetener.compareTo(BigDecimal.ZERO) <= 0) {
         return BigDecimal.ZERO;
      }
      BigDecimal tasa = porcentajeRetener;
      if (tasa.compareTo(BigDecimal.ONE) > 0) {
         tasa = tasa.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
      }
      return baseImponible.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
   }

   private BigDecimal calcularValorRetenido(BigDecimal baseImponible, BigDecimal porcentajeRetener) {
      if (baseImponible == null || porcentajeRetener == null) {
         return BigDecimal.ZERO;
      }
      if (baseImponible.compareTo(BigDecimal.ZERO) <= 0 || porcentajeRetener.compareTo(BigDecimal.ZERO) <= 0) {
         return BigDecimal.ZERO;
      }
      BigDecimal tasa = porcentajeRetener;
      if (tasa.compareTo(BigDecimal.ONE) > 0) {
         tasa = tasa.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
      }
      return baseImponible.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
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

   private String formatearSecuencial(String secuencial, Long fallback) {
      String soloDigitos = normalizarNumero(secuencial);
      if (!soloDigitos.isBlank()) {
         return String.format("%09d", Long.parseLong(soloDigitos));
      }
      if (fallback != null) {
         return String.format("%09d", fallback);
      }
      return "000000000";
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
      String clean = serie.replaceAll("[^0-9]", "");
      if (!clean.isBlank()) {
         return clean;
      }
      return serie.trim();
   }

   private String normalizarNumero(String valor) {
      if (valor == null) {
         return "";
      }
      return valor.replaceAll("[^0-9]", "").trim();
   }

   private String valueOf(Object value) {
      return value == null ? "" : String.valueOf(value);
   }

   private String valueOf(Object value, String fallback) {
      String str = valueOf(value);
      return str.isBlank() ? fallback : str;
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

   private String normalizar(String value) {
      return value == null ? "" : value.trim();
   }

   private boolean matchesAny(String candidate, String... values) {
      if (candidate == null) {
         return false;
      }
      for (String value : values) {
         if (value != null && candidate.equals(value.trim())) {
            return true;
         }
      }
      return false;
   }

   private String formatDecimal(BigDecimal value) {
      BigDecimal normalized = value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
      return normalized.toPlainString();
   }

   private void appendTag(StringBuilder xml, String indent, String tag, String value) {
      xml.append(indent)
            .append("<")
            .append(tag)
            .append(">")
            .append(escapeXml(value))
            .append("</")
            .append(tag)
            .append(">\n");
   }

   private String escapeXml(String value) {
      if (value == null) {
         return "";
      }
      return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
   }

   private Date toDate(java.time.LocalDate localDate) {
      if (localDate == null) {
         return null;
      }
      return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
   }

   private java.time.LocalDate toLocalDate(Date date) {
      if (date == null) {
         return null;
      }
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
   }

   private static class ImpuestoRetencionXml {
      private String codigo;
      private String codigoRetencion;
      private BigDecimal baseImponible;
      private BigDecimal porcentajeRetener;
      private BigDecimal valorRetenido;
      private String codDocSustento;
      private String numDocSustento;
      private Date fechaEmisionDocSustento;
   }
}
