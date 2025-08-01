package com.epmapat.erp_epmapat.controlador;

import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.AguaTramite;
import com.epmapat.erp_epmapat.servicio.AguaTramiteServicio;

@RestController
@RequestMapping("/aguatramite")
@CrossOrigin("*")

public class AguaTramiteApi {

   @Autowired
   private AguaTramiteServicio aguatServicio;

   @GetMapping
   public List<AguaTramite> getAll(@Param(value = "desde") Long desde,
         @Param(value = "hasta") Long hasta, @Param(value = "nombre") String nombre) {
      if (nombre != null)
         return aguatServicio.findByNombre(nombre);
      else
         return aguatServicio.findAll(desde, hasta);
   }

   @GetMapping("/{idaguatramite}")
   public ResponseEntity<AguaTramite> getById(@PathVariable Long idaguatramite) {
      AguaTramite x = aguatServicio.findById(idaguatramite)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Factura  Id: " + idaguatramite)));
      return ResponseEntity.ok(x);
   }

   @GetMapping("tipotramite/{idtipotramite}/{estado}/{d}/{h}")
   public ResponseEntity<List<AguaTramite>> getByIdtipotramite(@PathVariable("idtipotramite") Long idtipotramite,
         @PathVariable("estado") Long estado, @PathVariable("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @PathVariable("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return ResponseEntity.ok(aguatServicio.findByIdTipTramite(idtipotramite, estado, d, h));
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public AguaTramite saveAguaTramite(@RequestBody AguaTramite x) {
      return aguatServicio.save(x);
   }

   @PutMapping("/{idaguatramite}")
   public ResponseEntity<AguaTramite> updateAguaTramite(@PathVariable Long idaguatramite,
         @RequestBody AguaTramite x) {
      AguaTramite y = aguatServicio.findById(idaguatramite)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No Existe el id: " + idaguatramite));
      y.setCodmedidor(x.getCodmedidor());
      y.setComentario(x.getComentario());
      y.setEstado(x.getEstado());
      y.setSistema(x.getSistema());
      y.setFechaterminacion(x.getFechaterminacion());
      y.setObservacion(x.getObservacion());
      y.setIdtipotramite_tipotramite(x.getIdtipotramite_tipotramite());
      y.setIdcliente_clientes(x.getIdcliente_clientes());
      y.setIdfactura_facturas(x.getIdfactura_facturas());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());
      y.setIddocumento_documentos(x.getIddocumento_documentos());
      y.setNrodocumento(x.getNrodocumento());
      AguaTramite updateAguaTramite = aguatServicio.save(y);
      return ResponseEntity.ok(updateAguaTramite);
   }

   @DeleteMapping(value = "/{idaguatramite}")
   public ResponseEntity<Object> deleteAguaTramite(@PathVariable("idaguatramite") Long idaguatramite) {
      aguatServicio.deleteById(idaguatramite);
      return ResponseEntity.ok(Boolean.TRUE);
   }

}
