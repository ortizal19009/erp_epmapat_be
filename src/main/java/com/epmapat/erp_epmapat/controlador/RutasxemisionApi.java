package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import com.epmapat.erp_epmapat.servicio.RutasxemisionServicio;

@RestController
@RequestMapping("/rutasxemision")
@CrossOrigin("*")

public class RutasxemisionApi {

	@Autowired
	RutasxemisionServicio ruxemiServicio;

	// Alternativa 1. Ok.
	@GetMapping
	public List<Rutasxemision> getByIdemision(@Param(value = "idemision") Long idemision) {
		if (idemision != null) {
			return ruxemiServicio.findByIdemision(idemision);
		} else {
			return null;
		}
	}

	@GetMapping("/{idrutaxemision}")
	public ResponseEntity<Rutasxemision> getByIdrutaxemision(@PathVariable Long idrutaxemision) {
		Rutasxemision y = ruxemiServicio.findById(idrutaxemision)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe Id: " + idrutaxemision)));
		return ResponseEntity.ok(y);
	}

	@GetMapping("/conteo")
	public Long contarPorEstadoYEmision(@RequestParam Long idemision_emisiones) {
		return ruxemiServicio.contarPorEstadoYEmision(idemision_emisiones);
	}

	@PostMapping
	public Rutasxemision save(@RequestBody Rutasxemision x) {
		return ruxemiServicio.save(x);
	}

	@PutMapping("/{idrutaxemision}")
	public ResponseEntity<Rutasxemision> update(@PathVariable Long idrutaxemision, @RequestBody Rutasxemision x) {
		Rutasxemision y = ruxemiServicio.findById(idrutaxemision)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe Rutaxemision con Id: " + idrutaxemision)));
		y.setEstado(x.getEstado());
		y.setUsuariocierre(x.getUsuariocierre());
		y.setFechacierre(x.getFechacierre());
		y.setIdemision_emisiones(x.getIdemision_emisiones());
		y.setIdruta_rutas(x.getIdruta_rutas());
		y.setM3(x.getM3());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setM3(x.getM3());
		y.setTotal(x.getTotal());

		Rutasxemision actualizar = ruxemiServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}
	@GetMapping("/emiruta")
	public ResponseEntity<Rutasxemision> getByEmisionRuta(@RequestParam("idemision") Long idemision,@RequestParam("idruta") Long idruta){
		Rutasxemision rutasxemision = ruxemiServicio.findByEmisionRuta(idemision, idruta);
		return ResponseEntity.ok(rutasxemision);
	}

}
