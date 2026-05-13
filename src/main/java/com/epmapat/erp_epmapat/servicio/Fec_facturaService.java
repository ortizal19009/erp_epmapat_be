package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.DefinirProjection;
import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.interfaces.Fecfactura;
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
   private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("ddMMyyyy");
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

         Fec_factura_detalles fecFacturaDetalles = new Fec_factura_detalles(); // ✅ nuevo objeto por item
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

         // ✅ rubro resultante: 1004
         Long rubroFinal = 1004L;

         String idfacdetalle = String.valueOf(idfactura + "" + rubroFinal);
         Long idDetalle = Long.valueOf(idfacdetalle);

         detConsolidado.setIdfacturadetalle(idDetalle);
         detConsolidado.setCodigoprincipal(String.valueOf(rubroFinal));
         detConsolidado.setCantidad(BigDecimal.ONE); // ✅ cantidad = 1
         detConsolidado.setPreciounitario(suma); // ✅ precioUnitario sumado
         detConsolidado.setDescripcion("Conservación de fuentes"); // ✅

         fecFacturaDetallesR.save(detConsolidado);

         // ✅ impuesto único tomando base imponible = suma
         generarFecFacturaDetallesImpuestosConsolidado(idDetalle, suma);
      }
   }

   @Transactional
   public void generarFecFacturaDetalles(Long idfactura) {

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
                  && !rubrosConsolidar.contains(x.getIdrubro_rubros().getIdrubro()))
            .toList();

      // ---------------------------
      // A) Construir y guardar DETALLES
      // ---------------------------
      List<Fec_factura_detalles> detallesAGuardar = new ArrayList<>();

      // normales (sin 1006/1007)
      for (Rubroxfac item : normales) {
         Long rubro = item.getIdrubro_rubros().getIdrubro();
         Long idDetalle = Long.valueOf(String.valueOf(idfactura) + rubro);

         Fec_factura_detalles d = new Fec_factura_detalles();
         d.setIdfactura(idfactura);
         d.setIdfacturadetalle(idDetalle);
         d.setCodigoprincipal(String.valueOf(rubro));
         d.setCantidad(BigDecimal.valueOf(item.getCantidad()));
         d.setPreciounitario(item.getValorunitario());
         d.setDescripcion(item.getIdrubro_rubros().getDescripcion());
         d.setDescuento(BigDecimal.ZERO);

         detallesAGuardar.add(d);
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
         idDetalleConsolidado = Long.valueOf(String.valueOf(idfactura) + rubroFinal);

         Fec_factura_detalles d = new Fec_factura_detalles();
         d.setIdfactura(idfactura);
         d.setIdfacturadetalle(idDetalleConsolidado);
         d.setCodigoprincipal(String.valueOf(rubroFinal));
         d.setCantidad(BigDecimal.ONE);
         d.setPreciounitario(suma);
         d.setDescripcion("Conservación de fuentes");
         d.setDescuento(BigDecimal.ZERO);

         detallesAGuardar.add(d);
      }

      // ✅ Guardar todos los DETALLES primero
      fecFacturaDetallesR.saveAll(detallesAGuardar);

      // ✅ Forzar que existan en BD antes de insertar impuestos
      fecFacturaDetallesR.flush();

      // ---------------------------
      // B) Ahora sí, construir y guardar IMPUESTOS
      // ---------------------------
      List<Fec_factura_detalles_impuestos> impuestos = new ArrayList<>();

      // impuestos de normales
      for (Rubroxfac item : normales) {
         Long rubro = item.getIdrubro_rubros().getIdrubro();
         Long idDetalle = Long.valueOf(String.valueOf(idfactura) + rubro);

         impuestos.add(crearImpuesto(idDetalle, rubro, item.getValorunitario()));
      }

      // impuesto único consolidado (1004)
      if (hayConsolidado) {
         impuestos.add(crearImpuesto(idDetalleConsolidado, 1004L, suma));
      }

      fecFacturaDetallesImpuestosR.saveAll(impuestos);
   }

   private Fec_factura_detalles_impuestos crearImpuesto(Long idDetalle, Long rubro, BigDecimal base) {
      Fec_factura_detalles_impuestos imp = new Fec_factura_detalles_impuestos();

      // id único (mejor hacerlo robusto, pero te dejo tu estilo)
      Long idImp = Long.valueOf(String.valueOf(rubro) + idDetalle);

      imp.setIdfacturadetalleimpuestos(idImp);
      imp.setIdfacturadetalle(idDetalle);
      imp.setCodigoimpuesto("2");
      imp.setCodigoporcentaje("0");
      imp.setBaseimponible(base);

      return imp;
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

   // ✅ impuesto único para el consolidado 1004
   public void generarFecFacturaDetallesImpuestosConsolidado(Long idfecfacturadetalle, BigDecimal baseImponible) {

      Fec_factura_detalles_impuestos imp = new Fec_factura_detalles_impuestos();

      // id único (puedes dejar esta lógica o definir una más segura)
      String idfacdetalleimpuestos = String.valueOf(1004L + "" + idfecfacturadetalle);

      imp.setIdfacturadetalleimpuestos(Long.valueOf(idfacdetalleimpuestos));
      imp.setIdfacturadetalle(idfecfacturadetalle);
      imp.setCodigoimpuesto("2");
      imp.setCodigoporcentaje("0");
      imp.setBaseimponible(baseImponible); // ✅ suma

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
         throw new IllegalArgumentException("El código no tiene el formato esperado");
      }

      // Separa por guiones
      String[] partes = codigo.split("-");

      if (partes.length != 3) {
         throw new IllegalArgumentException("El código debe tener 3 partes (ej: 001-013-000022233)");
      }

      return partes;
   }
   // 2909202501046002881000110020180003172614598738814

   public String generarClaveAcceso(Fec_factura factura, DefinirProjection definir) {
      Objects.requireNonNull(factura, "Factura no puede ser nula");
      Objects.requireNonNull(definir, "Definir no puede ser nulo");

      // 1) Fecha de emisión (DDMMYYYY)
      String fechaEmision = formatToDDMMYYYY(factura.getFechaemision());

      // 2) Tipo de comprobante (2 dígitos)
      String tipoComprobante = TIPO_COMPROBANTE_FACTURA;

      // 3) RUC del emisor (13 dígitos)
      String ruc = leftPadDigits(requireDigits(definir.getRuc(), "RUC"), 13);

      // 4) Ambiente (1 dígito: 1=Pruebas, 2=Producción)
      String ambiente = requireDigits(definir.getTipoambiente() == null ? null : definir.getTipoambiente().toString(),
            "Tipo de ambiente");
      if (!(ambiente.equals("1") || ambiente.equals("2"))) {
         throw new IllegalArgumentException("El ambiente debe ser '1' (pruebas) o '2' (producción)");
      }

      // 5) Serie: 3 dígitos establecimiento + 3 dígitos punto de emisión
      String estab = leftPadDigits(requireDigits(factura.getEstablecimiento(), "Establecimiento"), 3);
      String ptoEmi = leftPadDigits(requireDigits(factura.getPuntoemision(), "Punto de emisión"), 3);
      String serie = estab + ptoEmi;

      // 6) Secuencial (9 dígitos)
      String secuencial = leftPadDigits(requireDigits(factura.getSecuencial(), "Secuencial"), 9);

      // 7) Código numérico (8 dígitos) — aleatorio o el que definas en tu negocio
      String codigoNumerico = generarCodigoNumerico8();

      // 8) Tipo de emisión (1 dígito, normal=1)
      String tipoEmision = "1";

      // Concatenar base de 48 dígitos
      String base48 = fechaEmision + tipoComprobante + ruc + ambiente + serie + secuencial + codigoNumerico
            + tipoEmision;

      if (base48.length() != 48) {
         throw new IllegalStateException(
               "La base de la clave de acceso debe tener 48 dígitos, actual: " + base48.length());
      }
      if (!base48.chars().allMatch(Character::isDigit)) {
         throw new IllegalStateException("La base de la clave de acceso debe contener solo dígitos");
      }

      // 9) Dígito verificador (módulo 11 con pesos 2..7 de derecha a izquierda)
      char dv = calcularDigitoVerificadorModulo11(base48);

      // 10) Clave completa (49 dígitos)
      return base48 + dv;
   }

   // ----------------- Utilidades -----------------

   /** Formatea LocalDateTime a DDMMYYYY (SRI). */
   private String formatToDDMMYYYY(LocalDateTime dateTime) {
      if (dateTime == null)
         throw new IllegalArgumentException("La fecha no puede ser nula");
      return dateTime.format(DDMMYYYY);
   }

   /** Genera un código numérico aleatorio de 8 dígitos (00000000–99999999). */
   private static String generarCodigoNumerico8() {
      // Si no deseas ceros a la izquierda, usa rango 10000000..99999999
      int n = ThreadLocalRandom.current().nextInt(0, 100_000_000);
      return String.format("%08d", n);
   }

   /**
    * Cálculo del dígito verificador para clave de acceso SRI (módulo 11):
    * - Ponderación cíclica 2..7 de derecha a izquierda.
    * - dv = 11 - (suma % 11)
    * - Si dv == 11 -> 0 ; si dv == 10 -> 1.
    */
   private static char calcularDigitoVerificadorModulo11(String base48) {
      if (base48 == null || base48.length() != 48 || !base48.chars().allMatch(Character::isDigit)) {
         throw new IllegalArgumentException("Se espera base de 48 dígitos numéricos");
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

   /** Verifica y normaliza que el texto contenga solo dígitos. */
   private static String requireDigits(String value, String label) {
      if (value == null || value.isBlank())
         throw new IllegalArgumentException(label + " no puede ser nulo o vacío");
      String digits = value.replaceAll("\\D", "");
      if (digits.isEmpty())
         throw new IllegalArgumentException(label + " debe contener dígitos");
      return digits;
   }

   /** Rellena a la izquierda con ceros hasta la longitud indicada. */
   private static String leftPadDigits(String digits, int length) {
      if (!digits.chars().allMatch(Character::isDigit))
         throw new IllegalArgumentException("Valor no numérico: " + digits);
      if (digits.length() > length)
         throw new IllegalArgumentException("Longitud mayor a " + length + ": " + digits);
      return String.format("%0" + length + "d", Long.parseLong(digits));
   }

   public static void validarClaveAcceso(String clave) {
      if (clave == null || !clave.matches("\\d{49}")) {
         throw new IllegalArgumentException("claveAcceso inválida: debe tener 49 dígitos");
      }
   }

   public void delete(Long id) {
      dao.deleteById(id);
   }

}
