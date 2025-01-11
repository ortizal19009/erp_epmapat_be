package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.NiifCuentas;
import com.epmapat.erp_epmapat.servicio.contabilidad.NiifCuentaServicio;

@RestController
@RequestMapping("/niifcuentas")
@CrossOrigin("*")

public class NiifCuentasApi {

	@Autowired
	private NiifCuentaServicio niifcueServicio;

	// @GetMapping
	// public List<NiifCuentas> getAll() {
	// return niifcueServicio.findAll();
	// }

	@GetMapping
	public List<NiifCuentas> getByCodigoyNombre(@Param(value = "codcue") String codcue,
			@Param(value = "nomcue") String nomcue) {
		return niifcueServicio.findByCodigoyNombre(codcue, nomcue.toLowerCase());
	}

	// @GetMapping("/codCue/{codcue}")
	// public List<NiifCuentas> getByCodCuentas(@PathVariable("codcue") String
	// codcue) {
	// return niifcueServicio.findByCodcue(codcue);
	// }

	@PostMapping
	public ResponseEntity<NiifCuentas> saveNiifCuenta(@RequestBody NiifCuentas niifcuentas) {
		return ResponseEntity.ok(niifcueServicio.save(niifcuentas));
	}

	@PutMapping("/upd/{idniifcue}")
	public ResponseEntity<NiifCuentas> updateNiifCuenta(@PathVariable Long idniifcue,
			@RequestBody NiifCuentas niifcuentas) {
		NiifCuentas niifCuentas = niifcueServicio.findById(idniifcue)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe id: " + idniifcue));
		niifCuentas.setCodcue(niifcuentas.getCodcue());
		niifCuentas.setNomcue(niifcuentas.getNomcue());
		niifCuentas.setGrucue(niifcuentas.getGrucue());
		niifCuentas.setNivcue(niifcuentas.getNivcue());
		niifCuentas.setMovcue(niifcuentas.getMovcue());
		niifCuentas.setUsucrea(niifcuentas.getUsucrea());
		niifCuentas.setUsumodi(niifcuentas.getUsumodi());
		niifCuentas.setFeccrea(niifcuentas.getFeccrea());
		niifCuentas.setFecmodi(niifcuentas.getFecmodi());
		NiifCuentas updateNiifCue = niifcueServicio.save(niifCuentas);
		return ResponseEntity.ok(updateNiifCue);
	}

}
