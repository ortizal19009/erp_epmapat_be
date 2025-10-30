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

import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucio;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.servicio.contabilidad.EjecucioServicio;
import com.epmapat.erp_epmapat.servicio.contabilidad.PresupueServicio;

@RestController
@RequestMapping("/ejecucio")
@CrossOrigin(origins = "*")

public class EjecucioApi {

   private EjecucioServicio ejecuServicio;
   private PresupueServicio presuServicio;

   @Autowired
   public void EjecucioController(EjecucioServicio ejecuServicio, PresupueServicio presuServicio) {
      this.ejecuServicio = ejecuServicio;
      this.presuServicio = presuServicio;
   }

   @GetMapping
   public List<Ejecucio> getAllLista(
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

   // Verifica si una partida tiene ejecución
   @GetMapping("/tieneEjecucio")
   public ResponseEntity<Boolean> tieneEjecucio(@Param(value = "codpar") String codpar) {
      boolean b = ejecuServicio.tieneEjecucio(codpar);
      return ResponseEntity.ok( b );
   }

   @GetMapping("/{inteje}")
   public ResponseEntity<Ejecucio> getByInteje(@PathVariable Long inteje) {
      Ejecucio x = ejecuServicio.findById(inteje)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Ejecucion con Id: " + inteje)));
      return ResponseEntity.ok(x);
   }

   // Contar por intpre
   @GetMapping("/countByIntpre")
   public Long countByIntpre(@Param(value = "intpre") Long intpre) {
      return ejecuServicio.countByIntpre(intpre);
   }

   //Partidas de un Trámite
   @GetMapping("/partixtrami")
   public List<Ejecucio> partixtrami(@Param(value = "idtrami") Long idtrami){
      return ejecuServicio.partixtrami(idtrami);
   }

   @GetMapping("/modi")
   public Double totalModi(@RequestParam(required = true) String codpar,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
      Double tmodi = ejecuServicio.totalModi(codpar, desdeFecha, hastaFecha);
      return tmodi;
   }

   @GetMapping("/deven")
   public Double totalDeven(@RequestParam(required = true) String codpar,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
      Double tdeven = ejecuServicio.totalDeven(codpar, desdeFecha, hastaFecha);
      return tdeven;
   }

   @GetMapping("/cobpagado")
   public Double totalCobpagado(@RequestParam(required = true) String codpar,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
      Double tdeven = ejecuServicio.totalCobpagado(codpar, desdeFecha, hastaFecha);
      return tdeven;
   }

   @PostMapping
   public ResponseEntity<Ejecucio> save(@RequestBody Ejecucio x) {
      return ResponseEntity.ok(ejecuServicio.save(x));
   }

   @PutMapping("/{inteje}")
   public ResponseEntity<Ejecucio> update(@PathVariable Long inteje, @RequestBody Ejecucio x) {
      Ejecucio y = ejecuServicio.findById(inteje)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Ejecucion con Id: " + inteje)));
      y.setCodpar(x.getCodpar());
      y.setFecha_eje(x.getFecha_eje());
      y.setTipeje(x.getTipeje());
      y.setModifi(x.getModifi());
      y.setPrmiso(x.getPrmiso());
      y.setTotdeven(x.getTotdeven());
      y.setDevengado(x.getDevengado());
      y.setCobpagado(x.getCobpagado());
      y.setConcep(x.getConcep());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());
      y.setIdrefo(x.getIdrefo());
      y.setIdtrami(x.getIdtrami());
      y.setIdparxcer(x.getIdparxcer());
      y.setIdasiento(x.getIdasiento());
      y.setInttra(x.getInttra());
      y.setIntpre(x.getIntpre());
      y.setIdprmiso(x.getIdprmiso());
      y.setIdevenga(x.getIdevenga());

      Ejecucio actualizar = ejecuServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{inteje}")
   private ResponseEntity<Boolean> deleteEjecucion(@PathVariable("inteje") Long inteje) {
      ejecuServicio.deleteById(inteje);
      return ResponseEntity.ok(!(ejecuServicio.findById(inteje) != null));
   }

   @PatchMapping("/{intpre}")
   public ResponseEntity<List<Ejecucio>> actualizarCodpar(@PathVariable Long intpre,
         @RequestParam String nuevoCodpar) {
      Optional<Presupue> presupueOptional = presuServicio.findById(intpre);

      if (presupueOptional.isPresent()) {
         Presupue presupue = presupueOptional.get();
         List<Ejecucio> x = ejecuServicio.actualizarCodpar(presupue, nuevoCodpar);
         return ResponseEntity.ok(x);
      } else {
         return ResponseEntity.notFound().build();
      }
   }

}
