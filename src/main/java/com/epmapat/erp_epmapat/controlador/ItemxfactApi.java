package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Itemxfact;
import com.epmapat.erp_epmapat.servicio.ItemxfactServicio;

@RestController
@RequestMapping("/itemxfact")
@CrossOrigin("*")

public class ItemxfactApi {

   @Autowired
   private ItemxfactServicio ixfServicio;

   @GetMapping
   public List<Itemxfact> get(@Param(value = "idfacturacion") Long idfacturacion, @Param(value = "idcatalogoitems") Long idcatalogoitems) {
      if (idfacturacion != null)
         return ixfServicio.getByIdfacturacion(idfacturacion);
      else if (idcatalogoitems != null)
         return ixfServicio.getByIdcatalogoitems(idcatalogoitems);
      else
         return null;
   }

   // @GetMapping
   // public List<Itemxfact> getByIdcatalogoitems(@Param(value = "idcatalogoitems")
   // Long idcatalogoitems ) {
   // return ixfServicio.getByIdcatalogoitems(idcatalogoitems);
   // }

   // @GetMapping
   // public List<Itemxfact> getAll(@Param(value = "idfacturacion") Long
   // idfacturacion ) {
   // return ixfServicio.getByIdfact(idfacturacion);
   // }

   // @GetMapping("/{idfacturacion}")
   // public ResponseEntity<Itemxfact> getById(@PathVariable Long iditemxfac) {
   // Itemxfact x = ixfServicio.findByIdiditemxfac(iditemxfac)
   // .orElseThrow(() -> new ResourceNotFoundExcepciones(
   // ("No existe Facturacion Id: " + iditemxfac)));
   // return ResponseEntity.ok(x);
   // }

   // @PostMapping
   // public ResponseEntityFacturacion> save(@RequestBody Facturacion facturacion)
   // {
   // return ResponseEntity.ok(factuServicio.save( facturacion ));
   // }

   @PostMapping
   public Itemxfact save(@RequestBody Itemxfact itemxfact) {
      return ixfServicio.save(itemxfact);
   }

}
