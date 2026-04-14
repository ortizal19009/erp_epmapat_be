package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.Partixcerti;
import com.epmapat.erp_epmapat.servicio.contabilidad.PartixcertiServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/partixcerti")
@RequiredArgsConstructor
public class PartixcertiApi {

	final private PartixcertiServicio parxcerServicio;

	// Partidas de una Certipresu
	@GetMapping("/idcerti/{idcerti}")
	public List<Partixcerti> getByIdCerti(@PathVariable Long idcerti) {
		return parxcerServicio.findByIdcerti(idcerti);
	}

	@GetMapping("/{idparxcer}")
	public Optional<Partixcerti> findById(@PathVariable Long idparxcer) {
		return parxcerServicio.findById(idparxcer);
	}

	// Cuenta las partidas de una certipresu
	@GetMapping("/count/{idcerti}")
	public short contar(@PathVariable Long idcerti) {
		return parxcerServicio.contarPorIdCerti(idcerti);
	}

	//Partixcerti por intpre (Certificaciones de una partida)
	@GetMapping("/porIntpre")
    public ResponseEntity<List<Partixcerti>> buscaPorIntpre(
            @RequestParam Long intpre,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        List<Partixcerti> resultado = parxcerServicio.buscaPorIntpreDesdeHasta(intpre, desde, hasta);
        return ResponseEntity.ok(resultado);
    }

	// Nueva
	@PostMapping
	public Partixcerti savePartiCerti(@RequestBody Partixcerti partixcerti) {
		return parxcerServicio.save(partixcerti);
	}

	// Actualiza
	@PutMapping("/{idparxcer}")
	public ResponseEntity<Partixcerti> updatePartixcerti(
			@PathVariable Long idparxcer,
			@RequestBody Partixcerti partixcerti) {
		Partixcerti updated = parxcerServicio.updatePartixcerti(idparxcer, partixcerti);
		return ResponseEntity.ok(updated);
	}

	// Elimina (Si no existe devuelve 404)
	@DeleteMapping("/{idparxcer}")
	public ResponseEntity<?> deletePartrixcedrti(@PathVariable Long idparxcer) {
		try {
			parxcerServicio.deleteById(idparxcer);
			return ResponseEntity.ok(true);
		} catch (EntityNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
