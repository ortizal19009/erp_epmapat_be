package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
      return ResponseEntity.ok(b);
   }

   @GetMapping("/{inteje}")
   public ResponseEntity<Ejecucio> getByInteje(@PathVariable Long inteje) {
      Ejecucio x = ejecuServicio.findById(inteje)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Ejecucion con Id: " + inteje)));
      return ResponseEntity.ok(x);
   }

   // Cuenta por idparxcer
   @GetMapping("/countByIdparxcer/{idparxcer}")
   public short countEjecucioByIdparxcer(@PathVariable Long idparxcer) {
      return ejecuServicio.countByIdparxcer(idparxcer);
   }

   // Contar por intpre
   @GetMapping("/countByIntpre")
   public Long countByIntpre(@Param(value = "intpre") Long intpre) {
      return ejecuServicio.countByIntpre(intpre);
   }

   // Contar las Partidas de un Trámite
   @GetMapping("/countByIdtrami/{idtrami}")
   public ResponseEntity<Short> contarPorIdtrami(@PathVariable Long idtrami) {
      short total = ejecuServicio.contarPorIdtrami(idtrami);
      return ResponseEntity.ok(total);
   }

   // Partidas de un Trámite
   @GetMapping("/partixtrami")
   public List<Ejecucio> partixtrami(@Param(value = "idtrami") Long idtrami) {
      return ejecuServicio.partixtrami(idtrami);
   }

   @GetMapping("/modi")
   public Double totalModi(@RequestParam(required = true) String codpar,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
      Double tmodi = ejecuServicio.totalModi(codpar, desdeFecha, hastaFecha);
      // System.out.println("tmodi: " + tmodi);
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

   // Compromisos de una partixcerti
   @GetMapping("/poridparxcer/{idparxcer}")
   public ResponseEntity<List<Ejecucio>> obtenerPorIdParxcer(@PathVariable Long idparxcer) {
      List<Ejecucio> lista = ejecuServicio.obtenerPorIdParxcer(idparxcer);
      if (lista == null || lista.isEmpty()) {
         return ResponseEntity.noContent().build();
      }
      return ResponseEntity.ok(lista);
   }

   // Ultima Fecha
   @GetMapping("/ultimafecha")
   public ResponseEntity<LocalDate> obtenerUltimaFecha() {
      LocalDate fecha = ejecuServicio.obtenerUltimaFechaEje();
      return ResponseEntity.ok(fecha);
   }

   // Ejecución de un Asiento
   @GetMapping("/idasiento/{idasiento}")
   public List<Ejecucio> findByIdasiento(@PathVariable Long idasiento) {
      return ejecuServicio.findByIdasiento(idasiento);
   }

   // Ejecución de un Asiento y tippar
   @GetMapping("/idasiento/{idasiento}/tippar/{tippar}")
   public List<Ejecucio> findByIdasientoAndTippar(@PathVariable Long idasiento, @PathVariable Integer tippar) {
      return ejecuServicio.findByIdasientoAndTippar(idasiento, tippar);
   }

   // Ejecucion de una transaci.inttra
   @GetMapping("/inttra/{inttra}")
   public ResponseEntity<Ejecucio> getByInttra(@PathVariable Long inttra) {
      return ejecuServicio.buscarPorInttra(inttra)
            .map(ResponseEntity::ok) // 200 con el registro
            .orElseGet(() -> ResponseEntity.ok(null)); // 200 con null
   }

   // Compromisos que tienen saldo pendiente (Los comodines están en el servicio)
   @GetMapping("/misosPendientes")
   public List<Ejecucio> obtenerCompromisos(
         @RequestParam("nomben") String nomben,
         @RequestParam("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta) {
      return ejecuServicio.getMisosPendientes(nomben, hasta);
   }

   // Detalle de Devengados de un compromiso (Busca: ejecucio.idprmiso)
   @GetMapping("/poridprmiso/{idprmiso}")
   public ResponseEntity<List<Ejecucio>> obtenerPorIdPrmiso(@PathVariable Long idprmiso) {
      List<Ejecucio> lista = ejecuServicio.obtenerPorIdPrmiso(idprmiso);
      return ResponseEntity.ok(lista);
   }

   // Contar lo devengados de un compromiso
   @GetMapping("/countidprmiso/{idprmiso}")
   public short contarPorIdprmiso(@PathVariable Long idprmiso) {
      return ejecuServicio.contarPorIdprmiso(idprmiso);
   }

   @PostMapping
   public ResponseEntity<Ejecucio> save(@RequestBody Ejecucio ejecucio) {
      return ResponseEntity.ok(ejecuServicio.save(ejecucio));
   }

   // Nueva con claves foraneas credas en el servicio
   // @PostMapping("/foraneas")
   // public ResponseEntity<Ejecucio> saveEjecu(@RequestBody Ejecucio ejecucio) {
   // Ejecucio saved = ejecuServicio.save(ejecucio);
   // return ResponseEntity.status(HttpStatus.CREATED).body(saved);
   // }

   // Actualiza solo los modificados
   @PatchMapping("/{inteje}")
   public ResponseEntity<Ejecucio> updateEjecucio(
         @PathVariable Long inteje,
         @RequestBody Ejecucio ejecucio) {
      Ejecucio updated = ejecuServicio.updateEjecucio(inteje, ejecucio);
      return ResponseEntity.ok(updated);
   }

   // Actualiza codpar
   @PatchMapping("/codpar/{intpre}")
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

   // Actualiza ejecucio.totdeven (Ya se actualiza en nuevo )
   @PatchMapping("/totdeven")
   public void updateTotdeven(@Param(value = "inteje") Long inteje,
         @Param(value = "totdeven") BigDecimal totdeven) {
      ejecuServicio.updateTotdeven(inteje, totdeven);
   }

   @DeleteMapping("/{inteje}")
   private ResponseEntity<Boolean> deleteEjecucion(@PathVariable("inteje") Long inteje) {
      ejecuServicio.deleteById(inteje);
      return ResponseEntity.ok(!(ejecuServicio.findById(inteje) != null));
   }

}
