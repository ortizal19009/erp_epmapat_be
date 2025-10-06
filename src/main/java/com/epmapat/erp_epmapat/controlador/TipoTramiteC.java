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
import com.epmapat.erp_epmapat.modelo.TipoTramite;
import com.epmapat.erp_epmapat.servicio.TipoTramiteS;

@RestController
@RequestMapping("/tipotramite")
@CrossOrigin("*")

public class TipoTramiteC {

	@Autowired
	private TipoTramiteS tipotramiteS;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<TipoTramite> getAllTipoTramite(){
		return tipotramiteS.findAll(Sort.by(Sort.Order.asc("descripcion")));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TipoTramite saveTipoTramite(@RequestBody TipoTramite tipotramiteM) {
		return tipotramiteS.save(tipotramiteM);
	}

	@GetMapping("/{idtipotramite}")
	public ResponseEntity<TipoTramite> getByIdTipoTramite(@PathVariable Long idtipotramite){
		TipoTramite tipotramiteM = tipotramiteS.findById(idtipotramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No se encuentra este tipo de tramite: "+ idtipotramite));
		return ResponseEntity.ok(tipotramiteM);
	}

	@PutMapping("/{idtipotramite}")
	public ResponseEntity<TipoTramite> updateTipoTramite(@PathVariable Long idtipotramite, @RequestBody TipoTramite tipotramitem){
		TipoTramite tipotramiteM = tipotramiteS.findById(idtipotramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No se encuentra este tipo de tramite: "+ idtipotramite));
		tipotramiteM.setDescripcion(tipotramitem.getDescripcion());
		tipotramiteM.setFacturable(tipotramitem.getFacturable());
		tipotramiteM.setUsucrea(tipotramitem.getUsucrea());
		tipotramiteM.setFeccrea(tipotramitem.getFeccrea());
		tipotramiteM.setUsumodi(tipotramitem.getUsumodi());
		tipotramiteM.setFecmodi(tipotramitem.getFecmodi());
		TipoTramite updateTipoTramite = tipotramiteS.save(tipotramiteM);
		return ResponseEntity.ok(updateTipoTramite);
	}
	
	@DeleteMapping(value = "/{idtipotramite}")
	public ResponseEntity<Object> deleteTipoTramite(@PathVariable("idtipotramite") Long idtipotramite){
		tipotramiteS.deleteById(idtipotramite);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	
}
