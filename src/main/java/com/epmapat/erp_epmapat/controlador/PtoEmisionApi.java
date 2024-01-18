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
import com.epmapat.erp_epmapat.modelo.PtoEmisionM;
import com.epmapat.erp_epmapat.servicio.PtoEmisionS;

@RestController
@RequestMapping("/ptoemision")
@CrossOrigin(origins = "*")

public class PtoEmisionApi {

	@Autowired
	private PtoEmisionS ptoServicio;

	@GetMapping
	public List<PtoEmisionM> getAllPtoEmision(@Param(value = "idused") Long idused){
		if(idused == null) {
			return ptoServicio.findAll(Sort.by(Sort.Order.asc("establecimiento")));
		}else {
			return ptoServicio.used(idused);
		}
	}

	@PostMapping
	public PtoEmisionM savePtoEmision(@RequestBody PtoEmisionM ptoemisionM) {
		return ptoServicio.save(ptoemisionM);
	}

	@GetMapping("/{idptoemision}")
	public ResponseEntity<PtoEmisionM> getByIdPtoEmision(@PathVariable Long idptoemision){
		PtoEmisionM ptoemisionM = ptoServicio.findById(idptoemision)
				.orElseThrow(()-> new ResourceNotFoundExcepciones(("No extiste esta Pto Emision con el ID: "+idptoemision)));
		return ResponseEntity.ok(ptoemisionM);
	}

	@PutMapping("/{idptoemision}")
	public ResponseEntity<PtoEmisionM> updatePtoEmision(@PathVariable Long idptoemision, @RequestBody PtoEmisionM ptoemisionm){
		PtoEmisionM ptoemisionM = ptoServicio.findById(idptoemision)
				.orElseThrow(()->new ResourceNotFoundExcepciones(("No existe esta Pto Emision con el Id: "+ idptoemision)));
		ptoemisionM.setEstablecimiento(ptoemisionm.getEstablecimiento());
		ptoemisionM.setDireccion(ptoemisionm.getDireccion());
		ptoemisionM.setEstado(ptoemisionm.getEstado());
		ptoemisionM.setTelefono(ptoemisionm.getTelefono());
		ptoemisionM.setNroautorizacion(ptoemisionm.getNroautorizacion());
		ptoemisionM.setUsucrea(ptoemisionm.getUsucrea());
		ptoemisionM.setFeccrea(ptoemisionm.getFeccrea());
		PtoEmisionM updatePtoEmision = ptoServicio.save(ptoemisionM);
		return ResponseEntity.ok(updatePtoEmision);
	}

	@DeleteMapping(value = "/{idptoemision}")
	private ResponseEntity<Object> deletePtoEmision(@PathVariable ("idptoemision") Long idptoemision){
		ptoServicio.deleteById(idptoemision);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	
}
