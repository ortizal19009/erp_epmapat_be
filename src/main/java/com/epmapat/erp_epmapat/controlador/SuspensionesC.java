package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.SuspensionesM;
import com.epmapat.erp_epmapat.servicio.SuspensionesS;

@RestController
@RequestMapping("/suspensiones")
@CrossOrigin("*")

public class SuspensionesC {

	@Autowired
	private SuspensionesS suspensionesS;

	@GetMapping
	public List<SuspensionesM> getAllSuspensiones(){
		return suspensionesS.findAll();
	}
	
	@PostMapping
	public SuspensionesM saveSuspencion(@RequestBody SuspensionesM suspensionesM) {
		return suspensionesS.save(suspensionesM);
	}
	@GetMapping("/ultimo")
	public SuspensionesM getUltima(){
		return suspensionesS.findFirstByOrderByIdsuspensionDesc();
	}
	@GetMapping("/habilitaciones")
	public List<SuspensionesM> getByHabilitaciones(){
		return suspensionesS.findHabilitaciones();
	}

}
