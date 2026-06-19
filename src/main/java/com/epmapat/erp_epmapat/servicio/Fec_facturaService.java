package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.epmapat.erp_epmapat.DTO.FecFacturaGestionFiltroDto;
import com.epmapat.erp_epmapat.interfaces.DefinirProjection;
import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.interfaces.FecFacturaGestionProjection;
import com.epmapat.erp_epmapat.interfaces.Fecfactura;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles;
import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles_impuestos;
import com.epmapat.erp_epmapat.modelo.Fec_factura_pagos;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.Fec_facturaR;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_detallesR;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_detalles_impuestosR;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_pagosR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.sri.interfaces.fecFacturaDatos;

@Service
public class Fec_facturaService {
   // Tipo de comprobante: Factura = "01"
   private static final String TIPO_COMPROBANTE_FACTURA = "01";
   private static final String CODIGO_IMPUESTO_IVA = "2";
   private static final String CODIGO_PORCENTAJE_IVA_0 = "0";
   private static final String CODIGO_PORCENTAJE_IVA_12 = "2";
   private static final String CODIGO_PORCENTAJE_IVA_14 = "3";
   private static final String CODIGO_PORCENTAJE_IVA_15 = "6";
   private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("ddMMyyyy");
   private static final int MAX_INTENTOS_AUTORIZACION = 10;
   private static final long DETALLE_RUBRO_FACTOR = 1_000_000L;
   private static final long IMPUESTO_RUBRO_FACTOR = 10_000_000_000_000L;
   private static final long RUBRO_INTERES_ID = 5L;
   @Autowired
   private Fec_facturaR dao;
   @Autowired
   private FacturasR facturasR;
   @Autowired
   private RubroxfacR rubroxfacR;
   @Autowired
   private DefinirR definirR;
   @Autowired
   private LecturasR lecturasR;
   @Autowired
   private Fec_factura_detallesR fecFacturaDetallesR;
   @Autowired
   private Fec_factura_detalles_impuestosR fecFacturaDetallesImpuestosR;
   @Autowired
   private Fec_factura_pagosR fecFacturaPagosR;
   @Autowired
   private RestTemplate restTemplate;
   @Autowired
   private FecFacturaLogService logService;

   @Value("${sri.microservice.base-url:http://192.168.0.33:9096}")
   private String sriMicroserviceBaseUrl;

   public List<Fec_factura> findAll() {
      return dao.findAll();
   }

   public List<Fec_factura> findByEstado(String estado, Long limit) {
      return dao.findByEstado(estado, limit);
   }

   public List<Fec_factura> findByCuenta(String referencia) {
      return dao.findByCuenta(referencia);
   }

   public List<Fec_factura> findByNombreCliente(String cliente) {
      return dao.findByNombreCliente(cliente);
   }

   public List<Fec_factura> findByFechaEmisionAndEstados(LocalDateTime desde, LocalDateTime hastaExclusive) {
      return dao.findByFechaEmisionAndEstados(desde, hastaExclusive);
   }

   public List<FecFacturaGestionProjection> buscarGestion(FecFacturaGestionFiltroDto filtro) {
      Integer limit = filtro.getLimit() == null || filtro.getLimit() <= 0 ? 300 : filtro.getLimit();
      return dao.buscarGestion(
            clean(filtro.getNumeroFactura()),
            clean(filtro.getClaveAcceso()),
            clean(filtro.getEstadoSri()),
            clean(filtro.getCliente()),
            clean(filtro.getIdentificacion()),
            clean(filtro.getEstablecimiento()),
            clean(filtro.getPuntoEmision()),
            filtro.getIdusuario(),
            digits(filtro.getSecuencialDesde()),
            digits(filtro.getSecuencialHasta()),
            clean(filtro.getFechaDesde()),
            clean(filtro.getFechaHasta()),
            clean(filtro.getEmailEstado()),
            filtro.getSwmail(),
            filtro.getMailIntentos(),
            clean(filtro.getMailError()),
            filtro.getSoloFallidos(),
            limit);
   }

   public <S extends Fec_factura> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Fec_factura> findById(Long id) {
      return dao.findById(id);
   }

   public fecFacturaDatos getNroFactura(Long idfactura) {
      return dao.getNroFactura(idfactura);
   }

   // CREAR LAS FACTURAS
   @Transactional
   public Map<String, Object> generarFecFactura(Long idfactura) {
      Map<String, Object> response = new HashMap<>();
      Fecfactura factura = facturasR.forFecfactura(idfactura);
      Fec_factura fecFactura = dao.findById(idfactura).orElseGet(Fec_factura::new);
      DefinirProjection definir = definirR.findDefinirWithoutFirma(1L);
      String concepto = "OTROS SERVICIOS";
      if (factura.getNrofactura() == null || factura.getNrofactura().isEmpty()) {
         response.put("message", "No se encontro el numero de factura" + idfactura);
         return response;
      }
      String[] partes = separarCodigo(factura.getNrofactura());
      String establecimiento = partes[0]; // 001
      String puntoEmision = partes[1]; // 013
      String secuencial = partes[2]; // 000022233

      Lecturas lectura = lecturasR.findOnefactura(idfactura);
      float m3 = 0;
      if (lectura != null) {
         SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
         Date fecemision = lecturasR.findDateByIdfactura(idfactura);
         String fechaFormateada = sdf.format(fecemision);
         m3 = lectura.getLecturaactual() - lectura.getLecturaanterior();
         concepto = "M3: " + m3 + " Emision: " + fechaFormateada + " Nro medidor: "
               + lectura.getIdabonado_abonados().getNromedidor();

      }
      fecFactura.setConcepto(concepto);
      limpiarEstructuraFacturaElectronica(idfactura);
      // BUILD FECFACTURA
      fecFactura.setIdfactura(factura.getIdfactura());
      fecFactura.setDireccionestablecimiento(definir.getDirmatriz());
      fecFactura.setEmailcomprador(factura.getEmail());
      fecFactura.setRecaudador(factura.getNomusu());
      fecFactura.setFechaemision(factura.getFechacobro().atStartOfDay());
      fecFactura.setEstablecimiento(establecimiento);
      fecFactura.setPuntoemision(puntoEmision);
      fecFactura.setSecuencial(secuencial);
      fecFactura.setRazonsocialcomprador(factura.getNombre());
      fecFactura.setIdentificacioncomprador(factura.getCedula());
      fecFactura.setTelefonocomprador(factura.getTelefono());
      fecFactura.setTipoidentificacioncomprador(factura.getCodigo());
      fecFactura.setReferencia(factura.getIdabonado());
      fecFactura.setDireccioncomprador(factura.getDireccion());
      fecFactura.setClaveacceso(generarClaveAcceso(fecFactura, definir));
      fecFactura.setEstado("I");
      fecFactura.setXmlautorizado(null);
      fecFactura.setErrores(null);
      fecFactura.setIntentosAutorizacion(0);
      fecFactura.setFechaUltimoIntento(null);
      fecFactura.setFechaAutorizacion(null);
      fecFactura.setMailEnviado(Boolean.FALSE);
      fecFactura.setMailIntentos(0);
      fecFactura.setMailError(null);
      fecFactura.setEmailEstado("NO_ENVIADO");
      fecFactura.setFechaReenvio(null);
      dao.save(fecFactura);
      generarFecFacturaDetalles(idfactura);
      generarFecFacturaPagos(idfactura, (long) m3);
      response.put("idfactura", idfactura);
      response.put("claveacceso", fecFactura.getClaveacceso());
      response.put("estado", fecFactura.getEstado());
      response.put("message", "Factura electronica generada correctamente");
      return response;
   }

   @Transactional
   public Map<String, Object> asegurarFecFactura(Long idfactura) {
      return generarFecFactura(idfactura);
   }

   public List<Fec_factura> findPendientesAutorizacion(int limit) {
      return dao.findPendientesAutorizacion(MAX_INTENTOS_AUTORIZACION, limit);
   }

   public List<Fec_factura> findListasParaCorreo(int limit) {
      return dao.findListasParaCorreo(limit);
   }

   public Fec_factura marcarPendienteAutorizacion(Fec_factura factura, String motivo) {
      factura.setEstado("P");
      factura.setErrores(motivo);
      factura.setFechaUltimoIntento(LocalDateTime.now());
      Fec_factura actualizada = dao.save(factura);
      logService.registrar(factura.getIdfactura(), "P", motivo);
      return actualizada;
   }

   public Fec_factura registrarAutorizacionRecuperada(Fec_factura factura, String xmlAutorizado) {
      factura.setXmlautorizado(xmlAutorizado);
      factura.setEstado("X");
      factura.setErrores(null);
      factura.setFechaAutorizacion(LocalDateTime.now());
      factura.setFechaUltimoIntento(LocalDateTime.now());
      Fec_factura actualizada = dao.save(factura);
      logService.registrar(factura.getIdfactura(), "X", "XML autorizado recuperado correctamente");
      return actualizada;
   }

   public Fec_factura incrementarIntentoAutorizacion(Fec_factura factura, String motivo) {
      int intentos = factura.getIntentosAutorizacion() == null ? 0 : factura.getIntentosAutorizacion();
      intentos++;
      factura.setIntentosAutorizacion(intentos);
      factura.setFechaUltimoIntento(LocalDateTime.now());
      factura.setErrores(motivo);
      factura.setEstado(intentos >= MAX_INTENTOS_AUTORIZACION ? "E" : "P");
      Fec_factura actualizada = dao.save(factura);
      logService.registrar(factura.getIdfactura(), factura.getEstado(), motivo + " (intento " + intentos + ")");
      return actualizada;
   }

   public Fec_factura marcarCorreoEnviado(Fec_factura factura) {
      factura.setMailEnviado(Boolean.TRUE);
      if ("O".equals(String.valueOf(factura.getEstado()))) {
         factura.setEstado("A");
      }
      factura.setErrores(null);
      factura.setEmailEstado("ENVIADO");
      factura.setMailError(null);
      factura.setMailIntentos(factura.getMailIntentos() == null ? 1 : factura.getMailIntentos());
      factura.setFechaReenvio(LocalDateTime.now());
      return dao.save(factura);
   }

   public Fec_factura marcarErrorPostAutorizacion(Fec_factura factura, String motivo) {
      factura.setErrores(motivo);
      factura.setEstado("E");
      factura.setMailError(motivo);
      factura.setEmailEstado("ERROR_ENVIO");
      return dao.save(factura);
   }

   public Optional<Fec_factura> recuperarXmlAutorizado(Long idfactura) {
      Fec_factura factura = dao.findById(idfactura).orElseThrow(() ->
            new IllegalArgumentException("No existe fec_factura para idfactura=" + idfactura));
      return recuperarXmlAutorizado(factura);
   }

   public Optional<Fec_factura> recuperarXmlAutorizado(Fec_factura factura) {
      try {
         String url = sriMicroserviceBaseUrl + "/api/singsend/autorizacion?claveAcceso=" + factura.getClaveacceso();
         String xml = restTemplate.getForObject(url, String.class);
         if (xml == null || xml.isBlank()) {
            marcarPendienteAutorizacion(factura, "SRI sin XML autorizado disponible todavia");
            return Optional.empty();
         }
         return Optional.of(registrarAutorizacionRecuperada(factura, xml));
      } catch (Exception e) {
         incrementarIntentoAutorizacion(factura, "No fue posible recuperar XML autorizado: " + e.getMessage());
         return Optional.empty();
      }
   }

   private void limpiarEstructuraFacturaElectronica(Long idfactura) {
      List<Long> detalleIds = fecFacturaDetallesR.findByIdfactura(idfactura).stream()
            .map(Fec_factura_detalles::getIdfacturadetalle)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

      if (!detalleIds.isEmpty()) {
         fecFacturaDetallesImpuestosR.deleteByIdfacturadetalleIn(detalleIds);
      }

      fecFacturaDetallesR.deleteByIdfactura(idfactura);
      fecFacturaPagosR.deleteByIdfactura(idfactura);
   }

   // CREAR FACTURA DETALLE
   public void _generarFecFacturaDetalles(Long idfactura) {
      Fec_factura_detalles fecFacturaDetalles = new Fec_factura_detalles();
      List<Rubroxfac> rxf = rubroxfacR.findByIdfactura(idfactura);
      fecFacturaDetalles.setIdfactura(idfactura);
      fecFacturaDetalles.setDescuento(BigDecimal.valueOf(0));
      rxf.forEach(item -> {
         String idfacdetalle = String.valueOf(idfactura + "" + item.getIdrubro_rubros().getIdrubro());
         fecFacturaDetalles.setIdfacturadetalle(Long.valueOf(idfacdetalle));
         fecFacturaDetalles.setCodigoprincipal(String.valueOf(item.getIdrubro_rubros().getIdrubro()));
         fecFacturaDetalles.setCantidad(BigDecimal.valueOf(item.getCantidad()));
         fecFacturaDetalles.setPreciounitario(item.getValorunitario());
         fecFacturaDetalles.setDescripcion(item.getIdrubro_rubros().getDescripcion());
         fecFacturaDetallesR.save(fecFacturaDetalles);
         _generarFecFacturaDetallesImpuestos(item, Long.valueOf(idfacdetalle));
      });
   }

   public void _generarFecFacturaDetallesImpuestos(Rubroxfac rxf, Long idfecfacturadetalle) {
      Fec_factura_detalles_impuestos fecFacturaDetallesImpuestos = new Fec_factura_detalles_impuestos();
      String idfacdetalleimpuestos = String.valueOf(rxf.getIdrubro_rubros().getIdrubro() + "" + idfecfacturadetalle);
      fecFacturaDetallesImpuestos.setIdfacturadetalleimpuestos(Long.valueOf(idfacdetalleimpuestos));
      fecFacturaDetallesImpuestos.setIdfacturadetalle(idfecfacturadetalle);
      fecFacturaDetallesImpuestos.setCodigoimpuesto("2");
      fecFacturaDetallesImpuestos.setCodigoporcentaje("0");
      fecFacturaDetallesImpuestos.setBaseimponible(rxf.getValorunitario());
      fecFacturaDetallesImpuestosR.save(fecFacturaDetallesImpuestos);

   }

   // CREAR FACTURA DETALLE
   public void __generarFecFacturaDetalles(Long idfactura) {

      List<Rubroxfac> rxf = rubroxfacR.findByIdfactura(idfactura);

      // --- 1) Separar rubros 1006/1007 para consolidar ---
      Set<Long> rubrosConsolidar = Set.of(1006L, 1007L);

      List<Rubroxfac> aConsolidar = rxf.stream()
            .filter(x -> x.getIdrubro_rubros() != null
                  && x.getIdrubro_rubros().getIdrubro() != null
                  && rubrosConsolidar.contains(x.getIdrubro_rubros().getIdrubro()))
            .toList();

      List<Rubroxfac> normales = rxf.stream()
            .filter(x -> x.getIdrubro_rubros() == null
                  || x.getIdrubro_rubros().getIdrubro() == null
                  || !rubrosConsolidar.contains(x.getIdrubro_rubros().getIdrubro()))
            .toList();

      // --- 2) Guardar los rubros normales (sin 1006/1007) ---
      for (Rubroxfac item : normales) {

         Fec_factura_detalles fecFacturaDetalles = new Fec_factura_detalles(); // âœ… nuevo objeto por item
         fecFacturaDetalles.setIdfactura(idfactura);
         fecFacturaDetalles.setDescuento(BigDecimal.ZERO);

         String idfacdetalle = String.valueOf(idfactura + "" + item.getIdrubro_rubros().getIdrubro());

         fecFacturaDetalles.setIdfacturadetalle(Long.valueOf(idfacdetalle));
         fecFacturaDetalles.setCodigoprincipal(String.valueOf(item.getIdrubro_rubros().getIdrubro()));
         fecFacturaDetalles.setCantidad(BigDecimal.valueOf(item.getCantidad())); // tu cantidad
         fecFacturaDetalles.setPreciounitario(item.getValorunitario());
         fecFacturaDetalles.setDescripcion(item.getIdrubro_rubros().getDescripcion());

         fecFacturaDetallesR.save(fecFacturaDetalles);

         generarFecFacturaDetallesImpuestos(item, Long.valueOf(idfacdetalle));
      }

      // --- 3) Guardar el consolidado 1006+1007 como 1004 ---
      if (!aConsolidar.isEmpty()) {

         BigDecimal suma = aConsolidar.stream()
               .map(Rubroxfac::getValorunitario)
               .filter(v -> v != null)
               .reduce(BigDecimal.ZERO, BigDecimal::add);

         Fec_factura_detalles detConsolidado = new Fec_factura_detalles();
         detConsolidado.setIdfactura(idfactura);
         detConsolidado.setDescuento(BigDecimal.ZERO);

         // âœ… rubro resultante: 1004
         Long rubroFinal = 1004L;

         String idfacdetalle = String.valueOf(idfactura + "" + rubroFinal);
         Long idDetalle = Long.valueOf(idfacdetalle);

         detConsolidado.setIdfacturadetalle(idDetalle);
         detConsolidado.setCodigoprincipal(String.valueOf(rubroFinal));
         detConsolidado.setCantidad(BigDecimal.ONE); // âœ… cantidad = 1
         detConsolidado.setPreciounitario(suma); // âœ… precioUnitario sumado
         detConsolidado.setDescripcion("ConservaciÃ³n de fuentes"); // âœ…

         fecFacturaDetallesR.save(detConsolidado);

         // âœ… impuesto Ãºnico tomando base imponible = suma
         generarFecFacturaDetallesImpuestosConsolidado(idDetalle, suma);
      }
   }

   @Transactional
   public void generarFecFacturaDetalles(Long idfactura) {
      DefinirProjection definir = definirR.findDefinirWithoutFirma(1L);
      String codigoPorcentajeIva = resolverCodigoPorcentajeIva(definir);

      Facturas factura = facturasR.findById(idfactura)
            .orElseThrow(() -> new IllegalArgumentException("No existe la factura " + idfactura));

      BigDecimal interesCobrado = factura.getInterescobrado() == null
            ? BigDecimal.ZERO
            : factura.getInterescobrado();
      boolean hayInteres = interesCobrado.compareTo(BigDecimal.ZERO) > 0;

      List<Rubroxfac> rxf = rubroxfacR.findByIdfactura(idfactura);

      Set<Long> rubrosConsolidar = Set.of(1006L, 1007L);

      List<Rubroxfac> aConsolidar = rxf.stream()
            .filter(x -> x.getIdrubro_rubros() != null
                  && x.getIdrubro_rubros().getIdrubro() != null
                  && rubrosConsolidar.contains(x.getIdrubro_rubros().getIdrubro()))
            .toList();

      List<Rubroxfac> normales = rxf.stream()
            .filter(x -> x.getIdrubro_rubros() != null
                  && x.getIdrubro_rubros().getIdrubro() != null
                  && !Objects.equals(x.getIdrubro_rubros().getIdrubro(), RUBRO_INTERES_ID)
                  && !rubrosConsolidar.contains(x.getIdrubro_rubros().getIdrubro()))
            .toList();

      List<Rubroxfac> rubrosInteres = rxf.stream()
            .filter(x -> x.getIdrubro_rubros() != null
                  && x.getIdrubro_rubros().getIdrubro() != null
                  && Objects.equals(x.getIdrubro_rubros().getIdrubro(), RUBRO_INTERES_ID))
            .toList();

      // ---------------------------
      // A) Construir y guardar DETALLES
      // ---------------------------
      Map<Long, Fec_factura_detalles> detallesPorId = new LinkedHashMap<>();

      // normales (sin 1006/1007)
      for (Rubroxfac item : normales) {
         Long rubro = item.getIdrubro_rubros().getIdrubro();
         Long idDetalle = buildDetalleId(idfactura, rubro);

         Fec_factura_detalles d = new Fec_factura_detalles();
         d.setIdfactura(idfactura);
         d.setIdfacturadetalle(idDetalle);
         d.setCodigoprincipal(String.valueOf(rubro));
         d.setCantidad(BigDecimal.valueOf(item.getCantidad()));
         d.setPreciounitario(item.getValorunitario());
         d.setDescripcion(item.getIdrubro_rubros().getDescripcion());
         d.setDescuento(BigDecimal.ZERO);

         detallesPorId.put(idDetalle, d);
      }

      // consolidado 1006+1007 -> 1004
      BigDecimal suma = aConsolidar.stream()
            .map(Rubroxfac::getValorunitario)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

      boolean hayConsolidado = suma.compareTo(BigDecimal.ZERO) > 0;

      Long idDetalleConsolidado = null;
      if (hayConsolidado) {
         Long rubroFinal = 1004L;
         idDetalleConsolidado = buildDetalleId(idfactura, rubroFinal);

         Fec_factura_detalles d = new Fec_factura_detalles();
         d.setIdfactura(idfactura);
         d.setIdfacturadetalle(idDetalleConsolidado);
         d.setCodigoprincipal(String.valueOf(rubroFinal));
         d.setCantidad(BigDecimal.ONE);
         d.setPreciounitario(suma);
         d.setDescripcion("ConservaciÃ³n de fuentes");
         d.setDescuento(BigDecimal.ZERO);

         detallesPorId.put(idDetalleConsolidado, d);
      }

      Long idDetalleInteres = null;
      if (hayInteres) {
         idDetalleInteres = buildDetalleId(idfactura, RUBRO_INTERES_ID);

         Fec_factura_detalles detalleInteres = new Fec_factura_detalles();
         detalleInteres.setIdfactura(idfactura);
         detalleInteres.setIdfacturadetalle(idDetalleInteres);
         detalleInteres.setCodigoprincipal(String.valueOf(RUBRO_INTERES_ID));
         detalleInteres.setCantidad(BigDecimal.ONE);
         detalleInteres.setPreciounitario(interesCobrado);
         detalleInteres.setDescripcion(rubrosInteres.stream()
               .map(Rubroxfac::getIdrubro_rubros)
               .filter(Objects::nonNull)
               .map(rubro -> rubro.getDescripcion())
               .filter(Objects::nonNull)
               .findFirst()
               .orElse("Interés"));
         detalleInteres.setDescuento(BigDecimal.ZERO);

         detallesPorId.put(idDetalleInteres, detalleInteres);
      }

      // âœ… Guardar todos los DETALLES primero
      List<Fec_factura_detalles> detallesAGuardar = new ArrayList<>(detallesPorId.values());
      fecFacturaDetallesR.saveAll(detallesAGuardar);

      // âœ… Forzar que existan en BD antes de insertar impuestos
      fecFacturaDetallesR.flush();

      Set<Long> detalleIdsPersistidos = fecFacturaDetallesR.findByIdfactura(idfactura).stream()
            .map(Fec_factura_detalles::getIdfacturadetalle)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

      // ---------------------------
      // B) Ahora sÃ­, construir y guardar IMPUESTOS
      // ---------------------------
      List<Fec_factura_detalles_impuestos> impuestos = new ArrayList<>();

      // impuestos de normales
      for (Rubroxfac item : normales) {
         Long rubro = item.getIdrubro_rubros().getIdrubro();
         Long idDetalle = buildDetalleId(idfactura, rubro);

         if (detalleIdsPersistidos.contains(idDetalle)) {
            impuestos.add(crearImpuesto(idDetalle, rubro, item.getValorunitario(),
                  Boolean.TRUE.equals(item.getIdrubro_rubros().getSwiva()), codigoPorcentajeIva));
         }
      }

      // impuesto Ãºnico consolidado (1004)
      if (hayConsolidado && detalleIdsPersistidos.contains(idDetalleConsolidado)) {
         boolean consolidadoGravaIva = aConsolidar.stream()
               .map(Rubroxfac::getIdrubro_rubros)
               .filter(Objects::nonNull)
               .anyMatch(rubro -> Boolean.TRUE.equals(rubro.getSwiva()));
         impuestos.add(crearImpuesto(idDetalleConsolidado, 1004L, suma, consolidadoGravaIva, codigoPorcentajeIva));
      }

      if (hayInteres && idDetalleInteres != null && detalleIdsPersistidos.contains(idDetalleInteres)) {
         boolean interesGravaIva = rubrosInteres.stream()
               .map(Rubroxfac::getIdrubro_rubros)
               .filter(Objects::nonNull)
               .anyMatch(rubro -> Boolean.TRUE.equals(rubro.getSwiva()));
         impuestos.add(crearImpuesto(idDetalleInteres, RUBRO_INTERES_ID, interesCobrado, interesGravaIva,
               codigoPorcentajeIva));
      }

      fecFacturaDetallesImpuestosR.saveAll(impuestos);
      fecFacturaDetallesImpuestosR.flush();
   }

   private Fec_factura_detalles_impuestos crearImpuesto(Long idDetalle, Long rubro, BigDecimal base,
         boolean aplicaIva, String codigoPorcentajeIva) {
      Fec_factura_detalles_impuestos imp = new Fec_factura_detalles_impuestos();

      // id Ãºnico (mejor hacerlo robusto, pero te dejo tu estilo)
      Long idImp = buildImpuestoId(idDetalle, rubro);

      imp.setIdfacturadetalleimpuestos(idImp);
      imp.setIdfacturadetalle(idDetalle);
      imp.setCodigoimpuesto(CODIGO_IMPUESTO_IVA);
      imp.setCodigoporcentaje(aplicaIva ? codigoPorcentajeIva : CODIGO_PORCENTAJE_IVA_0);
      imp.setBaseimponible(base);

      return imp;
   }

   private Long buildDetalleId(Long idfactura, Long rubro) {
      if (idfactura == null || rubro == null) {
         throw new IllegalArgumentException("idfactura y rubro son obligatorios para generar idfacturadetalle");
      }
      if (rubro >= DETALLE_RUBRO_FACTOR) {
         throw new IllegalArgumentException("idrubro fuera de rango para generar idfacturadetalle: " + rubro);
      }
      return idfactura * DETALLE_RUBRO_FACTOR + rubro;
   }

   private Long buildImpuestoId(Long idDetalle, Long rubro) {
      if (idDetalle == null || rubro == null) {
         throw new IllegalArgumentException("idDetalle y rubro son obligatorios para generar idfacturadetalleimpuestos");
      }
      if (rubro >= DETALLE_RUBRO_FACTOR) {
         throw new IllegalArgumentException("idrubro fuera de rango para generar idfacturadetalleimpuestos: " + rubro);
      }
      return rubro * IMPUESTO_RUBRO_FACTOR + idDetalle;
   }

   private String resolverCodigoPorcentajeIva(DefinirProjection definir) {
      BigDecimal porcentaje = definir == null || definir.getPorciva() == null
            ? BigDecimal.ZERO
            : definir.getPorciva().stripTrailingZeros();
      if (BigDecimal.valueOf(12).compareTo(porcentaje) == 0) {
         return CODIGO_PORCENTAJE_IVA_12;
      }
      if (BigDecimal.valueOf(14).compareTo(porcentaje) == 0) {
         return CODIGO_PORCENTAJE_IVA_14;
      }
      if (BigDecimal.valueOf(15).compareTo(porcentaje) == 0) {
         return CODIGO_PORCENTAJE_IVA_15;
      }
      return CODIGO_PORCENTAJE_IVA_0;
   }

   public void generarFecFacturaDetallesImpuestos(Rubroxfac rxf, Long idfecfacturadetalle) {
      Fec_factura_detalles_impuestos fecFacturaDetallesImpuestos = new Fec_factura_detalles_impuestos();

      String idfacdetalleimpuestos = String.valueOf(
            rxf.getIdrubro_rubros().getIdrubro() + "" + idfecfacturadetalle);

      fecFacturaDetallesImpuestos.setIdfacturadetalleimpuestos(Long.valueOf(idfacdetalleimpuestos));
      fecFacturaDetallesImpuestos.setIdfacturadetalle(idfecfacturadetalle);
      fecFacturaDetallesImpuestos.setCodigoimpuesto("2");
      fecFacturaDetallesImpuestos.setCodigoporcentaje("0");
      fecFacturaDetallesImpuestos.setBaseimponible(rxf.getValorunitario());

      fecFacturaDetallesImpuestosR.save(fecFacturaDetallesImpuestos);
   }

   // âœ… impuesto Ãºnico para el consolidado 1004
   public void generarFecFacturaDetallesImpuestosConsolidado(Long idfecfacturadetalle, BigDecimal baseImponible) {

      Fec_factura_detalles_impuestos imp = new Fec_factura_detalles_impuestos();

      // id Ãºnico (puedes dejar esta lÃ³gica o definir una mÃ¡s segura)
      String idfacdetalleimpuestos = String.valueOf(1004L + "" + idfecfacturadetalle);

      imp.setIdfacturadetalleimpuestos(Long.valueOf(idfacdetalleimpuestos));
      imp.setIdfacturadetalle(idfecfacturadetalle);
      imp.setCodigoimpuesto("2");
      imp.setCodigoporcentaje("0");
      imp.setBaseimponible(baseImponible); // âœ… suma

      fecFacturaDetallesImpuestosR.save(imp);
   }

   public void generarFecFacturaPagos(Long idfactura, Long m3) {
      List<FacIntereses> intereses = facturasR.getForIntereses(idfactura);
      for (FacIntereses item : intereses) {
         Fec_factura_pagos fecFacturaPagos = new Fec_factura_pagos();
         fecFacturaPagos.setTotal(BigDecimal.valueOf(item.getSuma()));
         Long formapago = item.getFormapago();
         if (formapago == 1 || formapago == 3 || formapago == 6) {
            fecFacturaPagos.setFormapago("01"); // Efectivo
         } else if (formapago == 4 || formapago == 7) {
            fecFacturaPagos.setFormapago("20"); // Tarjeta
         } else if (formapago == 5) {
            fecFacturaPagos.setFormapago("19"); // Otro medio
         } else {
            fecFacturaPagos.setFormapago("99"); // Desconocido
         }
         String idfacturapagos = String.valueOf(idfactura + "" + m3);
         fecFacturaPagos.setIdfacturapagos(Long.valueOf(idfacturapagos));
         fecFacturaPagos.setUnidadtiempo("dias");
         fecFacturaPagos.setPlazo(0);
         fecFacturaPagos.setIdfactura(idfactura);
         fecFacturaPagosR.save(fecFacturaPagos);

      }
   }

   // COMPLEMENTOS PARA GENERAR LA FECFACTURA
   public static String[] separarCodigo(String codigo) {
      if (codigo == null || !codigo.contains("-")) {
         throw new IllegalArgumentException("El cÃ³digo no tiene el formato esperado");
      }

      // Separa por guiones
      String[] partes = codigo.split("-");

      if (partes.length != 3) {
         throw new IllegalArgumentException("El cÃ³digo debe tener 3 partes (ej: 001-013-000022233)");
      }

      return partes;
   }
   // 2909202501046002881000110020180003172614598738814

   public String generarClaveAcceso(Fec_factura factura, DefinirProjection definir) {
      Objects.requireNonNull(factura, "Factura no puede ser nula");
      Objects.requireNonNull(definir, "Definir no puede ser nulo");

      // 1) Fecha de emisiÃ³n (DDMMYYYY)
      String fechaEmision = formatToDDMMYYYY(factura.getFechaemision());

      // 2) Tipo de comprobante (2 dÃ­gitos)
      String tipoComprobante = TIPO_COMPROBANTE_FACTURA;

      // 3) RUC del emisor (13 dÃ­gitos)
      String ruc = leftPadDigits(requireDigits(definir.getRuc(), "RUC"), 13);

      // 4) Ambiente (1 dÃ­gito: 1=Pruebas, 2=ProducciÃ³n)
      String ambiente = requireDigits(definir.getTipoambiente() == null ? null : definir.getTipoambiente().toString(),
            "Tipo de ambiente");
      if (!(ambiente.equals("1") || ambiente.equals("2"))) {
         throw new IllegalArgumentException("El ambiente debe ser '1' (pruebas) o '2' (producciÃ³n)");
      }

      // 5) Serie: 3 dÃ­gitos establecimiento + 3 dÃ­gitos punto de emisiÃ³n
      String estab = leftPadDigits(requireDigits(factura.getEstablecimiento(), "Establecimiento"), 3);
      String ptoEmi = leftPadDigits(requireDigits(factura.getPuntoemision(), "Punto de emisiÃ³n"), 3);
      String serie = estab + ptoEmi;

      // 6) Secuencial (9 dÃ­gitos)
      String secuencial = leftPadDigits(requireDigits(factura.getSecuencial(), "Secuencial"), 9);

      // 7) CÃ³digo numÃ©rico (8 dÃ­gitos) â€” aleatorio o el que definas en tu negocio
      String codigoNumerico = generarCodigoNumerico8();

      // 8) Tipo de emisiÃ³n (1 dÃ­gito, normal=1)
      String tipoEmision = "1";

      // Concatenar base de 48 dÃ­gitos
      String base48 = fechaEmision + tipoComprobante + ruc + ambiente + serie + secuencial + codigoNumerico
            + tipoEmision;

      if (base48.length() != 48) {
         throw new IllegalStateException(
               "La base de la clave de acceso debe tener 48 dÃ­gitos, actual: " + base48.length());
      }
      if (!base48.chars().allMatch(Character::isDigit)) {
         throw new IllegalStateException("La base de la clave de acceso debe contener solo dÃ­gitos");
      }

      // 9) DÃ­gito verificador (mÃ³dulo 11 con pesos 2..7 de derecha a izquierda)
      char dv = calcularDigitoVerificadorModulo11(base48);

      // 10) Clave completa (49 dÃ­gitos)
      return base48 + dv;
   }

   // ----------------- Utilidades -----------------

   /** Formatea LocalDateTime a DDMMYYYY (SRI). */
   private String formatToDDMMYYYY(LocalDateTime dateTime) {
      if (dateTime == null)
         throw new IllegalArgumentException("La fecha no puede ser nula");
      return dateTime.format(DDMMYYYY);
   }

   /** Genera un cÃ³digo numÃ©rico aleatorio de 8 dÃ­gitos (00000000â€“99999999). */
   private static String generarCodigoNumerico8() {
      // Si no deseas ceros a la izquierda, usa rango 10000000..99999999
      int n = ThreadLocalRandom.current().nextInt(0, 100_000_000);
      return String.format("%08d", n);
   }

   /**
    * CÃ¡lculo del dÃ­gito verificador para clave de acceso SRI (mÃ³dulo 11):
    * - PonderaciÃ³n cÃ­clica 2..7 de derecha a izquierda.
    * - dv = 11 - (suma % 11)
    * - Si dv == 11 -> 0 ; si dv == 10 -> 1.
    */
   private static char calcularDigitoVerificadorModulo11(String base48) {
      if (base48 == null || base48.length() != 48 || !base48.chars().allMatch(Character::isDigit)) {
         throw new IllegalArgumentException("Se espera base de 48 dÃ­gitos numÃ©ricos");
      }
      final int[] pesos = { 2, 3, 4, 5, 6, 7 };
      int suma = 0;
      int idx = 0;
      for (int i = base48.length() - 1; i >= 0; i--) {
         int digito = base48.charAt(i) - '0';
         suma += digito * pesos[idx];
         idx = (idx + 1) % pesos.length;
      }
      int mod = suma % 11;
      int dv = 11 - mod;
      if (dv == 11)
         dv = 0;
      else if (dv == 10)
         dv = 1;
      return (char) ('0' + dv);
   }

   /** Verifica y normaliza que el texto contenga solo dÃ­gitos. */
   private static String requireDigits(String value, String label) {
      if (value == null || value.isBlank())
         throw new IllegalArgumentException(label + " no puede ser nulo o vacÃ­o");
      String digits = value.replaceAll("\\D", "");
      if (digits.isEmpty())
         throw new IllegalArgumentException(label + " debe contener dÃ­gitos");
      return digits;
   }

   /** Rellena a la izquierda con ceros hasta la longitud indicada. */
   private static String leftPadDigits(String digits, int length) {
      if (!digits.chars().allMatch(Character::isDigit))
         throw new IllegalArgumentException("Valor no numÃ©rico: " + digits);
      if (digits.length() > length)
         throw new IllegalArgumentException("Longitud mayor a " + length + ": " + digits);
      return String.format("%0" + length + "d", Long.parseLong(digits));
   }

   public static void validarClaveAcceso(String clave) {
      if (clave == null || !clave.matches("\\d{49}")) {
         throw new IllegalArgumentException("claveAcceso invÃ¡lida: debe tener 49 dÃ­gitos");
      }
   }

   public void delete(Long id) {
      dao.deleteById(id);
   }

   private String clean(String value) {
      if (value == null) {
         return null;
      }
      String trimmed = value.trim();
      return trimmed.isEmpty() ? null : trimmed;
   }

   private String digits(String value) {
      String cleaned = clean(value);
      if (cleaned == null) {
         return null;
      }
      String onlyDigits = cleaned.replaceAll("\\D", "");
      return onlyDigits.isEmpty() ? null : onlyDigits;
   }

}


