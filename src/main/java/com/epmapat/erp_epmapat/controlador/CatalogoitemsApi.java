package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Catalogoitems;
import com.epmapat.erp_epmapat.servicio.CatalogoitemServicio;

@RestController
@RequestMapping("/catalogoitems")


public class CatalogoitemsApi {

	@Autowired
	private CatalogoitemServicio catiServicio;

	// Productos por Seccion y/o Descripcion
	@GetMapping
	public List<Catalogoitems> getProductos(@RequestParam(value = "idmodulo1", required = false) Long idmodulo1,
			@RequestParam(value = "idmodulo2", required = false) Long idmodulo2,
			@RequestParam(value = "descripcion", required = false, defaultValue = "") String descripcion) {
		if (idmodulo1 != null && idmodulo2 != null) {
			return catiServicio.findProductos(idmodulo1, idmodulo2, descripcion == null ? "" : descripcion.trim());
		}
		return List.of();
	}

	@GetMapping("/{idcatalogoitems}")
	public ResponseEntity<Catalogoitems> getByIdcatalogoitems(@PathVariable Long idcatalogoitems) {
		Catalogoitems x = catiServicio.findById(idcatalogoitems)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe Facturacion Id: " + idcatalogoitems)));
		return ResponseEntity.ok(x);
	}

	@GetMapping("/rubro/{idrubro}")
	public List<Catalogoitems> findByIdrubro(@PathVariable Long idrubro) {
		return catiServicio.findByIdrubro(idrubro);
	}

	@GetMapping("/usoitem/{idusoitems}")
	public List<Catalogoitems> findByIdusoitems(@PathVariable Long idusoitems) {
		return catiServicio.findByIdusoitems(idusoitems);
	}

	@GetMapping("/valnombre")
	public List<Catalogoitems> valNombre(@RequestParam(value = "idusoitems", required = false) Long idusoitems,
			@RequestParam(value = "descripcion", required = false, defaultValue = "") String descripcion) {
		if (idusoitems != null && descripcion != null) {
			return catiServicio.findByNombre(idusoitems, descripcion.toLowerCase().trim());
		}
		return List.of();
	}

	@PostMapping
	public ResponseEntity<Catalogoitems> save(@RequestBody Catalogoitems catalogoitems) {
		return ResponseEntity.ok(catiServicio.save(catalogoitems));
	}

	@PutMapping("/{idcatalogoitems}")
	public ResponseEntity<Catalogoitems> update(@PathVariable Long idcatalogoitems, @RequestBody Catalogoitems x) {
		Catalogoitems y = catiServicio.findById(idcatalogoitems)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe el Rubro con Id: " + idcatalogoitems)));
		y.setDescripcion(x.getDescripcion());
		y.setCantidad(x.getCantidad());
		y.setFacturable(x.getFacturable());
		y.setEstado(x.getEstado());
		y.setIdusoitems_usoitems(x.getIdusoitems_usoitems());
		y.setIdrubro_rubros(x.getIdrubro_rubros());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());

		Catalogoitems actualizar = catiServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}

}
