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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.servicio.contabilidad.RetencionesServicio;

@RestController
@RequestMapping("/retenciones")
@CrossOrigin("*")

public class RetencionesApi {

   @Autowired
   private RetencionesServicio reteServicio;

   @GetMapping
   public List<Retenciones> getAllLista(@Param(value = "idasiento") Long idasiento) {
      if (idasiento != null) {
         return reteServicio.findByIdasiento(idasiento);
      } else
         return reteServicio.findAll();
   }

   @GetMapping("/desdehasta")
   public List<Retenciones> getDesdeHasta(
         @Param(value = "desdeSecu") String desdeSecu,
         @Param(value = "hastaSecu") String hastaSecu,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
      return reteServicio.findDesdeHasta(desdeSecu, hastaSecu, desdeFecha, hastaFecha);
   }

   @GetMapping("/{idrete}")
   public Optional<Retenciones> findByIdRetenciones(@PathVariable Long idrete) {
      return reteServicio.findById(idrete);
   }

   @GetMapping("/ultimo")
   public Retenciones ultimo() {
      return reteServicio.findFirstByOrderBySecretencion1Desc();
   }

   // Valida Secretencion1
   @GetMapping("/valSecretencion1")
   public ResponseEntity<Boolean> valSecretencion1(@Param(value = "secretencion1") String secretencion1) {
      boolean esValido = reteServicio.valSecretencion1(secretencion1);
      return ResponseEntity.ok(esValido);
   }

   @PostMapping
   public Retenciones saveRetencion(@RequestBody Retenciones retenciones) {
      return reteServicio.save(retenciones);
   }

   @PutMapping("/{idrete}")
   public ResponseEntity<Retenciones> updateCertiPresu(@PathVariable Long idrete,
         @RequestBody Retenciones retencionesm) {
      Retenciones retenciones = reteServicio.findById(idrete)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idrete));
      retenciones.setFecharegistro(retencionesm.getFecharegistro());
      retenciones.setSecretencion1(retencionesm.getSecretencion1());
      retenciones.setFechaemision(retencionesm.getFechaemision());
      retenciones.setFechaemiret1(retencionesm.getFechaemiret1());
      retenciones.setNumdoc(retencionesm.getNumdoc());
      retenciones.setPorciva(retencionesm.getPorciva());
      retenciones.setSwretencion(retencionesm.getSwretencion());
      retenciones.setBaseimponible(retencionesm.getBaseimponible());
      retenciones.setBaseimpgrav(retencionesm.getBaseimpgrav());
      retenciones.setBasenograiva(retencionesm.getBasenograiva());
      retenciones.setBaseimpice(retencionesm.getBaseimpice());
      retenciones.setMontoiva(retencionesm.getMontoiva());
      retenciones.setPorcentajeice(retencionesm.getPorcentajeice());
      retenciones.setMontoice(retencionesm.getMontoice());
      retenciones.setMontoivabienes(retencionesm.getMontoivabienes());
      retenciones.setCodretbienes(retencionesm.getCodretbienes());
      retenciones.setPorretbienes(retencionesm.getPorretbienes());
      retenciones.setValorretbienes(retencionesm.getValorretbienes());
      retenciones.setMontoivaservicios(retencionesm.getMontoivaservicios());
      retenciones.setCodretservicios(retencionesm.getCodretservicios());
      retenciones.setPorretservicios(retencionesm.getPorretservicios());
      retenciones.setValorretservicios(retencionesm.getValorretservicios());
      retenciones.setMontoivaserv100(retencionesm.getMontoivaserv100());
      retenciones.setCodretserv100(retencionesm.getCodretserv100());
      retenciones.setPorretserv100(retencionesm.getPorretserv100());
      retenciones.setValretserv100(retencionesm.getValretserv100());
      retenciones.setBaseimpair(retencionesm.getBaseimpair());
      retenciones.setCodretair(retencionesm.getCodretair());
      retenciones.setPorcentajeair(retencionesm.getPorcentajeair());
      retenciones.setValretair(retencionesm.getValretair());
      retenciones.setNumautoriza(retencionesm.getNumautoriza());
      retenciones.setNumserie(retencionesm.getNumserie());
      retenciones.setFechacaduca(retencionesm.getFechacaduca());
      retenciones.setDescripcion(retencionesm.getDescripcion());
      retenciones.setIdasiento(retencionesm.getIdasiento());
      retenciones.setIdbene(retencionesm.getIdbene());
      retenciones.setIntdoc(retencionesm.getIntdoc());
      retenciones.setIdautoriza(retencionesm.getIdautoriza());
      retenciones.setIdtabla01(retencionesm.getIdtabla01());
      retenciones.setIdtabla15(retencionesm.getIdtabla15());
      retenciones.setIdtabla5c_100(retencionesm.getIdtabla5c_100());
      retenciones.setIdtabla5c_bie(retencionesm.getIdtabla5c_bie());
      retenciones.setIdtabla5c_ser(retencionesm.getIdtabla5c_ser());
      retenciones.setClaveacceso(retencionesm.getClaveacceso());
      retenciones.setNumautoriza_e(retencionesm.getNumautoriza_e());
      retenciones.setFecautoriza(retencionesm.getFecautoriza());
      retenciones.setEstado(retencionesm.getEstado());
      retenciones.setAmbiente(retencionesm.getAmbiente());
      retenciones.setAutorizacion(retencionesm.getAutorizacion());
      retenciones.setSwelectro(retencionesm.getSwelectro());
      retenciones.setIdtabla17(retencionesm.getIdtabla17());
      retenciones.setInttra(retencionesm.getInttra());
      Retenciones updateRetencion = reteServicio.save(retenciones);
      return ResponseEntity.ok(updateRetencion);
   }

   @DeleteMapping(value = "/{idrete}")
   public ResponseEntity<Boolean> deleteRetenciones(@PathVariable("idrete") Long idrete) {
      reteServicio.deleteById(idrete);
      return ResponseEntity.ok(!(reteServicio.findById(idrete) != null));
   }

}
