package com.epmapat.erp_epmapat.controlador;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.epmapat.erp_epmapat.DTO.FecFacturaUpdateDto;
import com.epmapat.erp_epmapat.DTO.FecFacturaGestionFiltroDto;
import com.epmapat.erp_epmapat.DTO.FecMailQueueRequestDto;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.FecFacturaGestionProjection;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.modelo.Fec_factura_log;
import com.epmapat.erp_epmapat.servicio.FecFacturaLogService;
import com.epmapat.erp_epmapat.servicio.FecMailQueueService;
import com.epmapat.erp_epmapat.servicio.Fec_facturaService;
import com.epmapat.erp_epmapat.sri.interfaces.fecFacturaDatos;

@RestController
@RequestMapping("/fec_factura")

public class Fec_facturaApi {
   private static final Logger log = LoggerFactory.getLogger(Fec_facturaApi.class);

   @Autowired
   private Fec_facturaService fecfacServicio;
   @Autowired
   private FecFacturaLogService fecFacturaLogService;
   @Autowired
   private FecMailQueueService fecMailQueueService;

   @Value("${sri.microservice.base-url:http://192.168.0.33:9096}")
   private String sriMicroserviceBaseUrl;

   @GetMapping
   public List<Fec_factura> getAll() {
      return fecfacServicio.findAll();
   }

   @GetMapping("/estado")
   public List<Fec_factura> getByEstado(@RequestParam("estado") String estado, @RequestParam("limit") Long limit) {
      return fecfacServicio.findByEstado(estado, limit);
   }

   @GetMapping("/referencia")
   public List<Fec_factura> getByCuenta(@RequestParam("referencia") String referencia) {
      return fecfacServicio.findByCuenta(referencia);
   }

   @GetMapping("/cliente")
   public List<Fec_factura> getByNombreCliente(@RequestParam("cliente") String cliente) {
      return fecfacServicio.findByNombreCliente(cliente);
   }

   @PostMapping("/gestion")
   public List<FecFacturaGestionProjection> buscarGestion(@RequestBody FecFacturaGestionFiltroDto filtro) {
      List<FecFacturaGestionProjection> respuesta = fecfacServicio.buscarGestion(filtro);
      log.info("POST /fec_factura/gestion filtros={} total={}", resumirFiltro(filtro), respuesta.size());
      return respuesta;
   }

   @GetMapping("/gestion")
   public List<FecFacturaGestionProjection> buscarGestionGet(
         @RequestParam(value = "numeroFactura", required = false) String numeroFactura,
         @RequestParam(value = "claveAcceso", required = false) String claveAcceso,
         @RequestParam(value = "estadoSri", required = false) String estadoSri,
         @RequestParam(value = "cliente", required = false) String cliente,
         @RequestParam(value = "identificacion", required = false) String identificacion,
         @RequestParam(value = "establecimiento", required = false) String establecimiento,
         @RequestParam(value = "puntoEmision", required = false) String puntoEmision,
         @RequestParam(value = "idusuario", required = false) Long idusuario,
         @RequestParam(value = "secuencialDesde", required = false) String secuencialDesde,
         @RequestParam(value = "secuencialHasta", required = false) String secuencialHasta,
         @RequestParam(value = "fechaDesde", required = false) String fechaDesde,
         @RequestParam(value = "fechaHasta", required = false) String fechaHasta,
         @RequestParam(value = "emailEstado", required = false) String emailEstado,
         @RequestParam(value = "swmail", required = false) Boolean swmail,
         @RequestParam(value = "mailIntentos", required = false) Integer mailIntentos,
         @RequestParam(value = "mailError", required = false) String mailError,
         @RequestParam(value = "soloFallidos", required = false) Boolean soloFallidos,
         @RequestParam(value = "limit", required = false) Integer limit) {
      FecFacturaGestionFiltroDto filtro = new FecFacturaGestionFiltroDto();
      filtro.setNumeroFactura(numeroFactura);
      filtro.setClaveAcceso(claveAcceso);
      filtro.setEstadoSri(estadoSri);
      filtro.setCliente(cliente);
      filtro.setIdentificacion(identificacion);
      filtro.setEstablecimiento(establecimiento);
      filtro.setPuntoEmision(puntoEmision);
      filtro.setIdusuario(idusuario);
      filtro.setSecuencialDesde(secuencialDesde);
      filtro.setSecuencialHasta(secuencialHasta);
      filtro.setFechaDesde(fechaDesde);
      filtro.setFechaHasta(fechaHasta);
      filtro.setEmailEstado(emailEstado);
      filtro.setSwmail(swmail);
      filtro.setMailIntentos(mailIntentos);
      filtro.setMailError(mailError);
      filtro.setSoloFallidos(soloFallidos);
      filtro.setLimit(limit);
      List<FecFacturaGestionProjection> respuesta = fecfacServicio.buscarGestion(filtro);
      log.info("GET /fec_factura/gestion filtros={} total={}", resumirFiltro(filtro), respuesta.size());
      return respuesta;
   }

   private Map<String, Object> resumirFiltro(FecFacturaGestionFiltroDto filtro) {
      Map<String, Object> resumen = new LinkedHashMap<>();
      resumen.put("numeroFactura", filtro.getNumeroFactura());
      resumen.put("claveAcceso", filtro.getClaveAcceso());
      resumen.put("estadoSri", filtro.getEstadoSri());
      resumen.put("establecimiento", filtro.getEstablecimiento());
      resumen.put("puntoEmision", filtro.getPuntoEmision());
      resumen.put("secuencialDesde", filtro.getSecuencialDesde());
      resumen.put("secuencialHasta", filtro.getSecuencialHasta());
      resumen.put("fechaDesde", filtro.getFechaDesde());
      resumen.put("fechaHasta", filtro.getFechaHasta());
      resumen.put("emailEstado", filtro.getEmailEstado());
      resumen.put("swmail", filtro.getSwmail());
      resumen.put("soloFallidos", filtro.getSoloFallidos());
      resumen.put("limit", filtro.getLimit());
      return resumen;
   }

   @GetMapping("/reenvio")
   public List<Fec_factura> getByFechaEmisionReenvio(
         @RequestParam("desde") String desde,
         @RequestParam("hasta") String hasta) {
      LocalDate fechaDesde = LocalDate.parse(desde);
      LocalDate fechaHasta = LocalDate.parse(hasta);
      return fecfacServicio.findByFechaEmisionAndEstados(
            fechaDesde.atStartOfDay(),
            fechaHasta.plusDays(1).atStartOfDay());
   }

   @GetMapping("/factura")
   public ResponseEntity<Fec_factura> getByIdFactura(@RequestParam("idfactura") Long idfactura) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe fec_factura para idfactura=" + idfactura));
      return ResponseEntity.ok(factura);
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public ResponseEntity<Fec_factura> saveFec_factura(@RequestBody Fec_factura x) {
      Optional<Fec_factura> fecfactura = fecfacServicio.findById(x.getIdfactura());
      if (fecfactura.isPresent()) {
         if ("A".equals(fecfactura.get().getEstado()) || "O".equals(fecfactura.get().getEstado())) {
            x = fecfacServicio.save(fecfactura.get());
         } else {
            x = fecfacServicio.save(x);
         }
         return ResponseEntity.ok(x);
      } else {
         // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
         return ResponseEntity.ok(fecfacServicio.save(x));
      }
   }

   @PutMapping
   public ResponseEntity<Fec_factura> updateFecFactura(@RequestParam("idfactura") Long idfactura,
         @RequestBody Fec_factura fecfactura) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));
      factura.setClaveacceso(fecfactura.getClaveacceso());
      factura.setSecuencial(fecfactura.getSecuencial());
      factura.setXmlautorizado(fecfactura.getXmlautorizado());
      factura.setErrores(fecfactura.getErrores());
      factura.setEstado(fecfactura.getEstado());
      factura.setEstablecimiento(fecfactura.getEstablecimiento());
      factura.setPuntoemision(fecfactura.getPuntoemision());
      factura.setDireccionestablecimiento(fecfactura.getDireccionestablecimiento());
      factura.setFechaemision(fecfactura.getFechaemision());
      factura.setTipoidentificacioncomprador(fecfactura.getTipoidentificacioncomprador());
      factura.setGuiaremision(fecfactura.getGuiaremision());
      factura.setRazonsocialcomprador(fecfactura.getRazonsocialcomprador());
      factura.setIdentificacioncomprador(fecfactura.getIdentificacioncomprador());
      factura.setDireccioncomprador(fecfactura.getDireccioncomprador());
      factura.setTelefonocomprador(fecfactura.getTelefonocomprador());
      factura.setEmailcomprador(fecfactura.getEmailcomprador());
      factura.setConcepto(fecfactura.getConcepto());
      factura.setReferencia(fecfactura.getReferencia());
      factura.setRecaudador(fecfactura.getRecaudador());
      factura.setIntentosAutorizacion(fecfactura.getIntentosAutorizacion());
      factura.setFechaUltimoIntento(fecfactura.getFechaUltimoIntento());
      factura.setFechaAutorizacion(fecfactura.getFechaAutorizacion());
      factura.setMailEnviado(fecfactura.getMailEnviado());
      factura.setMailIntentos(fecfactura.getMailIntentos());
      factura.setMailError(fecfactura.getMailError());
      factura.setEmailEstado(fecfactura.getEmailEstado());
      factura.setFechaReenvio(fecfactura.getFechaReenvio());
      Fec_factura upfecfactura = fecfacServicio.save(factura);
      return ResponseEntity.ok(upfecfactura);
   }

   @PutMapping("/sri/{idfactura}")
   public ResponseEntity<Fec_factura> updateSriFields(
         @PathVariable Long idfactura,
         @RequestBody FecFacturaUpdateDto dto) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));

      factura.setEstado(dto.getEstado());
      factura.setClaveacceso(dto.getClaveacceso());
      factura.setXmlautorizado(dto.getXmlautorizado());
      if ("A".equals(dto.getEstado())) {
         return ResponseEntity.ok(fecfacServicio.marcarCorreoEnviado(factura));
      }
      return ResponseEntity.ok(fecfacServicio.save(factura));
   }

   @GetMapping("/fecFacturaDatos")
   public fecFacturaDatos getDatosFecFactura(@RequestParam Long idfactura) {
      fecFacturaDatos fecFactura = fecfacServicio.getNroFactura(idfactura);
      return fecFactura;
   }

   @Transactional
   @GetMapping("/createFacElectro")
   public ResponseEntity<Map<String, Object>> generarFecFactura(@RequestParam Long idfactura) {
      return ResponseEntity.ok(fecfacServicio.generarFecFactura(idfactura));
   }

   @GetMapping("/{idfactura}")
   public ResponseEntity<Fec_factura> findByIdFactura(@PathVariable Long idfactura) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe fec_factura para idfactura=" + idfactura));
      return ResponseEntity.ok(factura);
   }

   @GetMapping("/{idfactura}/seguimiento")
   public ResponseEntity<Map<String, Object>> seguimientoFactura(@PathVariable Long idfactura) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));
      List<Fec_factura_log> historial = fecFacturaLogService.listarPorFactura(idfactura);

      Map<String, Object> response = new LinkedHashMap<>();
      response.put("idfactura", idfactura);
      response.put("estado", factura.getEstado());
      response.put("claveacceso", factura.getClaveacceso());
      response.put("mailEnviado", factura.getMailEnviado());
      response.put("intentosAutorizacion", factura.getIntentosAutorizacion());
      response.put("fechaUltimoIntento", factura.getFechaUltimoIntento());
      response.put("fechaAutorizacion", factura.getFechaAutorizacion());
      response.put("mailIntentos", factura.getMailIntentos());
      response.put("mailError", factura.getMailError());
      response.put("emailEstado", factura.getEmailEstado());
      response.put("fechaReenvio", factura.getFechaReenvio());
      response.put("errores", factura.getErrores());
      response.put("factura", factura);
      response.put("historial", historial);
      return ResponseEntity.ok(response);
   }

   @PostMapping("/mail-queue")
   public ResponseEntity<Map<String, Object>> enqueueMailQueue(@RequestBody FecMailQueueRequestDto request) {
      return ResponseEntity.accepted().body(fecMailQueueService.enqueue(request));
   }

   @PutMapping("/setxml")
   public ResponseEntity<Fec_factura> setXmlToFactura(@RequestParam Long idfactura, @RequestBody Fec_factura ff) {
      try {
         return fecfacServicio.recuperarXmlAutorizado(idfactura)
               .map(ResponseEntity::ok)
               .orElseGet(() -> fecfacServicio.findById(idfactura)
                     .map(facturaPendiente -> ResponseEntity.status(HttpStatus.ACCEPTED).body(facturaPendiente))
                     .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura)));
      } catch (Exception e) {
         Fec_factura factura = fecfacServicio.findById(idfactura)
               .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(factura);
      }
   }

   @DeleteMapping("/{idfactura}")
   public ResponseEntity<Fec_factura> deleteFec_factura(@PathVariable Long idfactura) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));
      fecfacServicio.delete(idfactura);
      return ResponseEntity.ok(factura);
   }
}
