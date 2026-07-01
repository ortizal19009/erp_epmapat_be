package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.FacturacionCuotasPendientesDTO;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Facturacion;
import com.epmapat.erp_epmapat.servicio.FacturacionServicio;
import java.util.Date;

@RestController
@RequestMapping("/facturacion")

public class FacturacionApi {

   @Autowired
   private FacturacionServicio factuServicio;

   @GetMapping
   public List<Facturacion> getFacturacion(@RequestParam(value = "desde", required = false) Long desde,
         @RequestParam(value = "hasta", required = false) Long hasta,
         @RequestParam(value = "del", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date del,
         @RequestParam(value = "al", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date al,
         @RequestParam(value = "cliente", required = false) String cliente) {
      if (cliente != null) {
         return factuServicio.findByCliente(cliente.toLowerCase(), del, al);
      } else
         return factuServicio.findDesdeHasta(desde, hasta, del, al);
   }

   @GetMapping("/ultimo")
   public Facturacion ultimo() {
      return factuServicio.ultimo();
   }

   @GetMapping("/{idfacturacion}")
   public ResponseEntity<Facturacion> getById(@PathVariable Long idfacturacion) {
      Facturacion x = factuServicio.findById(idfacturacion)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Facturacion Id: " + idfacturacion)));
      return ResponseEntity.ok(x);
   }

   @GetMapping("/reportes/pendientes-cuotas")
   public List<FacturacionCuotasPendientesDTO> getFacturacionesConCuotasPendientes(
         @RequestParam(value = "desde", required = false) Long desde,
         @RequestParam(value = "hasta", required = false) Long hasta,
         @RequestParam(value = "del", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date del,
         @RequestParam(value = "al", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date al,
         @RequestParam(value = "cliente", required = false) String cliente) {
      return factuServicio.findFacturacionesConCuotasPendientes(desde, hasta, del, al, cliente != null ? cliente.toLowerCase() : null);
   }

   @PostMapping
   public ResponseEntity<Facturacion> save(@RequestBody Facturacion facturacion) {
      return ResponseEntity.ok(factuServicio.save(facturacion));
   }

   // @PostMapping //Alternativa 2: También funciona
   // public Facturacion save(@RequestBody Facturacion facturacion) {
   // return factuServicio.save( facturacion );
   // }

   @DeleteMapping(value = "/{idfacturacion}")
   private ResponseEntity<Boolean> delete(@PathVariable("idfacturacion") Long idfacturacion) {
      factuServicio.deleteById(idfacturacion);
      return ResponseEntity.ok(!(factuServicio.findById(idfacturacion) != null));
   }

}
