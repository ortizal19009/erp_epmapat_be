package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.EmisionOfCuentaDTO;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.ConsumoxCat_int;
import com.epmapat.erp_epmapat.interfaces.CountRubrosByEmision;
import com.epmapat.erp_epmapat.interfaces.FecEmision;
import com.epmapat.erp_epmapat.interfaces.RepEmisionEmi;
import com.epmapat.erp_epmapat.interfaces.RepFacEliminadasByEmision;
import com.epmapat.erp_epmapat.interfaces.RubroxfacIReport;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.servicio.LecturaServicio;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/lecturas")
@CrossOrigin("*")

public class LecturasApi {

	@Autowired
	LecturaServicio lecServicio;

	// Busca por Planilla (Es una a una)
	@GetMapping("/onePlanilla/{idfactura}")
	public Lecturas getOnefactura(@PathVariable Long idfactura) {
		return lecServicio.findOnefactura(idfactura);
	}

	@GetMapping
	public List<Lecturas> getByIdemision(@Param(value = "idemision") Long idrutaxemision,
			@Param(value = "idabonado") Long idabonado, @Param(value = "limit") Long limit) {
		if (idrutaxemision != null) {
			return lecServicio.findByIdrutaxemision(idrutaxemision);
		} else {
			if (idabonado != null) {
				return lecServicio.findByIdabonado(idabonado, limit);
			}
			return null;
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

	@PostMapping
	public Lecturas saveLectura(@RequestBody Lecturas x) {
		return lecServicio.saveLectura(x);
	}

	@PutMapping("/{idlectura}")
	public ResponseEntity<Lecturas> update(@PathVariable Long idlectura, @RequestBody Lecturas x) {
		Lecturas y = lecServicio.findById(idlectura)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe la Lectura Id: " + idlectura)));
		y.setEstado(x.getEstado());
		y.setFechaemision(x.getFechaemision());
		y.setLecturaanterior(x.getLecturaanterior());
		y.setLecturaactual(x.getLecturaactual());
		y.setLecturadigitada(x.getLecturadigitada());
		y.setMesesmulta(x.getMesesmulta());
		y.setObservaciones(x.getObservaciones());
		y.setIdnovedad_novedades(x.getIdnovedad_novedades());
		y.setIdemision(x.getIdemision());
		y.setIdabonado_abonados(x.getIdabonado_abonados());
		y.setIdresponsable(x.getIdresponsable());
		y.setIdcategoria(x.getIdcategoria());
		y.setIdrutaxemision_rutasxemision(x.getIdrutaxemision_rutasxemision());
		y.setIdfactura(x.getIdfactura());
		y.setTotal1(x.getTotal1());
		y.setTotal31(x.getTotal31());
		y.setTotal32(x.getTotal32());

		Lecturas actualizar = lecServicio.saveLectura(y);
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

	@PostMapping("/valoresEmisiones")
	public ResponseEntity<BigDecimal> getValoresEmision(@RequestBody EmisionOfCuentaDTO datos) {
		return ResponseEntity.ok(lecServicio.calcularValores(datos.getCuenta(), datos.getIdfactura(), datos.getM3(),
				datos.getCategoria(), datos.isSwMunicipio(), datos.isSwAdultoMayor(), datos.isSwAguapotable()));
	}
}
