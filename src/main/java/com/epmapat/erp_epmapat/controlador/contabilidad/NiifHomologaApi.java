package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.NiifHomologa;
import com.epmapat.erp_epmapat.servicio.contabilidad.NiifHomologaServicio;

@RestController
@RequestMapping("/niifhomologa")
@CrossOrigin("*")

public class NiifHomologaApi {

   @Autowired
   private NiifHomologaServicio niifhomoServicio;

   @GetMapping
   public List<NiifHomologa> getAllNiifHomologa() {
      return niifhomoServicio.findAll();
   }

   @GetMapping("/niifcuenta")
   public List<NiifHomologa> getByIdNiifCue(@RequestParam("idniifcue") Long idniifcue) {
      return niifhomoServicio.findByIdNiifCue(idniifcue);
   }

   @GetMapping("/cuenta")
   public List<NiifHomologa> getByIdCuenta(@RequestParam("idcue") Long idcue) {
      return niifhomoServicio.findByIdCuenta(idcue);
   }

   @PostMapping
   public ResponseEntity<NiifHomologa> saveNiifHomologa(@RequestBody NiifHomologa niifhomologa) {
      return ResponseEntity.ok(niifhomoServicio.save(niifhomologa));
   }

   @DeleteMapping("/{idhomologa}")
   public ResponseEntity<Boolean> deleteHomologa(@PathVariable("idhomologa") Long idhomologa) {
      niifhomoServicio.deleteById(idhomologa);
      return ResponseEntity.ok(!(niifhomoServicio.findById(idhomologa) != null));
   }

   @PutMapping("/upd/{idhomologa}")
   public ResponseEntity<NiifHomologa> updateNiifHomologa(@PathVariable("idhomologa") Long idhomologa,
         @RequestBody NiifHomologa niifhomologa) {
      NiifHomologa niifHomologa = niifhomoServicio.findById(idhomologa)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe homologacion: " + idhomologa));
      niifHomologa.setCodcueniif(niifhomologa.getCodcueniif());
      niifHomologa.setCodcue(niifhomologa.getCodcue());
      niifHomologa.setIdcuenta(niifhomologa.getIdcuenta());
      niifHomologa.setIdniifcue(niifhomologa.getIdniifcue());
      NiifHomologa updateNiifHomologa = niifhomoServicio.save(niifHomologa);
      return ResponseEntity.ok(updateNiifHomologa);
   }

}
