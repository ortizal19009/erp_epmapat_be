package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
// import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Categorias;
import com.epmapat.erp_epmapat.servicio.CategoriaServicio;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "*")

public class CategoriasApi {

	@Autowired
	private CategoriaServicio cateServicio;

	@GetMapping
	public List<Categorias> getAllCategorias(@Param(value = "descripcion") String descripcion,
			@Param(value = "idused") Long idused) {
		if (descripcion != null) {
			return cateServicio.findByDescri(descripcion);
		} else {
			if (idused == null) {
				return cateServicio.findAll(Sort.by(Sort.Order.asc("codigo")));
			} else {
				return cateServicio.used(idused);
			}
		}
	}

	// Categorias habilitadas
	@GetMapping("/categorias")
	public List<String> obtenerCategorias() {
		return cateServicio.listaCategorias();
	}

	@GetMapping("/suma-totaltarifa")
	public ResponseEntity<BigDecimal> sumTotalTarifa() {
		BigDecimal suma = cateServicio.sumTotalTarifa();
		return ResponseEntity.ok(suma);
	}

	@PostMapping
	public Categorias saveCategoria(@RequestBody Categorias categoriaM) {
		return cateServicio.save(categoriaM);
	}

	@GetMapping("/{idcategoria}")
	public ResponseEntity<Categorias> getByIdCategoria(@PathVariable Long idcategoria) {
		Categorias categoriaM = cateServicio.findById(idcategoria)
				.orElseThrow(
						() -> new ResourceNotFoundExcepciones(("No existe la Categoria con el Id: " + idcategoria)));
		return ResponseEntity.ok(categoriaM);
	}

	@PutMapping(value = "/{idcategoria}")
	public ResponseEntity<Categorias> updateCategoria(@PathVariable Long idcategoria,
			@RequestBody Categorias categoriam) {
		Categorias categoriaM = cateServicio.findById(idcategoria)
				.orElseThrow(
						() -> new ResourceNotFoundExcepciones(("No existe esa categoria con ese Id: " + idcategoria)));
		categoriaM.setDescripcion(categoriam.getDescripcion());
		categoriaM.setCodigo(categoriam.getCodigo());
		categoriaM.setHabilitado(categoriam.getHabilitado());
		categoriaM.setFijoagua(categoriam.getFijoagua());
		categoriaM.setFijosanea(categoriam.getFijosanea());
		categoriaM.setUsucrea(categoriam.getUsucrea());
		categoriaM.setFeccrea(categoriam.getFeccrea());
		categoriaM.setUsumodi(categoriam.getUsumodi());
		Categorias updateCategoria = cateServicio.save(categoriaM);
		return ResponseEntity.ok(updateCategoria);
	}

	@DeleteMapping(value = "/{idcategoria}")
	public ResponseEntity<Object> deleteCategoria(@PathVariable("idcategoria") Long idcategoria) {
		cateServicio.deleteById(idcategoria);
		return ResponseEntity.ok(Boolean.TRUE);
	}

}
