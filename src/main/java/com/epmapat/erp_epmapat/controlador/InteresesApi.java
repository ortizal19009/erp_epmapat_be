package com.epmapat.erp_epmapat.controlador;

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
import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.servicio.InteresServicio;

@RestController
@RequestMapping("/intereses")
@CrossOrigin(origins = "*")

public class InteresesApi {

	@Autowired
	private InteresServicio inteServicio;

	@GetMapping
	public List<Intereses> getAllLista(@Param(value = "anio") Number anio, @Param(value = "mes") Number mes) {
		if (anio != null && mes != null) {
			return inteServicio.findByAnioMes(anio, mes);
		} else {
			return inteServicio.findAll(Sort.by(Sort.Order.desc("anio"), Sort.Order.desc("mes")));
		}
	}

	// Busca el último
	@GetMapping("/ultimo")
	public List<Intereses> getUltimo() {
		return inteServicio.findUltimo();
	}

	@PostMapping
	public ResponseEntity<Intereses> saveIntereses(@RequestBody Intereses x) {
		return ResponseEntity.ok(inteServicio.save( x ));
	}

	@GetMapping("/{idinteres}")
	public ResponseEntity<Intereses> getByIdIntereses(@PathVariable Long idinteres) {
		Intereses interesesM = inteServicio.findById(idinteres)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe ese interes con ese Id: " + idinteres));
		return ResponseEntity.ok(interesesM);
	}

	@PutMapping("/{idinteres}")
	public ResponseEntity<Intereses> updateIntereses(@PathVariable Long idinteres, @RequestBody Intereses interesM) {
		Intereses interesesM = inteServicio.findById(idinteres)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe ese interes con ese Id: " + idinteres));
		interesesM.setAnio(interesM.getAnio());
		interesesM.setMes(interesM.getMes());
		interesesM.setPorcentaje(interesM.getPorcentaje());
		interesesM.setUsucrea(interesM.getUsucrea());
		interesesM.setFeccrea(interesM.getFeccrea());
		interesesM.setUsumodi(interesM.getUsumodi());
		interesesM.setFecmodi(interesM.getFecmodi());
		Intereses updatedInteres = inteServicio.save(interesesM);
		return ResponseEntity.ok(updatedInteres);
	}

	@DeleteMapping(value = "/{idinteres}")
	public ResponseEntity<Boolean> deleteInteres(@PathVariable("idinteres") Long idinteres) {
		inteServicio.deleteById(idinteres);
		return ResponseEntity.ok(!(inteServicio.findById(idinteres) != null));
	}

}
