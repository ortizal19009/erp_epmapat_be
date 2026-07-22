package com.epmapat.erp_epmapat.controlador;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import com.epmapat.erp_epmapat.DTO.EmisionOfCuentaDTO;
import com.epmapat.erp_epmapat.DTO.CierreRutaReporteDTO;
import com.epmapat.erp_epmapat.DTO.LecturaDto;
import com.epmapat.erp_epmapat.DTO.LecturaUploadItemDto;
import com.epmapat.erp_epmapat.DTO.LecturasByRutasRequest;
import com.epmapat.erp_epmapat.DTO.LecturasByUsuarioEmisionRequest;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.ConsumoxCat_int;
import com.epmapat.erp_epmapat.interfaces.CountRubrosByEmision;
import com.epmapat.erp_epmapat.interfaces.EmisionesInterface;
import com.epmapat.erp_epmapat.interfaces.FecEmision;
import com.epmapat.erp_epmapat.interfaces.RepEmisionEmi;
import com.epmapat.erp_epmapat.interfaces.RepFacEliminadasByEmision;
import com.epmapat.erp_epmapat.interfaces.RubroxfacIReport;
import com.epmapat.erp_epmapat.mappers.LecturaMapper;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.NovedadR;
import com.epmapat.erp_epmapat.repositorio.RutasxemisionR;
import com.epmapat.erp_epmapat.servicio.EmisionServicioOptimizado;
import com.epmapat.erp_epmapat.servicio.EmisionServicioOptimizadoV2;
import com.epmapat.erp_epmapat.servicio.EmisionServicioOptimizado_anterior;
import com.epmapat.erp_epmapat.servicio.LecturaFotoStorageService;
import com.epmapat.erp_epmapat.servicio.LecturaServicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/lecturas")
public class LecturasApi {

	private static final String NEXTCLOUD_LECTURAS_FOLDER = "LECTURAS";

	private final LecturaServicio lecServicio;
	private final EmisionServicioOptimizado emisionServicioOptimizado;
	private final EmisionServicioOptimizadoV2 emisionServicioOptimizadoV2;
	private final EmisionServicioOptimizado_anterior emisionServicioOptimizado_anterior;
	private final AbonadosR abonadosR;
	private final NovedadR novedadR;
	private final RutasxemisionR rutasxemisionR;
	private final LecturaFotoStorageService lecturaFotoStorageService;

	private String resolveIncomingFotoPath(LecturaUploadItemDto item, Lecturas original) {
		String incomingFotoPath = item.getFotoPath();
		if (!StringUtils.hasText(incomingFotoPath)) {
			return original == null ? null : original.getFotoPath();
		}

		String normalized = incomingFotoPath.trim();
		if (normalized.startsWith("content://")
				|| normalized.startsWith("/")
				|| normalized.startsWith("\\")) {
			return original == null ? null : original.getFotoPath();
		}

		if (normalized.matches("^[a-zA-Z]:[\\\\/].*")) {
			return original == null ? null : original.getFotoPath();
		}

		return normalized;
	}

	private String resolveLecturaFolder(Lecturas lectura) {
		if (lectura.getIdrutaxemision_rutasxemision() == null
				|| lectura.getIdrutaxemision_rutasxemision().getIdruta_rutas() == null) {
			return NEXTCLOUD_LECTURAS_FOLDER + "/sin-ruta";
		}

		String codigoRuta = lectura.getIdrutaxemision_rutasxemision().getIdruta_rutas().getCodigo();
		Long idruta = lectura.getIdrutaxemision_rutasxemision().getIdruta_rutas().getIdruta();
		String folderName = StringUtils.hasText(codigoRuta) ? codigoRuta : "ruta-" + idruta;
		folderName = folderName.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
		return NEXTCLOUD_LECTURAS_FOLDER + "/" + folderName;
	}

	private ResponseEntity<Resource> buildImageResponse(Resource resource, String storedPath) throws IOException {
		if (resource == null || !resource.exists() || !resource.isReadable()) {
			return ResponseEntity.notFound().build();
		}
		String filename = resource.getFilename();
		if (!StringUtils.hasText(filename)) {
			filename = StringUtils.getFilename(storedPath);
		}
		MediaType contentType = MediaTypeFactory.getMediaType(filename)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
		return ResponseEntity.ok()
				.contentType(contentType)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
				.body(resource);
	}

	// Busca por Planilla (Es una a una)
	@GetMapping("/onePlanilla/{idfactura}")
	public Lecturas getOnefactura(@PathVariable Long idfactura) {
		return lecServicio.findOnefactura(idfactura);
	}

	@GetMapping
	public List<Lecturas> getByIdemision(@RequestParam(required = false) Long idrutaxemision,
			@RequestParam(required = false) Long idabonado, @RequestParam(required = false) Long limit) {
		if (idrutaxemision != null) {
			return lecServicio.findByIdrutaxemision(idrutaxemision);
		} else {
			if (idabonado != null) {
				return lecServicio.findByIdabonado(idabonado, limit);
			}
			return List.of();
		}
	}

	@GetMapping("/rutasxemision/{idrutaxemision}")
	public List<Lecturas> getByIdRutaxEmision(@PathVariable Long idrutaxemision) {
		return lecServicio.findByIdRutasxEmision(idrutaxemision);
	}

	@GetMapping("/ruta/{idruta}")
	public List<Lecturas> getByIdRuta(@PathVariable Long idruta) {
		return lecServicio.findByRutas(idruta);
	}

	@GetMapping("/lbam/{idabonado}")
	public List<Lecturas> getByIdAbonado(@PathVariable Long idabonado) {
		return lecServicio.findByIdAbonado(idabonado);
	}

	@GetMapping("/lbncm/{nombre}")
	public List<Lecturas> getByNCliente(@PathVariable String nombre) {
		return lecServicio.findByNCliente(nombre);
	}

	@GetMapping("/lbicm/{cedula}")
	public List<Lecturas> getByICliente(@PathVariable String cedula) {
		return lecServicio.findByNCliente(cedula);
	}

	// Busca por Planilla (Es una a una)
	@GetMapping("/planilla/{idfactura}")
	public List<Lecturas> getByIdfactura(@PathVariable Long idfactura) {
		return lecServicio.findByIdfactura(idfactura);
	}

	@GetMapping("/{idlectura}")
	public ResponseEntity<Lecturas> getByIdlectura(@PathVariable Long idlectura) {
		Lecturas lectura = lecServicio.findById(idlectura)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe la Lectura con Id: " + idlectura)));
		return ResponseEntity.ok(lectura);
	}

	// Lecturas de una Emision
	@GetMapping("/emision/{idemision}")
	public List<Lecturas> getByIdemision(@PathVariable Long idemision) {
		return lecServicio.findByIdemision(idemision);
	}

	@GetMapping("/emision/{idemision}/{idabonado}")
	public List<Lecturas> findByIdemisionIdAbonado(@PathVariable Long idemision, @PathVariable Long idabonado) {
		return lecServicio.findByIdemisionIdAbonado(idemision, idabonado);
	}

	// Ultima lectura de un Abonado
	@GetMapping("/ultimalectura")
	public Long getUltimaLectura(@Param(value = "idabonado") Long idabonado) {
		return lecServicio.ultimaLectura(idabonado);
	}

	@GetMapping("/ultimalecturaByemision")
	public Long ultimaLecturaByIdemision(@Param(value = "idabonado") Long idabonado,
			@Param("idemision") Long idemision) {
		return lecServicio.ultimaLecturaByIdemision(idabonado, idemision);
	}

	@PostMapping(value = "/{idlectura}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadLecturaFoto(@PathVariable Long idlectura,
			@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
			@RequestParam(required = false, defaultValue = "0") Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Upload foto de lectura") String observacion) throws IOException {
		Lecturas lectura = lecServicio.findById(idlectura)
				.orElseThrow(() -> new com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones(
					"No existe la Lectura con Id: " + idlectura));

		try {
			String fotoPath = lecturaFotoStorageService.saveImageFile(idlectura, file, resolveLecturaFolder(lectura));
			lectura = lecServicio.actualizarFotoConAuditoria(idlectura, fotoPath, usumodi, observacion, tipo);
		} catch (IOException ex) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(Map.of(
							"message", ex.getMessage(),
							"detail", ex.getCause() != null ? ex.getCause().getMessage() : "sin detalle",
							"idlectura", idlectura));
		}

		return ResponseEntity.ok(LecturaMapper.toDto(lectura));
	}

	@GetMapping("/{idlectura}/foto")
	public ResponseEntity<Resource> getLecturaFoto(@PathVariable Long idlectura) throws IOException {
		Lecturas lectura = lecServicio.findById(idlectura)
				.orElseThrow(() -> new com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones(
					"No existe la Lectura con Id: " + idlectura));
		if (lectura.getFotoPath() == null) {
			return ResponseEntity.notFound().build();
		}
		return buildImageResponse(lecturaFotoStorageService.loadAsResource(lectura.getFotoPath()), lectura.getFotoPath());
	}

	@PostMapping
	public Lecturas saveLectura(@RequestBody Lecturas x) {
		return lecServicio.saveLectura(x);
	}

	@PutMapping("/{idlectura}")
	public ResponseEntity<Lecturas> update(@PathVariable Long idlectura, @RequestBody Lecturas x,
			@RequestParam(required = false, defaultValue = "0") Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Actualización de lectura") String observacion) {
		Lecturas actualizar = lecServicio.actualizarLecturaConAuditoria(idlectura, x, usumodi, observacion, tipo);
		return ResponseEntity.ok(actualizar);
	}

	private Lecturas buildLecturaFromMobileItem(Long idlectura, LecturaUploadItemDto item) {
		Lecturas original = idlectura == null ? null : lecServicio.findById(idlectura).orElse(null);
		Lecturas lectura = new Lecturas();
		lectura.setIdlectura(item.getIdlectura());
		lectura.setEstado(item.getEstado());
		lectura.setFechaemision(item.getFechaemision());
		lectura.setFechalectura(item.getFechalectura());
		lectura.setLecturaanterior(item.getLecturaanterior());
		lectura.setLecturaactual(item.getLecturaactual());
		lectura.setLecturadigitada(item.getLecturadigitada());
		lectura.setMesesmulta(item.getMesesmulta());
		lectura.setObservaciones(item.getObservaciones());
		lectura.setIdemision(item.getIdemision());
		lectura.setIdcategoria(item.getIdcategoria());
		lectura.setIdresponsable(original == null ? null : original.getIdresponsable());
		lectura.setUsuariolectura(item.getUsuariolectura());
		lectura.setIdfactura(item.getIdfactura());
		lectura.setUsumodi(item.getUsumodi());
		lectura.setFecmodi(item.getFecmodi());
		lectura.setTotal1(item.getTotal1());
		lectura.setTotal31(item.getTotal31());
		lectura.setTotal32(item.getTotal32());
		lectura.setFotoPath(resolveIncomingFotoPath(item, original));
		lectura.setIdabonado_abonados(item.getIdabonado_abonados() == null
				? (original == null ? null : original.getIdabonado_abonados())
				: abonadosR.findById(item.getIdabonado_abonados()).orElse(null));
		lectura.setIdnovedad_novedades(item.getIdnovedad() == null
				? (original == null ? null : original.getIdnovedad_novedades())
				: novedadR.findById(item.getIdnovedad()).orElse(null));
		lectura.setIdrutaxemision_rutasxemision(item.getIdrutaxemision_rutasxemision() == null
				? (original == null ? null : original.getIdrutaxemision_rutasxemision())
				: rutasxemisionR.findById(item.getIdrutaxemision_rutasxemision()).orElse(null));
		return lectura;
	}

	@PutMapping("/{idlectura}/mobile")
	public ResponseEntity<Lecturas> updateMobile(@PathVariable Long idlectura, @RequestBody LecturaUploadItemDto item,
			@RequestParam(required = false, defaultValue = "0") Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Actualización de lectura desde mobile") String observacion) {
		Lecturas actualizar = lecServicio.actualizarLecturaConAuditoria(
				idlectura,
				buildLecturaFromMobileItem(idlectura, item),
				usumodi,
				observacion,
				tipo);
		return ResponseEntity.ok(actualizar);
	}

	/* obtener la suma de una emision */
	@GetMapping("/emision/totalsuma")
	public ResponseEntity<BigDecimal> totalEmisionXFactura(@RequestParam("idemision") Long idemision) {
		return ResponseEntity.ok(lecServicio.totalEmisionXFactura(idemision));
	}

	/* obtener la suma de una emision */
	@GetMapping("/emision/rubros")
	public ResponseEntity<List<Object[]>> rubrosEmitidos(@RequestParam("idemision") Long idemision) {
		return ResponseEntity.ok(lecServicio.RubrosEmitidos(idemision));
	}

	@GetMapping("/reportes/emisionfinal")
	public ResponseEntity<List<Object[]>> R_EmisionFinal(@RequestParam("idemision") Long idemision) {
		return ResponseEntity.ok(lecServicio.R_EmisionFinal(idemision));

	}

	@GetMapping("/reportes/emisionactual")
	public ResponseEntity<List<Object[]>> R_EmisionActual(@RequestParam("idemision") Long idemision) {
		return ResponseEntity.ok(lecServicio.R_EmisionActual(idemision));
	}

	@GetMapping("/reportes/deudasxruta")
	public ResponseEntity<List<Lecturas>> findDeudoresByRuta(@RequestParam("idruta") Long idruta) {
		return ResponseEntity.ok(lecServicio.findDeudoresByRuta(idruta));
	}

	@GetMapping("/fecEmision")
	public ResponseEntity<Date> findDateByIdfactura(@RequestParam("idfactura") Long idfactura) {
		return ResponseEntity.ok(lecServicio.findDateByIdfactura(idfactura));
	}

	@GetMapping("/fecemision")
	public ResponseEntity<List<FecEmision>> findEmisionByIdfactura(@RequestParam("idfactura") Long idfactura) {
		return ResponseEntity.ok(lecServicio.getEmisionByIdfactura(idfactura));
	}

	@GetMapping("/emision")
	public ResponseEntity<List<Lecturas>> getByIdEmisiones(@RequestParam Long idemision) {
		return ResponseEntity.ok(lecServicio.findByIdEmisiones(idemision));
	}

	@GetMapping("reporte/emision")
	public ResponseEntity<List<RepFacEliminadasByEmision>> getByIdEmisionesR(@RequestParam Long idemision) {
		return ResponseEntity.ok(lecServicio.findByIdEmisionesR(idemision));
	}

	@GetMapping("/reportes/rubros/inicial")
	public CompletableFuture<List<RubroxfacIReport>> getAllRubrosEmisionInicial(@RequestParam Long idemision) {
		return lecServicio.getAllRubrosEmisionInicial(idemision);
	}

	@GetMapping("/reportes/rubros/inicial/cm3")
	public CompletableFuture<List<RubroxfacIReport>> getCuentaM3AllEmiInicial(@RequestParam Long idemision) {
		return lecServicio.getCuentaM3AllEmiInicial(idemision);
	}

	@GetMapping("/reportes/rubros/nuevos")
	public CompletableFuture<List<RubroxfacIReport>> getAllNewLecturas(@RequestParam Long idemision) {
		return lecServicio.getAllNewLecturas(idemision);
	}

	@GetMapping("/reportes/rubros/eliminados")
	public CompletableFuture<List<RubroxfacIReport>> getAllDeleteLecturas(@RequestParam Long idemision) {
		return lecServicio.getAllDeleteLecturas(idemision);
	}

	@GetMapping("/reportes/rubros/actual")
	public CompletableFuture<List<RubroxfacIReport>> getAllActual(@RequestParam Long idemision) {
		return lecServicio.getAllActual(idemision);
	}

	@GetMapping("/reportes/valoresEmitidos")
	public ResponseEntity<List<RepEmisionEmi>> findReporteValEmitidosxEmision(@RequestParam Long idemision) {
		return ResponseEntity.ok(lecServicio.getReporteValEmitidosxEmision(idemision));
	}

	@GetMapping("/reportes/consumoxcategoria")
	public ResponseEntity<List<ConsumoxCat_int>> getConsumoxCategoria(@RequestParam Long idemision) {
		return ResponseEntity.ok(lecServicio.getConsumoxCategoria(idemision));
	}

	@GetMapping("/reportes/rubrozero")
	public ResponseEntity<List<CountRubrosByEmision>> getCuentaRubrosByEmision(@RequestParam Long idemision) {
		return ResponseEntity.ok(lecServicio.getCuentaRubrosByEmision(idemision));
	}

	@GetMapping("/reportes/cierre-ruta")
	public ResponseEntity<CierreRutaReporteDTO> getReporteCierreRuta(@RequestParam Long idrutaxemision) {
		return ResponseEntity.ok(lecServicio.getReporteCierreRuta(idrutaxemision));
	}

	@PostMapping("/valoresEmisiones")
	public ResponseEntity<BigDecimal> calcularValoresEmision(@RequestBody EmisionOfCuentaDTO datos) {
		return ResponseEntity.ok(
				emisionServicioOptimizado.calcularValores(
						datos.getIdemision(), // ✅ NUEVO
						datos.getCuenta(),
						datos.getIdfactura(),
						datos.getM3(),
						datos.getCategoria(),
						datos.isSwMunicipio(),
						datos.isSwAdultoMayor(),
						datos.isSwAguapotable(),
						datos.isSwRefacturacion()));
	}

	@PostMapping("/valoresemisionesanteriores")
	public ResponseEntity<BigDecimal> recalcularValoresEmisionAnterior(@RequestBody EmisionOfCuentaDTO datos) {

		return ResponseEntity.ok(
				emisionServicioOptimizado_anterior.calcularValores(
						datos.getCuenta(),
						datos.getIdfactura(),
						datos.getM3(),
						datos.getCategoria(),
						datos.isSwMunicipio(),
						datos.isSwAdultoMayor(),
						datos.isSwAguapotable(),
						datos.isSwRefacturacion()));
	}

	@GetMapping("/swalcantarillado")
	public List<EmisionesInterface> getSWalcatarillados(@RequestParam Long idemision) {
		return lecServicio.getSWalcatarillados(idemision);
	}

	@GetMapping("/duplicatos-emision")
	public ResponseEntity<List<EmisionesInterface>> getDuplicatosToEmision(@RequestParam Long idemision,
			@RequestParam Long top) {
		return ResponseEntity.ok(lecServicio.getDuplicadosToRecalculate(idemision, top));
	}

	@GetMapping("/preview/lecturas/{idcliente}")
	public List<Lecturas> previewLecturas(@PathVariable Long idcliente) {
		return lecServicio.findPendientesByCliente(idcliente);
	}

	@GetMapping("/simular")
	public ResponseEntity<Object> simularValores(@RequestParam int m3, @RequestParam int categoria,
			@RequestParam boolean swMunicipio,
			@RequestParam boolean swAdultoMayor, @RequestParam boolean swAguapotable) {
		return ResponseEntity
				.ok(emisionServicioOptimizado.simularValores(m3, categoria, swMunicipio, swAdultoMayor, swAguapotable));
	}

	@PostMapping("/valoresEmisiones/v2")
	public ResponseEntity<BigDecimal> calcularValoresEmisionV2(@RequestBody EmisionOfCuentaDTO datos) {
		BigDecimal resultado = emisionServicioOptimizadoV2.calcularValores(
				datos.getIdemision(),
				datos.getCuenta(),
				datos.getIdfactura(),
				datos.getM3(),
				datos.getCategoria(),
				datos.isSwMunicipio(),
				datos.isSwAdultoMayor(),
				datos.isSwAguapotable(),
				datos.isSwbasura(),
				datos.isSwRefacturacion());
		return ResponseEntity.ok(
				resultado);

	}

	@GetMapping("/simular/v2")
	public ResponseEntity<Object> simularValoresV2(@RequestParam int m3, @RequestParam int categoria,
			@RequestParam boolean swMunicipio,
			@RequestParam boolean swAdultoMayor, @RequestParam boolean swAguapotable) {
		return ResponseEntity
				.ok(emisionServicioOptimizadoV2.simularValores(m3, categoria, swMunicipio, swAdultoMayor,
						swAguapotable));
	}

	/*
	 * ====================================================================
	 * ENDPOINTS PARA MOBILE
	 * =====================================================================
	 */

	@PostMapping("/mobile/upload")
	public ResponseEntity<java.util.Map<String, Object>> uploadLecturasMobile(
			@RequestBody List<LecturaUploadItemDto> items,
			@RequestParam(required = false) Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Upload lecturas mobile") String observacion) {
		int ok = 0;
		int err = 0;
		java.util.List<String> details = new java.util.ArrayList<>();

		for (LecturaUploadItemDto item : items) {
			try {
				if (item.getIdlectura() == null) {
					err++;
					details.add("Lectura sin idlectura");
					continue;
				}

				Lecturas y = buildLecturaFromMobileItem(item.getIdlectura(), item);

				if (usumodi != null) {
					lecServicio.actualizarLecturaConAuditoria(item.getIdlectura(), y, usumodi, observacion, tipo);
				} else {
					lecServicio.saveLectura(y);
				}
				ok++;
			} catch (Exception ex) {
				err++;
				String detail = "Lectura " + item.getIdlectura() + ": " + ex.getMessage();
				details.add(detail);
				log.warn("Error actualizando lectura mobile {}: {}", item.getIdlectura(), ex.getMessage(), ex);
			}
		}

		java.util.Map<String, Object> out = new LinkedHashMap<>();
		out.put("ok", ok);
		out.put("error", err);
		out.put("total", items.size());
		out.put("details", details);
		return ResponseEntity.ok(out);
	}

	@PostMapping("/by-rutas")
	@Deprecated
	public ResponseEntity<List<LecturaDto>> downloadByRutasxEmisionIds(
			@RequestBody LecturasByRutasRequest request) {
		// Endpoint deprecado: usar /by-usuario-emision
		return ResponseEntity.status(410).build();
	}

	@PostMapping("/by-usuario-emision")
	public ResponseEntity<List<LecturaDto>> downloadByUsuarioEmision(
			@RequestBody LecturasByUsuarioEmisionRequest request) {
		List<LecturaDto> lecturas = lecServicio.downloadByUsuarioEmision(request.getIdusuario(),
				request.getIdemision());
		if (lecturas.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(lecturas);
	}

	@DeleteMapping("/emision")
	public ResponseEntity<Void> deleteRubrosByIdEmisin(@RequestParam Long idemision) {
		lecServicio.deleteRubrosByIdEmisin(idemision);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/findMultas")
	public ResponseEntity<BigDecimal> findMultas(@RequestParam Long cuenta) {
		BigDecimal multa = emisionServicioOptimizadoV2.multas(cuenta);
		return ResponseEntity.ok(multa);
	}
}
