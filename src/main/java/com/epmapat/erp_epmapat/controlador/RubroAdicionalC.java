package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.RubroAdicionalM;
import com.epmapat.erp_epmapat.servicio.RubroAdicionalS;

@RestController
@RequestMapping("/rubroadicional")
@CrossOrigin(origins = "*")

public class RubroAdicionalC {

	@Autowired
	private RubroAdicionalS rubadiServicio;

	@GetMapping
	public List<RubroAdicionalM> getAllRubrosAdicional(){
		return rubadiServicio.findAll();
	}

	@GetMapping("/tptramite/{idtptramite}")
	public List<RubroAdicionalM> getRubAdiByIdTpTramite(@PathVariable Long idtptramite){
		return rubadiServicio.findByIdTpTramtie(idtptramite);
	}

}
