package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.servicio.Fec_facturaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/fec_factura")
@CrossOrigin(origins = "*")

public class Fec_facturaApi {

   @Autowired
   private Fec_facturaService fecfacServicio;

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

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Fec_factura saveFec_factura(@RequestBody Fec_factura x) {
      return fecfacServicio.save(x);
   }

   @PutMapping("/{idfactura}")
   public ResponseEntity<Fec_factura> updateFecFactura(@PathVariable Long idfactura,
         @RequestBody Fec_factura fecfactura) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));
            
      return null;
   }
}
