package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.epmapat.erp_epmapat.DTO.EstadisticasAbonadosDTO;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.AbonadoI;
import com.epmapat.erp_epmapat.interfaces.EstadisticasAbonados;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.servicio.AbonadoServicio;

@RestController
@RequestMapping("/abonados")
@CrossOrigin(origins = "*")

public class AbonadosApi {

	@Autowired
	private AbonadoServicio aboServicio;

	/*
	 * @Autowired
	 * private ServiciosS serviciosS;
	 */

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
	public ResponseEntity<Abonados> updateAbonados(@PathVariable Long idabonado, @RequestBody Abonados abonadosm) {
		Abonados abonadosM = aboServicio.findById(idabonado)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe ese abonado con ese Id: " + idabonado)));
		abonadosM.setNromedidor(abonadosm.getNromedidor());
		abonadosM.setLecturainicial(abonadosm.getLecturainicial());
		abonadosM.setEstado(abonadosm.getEstado());
		abonadosM.setFechainstalacion(abonadosm.getFechainstalacion());
		abonadosM.setMarca(abonadosm.getMarca());
		abonadosM.setSecuencia(abonadosm.getSecuencia());
		abonadosM.setDireccionubicacion(abonadosm.getDireccionubicacion());
		abonadosM.setLocalizacion(abonadosm.getLocalizacion());
		abonadosM.setObservacion(abonadosm.getObservacion());
		abonadosM.setDepartamento(abonadosm.getDepartamento());
		abonadosM.setPiso(abonadosm.getPiso());
		abonadosM.setIdresponsable(abonadosm.getIdresponsable());
		abonadosM.setIdcategoria_categorias(abonadosm.getIdcategoria_categorias());
		abonadosM.setIdruta_rutas(abonadosm.getIdruta_rutas());
		abonadosM.setIdcliente_clientes(abonadosm.getIdcliente_clientes());
		abonadosM.setIdubicacionm_ubicacionm(abonadosm.getIdubicacionm_ubicacionm());
		abonadosM.setIdtipopago_tipopago(abonadosm.getIdtipopago_tipopago());
		abonadosM.setIdestadom_estadom(abonadosm.getIdestadom_estadom());
		abonadosM.setMedidorprincipal(abonadosm.getMedidorprincipal());
		abonadosM.setUsucrea(abonadosm.getUsucrea());
		abonadosM.setFeccrea(abonadosm.getFeccrea());
		abonadosM.setUsumodi(abonadosm.getUsumodi());
		abonadosM.setFecmodi(abonadosm.getFecmodi());
		abonadosM.setAdultomayor(abonadosm.getAdultomayor());
		abonadosM.setMunicipio(abonadosm.getMunicipio());
		abonadosM.setSwalcantarillado(abonadosm.getSwalcantarillado());
		abonadosM.setGeolocalizacion(abonadosm.getGeolocalizacion());
		Abonados updateAbonado = aboServicio.save(abonadosM);
		return ResponseEntity.ok(updateAbonado);
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
}
