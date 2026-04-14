package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.modelo.contabilidad.Asientos;
import com.epmapat.erp_epmapat.servicio.contabilidad.AsientoServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/asientos")
@RequiredArgsConstructor
public class AsientosApi {

	private final AsientoServicio asiServicio;

	// @GetMapping
	// public List<Asientos> getAsientos(
	// @Param(value = "asi_com") Integer asi_com, // 1 o 2
	// @Param(value = "tipcom") Integer tipcom,
	// @Param(value = "desdeNum") Long desdeNum,
	// @Param(value = "hastaNum") Long hastaNum,
	// @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate
	// desdeFecha,
	// @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate
	// hastaFecha) {
	// if (asi_com == 1) {
	// return asiServicio.findAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
	// } else if (asi_com == 2)
	// return asiServicio.findComprobantes(tipcom, desdeNum, hastaNum, desdeFecha,
	// hastaFecha);
	// else
	// return null;
	// }
	@GetMapping
	public List<Asientos> getAsientos(
			@RequestParam(value = "asi_com") Integer asi_com, // 1 o 2
			@RequestParam(value = "tipcom", required = false) Integer tipcom,
			@RequestParam(value = "desdeNum") Long desdeNum,
			@RequestParam(value = "hastaNum") Long hastaNum,
			@RequestParam("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desdeFecha,
			@RequestParam("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hastaFecha) {
		if (asi_com == 1) {
			return asiServicio.findAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
		} else if (asi_com == 2) {
			return asiServicio.findComprobantes(tipcom, desdeNum, hastaNum, desdeFecha, hastaFecha);
		} else {
			return List.of();
		}
	}

	@GetMapping("/ultimo")
	public Asientos ultimo() {
		return asiServicio.findFirstByOrderByAsientoDesc();
	}

	// Ultimo comprobante
	@GetMapping("/ultimocompro")
	public Long ultimocompro(@Param("tipcom") Integer tipcom) {
		return asiServicio.findLastComproByTipcom(tipcom);
	}

	// Un asiento por ID (con /asiento)
	@GetMapping("/asiento")
	public ResponseEntity<Asientos> buscaById(@RequestParam Long idasiento) {
		return asiServicio.findById(idasiento)
				.map(ResponseEntity::ok) // 200 OK si existe
				.orElseGet(() -> ResponseEntity.notFound().build()); // 404 si no existe
	}

	// Un Asiento por ID
	@GetMapping("/{idasiento}")
	public ResponseEntity<Asientos> findById(@PathVariable Long idasiento) {
		return asiServicio.findById(idasiento)
				.map(ResponseEntity::ok) // 200 OK si existe
				.orElseGet(() -> ResponseEntity.notFound().build()); // 404 si no existe
	}

	// Un Asiento por Número (Retorna 200:Ok 204: noContent)
	@GetMapping("/numero/{asiento}")
	public ResponseEntity<Asientos> buscarPorNumero(@PathVariable Long asiento) {
		Asientos asi = asiServicio.buscarPorNumero(asiento);
		return (asi != null)
				? ResponseEntity.ok(asi) // 200 OK con cuerpo
				: ResponseEntity.noContent().build(); // 204 No Content sin cuerpo
	}

	// Un Asiento por Número (Retorna 200:Ok 204: noContent)
	@GetMapping("/comprobante/{tipcom}/{compro}")
	public ResponseEntity<Asientos> buscarPorTipcomYCompro(
			@PathVariable Integer tipcom,
			@PathVariable Long compro) {
		Asientos asi = asiServicio.findByTipcomAndCompro(tipcom, compro);
		return (asi != null)
				? ResponseEntity.ok(asi) // 200 OK con cuerpo
				: ResponseEntity.noContent().build(); // 204 No Content
	}

	@GetMapping("/siguiente")
	public Long getSiguienteAsiento() {
		return asiServicio.obtenerSiguienteNumeroAsiento();
	}

	@GetMapping("/ultimafecha")
	public LocalDate obtenerUltimaFecha() {
		return asiServicio.obtenerUltimaFecha();
	}

	// Busca primer comprobante de un tipcom (para navegador)
	@GetMapping("/primercompro/{tipcom}")
	public ResponseEntity<Long> obtenerPrimerComprobante(@PathVariable Integer tipcom) {
		Long compro = asiServicio.obtenerPrimerComprobante(tipcom);
		return ResponseEntity.ok(compro);
	}

	// Valida número de comprobante
	@GetMapping("/valcompro")
	public ResponseEntity<Boolean> validarCompro(@RequestParam Integer tipcom, @RequestParam Long compro) {
		boolean esComproValido = asiServicio.valCompro(tipcom, compro);
		return ResponseEntity.ok(esComproValido);
	}

	// Guarda nuevo
	@PostMapping
	public Asientos saveAsiento(@RequestBody Asientos asiento) {
		return asiServicio.save(asiento);
	}

	// Actualiza
	@PutMapping("/{idasiento}")
	public ResponseEntity<Asientos> updateAsiento(
			@PathVariable Long idasiento,
			@RequestBody Asientos asiento) {
		Asientos updated = asiServicio.updateAsiento(idasiento, asiento);
		return ResponseEntity.ok(updated);
	}

	// Elimina (Si no existe devuelve 404)
	@DeleteMapping("/{idasiento}")
	public ResponseEntity<?> deleteAsiento(@PathVariable Long idasiento) {
		try {
			asiServicio.deleteById(idasiento);
			return ResponseEntity.ok(true);
		} catch (EntityNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
