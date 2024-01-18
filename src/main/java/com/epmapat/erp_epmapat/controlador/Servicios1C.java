package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Servicios1M;

import com.epmapat.erp_epmapat.servicio.Servicios1S;

@RestController
@RequestMapping("/servicios1")
@CrossOrigin("*")

public class Servicios1C {

	@Autowired
	private Servicios1S servicios1S;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Servicios1M> findAllModulos(){
		return servicios1S.findAll();
	}

	@PostMapping
	public Servicios1M saveServicios(@RequestBody Servicios1M servicios1M) {
		return servicios1S.save(servicios1M);
	}

   @GetMapping("/modulo/{idmodulo}")
   public List<Servicios1M> findByIdModulos(@PathVariable Long idmodulo){
    	return servicios1S.findByIdModulos(idmodulo);
   }

   @GetMapping("/{idservicio}")
   public ResponseEntity<Servicios1M> getByIdServicios(@PathVariable("idservicio") Long idservicio){
    	Servicios1M servicios1M = servicios1S.findById(idservicio)
   		.orElseThrow(()->new ResourceNotFoundExcepciones("No existe este servicio con este Id"+idservicio));
    	return ResponseEntity.ok(servicios1M);
   }

}
