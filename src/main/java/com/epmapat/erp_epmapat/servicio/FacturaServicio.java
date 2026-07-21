package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.DTO.RemiDTO;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.interfaces.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.Fec_facturaR;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_detallesR;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_detalles_impuestosR;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_logR;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_pagosR;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

@Service
public class FacturaServicio {

	@Autowired
	private FacturasR dao;
	@Autowired
	@Lazy
	private InteresServicio interesServicio;
	@Autowired
	@Lazy
	private AbonadoServicio abonadosServicio;
	@Autowired
	private RubroxfacR rubroxfacR;
	@Autowired
	private DefinirR dao_definir;
	@Autowired
	private LecturaServicio lecturaServicio;
	@Autowired
	private Fec_facturaR fecFacturaR;
	@Autowired
	private Fec_factura_detallesR fecFacturaDetallesR;
	@Autowired
	private Fec_factura_detalles_impuestosR fecFacturaDetallesImpuestosR;
	@Autowired
	private Fec_factura_logR fecFacturaLogR;
	@Autowired
	private Fec_factura_pagosR fecFacturaPagosR;
	@Autowired
	private AuditoriaGenericaService auditoriaGenericaService;
	@Autowired
	@Lazy
	private TmpinteresxfacService tmpinteresxfacService;

	public Facturas validarUltimafactura(String codrecaudador) {
		return dao.validarUltimafactura(codrecaudador);
	}

	public List<Facturas> findByUsucobro(Long idusuario, Date dfecha, Date hfecha) {
		return dao.findByUsucobro(idusuario, dfecha, hfecha);
	}

	public List<FacturasI> findByFechacobro(Date fechacobro) {
		return dao.findByFechacobro(fechacobro);
	}

	public List<Facturas> findAll() {
		return dao.findAll();
	}

	public List<Facturas> findDesde(Long desde, Long hasta) {
		return dao.findDesde(desde, hasta);
	}

	@SuppressWarnings("null")
	public Optional<Facturas> findById(Long idfactura) {
		return dao.findById(idfactura);
	}

	// Planillas por Cliente
	public List<Facturas> findByIdcliente(Long idcliente, Long limit) {
		int max = limit != null && limit > 0 ? Math.toIntExact(limit) : 20;
		return dao.findByIdcliente_IdclienteAndTotaltarifaGreaterThanOrderByIdfacturaDesc(
				idcliente,
				BigDecimal.ZERO,
				PageRequest.of(0, max));
	}

	// Planillas por Abonado
	public List<Facturas> findByIdabonado(Long idabonado) {
		return dao.findByIdabonado(idabonado);
	}

	public List<Facturas> findByIdabonadoLimit(Long idabonado, Long limit) {
		return dao.findByIdabonadoLimit(idabonado, limit);
	}

	public Page<Facturas> findByIdabonadoPage(Long idabonado, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return dao.findByIdabonadoAndFechaeliminacionIsNullOrderByIdfacturaDesc(idabonado, pageable);
	}

	// Una Planilla (como lista)
	public List<Facturas> buscarPlanilla(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	// Planillas por Abonado y Fecha
	public List<Facturas> buscarPorAbonadoYFechaCreacionRange(Long idabonado, LocalDate fechaDesde,
			LocalDate fechaHasta) {
		return dao.findByAbonadoAndFechaCreacionRange(idabonado, fechaDesde, fechaHasta);
	}

	// Planillas Sin Cobrar de un Cliente

	public List<Facturas> findSinCobro(Long idcliente) {
		return dao.findSinCobro(idcliente);
	}

	public List<CVFacturasNoConsumo> SinCobroOfCV(Long idcliente, LocalDate date) {
		return dao.SinCobroOfCV(idcliente, date);
	}

	public List<FacSinCobrar> findFacSincobro(Long idcliente) {
		return dao.findFacSincobro(idcliente);
	}

	public List<FacSinCobrar> findFacSincobroByCuetna(Long cuenta) {
		return dao.findFacSincobroByCuetna(cuenta);
	}

	public List<FacSinCobrar> findByCuenta(Long cuenta) {
		return dao.findByCuenta(cuenta);
	}

	public List<FacSinCobrar> findSincobroByCuetna(Long cuenta) {
		return dao.findSincobroByCuetna(cuenta);
	}

	// Planillas Sin Cobrar de un Abonado (para Multas)
	public List<Long> findSinCobroAbo(Long idabonado) {
		return dao.findSinCobroAbo(idabonado);
	}

	// Cuenta las Planillas Pendientes de un Abonado
	public long getCantidadFacturasByAbonadoAndPendientes(Long idabonado) {
		return dao.countFacturasByAbonadoAndPendientes(idabonado);
	}

	// Planillas Sin Cobrar de un Abonado (Para convenios)
	public List<Facturas> findSinCobrarAbo(Long idmodulo, Long idabonado) {
		return dao.findSinCobrarAbo(idmodulo, idabonado);
	}

	public List<Facturas> findSinCobrarAboMod(Long idabonado) {
		return dao.findSinCobrarAboMod(idabonado);
	}

	public Long countSinCobrarAbo(Long idabonado) {
		return dao.countSinCobrarAbo(idabonado);
	}

	// Recaudación diaria - Facturas cobrasdas <Facturas>
	// public List<Facturas> findByFechacobro(LocalDate fecha) {
	// return dao.findByFechacobro(fecha);
	// }

	// Recaudación diaria - Facturas cobradas (Sumando los rubros)
	public List<RepFacGlobal> findByFechacobroTotRangos(LocalDate d_fecha, LocalDate h_fecha) {
		return dao.findByFechacobroTotRangos(d_fecha, h_fecha);
	}

	public List<RepFacGlobal> findByFechacobroTotByRecaudador(LocalDate d_fecha, LocalDate h_fecha, Long idrecaudador) {
		return dao.findByFechacobroTotByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	// Total diario por Forma de cobro
	public List<Object[]> totalFechaFormacobroRangos(LocalDate d_fecha, LocalDate h_fecha) {
		return dao.totalFechaFormacobroRangos(d_fecha, h_fecha);
	}

	// Total diario por Forma de cobro
	public List<Object[]> totalFechaFormacobroByRecaudador(LocalDate d_fecha, LocalDate h_fecha, Long idrecaudador) {
		return dao.totalFechaFormacobroByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	public List<RepFacGlobal> findByFechacobroTot(LocalDate fecha) {
		return dao.findByFechacobroTot(fecha);
	}

	// Total diario por Forma de cobro
	public List<Object[]> totalFechaFormacobro(LocalDate fecha) {
		return dao.totalFechaFormacobro(fecha);
	}

	@SuppressWarnings("null")
	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public List<Facturas> findByIdFactura(Long idabonado) {
		return dao.findByIdFactura(idabonado);
	}

	public List<Facturas> findByNrofactura(String nrofactura) {
		return dao.findByNrofactura(nrofactura);
	}

	@Transactional
	public <S extends Facturas> S save(S entity) {
		prepararFechaCreacionSiEsNueva(entity);
		S saved = dao.save(entity);
		if (saved.getFechaanulacion() != null) {
			eliminarFacturaElectronicaEnCascada(
					saved.getIdfactura(),
					saved.getUsuarioanulacion(),
					saved.getRazonanulacion());
		}
		return saved;
	}

	public static void mergeFactura(Facturas target, Facturas source) {
		BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
	}

	private static String[] getNullPropertyNames(Facturas source) {
		final BeanWrapper beanWrapper = new BeanWrapperImpl(source);
		java.util.List<String> nullProperties = new java.util.ArrayList<>();
		for (var propertyDescriptor : beanWrapper.getPropertyDescriptors()) {
			String propertyName = propertyDescriptor.getName();
			if (beanWrapper.getPropertyValue(propertyName) == null) {
				nullProperties.add(propertyName);
			}
		}
		return nullProperties.toArray(new String[0]);
	}

	public <S extends Facturas> S saveForNewEmision(S entity) {
		prepararFechaCreacionSiEsNueva(entity);
		return dao.save(entity);
	}

	private void prepararFechaCreacionSiEsNueva(Facturas factura) {
		if (factura == null || factura.getIdfactura() != null) {
			return;
		}
		if (factura.getFeccrea() == null) {
			factura.setFeccrea(LocalDate.now().withDayOfMonth(1));
		}
	}
	@Transactional
	public void eliminarFacturaElectronicaEnCascada(Long idfactura, Long idusuario, String observacion) {
		if (idfactura == null || !fecFacturaR.existsById(idfactura)) {
			return;
		}
		auditarFacturaElectronica(idfactura, idusuario, observacion);
		List<Long> detalleIds = fecFacturaDetallesR.findByIdfactura(idfactura)
				.stream()
				.map(Fec_factura_detalles::getIdfacturadetalle)
				.collect(Collectors.toList());
		if (!detalleIds.isEmpty()) {
			fecFacturaDetallesImpuestosR.deleteByIdfacturadetalleIn(detalleIds);
		}
		fecFacturaDetallesR.deleteByIdfactura(idfactura);
		fecFacturaPagosR.deleteByIdfactura(idfactura);
		fecFacturaLogR.deleteByIdfactura(idfactura);
		fecFacturaR.deleteByIdfactura(idfactura);
	}

	private void auditarFacturaElectronica(Long idfactura, Long idusuario, String observacion) {
		Optional<Fec_factura> fecFacturaOpt = fecFacturaR.findById(idfactura);
		if (fecFacturaOpt.isEmpty()) {
			return;
		}

		Fec_factura fecFactura = fecFacturaOpt.get();
		List<Fec_factura_detalles> detalles = fecFacturaDetallesR.findByIdfactura(idfactura);
		Map<Long, List<Object>> impuestosPorDetalle = new LinkedHashMap<>();
		for (Fec_factura_detalles detalle : detalles) {
			impuestosPorDetalle.put(
					detalle.getIdfacturadetalle(),
					new ArrayList<>(fecFacturaDetallesImpuestosR.findByIdDetalle(detalle.getIdfacturadetalle())));
		}

		Map<String, Object> snapshot = new LinkedHashMap<>();
		snapshot.put("facturaElectronica", fecFactura);
		snapshot.put("detalles", detalles);
		snapshot.put("impuestosPorDetalle", impuestosPorDetalle);
		snapshot.put("pagos", fecFacturaPagosR.getByIdfactura(idfactura));
		snapshot.put("historialSri", fecFacturaLogR.findByIdfacturaOrderByFechaAsc(idfactura));

		auditoriaGenericaService.saveAudit(
				"fec_factura",
				idfactura,
				snapshot,
				idusuario == null ? 0L : idusuario,
				observacion == null || observacion.isBlank() ? "ANULACION DE FACTURA" : observacion,
				"ELIMINACION");
	}

	private boolean fueEnviadaAlSri(Fec_factura fecFactura) {
		if (fecFactura == null || fecFactura.getEstado() == null) {
			return false;
		}
		return Set.of("C", "U", "A", "O").contains(fecFactura.getEstado().trim().toUpperCase());
	}

	public Map<String, Object> obtenerDetalleAnulacionBaja(Long idfactura) {
		Facturas factura = findById(idfactura)
				.orElseThrow(() -> new RuntimeErrorException(null, "FACTURA NO ENCONTRADA"));
		Optional<Fec_factura> fecFacturaOpt = fecFacturaR.findById(idfactura);
		List<Lecturas> lecturas = lecturaServicio.findByIdfactura(idfactura);
		Object emision = lecturaServicio.getEmisionByIdfactura(idfactura);

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("factura", factura);
		response.put("fecFactura", fecFacturaOpt.orElse(null));
		response.put("tieneFacturaElectronica", fecFacturaOpt.isPresent());
		response.put("enviadaSri", fecFacturaOpt.map(this::fueEnviadaAlSri).orElse(false));
		response.put("lecturas", lecturas);
		response.put("emision", emision);

		if (!lecturas.isEmpty()) {
			Lecturas lectura = lecturas.get(0);
			Float lecturaAnterior = lectura.getLecturaanterior();
			Float lecturaActual = lectura.getLecturaactual();
			if (lecturaAnterior != null && lecturaActual != null) {
				response.put("m3", lecturaActual - lecturaAnterior);
			}
		}

		return response;
	}

	@Transactional
	public Facturas ejecutarAnulacion(Long idfactura, String motivo, Long idusuario) {
		Facturas factura = findById(idfactura)
				.orElseThrow(() -> new RuntimeErrorException(null, "FACTURA NO ENCONTRADA"));

		auditoriaGenericaService.saveAudit(
				"facturas",
				idfactura,
				factura,
				idusuario == null ? 0L : idusuario,
				motivo == null || motivo.isBlank() ? "ANULACION DE FACTURA" : motivo,
				"MODIFICACION");

		LocalDate hoy = LocalDate.now();
		factura.setFechaanulacion(hoy);
		factura.setNrofactura(null);
		factura.setRazonanulacion(motivo);
		factura.setFechacobro(null);
		factura.setPagado(0);
		factura.setUsuariocobro(null);
		factura.setUsuarioanulacion(idusuario);
		if (Objects.equals(factura.getFormapago(), 4L)) {
			factura.setEstado(1L);
			factura.setFormapago(1L);
		}
		if (Objects.equals(factura.getEstadoconvenio(), 1L)) {
			factura.setEstado(2L);
		}

		save(factura);
		return findById(idfactura)
				.orElseThrow(() -> new RuntimeErrorException(null, "FACTURA NO ENCONTRADA"));
	}

	@Transactional
	public Facturas ejecutarEliminacionLogica(Long idfactura, String motivo, Long idusuario) {
		Facturas factura = findById(idfactura)
				.orElseThrow(() -> new RuntimeErrorException(null, "FACTURA NO ENCONTRADA"));

		auditoriaGenericaService.saveAudit(
				"facturas",
				idfactura,
				factura,
				idusuario == null ? 0L : idusuario,
				motivo == null || motivo.isBlank() ? "ELIMINACION LOGICA DE FACTURA" : motivo,
				"MODIFICACION");

		LocalDate hoy = LocalDate.now();
		factura.setFechaeliminacion(hoy);
		factura.setRazoneliminacion(motivo);
		factura.setEstado(0L);
		factura.setUsuarioeliminacion(idusuario);
		Facturas actualizada = dao.save(factura);

		Long idmodulo = actualizada.getIdmodulo() != null ? actualizada.getIdmodulo().getIdmodulo() : null;
		if ((Objects.equals(idmodulo, 3L) || Objects.equals(idmodulo, 4L))
				&& actualizada.getIdabonado() != null
				&& actualizada.getIdabonado() > 0) {
			List<Lecturas> lecturas = lecturaServicio.findByIdfactura(idfactura);
			if (!lecturas.isEmpty()) {
				Lecturas lectura = lecturas.get(0);
				lectura.setEstado(0);
				lectura.setObservaciones(motivo);
				lecturaServicio.actualizarLecturaConAuditoria(
						lectura.getIdlectura(),
						lectura,
						idusuario == null ? 0L : idusuario,
						motivo == null || motivo.isBlank() ? "ELIMINACION LOGICA DE FACTURA" : motivo,
						"ELIMINACION");
			}
		}

		return findById(actualizada.getIdfactura())
				.orElseThrow(() -> new RuntimeErrorException(null, "FACTURA NO ENCONTRADA"));
	}

	public FacturasR getDao() {
		return dao;
	}

	public void setDao(FacturasR dao) {
		this.dao = dao;
	}

	// FACTURAS ANULACIÓN
	public List<Facturas> fingAllFacturasAnuladas(Long limit) {
		return this.dao.fingAllFacturasAnuladas(limit);
	}

	public List<Facturas> findCobradasByCliente(Long idcliente) {
		return this.dao.findCobradasByCliente(idcliente);
	}

	// FACTURAS ELIMINACIÓN
	public List<Facturas> fingAllFacturasEliminadas(Long limit) {
		return this.dao.fingAllFacturasEliminadas(limit);
	}

	public List<Facturas> findByFecEliminacion(LocalDate d, LocalDate h) {
		return this.dao.findByFecEliminacion(d, h);
	}

	public List<Facturas> findByFecAnulacion(LocalDate d, LocalDate h) {
		return this.dao.findByFecAnulacion(d, h);
	}

	/* transferencias cobradas */
	public List<R_transferencias> transferenciasCobradas(Date d_fecha, Date h_fecha) {
		return this.dao.transferenciasCobradas(d_fecha, h_fecha);
	}

	public List<Facturas> findFechaCobro(LocalDate d, LocalDate h) {
		return this.dao.findFechaCobro(d, h);
	}

	// Cartera a una fecha
	public List<Facturas> cartera(LocalDate hasta) {
		return dao.cartera(hasta);
	}

	// Cartera de un cliente a una fecha (Facturas)
	public List<Facturas> carteraCliente(Long idcliente, LocalDate hasta) {
		return dao.carteraCliente(idcliente, hasta);
	}

	// Cartera de un cliente a una fecha (Total, ya suma 1 a los del módulo 3)
	public Double totCarteraCliente(Long idcliente, LocalDate hasta) {
		return dao.totCarteraCliente(idcliente, hasta);
	}

	/* REPORTE DE FACTURAS ELIMINADAS POR RANGO DE FECHA */
	public List<RepFacEliminadas> findEliminadasXfecha(LocalDate d, LocalDate h) {
		return dao.findEliminadasXfecha(d, h);
	}

	/* REPORTE DE FACTURAS ANULADAS POR RANGO DE FECHA */
	public List<RepFacEliminadas> findAnuladasXfecha(LocalDate d, LocalDate h) {
		return dao.findAnuladasXfecha(d, h);
	}

	public List<FacIntereses> getForIntereses(Long idfactura) {
		return dao.getForIntereses(idfactura);
	}

	// REPORTES DE FACTURAS TRANSFERENCIAS
	public List<FacTransferencias> getFacAllTransferidas(LocalDate d, LocalDate h) {
		return dao.getFacAllTransferidas(d, h);
	}

	public List<FacTransferencias> getFacPagadasTransferidas(LocalDate d, LocalDate h) {
		return dao.getFacPagadasTransferidas(d, h);
	}

	public List<FacTransferencias> getFacNoPagadasTransferidas(LocalDate d, LocalDate h) {
		return dao.getFacNoPagadasTransferidas(d, h);
	}
	/* CARTERA VENCIDA POR FACTURAS */

	public List<CarteraVencidaFacturas> getCVByFacturasConsumo(LocalDate fecha) {
		return dao.getCVByFacturasConsumo(fecha);
	}

	public List<CVFacturasNoConsumo> getCVByFacturasNoConsumo(LocalDate fecha) {
		return dao.getCVByFacturasNoConsumo(fecha);
	}

	public Page<CarteraVencidaFacturas> getCVByConsumo(LocalDate fecha, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return dao.getCVByConsumo(fecha, pageable);
	}

	public Page<CVFacturasNoConsumo> getCVByNoConsumo(LocalDate fecha, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return dao.getCVByNoConsumo(fecha, pageable);
	}

	public Page<CVAbonados> getCVAbonados(LocalDate fecha, Long estado, Long idcategoria, Long idruta, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return dao.getCVAbonados(fecha, estado, idcategoria, idruta, pageable);
	}

	public List<CVFacturasNoConsumo> getCvFacturasByAbonado(Long cuenta, LocalDate fecha) {
		return dao.getCvFacturasByAbonado(cuenta, fecha);
	}

	public List<RemiDTO> getFacForRemisiones(Long idcliente, LocalDate topefecha) {
		List<Remision> _facturas = dao.getFacForRemisiones(idcliente, topefecha);
		List<RemiDTO> remision = new ArrayList<>();

		for (Remision item : _facturas) {
			RemiDTO remi = new RemiDTO();
			Object interes = interesServicio.facturaid(item.getIdfactura());
			if (interes instanceof Double) {
				remi.setIntereses(BigDecimal.valueOf((Double) interes)); // Convert Double to BigDecimal
			} else {
				remi.setIntereses(BigDecimal.ZERO); // Default if not a Double
			}
			remi.setIdfactura(item.getIdfactura());
			remi.setDescripcion(item.getDescripcion());
			remi.setTotal(item.getTotal());
			remi.setFeccrea(item.getFeccrea());
			remi.setNrofactura(item.getNrofactura());
			remision.add(remi);
		}

		return remision;
	}

	public List<RemiDTO> getFacForRemisionesAbonados(Long idcliente, Long cuenta, LocalDate topefecha) {
		List<Remision> _facturas = dao.getFacForRemisionesAbonados(idcliente, cuenta, topefecha);
		List<RemiDTO> remision = new ArrayList<>();

		for (Remision item : _facturas) {
			RemiDTO remi = new RemiDTO();
			Object interes = interesServicio.facturaid(item.getIdfactura());
			if (interes instanceof Double) {
				remi.setIntereses(BigDecimal.valueOf((Double) interes)); // Convert Double to BigDecimal
			} else {
				remi.setIntereses(BigDecimal.ZERO); // Default if not a Double
			}
			remi.setIdfactura(item.getIdfactura());
			remi.setDescripcion(item.getDescripcion());
			remi.setTotal(item.getTotal());
			remi.setFeccrea(item.getFeccrea());
			remi.setNrofactura(item.getNrofactura());
			remision.add(remi);
		}

		return remision;
	}

	public Facturas updateFactura(Long idfactura, Facturas factura) {

		Optional<Facturas> existingFactura = dao.findById(idfactura);
		if (existingFactura.isPresent()) {
			Facturas existingFact = existingFactura.get();
			existingFact.setConveniopago(factura.getConveniopago());
			existingFact.setFechaconvenio(factura.getFechaconvenio());
			existingFact.setSwcondonar(factura.getSwcondonar());
			existingFact.setUsumodi(factura.getUsumodi());
			existingFact.setFecmodi(factura.getFecmodi());
			return dao.save(existingFact);
		} else {
			throw new RuntimeErrorException(null, "FACTURA NO ENCONTRADA");
		}
	}

	public List<CVFacturasNoConsumo> getCvFacturasByRubro(Long idrubro, LocalDate fecha) {
		return dao.getCvFacturasByRubro(idrubro, fecha);
	}

	public List<ValorFactDTO> findFacturasSinCobro(Long cuenta) {
		// Obtener la lista de facturas desde el DAO
		List<FacturasSinCobroInter> facturas = dao.findFacturasSinCobro(cuenta);
		// Procesar la lista y transformar cada FacturasSinCobroInter en ValorFactDTO
		List<ValorFactDTO> facturasActualizadas = facturas.stream()
				.map(item -> {
					// Obtener el interés desde el servicio
					Object interesObj = interesServicio.facturaid(item.getIdfactura());
					BigDecimal interes = BigDecimal.ZERO; // Valor por defecto

					// Convertir el interés a BigDecimal
					if (interesObj instanceof Double) {
						interes = BigDecimal.valueOf((Double) interesObj); // Convertir Double a BigDecimal
					} else if (interesObj instanceof BigDecimal) {
						interes = (BigDecimal) interesObj; // Ya es BigDecimal, no es necesario convertir
					} else {
						System.err.println("Tipo de interés no soportado: " + interesObj.getClass().getName());
					}
					// Crear un nuevo objeto ValorFactDTO y asignar los valores
					ValorFactDTO dto = new ValorFactDTO();
					dto.setIdfactura(item.getIdfactura());
					dto.setSubtotal(item.getSubtotal());
					dto.setNumfacturas(facturas.size());
					dto.setInteres(interes);
					dto.setCuenta(cuenta);
					// Calcular el total sumando el subtotal y el interés
					BigDecimal total = BigDecimal.valueOf(item.getSubtotal()).add(interes);
					dto.setTotal(total);
					return dto; // Devolver el DTO
				})
				.collect(Collectors.toList()); // Recopilar los DTOs en una lista
		// Devolver la lista de DTOs
		return facturasActualizadas;
	}

	public ValorFactDTO getTotalesByAbonado(Long cuenta) {
		// Obtener la lista de facturas
		List<ValorFactDTO> facturas = findFacturasSinCobro(cuenta);

		// Inicializar acumuladores
		Float st = (float) 0; // Subtotal acumulado
		BigDecimal t = BigDecimal.ZERO; // Total acumulado
		BigDecimal i = BigDecimal.ZERO; // Interés acumulado

		// Calcular los totales
		for (ValorFactDTO item : facturas) {
			st += item.getSubtotal(); // Acumular subtotal
			t = t.add(item.getTotal()); // Acumular total
			i = i.add(item.getInteres()); // Acumular interés
		}

		// Crear un nuevo DTO con los totales calculados
		ValorFactDTO newFactura = new ValorFactDTO();
		newFactura.setSubtotal(st);
		newFactura.setTotal(t);
		newFactura.setInteres(i);
		newFactura.setNumfacturas(facturas.size());
		newFactura.setCuenta(cuenta);

		// Devolver el DTO con los totales
		return newFactura;
	}

	public List<ValorFactDTO> findSincobroDatos(Long cuenta) {
		// Obtener la lista de facturas desde el DAO
		List<FacturasSinCobroInter> facturas = dao.findSincobroDatos(cuenta);

		// Procesar la lista y transformar cada FacturasSinCobroInter en ValorFactDTO
		return facturas.stream().map(item -> {
			ValorFactDTO dto = new ValorFactDTO();
			// Asignar valores al DTO
			dto.setIdfactura(item.getIdfactura());
			dto.setSubtotal(item.getSubtotal());
			dto.setNumfacturas(facturas.size());
			dto.setCuenta(cuenta);
			dto.setNombre(item.getNombre());
			dto.setCedula(item.getCedula());
			dto.setDireccionubicacion(item.getDireccionubicacion());
			dto.setFeccrea(item.getFeccrea());
			dto.setFectransferencia(item.getFectransferencia());
			dto.setFormapago(item.getFormapago());
			Object interesObj = interesServicio.interesToFactura(dto);
			BigDecimal interes = BigDecimal.ZERO; // Valor por defecto
			// Convertir el interés a BigDecimal correctamente
			if (interesObj instanceof Double) {
				interes = BigDecimal.valueOf((Double) interesObj);
			} else if (interesObj instanceof BigDecimal) {
				interes = (BigDecimal) interesObj;
			} else if (interesObj instanceof Float) {
				interes = BigDecimal.valueOf((Float) interesObj);
			} else {
				System.err.println("Tipo de interés no soportado: "
						+ (interesObj != null ? interesObj.getClass().getName() : "null"));
			}

			dto.setInteres(interes);

			// Calcular el total correctamente usando BigDecimal
			BigDecimal total = BigDecimal.valueOf(item.getSubtotal()).add(interes);
			dto.setTotal(total);

			return dto;
		}).collect(Collectors.toList()); // Recopilar los DTOs en una lista
	}

	public ValorFactDTO getTotalesByAbonadoDatos(Long cuenta) {
		List<ValorFactDTO> facturas = findSincobroDatos(cuenta);

		if (!facturas.isEmpty()) {
			// Usamos reduce directamente para sumar
			float subtotal = facturas.stream()
					.map(ValorFactDTO::getSubtotal)
					.reduce(0f, Float::sum);

			BigDecimal total = facturas.stream()
					.map(ValorFactDTO::getTotal)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal interes = facturas.stream()
					.map(ValorFactDTO::getInteres)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			ValorFactDTO first = facturas.get(0);

			return crearValorFactDTO(
					cuenta,
					subtotal,
					total,
					interes,
					facturas.size(),
					first.getNombre(),
					first.getCedula(),
					first.getDireccionubicacion());
		}

		// Si no hay facturas, obtener datos del abonado
		return abonadosServicio.getByIdabonado(cuenta)
				.stream()
				.findFirst()
				.map(a -> crearValorFactDTO(
						cuenta,
						0f,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						0,
						a.getIdresponsable().getNombre(),
						a.getIdresponsable().getCedula(),
						a.getDireccionubicacion()))
				.orElseGet(() -> crearValorFactDTO(
						cuenta,
						0f,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						0,
						"N/A",
						"N/A",
						"N/A"));
	}

	public ValorFactDTO ___getTotalesByAbonadoDatos(Long cuenta) {
		List<ValorFactDTO> facturas = findSincobroDatos(cuenta);

		if (!facturas.isEmpty()) {
			// Calcular totales usando Streams
			Float subtotal = facturas.stream().map(ValorFactDTO::getSubtotal).reduce((float) 0,
					Float::sum);
			BigDecimal total = facturas.stream().map(ValorFactDTO::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal interes = facturas.stream().map(ValorFactDTO::getInteres).reduce(BigDecimal.ZERO,
					BigDecimal::add);

			// Tomar los datos del primer elemento
			ValorFactDTO firstFactura = facturas.get(0);
			return crearValorFactDTO(cuenta, subtotal, total, interes, facturas.size(), firstFactura.getNombre(),
					firstFactura.getCedula(), firstFactura.getDireccionubicacion());
		}

		// Manejo del caso donde no hay facturas
		List<Abonados> abonado = abonadosServicio.getByIdabonado(cuenta);
		if (abonado.isEmpty()) {
			return crearValorFactDTO(cuenta, (float) 0, BigDecimal.ZERO, BigDecimal.ZERO, 0, "N/A", "N/A", "N/A");
		}

		// Obtener los datos del abonado
		Abonados firstAbonado = abonado.get(0);
		return crearValorFactDTO(cuenta, (float) 0, BigDecimal.ZERO, BigDecimal.ZERO, 0,
				firstAbonado.getIdresponsable().getNombre(), firstAbonado.getIdresponsable().getCedula(),
				firstAbonado.getDireccionubicacion());
	}

	// Método auxiliar para evitar código repetitivo
	private ValorFactDTO crearValorFactDTO(Long cuenta, Float subtotal, BigDecimal total, BigDecimal interes,
			int numFacturas, String nombre, String cedula, String direccion) {
		ValorFactDTO dto = new ValorFactDTO();
		dto.setSubtotal(subtotal);
		dto.setTotal(total);
		dto.setInteres(interes);
		dto.setNumfacturas(numFacturas);
		dto.setCuenta(cuenta);
		dto.setNombre(nombre);
		dto.setCedula(cedula);
		dto.setDireccionubicacion(direccion);
		return dto;
	}

	public ValorFactDTO _getTotalesByAbonadoDatos(Long cuenta) {
		// Obtener la lista de facturas
		List<ValorFactDTO> facturas = findSincobroDatos(cuenta);
		ValorFactDTO newFactura = new ValorFactDTO();

		if (facturas.size() > 0) {
			// Inicializar acumuladores
			Float st = (float) 0; // Subtotal acumulado
			BigDecimal t = BigDecimal.ZERO; // Total acumulado
			BigDecimal i = BigDecimal.ZERO; // Interés acumulado

			// Calcular los totales
			for (ValorFactDTO item : facturas) {
				st += item.getSubtotal(); // Acumular subtotal
				t = t.add(item.getTotal()); // Acumular total
				i = i.add(item.getInteres()); // Acumular interés
			}

			// Crear un nuevo DTO con los totales calculados
			newFactura.setSubtotal(st);
			newFactura.setTotal(t);
			newFactura.setInteres(i);
			newFactura.setNumfacturas(facturas.size());
			newFactura.setCuenta(cuenta);
			newFactura.setNombre(facturas.get(0).getNombre());
			newFactura.setCedula(facturas.get(0).getCedula());
			newFactura.setDireccionubicacion(facturas.get(0).getDireccionubicacion());
			return newFactura;
		} else {
			List<Abonados> abonado = abonadosServicio.getByIdabonado(cuenta);
			newFactura.setSubtotal((float) 0);
			newFactura.setTotal(BigDecimal.ZERO);
			newFactura.setInteres(BigDecimal.ZERO);
			newFactura.setNumfacturas(facturas.size());
			newFactura.setCuenta(cuenta);
			newFactura.setNombre(abonado.get(0).getIdresponsable().getNombre());
			newFactura.setCedula(abonado.get(0).getIdresponsable().getCedula());
			newFactura.setDireccionubicacion(abonado.get(0).getDireccionubicacion());
			return newFactura;
		}

		// Devolver el DTO con los totales
		// return newFactura;
	}

	public List<FacSinCobrar> getIdsFromFacturasSincobrar() {
		return dao.getIdsFromFacturasSincobrar();
	}

	@Transactional
	public Facturas eliminarRubro6YRecalcularTotal(Long idfactura) {
		Facturas factura = dao.findById(idfactura)
				.orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + idfactura));

		/*
		 * List<Long> idfacturas = dao.findSinCobroAbo(factura.getIdabonado());
		 * if (idfacturas == null)
		 * return ZERO;
		 * 
		 * long nroPendientes = idfacturas.size();
		 * if (nroPendientes < 1)
		 * return ZERO;
		 * 
		 * Definir definir = dao_definir.findTopByOrderByIddefinirDesc();
		 * if (Objects.isNull(definir) || Objects.isNull(definir.getRbu()))
		 * return ZERO;
		 * 
		 * return definir.getRbu().multiply(BigDecimal.valueOf(0.005));
		 */

		rubroxfacR.deleteByFacturaIdAndRubroId(idfactura, 6L);

		BigDecimal nuevoTotal = rubroxfacR.findAllByFacturaId(idfactura).stream()
				.map(r -> {
					BigDecimal vu = r.getValorunitario() != null ? r.getValorunitario() : BigDecimal.ZERO;
					BigDecimal cant = r.getCantidad() != null ? BigDecimal.valueOf(r.getCantidad()) : BigDecimal.ONE;
					return vu.multiply(cant);
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		factura.setTotaltarifa(nuevoTotal);
		factura.setValorbase(nuevoTotal);
		return dao.save(factura);
	}

	@Transactional
	public Facturas validarMultasYRecalcularTotal(Long idfactura) {

		Facturas factura = dao.findById(idfactura)
				.orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + idfactura));

		// ✅ 1) Obtén la cuenta (AJUSTA el getter al tuyo real)
		Long cuenta = factura.getIdabonado();
		if (cuenta == null) {
			// Si no hay cuenta, por seguridad eliminamos la multa y recalculamos
			rubroxfacR.deleteByFacturaIdAndRubroId(idfactura, 6L);
			return recalcularYGuardarTotales(factura);
		}

		// ✅ 2) Calcula cuánto debe de multa (0 = no debe)
		BigDecimal valorMulta = multas(cuenta, idfactura);
		boolean debeMulta = valorMulta != null && valorMulta.compareTo(BigDecimal.ZERO) > 0;

		// ✅ 3) Si NO debe multa: borrar rubro 6 si existe
		if (!debeMulta) {
			rubroxfacR.deleteByFacturaIdAndRubroId(idfactura, 6L);
			return recalcularYGuardarTotales(factura);
		}
		Rubros rubro = new Rubros();
		rubro.setIdrubro(6L);

		// ✅ 4) Si SÍ debe multa: crear o actualizar rubro 6
		Rubroxfac multa = rubroxfacR.findByFacturaIdAndRubroId(idfactura, 6L)
				.orElseGet(() -> {
					Rubroxfac nuevo = new Rubroxfac();
					nuevo.setIdfactura_facturas(factura);
					nuevo.setIdrubro_rubros(rubro);
					// si tu entidad Rubroxfac tiene objeto rubro relacionado, setéalo también
					// nuevo.setIdrubro_rubros(rubrosRepo.getById(6L));
					nuevo.setCantidad(1F); // multa siempre 1
					return nuevo;
				});

		// ✅ valor multa va en valorunitario (cantidad=1)
		multa.setCantidad(1F);
		multa.setValorunitario(valorMulta);
		rubroxfacR.save(multa);

		// ✅ 5) recalcular totales
		return recalcularYGuardarTotales(factura);
	}

	@Transactional
	public Facturas setMulta(Long idfactura) {

		Facturas factura = dao.findById(idfactura)
				.orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + idfactura));
		Date emisionDate = lecturaServicio.findDateByIdfactura(idfactura);

		// ✅ 1) Obtén la cuenta (AJUSTA el getter al tuyo real)
		// Long cuenta = factura.getIdabonado();

		// ✅ 2) Calcula cuánto debe de multa (0 = no debe)
		BigDecimal valorMulta = set_multas(emisionDate);
		boolean debeMulta = valorMulta != null && valorMulta.compareTo(BigDecimal.ZERO) > 0;

		// ✅ 3) Si NO debe multa: borrar rubro 6 si existe
		if (!debeMulta) {
			rubroxfacR.deleteByFacturaIdAndRubroId(idfactura, 6L);
			return recalcularYGuardarTotales(factura);
		}
		Rubros rubro = new Rubros();
		rubro.setIdrubro(6L);

		// ✅ 4) Si SÍ debe multa: crear o actualizar rubro 6
		Rubroxfac multa = rubroxfacR.findByFacturaIdAndRubroId(idfactura, 6L)
				.orElseGet(() -> {
					Rubroxfac nuevo = new Rubroxfac();
					nuevo.setIdfactura_facturas(factura);
					nuevo.setIdrubro_rubros(rubro);
					// si tu entidad Rubroxfac tiene objeto rubro relacionado, setéalo también
					// nuevo.setIdrubro_rubros(rubrosRepo.getById(6L));
					nuevo.setCantidad(1F); // multa siempre 1
					return nuevo;
				});

		// ✅ valor multa va en valorunitario (cantidad=1)
		multa.setCantidad(1F);
		multa.setValorunitario(valorMulta);
		rubroxfacR.save(multa);

		// ✅ 5) recalcular totales
		return recalcularYGuardarTotales(factura);
	}

	/** Recalcula totales en base a rubroxfac */
	private Facturas recalcularYGuardarTotales(Facturas factura) {
		Long idfactura = factura.getIdfactura();

		BigDecimal nuevoTotal = rubroxfacR.findAllByFacturaId(idfactura).stream()
				.filter(Objects::nonNull)
				.map(r -> {
					BigDecimal vu = r.getValorunitario() != null ? r.getValorunitario() : BigDecimal.ZERO;

					// si cantidad es Integer/Long/Double ajusta aquí:
					BigDecimal cant = BigDecimal.ONE;
					if (r.getCantidad() != null) {
						// si cantidad es Integer/Long:
						cant = BigDecimal.valueOf(r.getCantidad());
						// si cantidad fuese BigDecimal, entonces: cant = r.getCantidad();
						// si fuese Double: cant = BigDecimal.valueOf(r.getCantidad()).setScale(2,
						// RoundingMode.HALF_UP);
					}

					return vu.multiply(cant);
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// si aquí además manejas subtotal, base, etc. lo puedes separar
		factura.setTotaltarifa(nuevoTotal);
		factura.setValorbase(nuevoTotal);

		return dao.save(factura);
	}

	private BigDecimal multas(Long cuenta, Long idfacturaActual) {
		if (cuenta == null)
			return BigDecimal.ZERO;

		long pendientes = (idfacturaActual == null)
				? dao.findSinCobroAbo(cuenta).size()
				: dao.countPendientesMultaExcluyendoFacturaActual(cuenta, idfacturaActual);
		if (pendientes <= 0)
			return BigDecimal.ZERO;

		Definir definir = dao_definir.findTopByOrderByIddefinirDesc();
		if (definir == null || definir.getRbu() == null)
			return BigDecimal.ZERO;

		// 0.5% del RBU
		return definir.getRbu().multiply(BigDecimal.valueOf(0.005));
	}

	private BigDecimal set_multas(Date fechaemision) {
		BigDecimal rbu2025 = BigDecimal.valueOf(470.00);
		BigDecimal porcentaje = BigDecimal.valueOf(0.005);

		Definir definir = dao_definir.findTopByOrderByIddefinirDesc();
		if (definir == null || definir.getRbu() == null)
			return BigDecimal.ZERO;
		if (fechaemision == null)
			return BigDecimal.ZERO;

		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaemision);
		LocalDate fecha = LocalDate.of(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DAY_OF_MONTH));

		if (fecha.isBefore(LocalDate.of(2025, 8, 1)))
			return BigDecimal.valueOf(2.00);

		if (fecha.isBefore(LocalDate.of(2025, 12, 1)))
			return rbu2025.multiply(porcentaje);

		return definir.getRbu().multiply(porcentaje);
	}

	@Transactional
	public Map<String, Object> reCalcularMultas(Long emision) {
		Map<String, Object> respuesta = new HashMap<>();
		int actualizados = 0;

		try {
			// 1. Obtener facturas de la emisión
			List<FacturasSinCobroInter> facturas = dao.findBothMultas(emision);

			// Fecha de referencia para el cálculo de pendientes
			LocalDate fechaReferencia = LocalDate.of(2026, 3, 3);

			for (FacturasSinCobroInter factura : facturas) {
				// 2. Obtener pendientes para verificar la condición (> 2 pendientes)
				List<FacturasSinCobroInter> pendientes = dao.calcularPendientesDeAbonados(
						factura.getCuenta(),
						fechaReferencia,
						fechaReferencia);

				if (pendientes != null && pendientes.size() == 1) {

					// 3. Borrar rubros de multas específicos (1011 y 6)
					// Asegúrate que estos métodos en rubroxfacR usen @Modifying
					rubroxfacR.deleteByFacturaIdAndRubroId(factura.getIdfactura(), 6L);

					// IMPORTANTE: Forzar el vaciado del caché de Hibernate para que la suma SQL sea
					// correcta
					// Si tienes el EntityManager inyectado, usa em.flush(); em.clear();

					// 4. Obtener la nueva suma de los rubros restantes
					BigDecimal valorFactura = rubroxfacR.sumRubrosFactura(factura.getIdfactura());

					if (valorFactura == null)
						valorFactura = BigDecimal.ZERO;

					// 5. Actualizar la entidad Factura
					// Corregido: 'fact' para el objeto y los nombres de los setters
					Facturas fact = dao.findById(factura.getIdfactura())
							.orElseThrow(
									() -> new RuntimeException("Factura no encontrada: " + factura.getIdfactura()));

					fact.setValorbase(valorFactura);
					fact.setTotaltarifa(valorFactura);

					// Guardamos los cambios en la tabla principal de facturas
					dao.save(fact);

					actualizados++;
				}
			}

			respuesta.put("status", "finalizado");
			respuesta.put("procesadas", facturas.size());
			respuesta.put("actualizadas", actualizados);

		} catch (Exception e) {
			System.err.println("Error en reCalcularMultas: " + e.getMessage());
			respuesta.put("status", "error");
			respuesta.put("mensaje", e.getMessage());
			// El @Transactional hará rollback de todos los borrados si algo falla aquí
		}

		return respuesta;
	}

	public List<FacturasProjection> findFacturasCobradasByEmision(Long idemision) {
		return dao.findFacturasCobradasByEmision(idemision);
	}
}



