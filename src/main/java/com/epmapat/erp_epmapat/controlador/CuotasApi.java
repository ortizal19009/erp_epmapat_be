package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Cuotas;
import com.epmapat.erp_epmapat.servicio.CuotaServicio;

@RestController
@RequestMapping("/cuotas")
@CrossOrigin(origins = "*")

public class CuotasApi {

	@Autowired
	private CuotaServicio cuotaServicio;

	@GetMapping
	public List<Cuotas> get(@Param(value = "idconvenio") Long idconvenio) {
		if (idconvenio != null) {
			return cuotaServicio.findByIdconvenio(idconvenio);
		} else {
			return cuotaServicio.find10();
		}
	}

	@PostMapping
	public Cuotas saveCuotas(@RequestBody Cuotas cuotas) {
		return cuotaServicio.save(cuotas);
	}
}
