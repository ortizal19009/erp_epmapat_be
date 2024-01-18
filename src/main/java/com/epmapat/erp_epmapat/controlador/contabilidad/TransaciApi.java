package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;
import com.epmapat.erp_epmapat.servicio.contabilidad.TransaciServicio;

@RestController
@RequestMapping("/transaci")
@CrossOrigin("*")

public class TransaciApi {

	@Autowired
	private TransaciServicio tranServicio;

	@GetMapping
	public ResponseEntity<List<Transaci>> getAllTransaci() {
		return ResponseEntity.ok(tranServicio.findAll());
	}

	@GetMapping("/ctrlBncs")
	public ResponseEntity<List<Transaci>> getControlBancos(@RequestParam("idcuenta") Long idcuenta,
			@RequestParam("mes") Long mes) {
		return ResponseEntity.ok(tranServicio.findControlBancos(idcuenta, mes));
	}

	// Cuentas (Transacciones) de un asiento
	@GetMapping("/asiento")
	public ResponseEntity<List<Transaci>> getTransaci(@Param("idasiento") Long idasiento) {
		return ResponseEntity.ok(tranServicio.findTransaci(idasiento));
	}

	// Valida si una Cuenta tiene Transacciones
	@GetMapping("/tieneTransaci")
	public ResponseEntity<Boolean> verificarEjecuciones(@Param(value = "codcue") String codcue) {
		boolean tieneTransaci = tranServicio.tieneTransaci(codcue);
		return ResponseEntity.ok(tieneTransaci);
	}

	// Valida si un Asiento tiene Transacciones
	@GetMapping("/porIdasiento")
	public ResponseEntity<Boolean> existsByIdasiento(@Param(value = "idasiento") Long idasiento) {
		boolean x = tranServicio.existsByIdasiento(idasiento);
		return ResponseEntity.ok(x);
	}

	@PostMapping
	public Transaci saveTransaci(@RequestBody Transaci transaci) {
		return tranServicio.save(transaci);
	}

	@PutMapping("/updtransaci/{idtransaci}")
	public ResponseEntity<Transaci> updateTransaci(@PathVariable Long idtransaci, @RequestBody Transaci transaci) {
		Transaci transacim = tranServicio.findById(idtransaci)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuentra esta transaci " + idtransaci));
		transacim.setOrden(transaci.getOrden());
		transacim.setCodcue(transaci.getCodcue());
		transacim.setValor(transaci.getValor());
		transacim.setDebcre(transaci.getDebcre());
		transacim.setDescri(transaci.getDescri());
		transacim.setNumdoc(transaci.getNumdoc());
		transacim.setTiptran(transaci.getTiptran());
		transacim.setTotbene(transaci.getTotbene());
		transacim.setSwconcili(transaci.getSwconcili());
		transacim.setMesconcili(transaci.getMesconcili());
		transacim.setIdasiento(transaci.getIdasiento());
		transacim.setIdbene(transaci.getIdbene());
		transacim.setIddocumento(transaci.getIddocumento());
		transacim.setIdpresupue(transaci.getIdpresupue());
		transacim.setCodpartr(transaci.getCodpartr());
		transacim.setCodcueiog(transaci.getCodcueiog());
		transacim.setDebeiog(transaci.getDebeiog());
		transacim.setHaberiog(transaci.getHaberiog());
		transacim.setAsierefe(transaci.getAsierefe());
		transacim.setUsucrea(transaci.getUsucrea());
		transacim.setFeccrea(transaci.getFeccrea());
		transacim.setUsumodi(transaci.getUsumodi());
		transacim.setFecmodi(transaci.getFecmodi());
		Transaci updateTransaci = tranServicio.save(transacim);
		return ResponseEntity.ok(updateTransaci);
	}

	@DeleteMapping(value = "/{idtransa}")
	public ResponseEntity<Boolean> deleteTransaci(@PathVariable("idtransa") Long idtransa) {
		tranServicio.deleteById(idtransa);
		return ResponseEntity.ok(!(tranServicio.findById(idtransa) != null));
	}

}
