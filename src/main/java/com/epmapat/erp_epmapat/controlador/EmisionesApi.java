package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

import javax.servlet.http.HttpServletRequest;

import com.epmapat.erp_epmapat.DTO.AnularEmisionRequest;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.ResEmisiones;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.servicio.AuditoriaGenericaService;
import com.epmapat.erp_epmapat.servicio.EmisionMantenimientoServicio;
import com.epmapat.erp_epmapat.servicio.EmisionServicio;
import com.epmapat.erp_epmapat.servicio.MultaBasuraRepairService;

@RestController
@RequestMapping("/emisiones")

public class EmisionesApi {

	@Autowired
	private EmisionServicio emiServicio;
	@Autowired
	private MultaBasuraRepairService multaBasuraRepairService;
	@Autowired
	private EmisionMantenimientoServicio emisionMantenimientoServicio;
	@Autowired
	private AuditoriaGenericaService auditoriaGenericaService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Emisiones> getAll(@Param(value = "desde") String desde, @Param(value = "hasta") String hasta) {
		if (desde != null && hasta != null)
			return emiServicio.findByDesdeHasta(desde, hasta);
		else
			return null;
	}

	@GetMapping("/ultimo")
	public Emisiones ultimo() {
		return emiServicio.findFirstByOrderByEmisionDesc();
	}

	@GetMapping("/{idemision}")
	public ResponseEntity<Emisiones> getByIdEmision(@PathVariable Long idemision) {
		Emisiones x = emiServicio.findById(idemision)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Emisión Id: " + idemision)));
		return ResponseEntity.ok(x);
	}

	@PostMapping
	public ResponseEntity<Emisiones> save(@RequestBody Emisiones x) {
		return ResponseEntity.ok(emiServicio.save(x));
	}

	@PutMapping("/{idemision}")
	public ResponseEntity<Emisiones> update(@PathVariable Long idemision, @RequestBody Emisiones x) {
		Emisiones y = emiServicio.findById(idemision)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Emisión Id: " + idemision)));
		y.setEmision(x.getEmision());
		y.setEstado(x.getEstado());
		y.setObservaciones(x.getObservaciones());
		y.setUsuariocierre(x.getUsuariocierre());
		y.setFechacierre(x.getFechacierre());
		y.setM3(x.getM3());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());

		Emisiones actualizar = emiServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}

	@GetMapping("/findall")
	public ResponseEntity<List<Emisiones>> getAllEmisiones() {
		List<Emisiones> emisiones = emiServicio.findAll(Sort.by(Sort.Direction.DESC, "idemision"));
		return ResponseEntity.ok(emisiones);
	}

	@GetMapping("/id")
	public ResponseEntity<List<Emisiones>> getByIdEmisiones(@RequestParam Long idemision) {
		return ResponseEntity.ok(emiServicio.findByIdEmisiones(idemision));
	}

	@GetMapping("/resumen")
	public ResponseEntity<List<ResEmisiones>> getResEmisiones(@RequestParam Long limit) {
		return ResponseEntity.ok(emiServicio.getResEmisiones(limit));
	}

	@PostMapping("/{idemision}/rutaxemision/{idrutaxemision}/multa-basura/recalcular")
	public ResponseEntity<?> recalcular(
			@PathVariable Long idemision,
			@PathVariable Long idrutaxemision) {
		var resultado = multaBasuraRepairService.recalcularPorRuta(idemision, idrutaxemision);
		return ResponseEntity.ok(resultado);
	}

	@PostMapping("/{idemision}/reabrir")
	public ResponseEntity<?> reabrirEmision(
			@PathVariable Long idemision,
			@RequestParam(required = false, defaultValue = "0") Long usumodi) {
		try {
			return ResponseEntity.ok(emisionMantenimientoServicio.reabrirEmision(idemision, usumodi));
		} catch (ResponseStatusException ex) {
			return ResponseEntity.status(ex.getStatus()).body(errorBody(ex));
		}
	}

	@PostMapping("/{idemision}/anular")
	public ResponseEntity<?> anularEmision(
			@PathVariable Long idemision,
			@RequestBody AnularEmisionRequest request,
			@RequestParam(required = false, defaultValue = "0") Long usumodi,
			HttpServletRequest httpRequest) {
		try {
			return ResponseEntity.ok(emisionMantenimientoServicio.anularEmision(idemision, request, usumodi, httpRequest));
		} catch (ResponseStatusException ex) {
			return ResponseEntity.status(ex.getStatus()).body(errorBody(ex));
		}
	}

	@PostMapping("/{idemision}/eliminar")
	public ResponseEntity<?> eliminarEmision(
			@PathVariable Long idemision,
			@RequestParam(required = false, defaultValue = "0") Long usumodi) {
		return ResponseEntity.ok(emisionMantenimientoServicio.eliminarEmision(idemision, usumodi));
	}

	@PostMapping("/audit")
	public ResponseEntity<?> registrarAuditoriaEmision(@RequestBody Map<String, Object> payload) {
		return ResponseEntity.ok(auditoriaGenericaService.saveAuditEntry(payload));
	}

	@GetMapping("/audit")
	public ResponseEntity<?> consultarAuditoriaEmision(
			@RequestParam(required = false) Long idemision,
			@RequestParam(required = false) String accion,
			@RequestParam(required = false) String desde,
			@RequestParam(required = false) String hasta) {
		return ResponseEntity.ok(auditoriaGenericaService.consultarAuditoriaEmisiones(idemision, accion, desde, hasta));
	}

	private Map<String, Object> errorBody(ResponseStatusException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("success", false);
		body.put("status", ex.getStatus().value());
		body.put("message", ex.getReason() == null ? "Solicitud inválida." : ex.getReason());
		return body;
	}

}
