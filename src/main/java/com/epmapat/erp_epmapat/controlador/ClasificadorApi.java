package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Clasificador;
import com.epmapat.erp_epmapat.servicio.ClasificadorServicio;

@RestController
@RequestMapping("/clasificador")
@CrossOrigin(origins = "*")

public class ClasificadorApi {

   @Autowired
   ClasificadorServicio clasifServicio;

   @PostMapping
   public Clasificador updateOrSave(@RequestBody Clasificador x) {
      return clasifServicio.save(x);
   }

   @GetMapping
   public List<Clasificador> getAllLista1(@Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      if (codpar != null) {
         return clasifServicio.buscaByCodigo(codpar);
      }
      if (nompar != null) {
         return clasifServicio.buscaByNombre(nompar.toLowerCase());
      }
      return null;
   }

   @GetMapping("/paringreso")
   public List<Clasificador> getParingresos(@Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      return clasifServicio.buscaParingreso(codpar, nompar);
   }

   //Partidas de Gastos por Código o Nombre (en un mismo campo)
   @GetMapping("/partidasG")
   public List<Clasificador> getPartidas(@Param(value = "codigoNombre") String codigoNombre ) {
      if ( codigoNombre != null) {
         return clasifServicio.findPartidasG( codigoNombre.toLowerCase() );
      } else return null;
   }

   @GetMapping("/{intcla}")
   public ResponseEntity<Clasificador> getById(@PathVariable Long intcla) {
      Clasificador x = clasifServicio.findById(intcla)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Institución con Id: " + intcla)));
      return ResponseEntity.ok(x);
   }

   @PutMapping("/{intcla}")
   public ResponseEntity<Clasificador> update(@PathVariable Long intcla, @RequestBody Clasificador x) {
      Clasificador y = clasifServicio.findById(intcla)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Id del Clasificador: " + intcla)));
      y.setCodpar(x.getCodpar());
      y.setNivpar(x.getNivpar());
      y.setGrupar(x.getGrupar());
      y.setNompar(x.getNompar());
      y.setDespar(x.getDespar());
      y.setCueejepresu(x.getCueejepresu());
      y.setPresupuesto(x.getPresupuesto());
      y.setEjecucion(x.getEjecucion());
      y.setDevengado(x.getDevengado());
      y.setReforma(x.getReforma());
      y.setAsigna_ini(x.getAsigna_ini());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());
      y.setGrupo(x.getGrupo());
      y.setBalancostos(x.getBalancostos());

      Clasificador actualizar = clasifServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{intcla}")
   private ResponseEntity<Boolean> deleteClasificador(@PathVariable("intcla") Long intcla) {
      clasifServicio.deleteById(intcla);
      return ResponseEntity.ok(!(clasifServicio.findById(intcla) != null));
   }

}
