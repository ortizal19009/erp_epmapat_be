package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.modelo.Tramites1M;
import com.epmapat.erp_epmapat.servicio.RubroServicio;
import com.epmapat.erp_epmapat.servicio.Tramites1S;

@RestController
@RequestMapping("/tramites1")
@CrossOrigin("*")

public class Tramites1C {

	@Autowired
	private Tramites1S tramites1S;
	@Autowired
	private RubroServicio rubrosS;

	@GetMapping
	public List<Tramites1M> getAllTramites(){
		return tramites1S.findAll();
	}

	@PostMapping
	public Tramites1M saveTramites(@RequestBody Tramites1M tramites1M) {
		return tramites1S.save(tramites1M);
	}

	@PutMapping("/{idtramite}/{idrubro}")
	public Tramites1M addRubrosxTramie(@PathVariable Long idtramite, @PathVariable Long idrubro) {
		Tramites1M tramite1M = tramites1S.findById(idtramite).get();
		Rubros rubrosM = rubrosS.findById(idrubro).get();
		tramite1M.addRubros(rubrosM);
		return tramites1S.save(tramite1M);	
	}

	@GetMapping("/{idtramite}")
	public ResponseEntity<Tramites1M> getTramiteById(@PathVariable ("idtramite") Long idtramite){
		Tramites1M tramite1M = tramites1S.findById(idtramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No existe este tramie con este Id: "+idtramite));
		return ResponseEntity.ok(tramite1M);
	}

}
