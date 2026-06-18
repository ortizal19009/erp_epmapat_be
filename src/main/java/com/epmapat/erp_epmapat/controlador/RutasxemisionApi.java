package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
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

	private static final ZoneId APP_ZONE = ZoneId.systemDefault();

	@Autowired
	RutasxemisionServicio ruxemiServicio;

	@GetMapping
	public List<Rutasxemision> getByIdemision(@Param(value = "idemision") Long idemision) {
		if (idemision != null) {
			return ruxemiServicio.findByIdemision(idemision);
		}
		return null;
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
		y.setFechacierre(normalizarFechaCierre(x.getFechacierre()));
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

		if (cambios.containsKey("estado")) {
			ruta.setEstado(((Number) cambios.get("estado")).intValue());
		}

		if (cambios.containsKey("usuariocierre")) {
			ruta.setUsuariocierre(((Number) cambios.get("usuariocierre")).longValue());
		}

		if (cambios.containsKey("fechacierre")) {
			Object valor = cambios.get("fechacierre");
			if (valor instanceof String) {
				ruta.setFechacierre(parseFechaCierre((String) valor));
			} else if (valor instanceof Number) {
				ruta.setFechacierre(parseFechaCierre(((Number) valor).longValue()));
			}
		}

		if (cambios.containsKey("m3")) {
			ruta.setM3(((Number) cambios.get("m3")).longValue());
		}

		if (cambios.containsKey("total")) {
			Object valor = cambios.get("total");
			if (valor instanceof Number) {
				ruta.setTotal(BigDecimal.valueOf(((Number) valor).doubleValue()));
			} else if (valor instanceof String) {
				ruta.setTotal(new BigDecimal((String) valor));
			}
		}

		Rutasxemision actualizada = ruxemiServicio.save(ruta);
		return ResponseEntity.ok(actualizada);
	}

	private Date parseFechaCierre(String valor) {
		String fechaStr = valor == null ? "" : valor.trim();
		if (fechaStr.isEmpty()) {
			throw new IllegalArgumentException("Formato de fecha inválido: " + valor);
		}

		try {
			if (fechaStr.length() <= 10) {
				return Date.valueOf(LocalDate.parse(fechaStr));
			}

			try {
				LocalDate fecha = OffsetDateTime.parse(fechaStr)
						.atZoneSameInstant(APP_ZONE)
						.toLocalDate();
				return Date.valueOf(fecha);
			} catch (DateTimeParseException ignored) {
				LocalDate fecha = Instant.parse(fechaStr)
						.atZone(APP_ZONE)
						.toLocalDate();
				return Date.valueOf(fecha);
			}
		} catch (DateTimeParseException ex) {
			try {
				LocalDate fecha = LocalDateTime.parse(fechaStr).toLocalDate();
				return Date.valueOf(fecha);
			} catch (DateTimeParseException inner) {
				throw new IllegalArgumentException("Formato de fecha inválido: " + valor, inner);
			}
		}
	}

	private Date parseFechaCierre(long epochMillis) {
		LocalDate fecha = Instant.ofEpochMilli(epochMillis)
				.atZone(APP_ZONE)
				.toLocalDate();
		return Date.valueOf(fecha);
	}

	private Date normalizarFechaCierre(java.util.Date fecha) {
		if (fecha == null) {
			return null;
		}
		LocalDate localDate = Instant.ofEpochMilli(fecha.getTime())
				.atZone(APP_ZONE)
				.toLocalDate();
		return Date.valueOf(localDate);
	}

	@GetMapping("/emiruta")
	public ResponseEntity<Rutasxemision> getByEmisionRuta(@RequestParam("idemision") Long idemision,
			@RequestParam("idruta") Long idruta) {
		Rutasxemision rutasxemision = ruxemiServicio.findByEmisionRuta(idemision, idruta);
		return ResponseEntity.ok(rutasxemision);
	}
}
