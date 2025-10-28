package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.PreviewResponse;
import com.epmapat.erp_epmapat.DTO.RecalculoRequest;
import com.epmapat.erp_epmapat.DTO.RecalculoResponse;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.servicio.InteresBatchService;
import com.epmapat.erp_epmapat.servicio.InteresServicio;

@RestController
@RequestMapping("/intereses")
@CrossOrigin(origins = "*")
public class InteresesApi {

	@Autowired
	private InteresServicio inteServicio;
	@Autowired
	private InteresBatchService batchService;

	@GetMapping
	public List<Intereses> getAllLista(@Param(value = "anio") Number anio, @Param(value = "mes") Number mes) {
		if (anio != null && mes != null) {
			return inteServicio.findByAnioMes(anio, mes);
		} else {
			return inteServicio.findAll(Sort.by(Sort.Order.desc("anio"), Sort.Order.desc("mes")));
		}
	}

	// Busca el último
	@GetMapping("/ultimo")
	public List<Intereses> getUltimo() {
		return inteServicio.findUltimo();
	}

	@PostMapping
	public ResponseEntity<Intereses> saveIntereses(@RequestBody Intereses x) {
		return ResponseEntity.ok(inteServicio.save(x));
	}

	@GetMapping("/{idinteres}")
	public ResponseEntity<Intereses> getByIdIntereses(@PathVariable Long idinteres) {
		Intereses interesesM = inteServicio.findById(idinteres)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe ese interes con ese Id: " + idinteres));
		return ResponseEntity.ok(interesesM);
	}

	@PutMapping("/{idinteres}")
	public ResponseEntity<Intereses> updateIntereses(@PathVariable Long idinteres, @RequestBody Intereses interesM) {
		Intereses interesesM = inteServicio.findById(idinteres)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe ese interes con ese Id: " + idinteres));
		interesesM.setAnio(interesM.getAnio());
		interesesM.setMes(interesM.getMes());
		interesesM.setPorcentaje(interesM.getPorcentaje());
		interesesM.setUsucrea(interesM.getUsucrea());
		interesesM.setFeccrea(interesM.getFeccrea());
		interesesM.setUsumodi(interesM.getUsumodi());
		interesesM.setFecmodi(interesM.getFecmodi());
		Intereses updatedInteres = inteServicio.save(interesesM);
		return ResponseEntity.ok(updatedInteres);
	}

	@DeleteMapping(value = "/{idinteres}")
	public ResponseEntity<Boolean> deleteInteres(@PathVariable("idinteres") Long idinteres) {
		inteServicio.deleteById(idinteres);
		return ResponseEntity.ok(!(inteServicio.findById(idinteres) != null));
	}

	@GetMapping("/calcular")
	public ResponseEntity<Object> calcularIntereses(@RequestParam Long idfactura) {
		return ResponseEntity.ok(inteServicio.facturaid(idfactura));
	}

	/**
	 * ===============================================================================================================
	 */

	/**
	 * POST /api/intereses/batch/recalcular
	 * Dispara el proceso batch que:
	 * - Carga porcentajes una sola vez
	 * - Precomputa factores acumulados
	 * - Calcula interés O(1) por factura
	 * - Hace upsert por lotes en tmpinteresxfac
	 *
	 * Body JSON (opcional):
	 * {
	 * "fechaCorte": "2025-10-20", // opcional, default: hoy
	 * "lagMeses": 1 // opcional, default: 1 (hasta mes anterior)
	 * }
	 */
	@PostMapping("/batch/recalcular")
	public ResponseEntity<RecalculoResponse> recalcularBatch(
			@RequestBody(required = false) RecalculoRequest req) {
		LocalDate corte = (req != null && req.fechaCorte() != null) ? req.fechaCorte() : LocalDate.now();
		int lag = (req != null && req.lagMeses() != null) ? Math.max(0, req.lagMeses()) : 1;

		Map<String, Object> out = batchService.recalcularIntereses(corte);

		RecalculoResponse resp = new RecalculoResponse(
				(int) out.getOrDefault("status", 200),
				(int) out.getOrDefault("totalFacturas", 0),
				(String) out.getOrDefault("desde", null),
				(String) out.getOrDefault("hasta", null),
				(String) out.getOrDefault("message", "OK"));
		return ResponseEntity.ok(resp);
	}

	/**
	 * GET /api/intereses/facturas/{id}/preview?fecha=YYYY-MM-DD&lag=1
	 * Calcula interés de una factura al corte indicado (no persiste).
	 * - Usa InteresServicio.calcularInteresFactura(...) que ya tienes.
	 */
	@GetMapping("/facturas/{id}/preview")
	public ResponseEntity<PreviewResponse> previewFactura(
			@PathVariable("id") Long idFactura,
			@RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			@RequestParam(value = "lag", required = false, defaultValue = "1") int lag) {
		LocalDate corte = (fecha != null) ? fecha : LocalDate.now();

		// Si tu InteresServicio ya implementa lag en el cálculo, úsalo.
		// Si no, puedes crear un método alterno con lag o simplemente llamar al
		// batchService para algo puntual.
		BigDecimal interes = inteServicio.calcularInteresFactura(idFactura, corte); // sin lag; ajusta si implementaste
																					// lag aquí

		PreviewResponse resp = new PreviewResponse(200, idFactura, corte, interes);
		return ResponseEntity.ok(resp);
	}

}
