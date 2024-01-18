package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.PersonJuridica;
import com.epmapat.erp_epmapat.servicio.PersonJuridicaServicio;

@RestController
@RequestMapping("/personeriajuridica")
@CrossOrigin(origins = "*")

public class PersoneriaJuridicaC {

	@Autowired
	private PersonJuridicaServicio pjService;

	@GetMapping
	public List<PersonJuridica> getAllPersoneriaJuridica(){
		return pjService.findAll();
	}
	
	@PostMapping
    public PersonJuridica updateOrSave(@RequestBody PersonJuridica pj) {
        return pjService.save(pj);
    }
}
