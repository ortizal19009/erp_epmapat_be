package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.modelo.contabilidad.Niveles;
import com.epmapat.erp_epmapat.servicio.contabilidad.NivelServicio;

@RestController
@RequestMapping("/niveles")
@CrossOrigin("*")

public class NivelesApi {

	@Autowired
	private NivelServicio nivServicio;

	@GetMapping
	public ResponseEntity<List<Niveles>> getAllNiveles() {
		return ResponseEntity.ok(nivServicio.findAll());
	}

	@GetMapping("/nivel")
	public Niveles findByNivcue(@Param(value = "nivcue") Integer nivcue) {
		return nivServicio.findByNivcue(nivcue);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Niveles> buscarPorId(@PathVariable Long id) {
		Optional<Niveles> nivelOptional = nivServicio.findById(id);
		if (nivelOptional.isPresent()) {
			return ResponseEntity.ok(nivelOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/siguiente")
	public ResponseEntity<Niveles> findSiguienteByNivcue(@Param(value = "nivcue") Integer nivcue) {
		Niveles nivel = nivServicio.findSiguienteByNivcue(nivcue);
		if (nivel == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(nivel);
		}
	}

}
