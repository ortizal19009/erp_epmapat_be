package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.util.List;

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

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
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
			@Param(value = "idabonado") Long idabonado) {
		if (idrutaxemision != null) {
			return lecServicio.findByIdrutaxemision(idrutaxemision);
		} else {
			if (idabonado != null) {
				return lecServicio.findByIdabonado(idabonado);
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
	public Lecturas findByIdemisionIdAbonado(@PathVariable Long idemision, @PathVariable Long idabonado) {
		return lecServicio.findByIdemisionIdAbonado(idemision, idabonado);
	}

	// Ultima lectura de un Abonado
	@GetMapping("/ultimalectura")
	public Long getUltimaLectura(@Param(value = "idabonado") Long idabonado) {
		return lecServicio.ultimaLectura(idabonado);
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

}
