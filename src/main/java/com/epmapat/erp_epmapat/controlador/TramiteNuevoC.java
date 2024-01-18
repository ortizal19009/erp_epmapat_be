package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.TramiteNuevo;
import com.epmapat.erp_epmapat.servicio.TramiteNuevoS;

@RestController
@RequestMapping("/tramitenuevo")
@CrossOrigin("*")

public class TramiteNuevoC {

   @Autowired
   private TramiteNuevoS tramitenuevoS;

   @GetMapping
   @ResponseStatus(HttpStatus.OK)
   public List<TramiteNuevo> getAllTramiteNuevo() {
      return tramitenuevoS.findAll(Sort.by(Sort.Order.asc("idtramitenuevo")));
   }

   @GetMapping("aguatramite/{idaguatramite}")
   public ResponseEntity<List<TramiteNuevo>> getByIdAguaTramite(@PathVariable("idaguatramite") Long idaguatramite) {
      return ResponseEntity.ok(tramitenuevoS.findByIdAguaTramite(idaguatramite));
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public TramiteNuevo saveTramiteNuevo(@RequestBody TramiteNuevo tramitenuevoM) {
      return tramitenuevoS.save(tramitenuevoM);
   }

   @GetMapping("/{idtramitenuevo}")
   public ResponseEntity<TramiteNuevo> getByIdTramiteNuevo(@PathVariable Long idtramitenuevo) {
      TramiteNuevo tramitenuevoM = tramitenuevoS.findById(idtramitenuevo)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe este tramite con este id" + idtramitenuevo));
      return ResponseEntity.ok(tramitenuevoM);
   }

   @PutMapping("/{idtramitenuevo}")
   public ResponseEntity<TramiteNuevo> updateTramiteNuevo(@PathVariable Long idtramitenuevo,
         @RequestBody TramiteNuevo tramitenuevom) {
      TramiteNuevo tramitenuevoM = tramitenuevoS.findById(idtramitenuevo)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe este tramite con este id" + idtramitenuevo));
      tramitenuevoM.setDireccion(tramitenuevom.getDireccion());
      tramitenuevoM.setNrocasa(tramitenuevom.getNrocasa());
      tramitenuevoM.setNrodepar(tramitenuevom.getNrodepar());
      tramitenuevoM.setBarrio(tramitenuevom.getBarrio());
      tramitenuevoM.setTipopredio(tramitenuevom.getTipopredio());
      tramitenuevoM.setPresentacedula(tramitenuevom.getPresentacedula());
      tramitenuevoM.setPresentaescritura(tramitenuevom.getPresentaescritura());
      tramitenuevoM.setSolicitaagua(tramitenuevom.getSolicitaagua());
      tramitenuevoM.setSolicitaalcantarillado(tramitenuevom.getSolicitaalcantarillado());
      tramitenuevoM.setAprobadoagua(tramitenuevom.getAprobadoagua());
      tramitenuevoM.setAprobadoalcantarillado(tramitenuevom.getAprobadoalcantarillado());
      tramitenuevoM.setFechainspeccion(tramitenuevom.getFechainspeccion());
      tramitenuevoM.setMedidorempresa(tramitenuevom.getMedidorempresa());
      tramitenuevoM.setMedidormarca(tramitenuevom.getMedidormarca());
      tramitenuevoM.setMedidornumero(tramitenuevom.getMedidornumero());
      tramitenuevoM.setMedidornroesferas(tramitenuevom.getMedidornroesferas());
      tramitenuevoM.setTuberiaprincipal(tramitenuevom.getTuberiaprincipal());
      tramitenuevoM.setTipovia(tramitenuevom.getTipovia());
      tramitenuevoM.setCodmedidor(tramitenuevom.getCodmedidor());
      tramitenuevoM.setCodmedidorvecino(tramitenuevom.getCodmedidorvecino());
      tramitenuevoM.setSecuencia(tramitenuevom.getSecuencia());
      tramitenuevoM.setInspector(tramitenuevom.getInspector());
      tramitenuevoM.setAreaconstruccion(tramitenuevom.getAreaconstruccion());
      tramitenuevoM.setNotificado(tramitenuevom.getNotificado());
      tramitenuevoM.setFechanotificacion(tramitenuevom.getFechanotificacion());
      tramitenuevoM.setObservaciones(tramitenuevom.getObservaciones());
      tramitenuevoM.setEstado(tramitenuevom.getEstado());
      tramitenuevoM.setFechafinalizacion(tramitenuevom.getFechafinalizacion());
      tramitenuevoM.setMedidordiametro(tramitenuevom.getMedidordiametro());
      tramitenuevoM.setIdcategoria_categorias(tramitenuevom.getIdcategoria_categorias());
      tramitenuevoM.setUsucrea(tramitenuevom.getUsucrea());
      tramitenuevoM.setFeccrea(tramitenuevom.getFeccrea());
      tramitenuevoM.setUsumodi(tramitenuevom.getUsumodi());
      tramitenuevoM.setFecmodi(tramitenuevom.getFecmodi());
      TramiteNuevo updatetramitenuevo = tramitenuevoS.save(tramitenuevoM);
      return ResponseEntity.ok(updatetramitenuevo);
   }

   @DeleteMapping(value = "/{idtramitenuevo}")
   public ResponseEntity<Object> deleteTramite(@PathVariable("idtramitenuevo") Long idtramitenuevo) {
      tramitenuevoS.deleteById(idtramitenuevo);
      return ResponseEntity.ok(Boolean.TRUE);
   }

}
