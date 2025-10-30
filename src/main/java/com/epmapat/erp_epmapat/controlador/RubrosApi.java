package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.servicio.RubroServicio;

@RestController
@RequestMapping("/rubros")
@CrossOrigin(origins = "*")

public class RubrosApi {

	@Autowired
	private RubroServicio rubServicio;

	@GetMapping
	public List<Rubros> getAllLista(@Param(value = "idmodulo") Long idmodulo,
			@Param(value = "descripcion") String descripcion) {
		if (idmodulo != null && descripcion != null) {
			return rubServicio.findByNombre(idmodulo, descripcion.toLowerCase());
		} else {
			return rubServicio.findAll();
		}
	}

	// Rubros por módulo (Sección)
	@GetMapping("/modulo/{idmodulo}")
	public List<Rubros> findByIdmodulo(@PathVariable Long idmodulo) {
		return rubServicio.findByIdmodulo(idmodulo);
	}

	@GetMapping("/{idrubro}")
	public ResponseEntity<Rubros> getByIdRubros(@PathVariable("idrubro") Long idrubro) {
		Rubros rubros = rubServicio.findById(idrubro)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe el rubro con Id: " + idrubro));
		return ResponseEntity.ok(rubros);
	}

	// Por Módulo y Descripción
	@GetMapping("/idmodulo")
	public List<Rubros> valNombre(@Param(value = "idmodulo") Long idmodulo,
			@Param(value = "descripcion") String descripcion) {
		if (idmodulo != null && descripcion != null) {
			return rubServicio.findByModulo(idmodulo, descripcion.toLowerCase());
		} else
			return null;
	}

	// Rubros de la Emisión (idmodulo=4)
	@GetMapping("/emision")
	public List<Rubros> findEmision() {
		return rubServicio.findEmision();
	}

	@PostMapping
	public ResponseEntity<Rubros> save(@RequestBody Rubros rubros) {
		return ResponseEntity.ok(rubServicio.save(rubros));
	}

	@PutMapping("/{idrubro}")
	public ResponseEntity<Rubros> update(@PathVariable Long idrubro, @RequestBody Rubros x) {
		Rubros y = rubServicio.findById(idrubro)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe el Rubro con Id: " + idrubro)));
		y.setIdmodulo_modulos(x.getIdmodulo_modulos());
		y.setDescripcion(x.getDescripcion());
		y.setEstado(x.getEstado());
		y.setValor(x.getValor());
		y.setCalculable(x.getCalculable());
		y.setSwiva(x.getSwiva());
		y.setTipo(x.getTipo());
		y.setFacturable(x.getFacturable());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());

		Rubros actualizar = rubServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}

	@GetMapping("/findByName")
	public ResponseEntity<List<Rubros>> getByName(@RequestParam String descripcion) {
		return ResponseEntity.ok(rubServicio.findByName(descripcion.toLowerCase()));

	}

}
