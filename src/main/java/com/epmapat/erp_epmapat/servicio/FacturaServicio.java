package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import com.epmapat.erp_epmapat.DTO.RemiDTO;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.controlador.AbonadosApi;
import com.epmapat.erp_epmapat.controlador.AboxSuspensionC;
import com.epmapat.erp_epmapat.interfaces.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.AboxSuspensionR;
import com.epmapat.erp_epmapat.repositorio.FacturasR;

@Service
public class FacturaServicio {

	private final AboxSuspensionR aboxSuspensionR;

	private final AboxSuspensionC aboxSuspensionC;

	private final AbonadosR abonadosR;

	private final AbonadoServicio abonadoServicio;

	private final AbonadosApi abonadosApi;

	@Autowired
	private FacturasR dao;
	@Autowired
	@Lazy
	private InteresServicio interesServicio;
	@Autowired
	@Lazy
	private AbonadoServicio abonadosServicio;

	FacturaServicio(AbonadosApi abonadosApi, AbonadoServicio abonadoServicio, AbonadosR abonadosR,
			AboxSuspensionC aboxSuspensionC, AboxSuspensionR aboxSuspensionR) {
		this.abonadosApi = abonadosApi;
		this.abonadoServicio = abonadoServicio;
		this.abonadosR = abonadosR;
		this.aboxSuspensionC = aboxSuspensionC;
		this.aboxSuspensionR = aboxSuspensionR;
	}

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
		return dao.findByIdcliente(idcliente, limit);
	}

	// Planillas por Abonado
	public List<Facturas> findByIdabonado(Long idabonado) {
		return dao.findByIdabonado(idabonado);
	}

	public List<Facturas> findByIdabonadoLimit(Long idabonado, Long limit) {
		return dao.findByIdabonadoLimit(idabonado, limit);
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

	public <S extends Facturas> S save(S entity) {
		return dao.save(entity);
	}

	public <S extends Facturas> S saveForNewEmision(S entity) {

		return dao.save(entity);
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

	public List<Facturas> findByFecEliminacion(Date d, Date h) {
		return this.dao.findByFecEliminacion(d, h);
	}

	public List<Facturas> findByFecAnulacion(Date d, Date h) {
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

	public List<FacSinCobrar> getIdsFromFacturasSincobrar(){
		return dao.getIdsFromFacturasSincobrar();
	}

}
