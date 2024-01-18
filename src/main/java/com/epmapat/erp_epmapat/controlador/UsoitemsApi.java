package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.epmapat.erp_epmapat.modelo.Usoitems;
import com.epmapat.erp_epmapat.servicio.UsoitemsServicio;

@RestController
@RequestMapping("/usoitems")
@CrossOrigin(origins = "*")

public class UsoitemsApi {

   @Autowired
   UsoitemsServicio usoServicio;

   @GetMapping
   public List<Usoitems> getAllLista(@Param(value = "idmodulo") Long idmodulo,
         @Param(value = "descripcion") String descripcion) {
      if (idmodulo != null && descripcion != null) {
         return usoServicio.findByNombre(idmodulo, descripcion.toLowerCase());
      } else {
         // return usoServicio.findAll();
         return null;
      }
   }

   @GetMapping("/modulo/{idmodulo}")
   public List<Usoitems> findByIdmodulo(@PathVariable Long idmodulo) {
      return usoServicio.findByIdmodulo(idmodulo);
   }

   @GetMapping("/{idusoitems}")
   public ResponseEntity<Usoitems> getByIdUsoitems(@PathVariable("idusoitems") Long idusoitems) {
      Usoitems usoitems = usoServicio.findById(idusoitems)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe un Usoitems con este Id: " + idusoitems));
      return ResponseEntity.ok(usoitems);
   }

   @PostMapping
   public ResponseEntity<Usoitems> save(@RequestBody Usoitems usoitems) {
      return ResponseEntity.ok(usoServicio.save(usoitems));
   }

   @PutMapping("/{idusoitems}")
   public ResponseEntity<Usoitems> update(@PathVariable Long idusoitems, @RequestBody Usoitems x) {
      Usoitems y = usoServicio.findById(idusoitems)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Usoitems con Id: " + idusoitems)));
      y.setIdmodulo_modulos(x.getIdmodulo_modulos());
      y.setDescripcion(x.getDescripcion());
      y.setEstado(x.getEstado());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());

      Usoitems actualizar = usoServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

}
