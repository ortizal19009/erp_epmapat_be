package com.epmapat.erp_epmapat.controlador.contabilidad;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.servicio.contabilidad.BeneficiarioServicio;

@RestController
@RequestMapping("/beneficiarios")
@CrossOrigin("*")

public class BeneficiariosApi {

	@Autowired
	private BeneficiarioServicio beneficiariosS;

	@GetMapping("/benName/{nombre}")
	public List<Beneficiarios> findByNombre(@PathVariable String nombre){
		return beneficiariosS.findByName(nombre.toLowerCase());
	}

	@GetMapping("/benGroup/{nombre}/{idgrupo}")
	public List<Beneficiarios> findByGrupo(@PathVariable String nombre, @PathVariable Long idgrupo){
		return beneficiariosS.findByGrupoBene(nombre.toLowerCase(), idgrupo);
	}

	@GetMapping("/{idbene}")
	public Optional<Beneficiarios> findByIdBene(@PathVariable Long idbene){
		return beneficiariosS.findById(idbene);
	}
	
}
