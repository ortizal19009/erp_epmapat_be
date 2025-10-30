package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;

import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucion;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.servicio.contabilidad.EjecucionServicio;
import com.epmapat.erp_epmapat.servicio.contabilidad.PresupueServicio;

@RestController
@RequestMapping("/ejecucion")
@CrossOrigin(origins = "*")

public class EjecucionApi {

   // @Autowired
   private EjecucionServicio ejecuServicio;
   private PresupueServicio presuServicio;

   @Autowired
   public void EjecucionController(EjecucionServicio ejecuServicio, PresupueServicio presuServicio) {
       this.ejecuServicio = ejecuServicio;
       this.presuServicio = presuServicio;
   }

   @GetMapping
   public List<Ejecucion> getAllLista(
         @Param(value = "idrefo") Long idrefo,
         @Param(value = "codpar") String codpar,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
      if (idrefo != null) {
         return ejecuServicio.buscaByIdrefo(idrefo);
      }
      if (codpar != null) {
         return ejecuServicio.findByCodparFecha(codpar, desdeFecha, hastaFecha);
      } else
         return null;
   }

   @GetMapping("/tieneEjecucion")
   public ResponseEntity<Boolean> verificarEjecuciones(@Param(value = "codpar") String codpar) {
      boolean tieneEjecuciones = ejecuServicio.verifiEjecucion(codpar);
      return ResponseEntity.ok(tieneEjecuciones);
   }

   @GetMapping("/{idejecu}")
   public ResponseEntity<Ejecucion> getByIdejecucion(@PathVariable Long idejecu) {
      Ejecucion ejecucion = ejecuServicio.findById(idejecu)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Ejecucion con Id: " + idejecu)));
      return ResponseEntity.ok(ejecucion);
   }

   @PostMapping
   public ResponseEntity<Ejecucion> save(@RequestBody Ejecucion x) {
      return ResponseEntity.ok(ejecuServicio.save(x));
   }

   @PutMapping("/{idejecu}")
   public ResponseEntity<Ejecucion> update(@PathVariable Long idejecu, @RequestBody Ejecucion x) {
      Ejecucion y = ejecuServicio.findById(idejecu)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Ejecucion con Id: " + idejecu)));
      y.setCodpar(x.getCodpar());
      y.setFecha_eje(x.getFecha_eje());
      y.setTipeje(x.getTipeje());
      y.setModifi(x.getModifi());
      y.setPrmiso(x.getPrmiso());
      y.setTotdeven(x.getTotdeven());
      y.setDevengado(x.getDevengado());
      y.setCobpagado(x.getCobpagado());
      y.setConcepto(x.getConcepto());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());
      y.setIdrefo(x.getIdrefo());
      y.setIdtrami(x.getIdtrami());
      y.setIdparxcer(x.getIdparxcer());
      y.setIdasiento(x.getIdasiento());
      y.setIdtransa(x.getIdtransa());
      y.setIdpresupue(x.getIdpresupue());
      y.setIdprmiso(x.getIdprmiso());
      y.setIdevenga(x.getIdevenga());

      Ejecucion actualizar = ejecuServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{idejecu}")
   private ResponseEntity<Boolean> deleteEjecucion(@PathVariable("idejecu") Long idejecu) {
      ejecuServicio.deleteById(idejecu);
      return ResponseEntity.ok(!(ejecuServicio.findById(idejecu) != null));
   }

   @PatchMapping("/{idpresupue}")
   public ResponseEntity<List<Ejecucion>> actualizarCodpar(@PathVariable Long idpresupue, @RequestParam String nuevoCodpar) {
       Optional<Presupue> presupueOptional = presuServicio.findById(idpresupue);

       if (presupueOptional.isPresent()) {
           Presupue presupue = presupueOptional.get();
           List<Ejecucion> ejecucionesActualizadas = ejecuServicio.actualizarCodpar(presupue, nuevoCodpar);
           return ResponseEntity.ok(ejecucionesActualizadas);
       } else {
           return ResponseEntity.notFound().build();
       }
   }
   
}
