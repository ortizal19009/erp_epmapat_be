package com.epmapat.erp_epmapat.controlador;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.FacturasI;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.administracion.ReporteModelDTO;
import com.epmapat.erp_epmapat.reportes.facturas.interfaces.i_ReporteFacturasCobradas_G;
import com.epmapat.erp_epmapat.servicio.FacturaServicio;

import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/facturas")
@CrossOrigin(origins = "*")

public class FacturasApi {

	@Autowired
	private FacturaServicio facServicio;
	@Autowired
	private i_ReporteFacturasCobradas_G i_reportefacturascobradas_g;

	@GetMapping
	public List<Facturas> getAll(@Param(value = "desde") Long desde, @Param(value = "hasta") Long hasta,
			@Param(value = "idcliente") Long idcliente) {
		if (desde != null)
			return facServicio.findDesde(desde, hasta);
		else {
			if (idcliente != null)
				return facServicio.findByIdcliente(idcliente);
			else
				return facServicio.findAll();
		}
	}

	@GetMapping("/validador/{codrecaudador}")
	public ResponseEntity<Facturas> validarUltimaFactura(@PathVariable("codrecaudador") String codrecaudador) {
		Facturas factura = facServicio.validarUltimafactura(codrecaudador);
		return ResponseEntity.ok(factura);
	}

	@GetMapping("/reportes/individual")
	public ResponseEntity<List<Facturas>> getByUsucobro(@RequestParam("idusuario") Long idusuario,
			@RequestParam("dfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dfecha,
			@RequestParam("hfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hfecha) {
		List<Facturas> facturas = facServicio.findByUsucobro(idusuario, dfecha, hfecha);
		if (!facturas.isEmpty()) {

			return ResponseEntity.ok(facturas);
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/reportes/fechacobro")
	public ResponseEntity<List<FacturasI>> getByFechacobro(
			@RequestParam("fechacobro") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechacobro) {
		List<FacturasI> facturas = facServicio.findByFechacobro(fechacobro);
		if (!facturas.isEmpty()) {

			return ResponseEntity.ok(facturas);
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/idabonado/{idabonado}")
	public List<Facturas> getByIdabonado(@PathVariable("idabonado") Long idabonado) {
		return facServicio.findByIdabonado(idabonado);
	}

	// Una Planilla (como lista)
	@GetMapping("/planilla")
	public ResponseEntity<List<Facturas>> buscarPlanilla(@Param(value = "idfactura") Long idfactura) {
		List<Facturas> x = facServicio.buscarPlanilla(idfactura);
		if (x.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(x);
	}

	// Planillas por Abonado y Fecha
	@GetMapping("/porabonado")
	public ResponseEntity<List<Facturas>> buscarPorAbonadoYFechaCreacionRange(
			@Param(value = "idabonado") Long idabonado,
			@Param("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaDesde,
			@Param("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaHasta) {
		List<Facturas> x = facServicio.buscarPorAbonadoYFechaCreacionRange(idabonado, fechaDesde, fechaHasta);
		if (x.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(x);
	}

	@GetMapping("/{idfactura}")
	public ResponseEntity<Facturas> getById(@PathVariable Long idfactura) {
		Facturas x = facServicio.findById(idfactura)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe la Factura  Id: " + idfactura)));
		return ResponseEntity.ok(x);
	}

	// Planillas sin cobro de un Cliente
	@GetMapping("/idcliente/{idcliente}")
	public List<Facturas> getSinCobro(@PathVariable("idcliente") Long idcliente) {
		return facServicio.findSinCobro(idcliente);
	} 

	// IDs de las Planillas sin cobrar de un Abonado
	@GetMapping("/sincobro")
	public List<Long> getSinCobroAbo(@Param(value = "idabonado") Long idabonado) {
		return facServicio.findSinCobroAbo(idabonado);
	}

	// Planillas sin cobrar de un Abonado
	@GetMapping("/sincobrarAbo")
	public List<Facturas> getSinCobrarAbo(@Param(value = "idmodulo") Long idmodulo,
			@Param(value = "idabonado") Long idabonado) {
		return facServicio.findSinCobrarAbo(idmodulo, idabonado);
	}
	// Planillas sin cobrar de un Abonado
	@GetMapping("/sincobrarAboMod")
	public List<Facturas> getSinCobrarAboMod(@Param(value = "idabonado") Long idabonado) {
		return facServicio.findSinCobrarAboMod(idabonado);
	}

	// Cuenta las Planillas pendientes de un Abonado
	@GetMapping("/pendientesabonado")
	public ResponseEntity<Long> getCantidadFacturasPendientes(@Param(value = "idabonado") Long idabonado) {
		return ResponseEntity.ok(facServicio.getCantidadFacturasByAbonadoAndPendientes(idabonado));
	}

	@GetMapping("/f_abonado/{idabonado}")
	public List<Facturas> getFacturaByAbonado(@PathVariable("idabonado") Long idabonado) {
		return facServicio.findByIdFactura(idabonado);
	}

	// Planilla por nrofactura
	@GetMapping("/nrofactura")
	public List<Facturas> getByNrofactura(@Param(value = "nrofactura") String nrofactura) {
		return facServicio.findByNrofactura(nrofactura);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	// @GetMapping("/cobradas")
	// public List<Facturas> findByFechacobro(@Param("fecha")
	// @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
	// return facServicio.findByFechacobro(fecha);
	// }

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("reportes/cobradastotrangos")
	public List<Object[]> findByFechacobroTotRangos(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha) {
		return facServicio.findByFechacobroTotRangos(d_fecha, h_fecha);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("/reportes/cobradastot")
	public List<Object[]> findByFechacobroTotByRecaudador(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
			@Param("idrecaudador") Long idrecaudador) {
		return facServicio.findByFechacobroTotByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("reportes/totalformacobrorangos")
	public List<Object[]> totalFechaFormacobroRangos(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha) {
		return facServicio.totalFechaFormacobroRangos(d_fecha, h_fecha);
	}

	@GetMapping("/reportes/totalformacobro")
	public List<Object[]> totalFechaFormacobroByRecaudador(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
			@Param("idrecaudador") Long idrecaudador) {

		return facServicio.totalFechaFormacobroByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("/cobradastot")
	public List<Object[]> findByFechacobroTot(
			@Param("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return facServicio.findByFechacobroTot(fecha);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("/totalformacobro")
	public List<Object[]> totalFechaFormacobro(
			@Param("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return facServicio.totalFechaFormacobro(fecha);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Facturas saveFacturas(@RequestBody Facturas x) {
		return facServicio.save(x);
	}

	@PutMapping("/{idfactura}")
	public ResponseEntity<Facturas> updateFacturas(@PathVariable long idfactura, @RequestBody Facturas x) {
		Facturas y = facServicio.findById(idfactura)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe esa factura con ese id" + idfactura));
		y.setConveniopago(x.getConveniopago());
		y.setEstado(x.getEstado());
		y.setEstadoconvenio(x.getEstadoconvenio());
		y.setFechaanulacion(x.getFechaanulacion());
		y.setFechacobro(x.getFechacobro());
		y.setFechaconvenio(x.getFechaconvenio());
		y.setFechaeliminacion(x.getFechaeliminacion());
		y.setFechatransferencia(x.getFechatransferencia());
		y.setFormapago(x.getFormapago());
		y.setHoracobro(x.getHoracobro());
		y.setIdabonado(x.getIdabonado());
		y.setIdcliente(x.getIdcliente());
		y.setIdmodulo(x.getIdmodulo());
		y.setNrofactura(x.getNrofactura());
		y.setPagado(x.getPagado());
		y.setPorcexoneracion(x.getPorcexoneracion());
		y.setRazonanulacion(x.getRazonanulacion());
		y.setRazoneliminacion(x.getRazoneliminacion());
		y.setRazonexonera(x.getRazonexonera());
		y.setRefeformapago(x.getRefeformapago());
		y.setTotaltarifa(x.getTotaltarifa());
		y.setUsuarioanulacion(x.getUsuarioanulacion());
		y.setUsuariocobro(x.getUsuariocobro());
		y.setUsuarioeliminacion(x.getUsuarioeliminacion());
		y.setUsuariotransferencia(x.getUsuariotransferencia());
		y.setInterescobrado(x.getInterescobrado());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setSwiva(x.getSwiva());
		// y.setFeccrea(x.getFeccrea());

		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());

		y.setValorbase(x.getValorbase());
		Facturas updateFacturas = facServicio.save(y);
		return ResponseEntity.ok(updateFacturas);
	}

	/*
	 * ==============================
	 * *********REPORTES*************
	 * ==============================
	 */

	@GetMapping("/reportes/facturascobradas")
	public ResponseEntity<Resource> reporteFacturasCobradas(
			@RequestParam("v_dfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam("v_hfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha)
			throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("fileName", "facturasCobradas");

		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = null;
		mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	@GetMapping("/reportes/facturascobradascaja")
	public ResponseEntity<Resource> reporteFacturasCobradasCaja(
			@RequestParam("v_dfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam("v_hfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha,
			@RequestParam("usuariocobro") Long usuariocobro)
			throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("usuariocobro", usuariocobro);
		params.put("fileName", "facturasCobradasCaja");

		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = null;
		mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	@GetMapping("/reportes/facturasrubros")
	public ResponseEntity<Resource> reporteFacturaRubros(
			@RequestParam("v_dfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam("v_hfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha,
			@RequestParam("c_feccrea") @DateTimeFormat(pattern = "yyyy-MM-dd") Date c_feccrea)
			throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("c_feccrea", c_feccrea);
		params.put("fileName", "facturasCobradasRubros");
		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = MediaType.APPLICATION_PDF;
		;
		/*
		 * if (tipo == "excel") {
		 * mediaType = MediaType.APPLICATION_OCTET_STREAM;
		 * } else {
		 * }
		 */
		// mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	@GetMapping("/reportes/facturasrubroscaja")
	public ResponseEntity<Resource> reporteFacturaRubrosCaja(
			@RequestParam("v_dfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam("v_hfecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha,
			@RequestParam("c_feccrea") @DateTimeFormat(pattern = "yyyy-MM-dd") Date c_feccrea,
			@RequestParam("usuariocobro") Long usuariocobro)
			throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("c_feccrea", c_feccrea);
		params.put("usuariocobro", usuariocobro);
		params.put("fileName", "facturasCobradasRubrosCaja");
		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = MediaType.APPLICATION_PDF;
		;
		/*
		 * if (tipo == "excel") {
		 * mediaType = MediaType.APPLICATION_OCTET_STREAM;
		 * } else {
		 * }
		 */
		// mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	// FACTURAS ANULACIÓN
	@GetMapping("/anulaciones")
	public ResponseEntity<List<Facturas>> getFacturasAnuladas(@RequestParam("limit") Long limit) {
		List<Facturas> facturas = facServicio.fingAllFacturasAnuladas(limit);
		return ResponseEntity.ok(facturas);
	}

	@GetMapping("/cobradas/cliente")
	public ResponseEntity<List<Facturas>> getFacturasAnuladasxac(@RequestParam("idcliente") Long idcliente) {
		List<Facturas> facturas = facServicio.findCobradasByCliente(idcliente);
		return ResponseEntity.ok(facturas);
	}

	// FACTURAS ELIMINACIÓN
	@GetMapping("/eliminaciones")
	public ResponseEntity<List<Facturas>> getFacturasEliminadas(@RequestParam("limit") Long limit) {
		List<Facturas> facturas = facServicio.fingAllFacturasEliminadas(limit);
		return ResponseEntity.ok(facturas);
	}

}
