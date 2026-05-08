package com.epmapat.erp_epmapat.sri.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_retencionesR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;
import com.epmapat.erp_epmapat.sri.dtos.ClaveAccesoValidationResult;

@Service
public class RetencionClaveAccesoService {

   private static final Logger log = LoggerFactory.getLogger(RetencionClaveAccesoService.class);
   private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("ddMMyyyy");
   private static final String COD_DOC_RETENCION = "07";
   private static final String TIPO_EMISION_NORMAL = "1";
   private static final int MAX_INTENTOS_GENERACION = 10;

   private final RetencionesR retencionesR;
   private final Fec_retencionesR fecRetencionesR;
   private final DefinirR definirR;
   private final Modulo11Calculator modulo11Calculator;

   public RetencionClaveAccesoService(
         RetencionesR retencionesR,
         Fec_retencionesR fecRetencionesR,
         DefinirR definirR,
         Modulo11Calculator modulo11Calculator) {
      this.retencionesR = retencionesR;
      this.fecRetencionesR = fecRetencionesR;
      this.definirR = definirR;
      this.modulo11Calculator = modulo11Calculator;
   }

   @Transactional
   public Retenciones asegurarClaveAcceso(Retenciones retencion) {
      Definir definir = getDefinir();
      String claveActual = normalizarNumero(retencion.getClaveacceso());
      ClaveAccesoValidationResult validacion = validarClaveAcceso(claveActual, retencion, definir);
      if (validacion.isValida() && !claveExisteEnOtraRetencion(claveActual, retencion.getIdrete())) {
         return retencion;
      }

      log.warn("Regenerando clave de acceso de retencion {}. Motivos: {}",
            retencion.getIdrete(), validacion.getErrores());
      retencion.setClaveacceso(generarClaveUnica(retencion, definir));
      return retencionesR.save(retencion);
   }

   @Transactional
   public Retenciones regenerarClaveAcceso(Long idretencion) {
      Retenciones retencion = retencionesR.findById(idretencion)
            .orElseThrow(() -> new IllegalArgumentException("No existe la retencion " + idretencion));
      Definir definir = getDefinir();
      retencion.setClaveacceso(generarClaveUnica(retencion, definir));
      log.info("Clave de acceso regenerada para retencion {}", idretencion);
      return retencionesR.save(retencion);
   }

   public ClaveAccesoValidationResult validarClaveAcceso(String claveAcceso) {
      return validarClaveAcceso(claveAcceso, null, getDefinir());
   }

   public ClaveAccesoValidationResult validarClaveAcceso(String claveAcceso, Retenciones retencion, Definir definir) {
      String clave = normalizarNumero(claveAcceso);
      List<String> errores = new ArrayList<>();
      if (!clave.matches("\\d{49}")) {
         errores.add("La clave de acceso debe tener exactamente 49 digitos.");
         return ClaveAccesoValidationResult.invalida(clave, errores);
      }
      if (!COD_DOC_RETENCION.equals(clave.substring(8, 10))) {
         errores.add("codDoc invalido: para comprobante de retencion debe ser 07.");
      }
      String ruc = normalizarNumero(definir != null ? definir.getRuc() : null);
      if (!ruc.matches("\\d{13}")) {
         errores.add("El RUC emisor configurado debe tener 13 digitos.");
      } else if (!ruc.equals(clave.substring(10, 23))) {
         errores.add("El RUC de la clave no coincide con el RUC emisor configurado.");
      }
      String ambiente = ambiente(definir);
      if (!clave.substring(23, 24).equals(ambiente)) {
         errores.add("El ambiente de la clave no coincide con el ambiente configurado.");
      }
      if (!"1".equals(clave.substring(47, 48))) {
         errores.add("El tipo de emision debe ser 1.");
      }
      if (!modulo11Calculator.esValido(clave)) {
         errores.add("El digito verificador modulo 11 no coincide.");
      }

      if (retencion != null) {
         String prefijoEsperado = formatearFechaClave(retencion.getFechaemision())
               + COD_DOC_RETENCION
               + (ruc.matches("\\d{13}") ? ruc : "0000000000000")
               + ambiente
               + serie(retencion)
               + secuencial(retencion);
         if (!clave.startsWith(prefijoEsperado)) {
            errores.add("La estructura fecha/codDoc/RUC/ambiente/serie/secuencial no coincide con la retencion.");
         }
      }

      if (errores.isEmpty()) {
         return ClaveAccesoValidationResult.valida(clave);
      }
      return ClaveAccesoValidationResult.invalida(clave, errores);
   }

   private String generarClaveUnica(Retenciones retencion, Definir definir) {
      for (int intento = 1; intento <= MAX_INTENTOS_GENERACION; intento++) {
         String clave = generarClave(retencion, definir);
         if (!claveExisteEnOtraRetencion(clave, retencion.getIdrete())) {
            return clave;
         }
         log.warn("Clave de acceso duplicada generada para retencion {} en intento {}", retencion.getIdrete(), intento);
      }
      throw new IllegalStateException("No se pudo generar una clave de acceso unica para la retencion "
            + retencion.getIdrete());
   }

   private String generarClave(Retenciones retencion, Definir definir) {
      String ruc = normalizarNumero(definir != null ? definir.getRuc() : null);
      if (!ruc.matches("\\d{13}")) {
         throw new IllegalStateException("El RUC emisor debe tener 13 digitos para generar la clave de acceso");
      }
      String base48 = formatearFechaClave(retencion.getFechaemision())
            + COD_DOC_RETENCION
            + ruc
            + ambiente(definir)
            + serie(retencion)
            + secuencial(retencion)
            + codigoNumerico(retencion)
            + TIPO_EMISION_NORMAL;
      if (!base48.matches("\\d{48}")) {
         throw new IllegalStateException("La base de la clave de acceso debe contener 48 digitos numericos");
      }
      return base48 + modulo11Calculator.calcularDigitoVerificador(base48);
   }

   private String codigoNumerico(Retenciones retencion) {
      long id = retencion.getIdrete() == null ? 0L : Math.abs(retencion.getIdrete());
      int random = ThreadLocalRandom.current().nextInt(0, 1000);
      long timestamp = System.currentTimeMillis() % 100000L;
      long codigo = ((id % 100000L) * 1000L + random + timestamp) % 100000000L;
      return String.format("%08d", codigo);
   }

   private boolean claveExisteEnOtraRetencion(String claveAcceso, Long idretencion) {
      if (claveAcceso == null || claveAcceso.isBlank()) {
         return false;
      }
      if (idretencion == null) {
         return fecRetencionesR.existsByClaveacceso(claveAcceso);
      }
      return fecRetencionesR.existsByClaveaccesoAndIdretencionNot(claveAcceso, idretencion);
   }

   private Definir getDefinir() {
      return definirR.findTopByOrderByIddefinirDesc();
   }

   private String ambiente(Definir definir) {
      String ambiente = definir == null || definir.getTipoambiente() == null ? "1" : String.valueOf(definir.getTipoambiente());
      if (!ambiente.matches("[12]")) {
         throw new IllegalStateException("El ambiente SRI debe ser 1 o 2");
      }
      return ambiente;
   }

   private String serie(Retenciones retencion) {
      return establecimiento(retencion) + puntoEmision(retencion);
   }

   public String establecimiento(Retenciones retencion) {
      String serie = normalizarNumero(retencion != null ? retencion.getNumserie() : null);
      if (serie.length() < 3) {
         throw new IllegalStateException("La serie debe contener establecimiento de 3 digitos");
      }
      return serie.substring(0, 3);
   }

   public String puntoEmision(Retenciones retencion) {
      String serie = normalizarNumero(retencion != null ? retencion.getNumserie() : null);
      if (serie.length() < 6) {
         throw new IllegalStateException("La serie debe contener punto de emision de 3 digitos");
      }
      return serie.substring(3, 6);
   }

   public String secuencial(Retenciones retencion) {
      String valor = normalizarNumero(retencion != null ? retencion.getSecretencion1() : null);
      if (valor.isBlank() && retencion != null && retencion.getIdrete() != null) {
         valor = String.valueOf(retencion.getIdrete());
      }
      if (valor.isBlank() || valor.length() > 9) {
         throw new IllegalStateException("El secuencial debe tener entre 1 y 9 digitos");
      }
      return String.format("%09d", Long.parseLong(valor));
   }

   private String formatearFechaClave(Date date) {
      if (date == null) {
         return LocalDateTime.now().format(DDMMYYYY);
      }
      if (date instanceof java.sql.Date) {
         return ((java.sql.Date) date).toLocalDate().format(DDMMYYYY);
      }
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DDMMYYYY);
   }

   private String normalizarNumero(String valor) {
      return valor == null ? "" : valor.replaceAll("[^0-9]", "").trim();
   }
}
