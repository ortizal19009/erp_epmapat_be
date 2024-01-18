package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.ConciliaBan;
import com.epmapat.erp_epmapat.servicio.contabilidad.ConciliaBanS;

@RestController
@RequestMapping("/conciliaban")
@CrossOrigin("*")

public class ConciliaBanApi {

	@Autowired
	private ConciliaBanS conciliabanS; 

	@GetMapping
	public ResponseEntity<List<ConciliaBan>> getAllConciliaBan(){
		return ResponseEntity.ok(conciliabanS.findAll());
	}
	
	@PostMapping
	public ResponseEntity<ConciliaBan> saveConciliaban(@RequestBody ConciliaBan conciliaban){
		return ResponseEntity.ok(conciliabanS.save(conciliaban));
	}

}
