package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.ConciliaBan;
import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;
import com.epmapat.erp_epmapat.servicio.contabilidad.ConciliaBanServicio;

@RestController
@RequestMapping("/conciliaban")
@CrossOrigin("*")

public class ConciliaBanApi {

	@Autowired
	private ConciliaBanServicio conciServicio; 

	@GetMapping
	public ResponseEntity<List<ConciliaBan>> getAllConciliaBan(){
		return ResponseEntity.ok(conciServicio.findAll());
	}

	@GetMapping("/")
	public ResponseEntity<ConciliaBan> getConciliaBan(@Param(value = "idcuenta") Cuentas idcuenta, @Param(value = "mes") Integer mes) {
	  ConciliaBan registro = conciServicio.getConciliaBan(idcuenta, mes);
	  if (registro != null) {
		 return ResponseEntity.ok(registro);
	  } else {
		 return ResponseEntity.notFound().build();
	  }
	}
 
	
	@PostMapping
	public ResponseEntity<ConciliaBan> saveConciliaban(@RequestBody ConciliaBan conciliaban){
		return ResponseEntity.ok(conciServicio.save(conciliaban));
	}

}
