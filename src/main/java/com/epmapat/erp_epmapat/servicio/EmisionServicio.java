package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.ControlRutaStats;
import com.epmapat.erp_epmapat.interfaces.EmisionBasica;
import com.epmapat.erp_epmapat.interfaces.EmisionControlResumen;
import com.epmapat.erp_epmapat.interfaces.ResEmisiones;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.RutasxemisionR;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;

@Service
public class EmisionServicio {

	@Autowired
	private EmisionesR dao;
	@Autowired
	private LecturasR lecturasR;
	@Autowired
	private RutasxemisionR rutasxemisionR;

	public List<Emisiones> findByDesdeHasta(String desde, String hasta) {
		List<Emisiones> emisiones = dao.findByDesdeHasta(desde, hasta);
		enriquecerLecturasEmisiones(emisiones);
		return emisiones;
	}

	public <S extends Emisiones> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Emisiones> findById(Long id) {
		return dao.findById(id);
	}

	// Busca la última Emisión
	public Emisiones findFirstByOrderByEmisionDesc() {
		return dao.findFirstByOrderByEmisionDesc();
	}

	public List<Emisiones> findAll(Sort sort) {
		List<Emisiones> emisiones = dao.findAll(sort);
		enriquecerLecturasEmisiones(emisiones);
		return emisiones;
	}

	public List<EmisionBasica> findAllBasicas() {
		return dao.findAllBasicas();
	}

	public List<EmisionControlResumen> findControlResumenByDesdeHasta(String desde, String hasta) {
		return dao.findControlResumenByDesdeHasta(desde, hasta);
	}

	public List<Emisiones> findByIdEmisiones(Long idemision) {
		List<Emisiones> emisiones = dao.findByIdEmisiones(idemision);
		enriquecerLecturasEmisiones(emisiones);
		return emisiones;
	}

	private void enriquecerLecturasEmisiones(List<Emisiones> emisiones) {
		if (emisiones == null || emisiones.isEmpty()) {
			return;
		}

		Map<Long, long[]> resumenPorEmision = new HashMap<>();

		for (Emisiones emision : emisiones) {
			if (emision == null || emision.getIdemision() == null) {
				continue;
			}

			List<ControlRutaStats> rutas = lecturasR.getControlRutaStatsByEmision(emision.getIdemision());
			long totalLecturas = rutas.stream()
					.map(ControlRutaStats::getLecturas)
					.filter(java.util.Objects::nonNull)
					.mapToLong(Long::longValue)
					.sum();
			long lecturasCargadas = rutas.stream()
					.map(ControlRutaStats::getLecturasTomadas)
					.filter(java.util.Objects::nonNull)
					.mapToLong(Long::longValue)
					.sum();
			resumenPorEmision.put(emision.getIdemision(), new long[] { totalLecturas, lecturasCargadas });
		}

		for (Emisiones emision : emisiones) {
			if (emision == null || emision.getIdemision() == null) {
				continue;
			}
			long[] resumen = resumenPorEmision.get(emision.getIdemision());
			emision.setTotalLecturas(resumen == null ? 0L : resumen[0]);
			emision.setLecturasCargadas(resumen == null ? 0L : resumen[1]);
		}
	}

	public List<ResEmisiones> getResEmisiones(Long limit) {
		return dao.ResumenEmisiones(limit);
	}

	public List<Emisiones> findPosterioresByEmisionAndEstadoIn(String emision, List<Integer> estados) {
		return dao.findPosterioresByEmisionAndEstadoIn(emision, estados);
	}

	public Map<String, Object> getControlDashboard(Long limit) {
		long limite = limit == null || limit <= 0 ? 24L : limit;
		List<ResEmisiones> resumenes = dao.ResumenEmisiones(Math.max(limite, 200L));
		Map<Long, ResEmisiones> resumenPorEmision = resumenes.stream()
				.collect(Collectors.toMap(ResEmisiones::getIdemision, r -> r, (a, b) -> a, LinkedHashMap::new));
		List<Emisiones> emisiones = dao.findAll(Sort.by(Sort.Direction.DESC, "idemision")).stream()
				.limit(limite)
				.collect(Collectors.toList());

		List<Map<String, Object>> items = emisiones.stream().map(emision -> {
			Map<String, Object> row = new LinkedHashMap<>();
			ResEmisiones resumen = resumenPorEmision.get(emision.getIdemision());
			List<com.epmapat.erp_epmapat.modelo.Rutasxemision> rutas = rutasxemisionR.findByIdemision(emision.getIdemision());
			Long totalRutas = (long) rutas.size();
			Long rutasCerradas = rutas.stream()
					.filter(r -> r.getEstado() != null && r.getEstado() == 1)
					.count();
			row.put("idemision", emision.getIdemision());
			row.put("emision", emision.getEmision());
			row.put("estado", emision.getEstado());
			row.put("feccrea", emision.getFeccrea());
			row.put("fechacierre", emision.getFechacierre());
			row.put("m3", resumen != null ? resumen.getM3() : null);
			row.put("ncuentas", resumen != null ? resumen.getNcuentas() : null);
			row.put("emitido", resumen != null ? resumen.getValemision() : null);
			row.put("cobrado", resumen != null ? resumen.getTotal_pagado() : null);
			row.put("pendiente", resumen != null ? resumen.getTotal_pendiente() : null);
			row.put("totalRutas", totalRutas);
			row.put("rutasCerradas", rutasCerradas);
			row.put("rutasPendientes", Math.max(0L, totalRutas - rutasCerradas));
			return row;
		}).collect(Collectors.toList());

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("emisiones", items);
		response.put("total", items.size());
		return response;
	}

	public Map<String, Object> getControlDetalle(Long idemision) {
		Emisiones emision = dao.findById(idemision)
				.orElseThrow(() -> new IllegalArgumentException("No existe la emisión " + idemision));

		Map<String, Object> response = new LinkedHashMap<>();
		Map<String, Object> emisionRow = new LinkedHashMap<>();
		emisionRow.put("idemision", emision.getIdemision());
		emisionRow.put("emision", emision.getEmision());
		emisionRow.put("estado", emision.getEstado());
		emisionRow.put("feccrea", emision.getFeccrea());
		emisionRow.put("fechacierre", emision.getFechacierre());
		emisionRow.put("observaciones", emision.getObservaciones());

		List<ControlRutaStats> rutas = lecturasR.getControlRutaStatsByEmision(idemision);
		long totalRutas = rutas.size();
		long rutasCerradas = rutas.stream().filter(r -> r.getEstadoRuta() != null && r.getEstadoRuta() == 1).count();
		long totalLecturas = rutas.stream().map(ControlRutaStats::getLecturas).filter(java.util.Objects::nonNull).mapToLong(Long::longValue).sum();
		long totalLecturasSinFactura = rutas.stream().map(ControlRutaStats::getLecturasSinFactura).filter(java.util.Objects::nonNull).mapToLong(Long::longValue).sum();
		boolean tieneLecturas = totalLecturas > 0;

		emisionRow.put("totalRutas", totalRutas);
		emisionRow.put("rutasCerradas", rutasCerradas);
		emisionRow.put("rutasPendientes", Math.max(0L, totalRutas - rutasCerradas));
		emisionRow.put("abonados", tieneLecturas
				? rutas.stream().map(ControlRutaStats::getAbonados).filter(java.util.Objects::nonNull).mapToLong(Long::longValue).sum()
				: null);
		emisionRow.put("lecturas", tieneLecturas ? totalLecturas : null);
		emisionRow.put("lecturasSinFactura", tieneLecturas ? totalLecturasSinFactura : null);
		emisionRow.put("m3", tieneLecturas
				? rutas.stream().map(ControlRutaStats::getM3).filter(java.util.Objects::nonNull)
						.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
				: null);
		emisionRow.put("emitido", tieneLecturas
				? rutas.stream().map(ControlRutaStats::getEmitido).filter(java.util.Objects::nonNull)
						.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
				: null);
		emisionRow.put("cobrado", tieneLecturas
				? rutas.stream().map(ControlRutaStats::getCobrado).filter(java.util.Objects::nonNull)
						.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
				: null);
		emisionRow.put("pendiente", tieneLecturas
				? rutas.stream().map(ControlRutaStats::getPendiente).filter(java.util.Objects::nonNull)
						.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
				: null);

		response.put("emision", emisionRow);
		response.put("rutas", rutas);
		return response;
	}

}
