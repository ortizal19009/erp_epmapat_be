package com.epmapat.erp_epmapat.controlador;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Recaudaxcaja;
import com.epmapat.erp_epmapat.servicio.RecaudaxcajaServicio;

@RestController
@RequestMapping("/recaudaxcaja")
@CrossOrigin("*")

public class RecaudaxcajaApi {

	@Autowired
	private RecaudaxcajaServicio recaxcajaServicio;

	@GetMapping
	public List<Recaudaxcaja> getByCaja(@Param(value = "idcaja") Long idcaja,
			@Param(value = "desde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
			@Param(value = "hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta) {
		if (idcaja != null && desde != null && hasta != null) {
			return recaxcajaServicio.findByCaja(idcaja, desde, hasta);
		} else
			return null;
	}

	@GetMapping("/lastconexion/{idcaja}")
	public ResponseEntity<Recaudaxcaja> getLastConexion(@PathVariable("idcaja") Long idcaja) {
		Recaudaxcaja recxcaja = recaxcajaServicio.findLastConexion(idcaja);
		return ResponseEntity.ok(recxcaja);
	}
	@PostMapping
	public ResponseEntity<Recaudaxcaja> saveRecaudaxcaja(@RequestBody Recaudaxcaja recxcaja){
		return ResponseEntity.ok(recaxcajaServicio.save(recxcaja));
	}

}
