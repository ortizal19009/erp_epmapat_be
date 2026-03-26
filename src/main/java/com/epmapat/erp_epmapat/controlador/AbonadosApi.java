package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.AbonadoGeoUploadItemDto;
import com.epmapat.erp_epmapat.DTO.EstadisticasAbonadosDTO;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.AbonadoI;
import com.epmapat.erp_epmapat.interfaces.EstadisticasAbonados;
import com.epmapat.erp_epmapat.interfaces.FacturasSinCobroInter;
import com.epmapat.erp_epmapat.interfaces.mobile.AbonadosMobile;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.servicio.AbonadoServicio;

@RestController
@RequestMapping("/abonados")

public class AbonadosApi {

	@Autowired
	private AbonadoServicio aboServicio;

	/*
	 * @Autowired
	 * private ServiciosS serviciosS;
	 */
	@Autowired
	private FacturasR facturaR;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Abonados> getAllAbonados(@Param(value = "consulta") String consulta,
			@Param(value = "idcliente") Long idcliente, @Param(value = "idabonado") Long idabonado,
			@Param(value = "idruta") Long idruta) {
		if (idabonado != null) {
			return aboServicio.getAbonadoByid(idabonado);
		}
		if (idcliente != null) {
			return aboServicio.findByIdcliente(idcliente);
		}
		if (idruta != null) {
			return aboServicio.findByIdruta(idruta);
		} else {
			return aboServicio.findAll(consulta.toLowerCase(), Sort.by(Sort.Order.asc("nromedidor")));
		}
	}

	@GetMapping("/clienteTieneAbonados")
	public boolean clienteTieneAbonados(@Param(value = "idcliente") Long idcliente) {
		return aboServicio.clienteTieneAbonados(idcliente);
	}

	// Todos los Abonados, campos específicos
	@GetMapping("/campos")
	public List<Map<String, Object>> allAbonadosCampos() {
		return aboServicio.allAbonadosCampos();
	}

	@PostMapping
	public Abonados saveAbonados(@RequestBody Abonados x) {
		return aboServicio.save(x);
	}

	@GetMapping("/tmp")
	public List<Abonados> tmpTodos() {
		return aboServicio.tmpTodos();
	}

	@GetMapping("/ncliente/{nombre}")
	@ResponseStatus(HttpStatus.OK)
	public List<Abonados> getAbonadoxNcliente(@PathVariable("nombre") String nombreCliente) {
		return aboServicio.findByNombreCliente(nombreCliente.toLowerCase());
	}

	@GetMapping("/cuenta/{idabonado}")
	@ResponseStatus(HttpStatus.OK)
	public List<Abonados> getAbonadoByid(@PathVariable("idabonado") Long idabonado) {
		return aboServicio.getAbonadoByid(idabonado);
	}

	@GetMapping("/{idabonado}")
	public ResponseEntity<Abonados> getByIdAbonados(@PathVariable Long idabonado) {
		Abonados x = aboServicio.findById(idabonado)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe ese abonado con ese Id: " + idabonado)));
		return ResponseEntity.ok(x);
	}

	@GetMapping("/cliente")
	public ResponseEntity<List<Abonados>> getByIdCliente(@RequestParam("idcliente") Long idcliente) {
		return ResponseEntity.ok(aboServicio.findByIdCliente(idcliente));
	}

	@PutMapping("/{idabonado}")
	public ResponseEntity<Abonados> updateAbonados(
			@PathVariable Long idabonado,
			@RequestBody Abonados abonadosm,
			@RequestParam Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Actualización de abonado") String observacion) {

		Abonados updated = aboServicio.actualizarAbonadoConAuditoria(idabonado, abonadosm, usumodi, observacion, tipo);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping(value = "/{idabonado}")
	public ResponseEntity<Boolean> deleteAbonados(@PathVariable("idabonado") Long idabonado) {
		aboServicio.deleteById(idabonado);
		return ResponseEntity.ok(!(aboServicio.findById(idabonado) != null));
	}

	@GetMapping("/oneabonado")
	public ResponseEntity<Abonados> getOne(@RequestParam("idabonado") Long idabonado) {
		Abonados abonado = aboServicio.findOne(idabonado);
		return ResponseEntity.ok(abonado);

	}

	// Un Abonado
	@GetMapping("/unabonado")
	public Abonados unAbonado(@Param("idabonado") Long idabonado) {
		Abonados x = aboServicio.unAbonado(idabonado);
		if (x == null) {
			return null;
		}
		return x;
	}

	@GetMapping("/cuenta")
	@ResponseStatus(HttpStatus.OK)
	public List<Abonados> getByIdabonado(@Param(value = "idcliente") Long idabonado) {
		return aboServicio.getByIdabonado(idabonado);
	}

	@GetMapping("/icliente/{identificacion}")
	@ResponseStatus(HttpStatus.OK)
	public List<Abonados> getAbonadoxIcliente(@PathVariable("identificacion") String identificacionCliente) {
		return aboServicio.findByidentIficacionCliente(identificacionCliente);
	}

	@GetMapping("/resabonado")
	public ResponseEntity<List<AbonadoI>> getAbonadoInterface(@RequestParam Long idabonado) {
		return ResponseEntity.ok(aboServicio.getAbonadoInterface(idabonado));
	}

	@GetMapping("/resabonado/nombre")
	public ResponseEntity<List<AbonadoI>> getAbonadoInterfaceNombre(@RequestParam String nombre) {
		return ResponseEntity.ok(aboServicio.getAbonadoInterfaceNombre(nombre.toLowerCase()));
	}

	@GetMapping("/resabonado/identificacion")
	public ResponseEntity<List<AbonadoI>> getAbonadoInterfaceIdentificacion(@RequestParam String identificacion) {
		return ResponseEntity.ok(aboServicio.getAbonadoInterfaceIdentificacion(identificacion));
	}

	@GetMapping("/resabonado/idcliente")
	public ResponseEntity<List<AbonadoI>> findAbonadoInterfaceIdCliente(@RequestParam Long idcliente) {
		return ResponseEntity.ok(aboServicio.getAbonadoInterfaceIdCliente(idcliente));
	}
	/*
	 * @PutMapping("/{idabonado}/s/{idservicio}")
	 * public Abonados addServxAbo(@PathVariable Long idabonado, @PathVariable Long
	 * idservicio) {
	 * Abonados abonadoM = aboServicio.findById(idabonado).get();
	 * ServiciosM serviciosM = serviciosS.findById(idservicio).get();
	 * abonadoM.addServicio(serviciosM);
	 * return aboServicio.save(abonadoM);
	 * }
	 */

	@GetMapping("/deudas")
	public ResponseEntity<List<ValorFactDTO>> getTotatesAbonadosByRuta(@RequestParam Long idruta) {
		return ResponseEntity.ok(aboServicio.getCuentasByRutas(idruta));
	}

	@GetMapping("/deudasByRuta")
	public ResponseEntity<List<FacturasSinCobroInter>> getDeudasOfAbonadosByRutas(@RequestParam Long idruta) {
		return ResponseEntity.ok(facturaR.findDeudasOfAbonadosByRutas(idruta));
	}

	@GetMapping("/ncuentasByCategoria")
	public List<EstadisticasAbonados> getCuentasByCategoria() {
		return aboServicio.getCuentasByCategoria();
	}

	@GetMapping("/ncuentasByEstado")
	public List<EstadisticasAbonadosDTO> getCuentasByEstado() {
		return aboServicio.getCuentasByEstado();
	}

	@GetMapping("/estado/{estado}")
	public ResponseEntity<List<Abonados>> getAbonadosByEstado(@PathVariable Long estado) {
		return ResponseEntity.ok(aboServicio.findByEstado(estado));
	}

	@GetMapping("/estado/{estado}/pageable")
	public ResponseEntity<Page<Abonados>> getAbonadosByEstadoPageable(@PathVariable Long estado, @PageableDefault(size = 20) Pageable pageable) {
		return ResponseEntity.ok(aboServicio.findByEstado(estado, pageable));
	}

	@GetMapping("/categoria/{idcategoria}")
	public ResponseEntity<List<Abonados>> getAbonadosByCategoria(@PathVariable Long idcategoria) {
		return ResponseEntity.ok(aboServicio.findByIdcategoria(idcategoria));
	}

	@GetMapping("/categoria/{idcategoria}/pageable")
	public ResponseEntity<Page<Abonados>> getAbonadosByCategoriaPageable(@PathVariable Long idcategoria, @PageableDefault(size = 20) Pageable pageable) {
		return ResponseEntity.ok(aboServicio.findByIdcategoria(idcategoria, pageable));
	}

	@GetMapping("/ruta/{idruta}")
	public ResponseEntity<List<Abonados>> getAbonadosByRuta(@PathVariable Long idruta) {
		return ResponseEntity.ok(aboServicio.findByIdruta(idruta));
	}

	@GetMapping("/ruta/{idruta}/pageable")
	public ResponseEntity<Page<Abonados>> getAbonadosByRutaPageable(@PathVariable Long idruta, @PageableDefault(size = 20) Pageable pageable) {
		return ResponseEntity.ok(aboServicio.findByIdruta(idruta, pageable));
	}

	@GetMapping("/buscar")
	public Page<Abonados> buscar(
			@RequestParam(required = false) Long idruta,
			@RequestParam(required = false) String responsable,
			@RequestParam(required = false) Long estado,
			@RequestParam(required = false) String cedula,
			@RequestParam(required = false) Long cuenta,
			@RequestParam(required = false) String ruta,
			@PageableDefault(size = 20) Pageable pageable) {

		return aboServicio.buscar(idruta, responsable, estado, cedula, cuenta, ruta, pageable);
	}

	/*
	 * =============================================================
	 * QUERYS PARA MOBILE
	 * =============================================================
	 */
	@GetMapping("/allabonadosmobile")
	public List<AbonadosMobile> getAllAbonadosMobile() {
		return aboServicio.getAllAbonadosMobile();
	}

	@PostMapping("/mobile/upload-geolocalizacion")
	public ResponseEntity<java.util.Map<String, Integer>> uploadGeolocalizacionMobile(
			@RequestBody List<AbonadoGeoUploadItemDto> items,
			@RequestParam Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Upload geolocalizacion") String observacion) {
		int ok = 0;
		int err = 0;

		for (AbonadoGeoUploadItemDto item : items) {
			try {
				if (item.getIdabonado() == null)
					throw new IllegalArgumentException("idabonado requerido");
				aboServicio.actualizarGeolocalizacionConAuditoria(item.getIdabonado(), item.getGeolocalizacion(), usumodi, observacion, tipo);
				ok++;
			} catch (Exception ex) {
				err++;
			}
		}

		java.util.Map<String, Integer> out = new java.util.HashMap<>();
		out.put("ok", ok);
		out.put("error", err);
		out.put("total", items.size());
		return ResponseEntity.ok(out);
	}
}
