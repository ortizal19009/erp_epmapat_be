package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
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

	@GetMapping("/movibank")
	public ResponseEntity<List<Transaci>> getMovibank(@Param("idcuenta") Long idcuenta,
			@Param("mes") Integer mes) {
		return ResponseEntity.ok(tranServicio.findMovibank(idcuenta, mes));
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

	// Mayor de una Cuenta
	@GetMapping("/mayor")
	public ResponseEntity<List<Transaci>> getByCodcue(@Param("codcue") String codcue,
			@Param("desde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
			@Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta) {
		return ResponseEntity.ok(tranServicio.findByCodcue(codcue, desde, hasta));
	}

	// Suma débitos o créditos de una cuenta
	@GetMapping("/sumValor")
	public ResponseEntity<BigDecimal> sumValor(@Param("codcue") String codcue, @Param("debcre") Integer debcre,
			@Param("desde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
			@Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta) {
		BigDecimal resultados = tranServicio.sumValor(codcue, debcre, desde, hasta);
		return ResponseEntity.ok(resultados);
	}

	// Saldo Anterior cuenta deudora
	@GetMapping("/saldo")
	public ResponseEntity<BigDecimal> saldo(@Param("codcue") String codcue,
			@Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta) {
		BigDecimal saldo = tranServicio.saldo(codcue, hasta);
		return ResponseEntity.ok(saldo);
	}

	// Balance de comprobación
	@GetMapping("/balance")
	public List<Map<String, Object>> obtenerBalance(
			@Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
			@Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
		return tranServicio.obtenerBalance(desdeFecha, hastaFecha);
	}

	// Estado de situación
	@GetMapping("/estados")
	public List<Map<String, Object>> obtenerEstados(@RequestParam(required = true) Long intgrupo,
			@Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
			@Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
		return tranServicio.obtenerEstados(intgrupo, desdeFecha, hastaFecha);
	}

	@GetMapping("/flujo")
	public Double totalFlujo(@RequestParam(required = true) String codcue,
			@Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
			@Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha,
			@Param("debcre") Long debcre) {
		Double tdeven = tranServicio.totalFlujo(codcue, desdeFecha, hastaFecha, debcre);
		return tdeven;
	}

	@GetMapping("/tipasi")
	public ResponseEntity<List<Transaci>> getByTipAsi(@RequestParam Long tipasi) {
		return ResponseEntity.ok(tranServicio.getByTipAsi(tipasi));
	}

	// Busca Transacciones por número y fechas de los Asientos
	@GetMapping("/asientos")
	public List<Transaci> tranAsientos(
			@Param(value = "opcion") Integer opcion, // 1:Asientos, 2:Comprobantes
			@Param(value = "tipcom") Integer tipcom,
			@Param(value = "desdeNum") Long desdeNum,
			@Param(value = "hastaNum") Long hastaNum,
			@Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
			@Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
		if (opcion == 1) {
			return tranServicio.tranAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
		} else if (opcion == 2)
			// return tranServicio.tranComprobantes(tipcom, desdeNum, hastaNum, desdeFecha,
			// hastaFecha);
			return tranServicio.tranAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
		else
			return null;
	}

	@GetMapping("/{inttra}")
	public Optional<Transaci> findById(@PathVariable Long inttra) {
		return tranServicio.findById( inttra );
	}

	@PostMapping
	public Transaci saveTransaci(@RequestBody Transaci transaci) {
		return tranServicio.save(transaci);
	}

	@PutMapping("/updtransaci/{inttra}")
	public ResponseEntity<Transaci> updateTransaci(@PathVariable Long inttra, @RequestBody Transaci transaci) {
		Transaci transacim = tranServicio.findById(inttra)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuentra esta transaci " + inttra));
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
		transacim.setIntdoc(transaci.getIntdoc());
		transacim.setIntpre(transaci.getIntpre());
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

	//Actualiza con id
	@PutMapping("/{inttra}")
	public ResponseEntity<Transaci> updateTransaci1(@PathVariable Long inttra, @RequestBody Transaci transaci) {
		Transaci transacim = tranServicio.findById(inttra)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuentra esta transaci " + inttra));
		transacim.setOrden(transaci.getOrden());
		transacim.setIdcuenta(transaci.getIdcuenta());
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
		transacim.setIntdoc(transaci.getIntdoc());
		transacim.setIntpre(transaci.getIntpre());
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

	@DeleteMapping(value = "/{inttra}")
	public ResponseEntity<Boolean> deleteTransaci(@PathVariable("inttra") Long inttra) {
		tranServicio.deleteById(inttra);
		return ResponseEntity.ok(!(tranServicio.findById(inttra) != null));
	}
	@GetMapping("/aperini")
	public ResponseEntity<List<Transaci>> getAperInicial(@RequestParam("codcue") String Codcue) {
		return ResponseEntity.ok(tranServicio.aperInicial(Codcue));
	}

}
