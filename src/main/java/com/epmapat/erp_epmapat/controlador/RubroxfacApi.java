package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.interfaces.RubroxfacIReport;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.servicio.RubroxfacServicio;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/rubroxfac")
@CrossOrigin(origins = "*")

public class RubroxfacApi {

   @Autowired
   private RubroxfacServicio rxfServicio;

   @GetMapping("/sumavalores")
   public Double findRubroxfac(@RequestParam("idfactura") Long idfactura) {
      return rxfServicio.findRubroxfac(idfactura);
   }

   @GetMapping("/sumarubros")
   public Double getSumaRubros(@RequestParam("idfactura") Long idfactura) {
      return rxfServicio.getSumaRubros(idfactura);
   }

   @GetMapping("/reportes/fechaCobro")
   public List<RubroxfacI> getByFechaCobro(@RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return rxfServicio.getByFechaCobro(d, h);
   }

   @GetMapping("/reportes/fecha")
   public List<Rubroxfac> getByFecha(@RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return rxfServicio.findByFecha(d, h);
   }

   @GetMapping("/sincobro/rubxfa")
   public List<Rubroxfac> getSinCobroRF(@RequestParam("cuenta") Long cuenta) {
      return rxfServicio.findSinCobroRF(cuenta);
   }

   @GetMapping
   public List<Rubroxfac> getByIdfactura(@Param(value = "idfactura") Long idfactura) {
      return rxfServicio.getByIdfactura(idfactura);
   }

   @GetMapping("/esiva")
   public List<Rubroxfac> getByIdfactura1(@Param(value = "idfactura") Long idfactura) {
      return rxfServicio.getByIdfactura1(idfactura);
   }

   // Campos Rubro.descripcion y rubroxfac.valorunitario de una Planilla
   @GetMapping("/rubros")
   public ResponseEntity<List<Object[]>> findRubros(@Param(value = "idfactura") Long idfactura) {
      List<Object[]> x = rxfServicio.findRubros(idfactura);
      return ResponseEntity.ok(x);
   }

   @GetMapping("/rubro/{idrubro}")
   public List<Rubroxfac> getByIdrubro(@PathVariable Long idrubro) {
      return rxfServicio.getByIdrubro(idrubro);
   }

   // Verifica si una Factura tiene Multa
   @GetMapping("/multa")
   public boolean getMulta(@Param(value = "idfactura") Long idfactura) {
      return rxfServicio.getMulta(idfactura);
   }

   @GetMapping("/{idrubroxfac}")
   public Optional<Rubroxfac> findByIdRubroxfac(@PathVariable Long idrubroxfac) {
      return rxfServicio.findById(idrubroxfac);
   }

   // Recaudacion diaria - Total por Rubros (Todas)
   @GetMapping("/totalrubros")
   public ResponseEntity<List<Object[]>> getRubroTotals(
         @Param("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
      List<Object[]> data = rxfServicio.getRubroTotalsByFechaCobro(fecha);
      return ResponseEntity.ok(data);
   }

   // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
   @GetMapping("/totalrubrosanteriorrangos")
   public ResponseEntity<List<Object[]>> totalRubrosAnteriorRangos(
         @Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
         @Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
         @Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta) {

      List<Object[]> resultados = rxfServicio.totalRubrosAnteriorRangos(d_fecha, h_fecha, hasta);
      return ResponseEntity.ok(resultados);
   }

   // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
   @GetMapping("/totalrubrosactualrangos")
   public ResponseEntity<List<Object[]>> totalRubrosActualRangos(
         @Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
         @Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
         @Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta) {
      List<Object[]> resultados = rxfServicio.totalRubrosActualRangos(d_fecha, h_fecha, hasta);
      return ResponseEntity.ok(resultados);
   }

   // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
   @GetMapping("/reportes/totalrubrosanterior")
   public ResponseEntity<List<Object[]>> totalRubrosAnteriorByRecaudador(
         @Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
         @Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
         @Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
         @Param("idrecaudador") Long idrecaudador) {

      List<Object[]> resultados = rxfServicio.totalRubrosAnteriorByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
      return ResponseEntity.ok(resultados);
   }

   // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Rangos
   @GetMapping("/reportes/totalrubrosactual")
   public ResponseEntity<List<Object[]>> totalRubrosActualByRecaudador(
         @Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
         @Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
         @Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
         @Param("idrecaudador") Long idrecaudador) {

      List<Object[]> resultados = rxfServicio.totalRubrosActualByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
      return ResponseEntity.ok(resultados);
   }

   // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
   @GetMapping("/totalrubrosanterior")
   public ResponseEntity<List<Object[]>> totalRubrosAnterior(
         @Param("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
         @Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta) {

      List<Object[]> resultados = rxfServicio.totalRubrosAnterior(fecha, hasta);
      return ResponseEntity.ok(resultados);
   }

   // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Rangos
   @GetMapping("/totalrubrosactual")
   public ResponseEntity<List<Object[]>> totalRubrosActual(
         @Param("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
         @Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta) {

      List<Object[]> resultados = rxfServicio.totalRubrosActual(fecha, hasta);
      return ResponseEntity.ok(resultados);
   }

   @PostMapping
   public Rubroxfac save(@RequestBody Rubroxfac x) {
      return rxfServicio.save(x);
   }

   @PutMapping("/{idrubroxfac}")
   public ResponseEntity<Rubroxfac> updateRubroxfac(@PathVariable Long idrubroxfac, @RequestBody Rubroxfac x) {
      Rubroxfac y = rxfServicio.findById(idrubroxfac)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta idrubroxfac: " + idrubroxfac));
      y.setValorunitario(x.getValorunitario());
      Rubroxfac z = rxfServicio.save(y);
      return ResponseEntity.ok(z);
   }

   @GetMapping("/iva")
   public ResponseEntity<List<Object[]>> getIva(@RequestParam("iva") BigDecimal iva,
         @RequestParam("idfactura") Long idfactura) {
      return ResponseEntity.ok(rxfServicio.getIva(iva, idfactura));
   }

   /* FACTURACIÓN ELECTRONICA */
   @GetMapping("/feRubros")
   public ResponseEntity<List<Rubroxfac>> getRubrosByFactura(@RequestParam("idfactura") Long idfactura) {
      return ResponseEntity.ok(rxfServicio.getRubrosByFactura(idfactura));
   }

   /* TOTALES DE RUBROS DE FACTURAS SIN COBRAR */
   @GetMapping("/reportes/rsincobro")
   public ResponseEntity<List<RubroxfacIReport>> getRubrosByAbonado(@RequestParam("idabonado") Long idabonado) {
      return ResponseEntity.ok(rxfServicio.getRubrosByAbonado(idabonado));
   }

   /* OBTENER MULTA POR FACTURA */
   @GetMapping("/multas")
   public ResponseEntity<List<Rubroxfac>> findMultaByIdFactura(@RequestParam("idfactura") Long idfactura) {
      return ResponseEntity.ok(rxfServicio.getMultaByIdFactura(idfactura));
   }
}
