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

import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;
import com.epmapat.erp_epmapat.servicio.contabilidad.CertipresuServicio;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/certipresu")


public class CertipresuApi {

	final private CertipresuServicio certiServicio;

	// Certificaciones o Reintegradas por numero y fechas
	@GetMapping
	public List<Certipresu> desdeHasta(@RequestParam int tipo, @RequestParam Long desdeNum, @RequestParam Long hastaNum,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desdeFecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hastaFecha) {
		return certiServicio.findDesdeHasta(tipo, desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	// Obtiene la última Certificacion o Reintegrada
	@GetMapping("/ultima")
	public Certipresu ultimo(@RequestParam Integer tipo) {
		return certiServicio.findFirstByTipoOrderByNumeroDesc(tipo);
	}

	// Valida número
	@GetMapping("/valnumero/numero/{numero}/tipo/{tipo}")
	public boolean valNumero(@PathVariable Long numero, @PathVariable Integer tipo) {
		return certiServicio.existsByNumeroAndTipo(numero, tipo);
	}

	// Busca por número (Retorna 200:Ok 204: noContent)
	@GetMapping("/numero")
	public ResponseEntity<Certipresu> findByNumeroAndTipo(@RequestParam Long numero, @RequestParam int tipo) {
		Certipresu c = certiServicio.findByNumeroAndTipo(numero, tipo);
		if (c == null) {
			return ResponseEntity.noContent().build();
		} // 204 No Content
		return ResponseEntity.ok(c); // 200 OK
	}

	// BUsca la última certificacion hasta una fecha (para el navegador)
	@GetMapping("/ultima/fecha")
	public ResponseEntity<Long> obtenerUltimoNumero(
			@RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		Long numero = certiServicio.obtenerUltimoNumeroTipo1HastaFecha(fecha);
		return ResponseEntity.ok(numero);
	}

	@GetMapping("/{idcerti}")
	public Optional<Certipresu> findByIdCertiPresu(@PathVariable Long idcerti) {
		return certiServicio.findById(idcerti);
	}

	// Nueva
	@PostMapping
	public Certipresu saveCertiPresu(@RequestBody Certipresu certipresu) {
		return certiServicio.save(certipresu);
	}

	// Actualiza
	@PutMapping("/{idcerti}")
	public ResponseEntity<Certipresu> updateCertipresu(
			@PathVariable Long idcerti,
			@RequestBody Certipresu certipresu) {
		Certipresu updated = certiServicio.updateCertipresu(idcerti, certipresu);
		return ResponseEntity.ok(updated);
	}

	// Elimina (Si no existe devuelve 404)
	@DeleteMapping("/{idcerti}")
	public ResponseEntity<?> deleteCertipresu(@PathVariable Long idcerti) {
		try {
			certiServicio.deleteById(idcerti);
			return ResponseEntity.ok(true);
		} catch (EntityNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
