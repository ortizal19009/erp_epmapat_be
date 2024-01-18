package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.epmapat.erp_epmapat.modelo.Reclamos;
import com.epmapat.erp_epmapat.servicio.ReclamosS;

@RestController
@RequestMapping("/reclamos")
@CrossOrigin(origins = "*")

public class ReclamosApi {

	@Autowired
	private ReclamosS reclamosS;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Reclamos> getAllReclamos(){
		return reclamosS.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Reclamos saveReclamos(@RequestBody Reclamos reclamosM) {
		return reclamosS.save(reclamosM);
	}

	@GetMapping("/{idreclamo}")
	public ResponseEntity<Reclamos> getByIdReclamos(@PathVariable Long idreclamo){
		Reclamos reclamosM = reclamosS.findById(idreclamo)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No Existe el reclamo con ese ID: "+ idreclamo));
		return ResponseEntity.ok(reclamosM);
	}

	@PutMapping("/{idreclamo}")
	public ResponseEntity<Reclamos> updateReclamos(@PathVariable Long idreclamo, @RequestBody Reclamos reclamosm ){
		Reclamos reclamosM = reclamosS.findById(idreclamo)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No Existe el reclamo con ese ID: "+ idreclamo));
		reclamosM.setObservacion(reclamosm.getObservacion());
		reclamosM.setReferencia(reclamosm.getReferencia());
		reclamosM.setFechaasignacion(reclamosm.getFechaasignacion());
		reclamosM.setEstado(reclamosm.getEstado());
		reclamosM.setReferenciadireccion(reclamosm.getReferenciadireccion());
		reclamosM.setPiso(reclamosm.getPiso());
		reclamosM.setDepartamento(reclamosm.getDepartamento());
		reclamosM.setFechamaxcontesta(reclamosm.getFechamaxcontesta());
		reclamosM.setFechacontesta(reclamosm.getFechacontesta());
		reclamosM.setContestacion(reclamosm.getContestacion());
		reclamosM.setFechaterminacion(reclamosm.getFechaterminacion());
		reclamosM.setResponsablereclamo(reclamosm.getResponsablereclamo());
		reclamosM.setModulo(reclamosm.getModulo());
		reclamosM.setNotificacion(reclamosm.getNotificacion());
		reclamosM.setEstadonotificacion(reclamosm.getEstadonotificacion());
		reclamosM.setIdtpreclamo_tpreclamo(reclamosm.getIdtpreclamo_tpreclamo());
		reclamosM.setIdmodulo_modulos(reclamosm.getIdmodulo_modulos());
		reclamosM.setUsucrea(reclamosm.getUsucrea());
		reclamosM.setFeccrea(reclamosm.getFeccrea());
		reclamosM.setUsumodi(reclamosm.getUsumodi());
		reclamosM.setFecmodi(reclamosm.getFecmodi());
		Reclamos updateReclamos = reclamosS.save(reclamosM);
		return ResponseEntity.ok(updateReclamos);
	}

	@DeleteMapping(value = "/{idreclamo}")
	public ResponseEntity<Boolean> deleteReclamo(@PathVariable("idreclamo")Long idreclamo){
		reclamosS.deleteById(idreclamo);
		return ResponseEntity.ok(!(reclamosS.findById(idreclamo)!=null));
	}

}
