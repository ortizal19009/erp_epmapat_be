package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
		y.setM3(x.getM3());
		y.setTotal(x.getTotal());

		Rutasxemision actualizar = ruxemiServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}

	@PatchMapping("/{idrutaxemision}")
	public ResponseEntity<Rutasxemision> updatePartial(
			@PathVariable Long idrutaxemision,
			@RequestBody Map<String, Object> cambios) {

		Rutasxemision ruta = ruxemiServicio.findById(idrutaxemision)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe Ruta con Id: " + idrutaxemision));

		// 🔹 estado (Integer)
		if (cambios.containsKey("estado"))
			ruta.setEstado(((Number) cambios.get("estado")).intValue());

		// 🔹 usuariocierre (Long)
		if (cambios.containsKey("usuariocierre"))
			ruta.setUsuariocierre(((Number) cambios.get("usuariocierre")).longValue());

		// 🔹 fechacierre (Date) — admite "yyyy-MM-dd" o ISO string
		if (cambios.containsKey("fechacierre")) {
			Object valor = cambios.get("fechacierre");
			if (valor instanceof String) {
				String fechaStr = (String) valor;
				try {
					// Acepta tanto "2025-11-05" como "2025-11-05T00:00:00"
					SimpleDateFormat formato = fechaStr.length() > 10
							? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
							: new SimpleDateFormat("yyyy-MM-dd");
					ruta.setFechacierre(formato.parse(fechaStr));
				} catch (ParseException e) {
					throw new IllegalArgumentException("Formato de fecha inválido: " + valor);
				}
			} else if (valor instanceof Number) {
				// timestamp milisegundos
				ruta.setFechacierre(new Date(((Number) valor).longValue()));
			}
		}

		// 🔹 m3 (Long)
		if (cambios.containsKey("m3"))
			ruta.setM3(((Number) cambios.get("m3")).longValue());

		// 🔹 total (BigDecimal)
		if (cambios.containsKey("total")) {
			Object valor = cambios.get("total");
			if (valor instanceof Number)
				ruta.setTotal(BigDecimal.valueOf(((Number) valor).doubleValue()));
			else if (valor instanceof String)
				ruta.setTotal(new BigDecimal((String) valor));
		}

		// ❌ No tocar usucrea ni feccrea (NOT NULL)
		// 🔸 Tampoco se cambian las relaciones idemision_emisiones ni idruta_rutas

		Rutasxemision actualizada = ruxemiServicio.save(ruta);
		return ResponseEntity.ok(actualizada);
	}

	@GetMapping("/emiruta")
	public ResponseEntity<Rutasxemision> getByEmisionRuta(@RequestParam("idemision") Long idemision,
			@RequestParam("idruta") Long idruta) {
		Rutasxemision rutasxemision = ruxemiServicio.findByEmisionRuta(idemision, idruta);
		return ResponseEntity.ok(rutasxemision);
	}

}
