package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.modelo.contabilidad.Niveles;
import com.epmapat.erp_epmapat.servicio.contabilidad.NivelServicio;

@RestController
@RequestMapping("/niveles")
@CrossOrigin("*")

public class NivelesApi {

	@Autowired
	private NivelServicio nivelesS; 
	
	@GetMapping
	public ResponseEntity<List<Niveles>> getAllNiveles(){
		return ResponseEntity.ok(nivelesS.findAll());
	}
	
}
