package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Asientos;
import com.epmapat.erp_epmapat.servicio.contabilidad.AsientoServicio;

@RestController
@RequestMapping("/asientos")
@CrossOrigin("*")

public class AsientosApi {

	@Autowired
	private AsientoServicio asiServicio;

	@GetMapping
	public List<Asientos> getAsientos(
			@Param(value = "asi_com") Integer asi_com,
			@Param(value = "tipcom1") Integer tipcom1,
			@Param(value = "tipcom2") Integer tipcom2,
			@Param(value = "desdeNum") Long desdeNum,
			@Param(value = "hastaNum") Long hastaNum,
			@Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
			@Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
		if (asi_com == 1) {
			return asiServicio.findAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
		} else if (asi_com == 2)
			return asiServicio.findComprobantes(tipcom1, tipcom2, desdeNum, hastaNum, desdeFecha, hastaFecha);
		else
			return null;
	}

	@GetMapping("/ultimo")
	public Asientos ultimo() {
		return asiServicio.findFirstByOrderByAsientoDesc();
	}

	@GetMapping("/ultimocompro")
	public Long ultimocompro(@Param("tipcom") Integer tipcom) {
		return asiServicio.findLastComproByTipcom(tipcom);
	}

	// @GetMapping("/tipcom/{tipcom}/compro/{compro}")
	// public Asientos valCompro(@PathVariable Long tipcom, @PathVariable Long
	// compro) {
	// return asiServicio.findByTipcomAndCompro(tipcom, compro);
	// }

	// @GetMapping("/tipcom/{tipcom}/lastcompro")
	// public Asientos valCompro(@PathVariable Integer tipcom) {
	// return asiServicio.findLastComproByTipcom(tipcom);
	// }

	@GetMapping("/asiento")
	public Asientos obtenerAsientoPorId(@Param("idasiento") Long idasiento) {
		Asientos asientos = asiServicio.obtenerAsientoPorId(idasiento);
		if (asientos == null) {
			throw new EntityNotFoundException("Asiento no encontrado");
		}
		return asientos;
	}

	@GetMapping("/{idasiento}")
	public Optional<Asientos> findByIdAsiento(@PathVariable Long idasiento) {
		return asiServicio.findById(idasiento);
	}

	@GetMapping("/siguiente")
	public Long getSiguienteAsiento() {
		return asiServicio.obtenerSiguienteNumeroAsiento();
	}

	@GetMapping("/ultimafecha")
	public Date obtenerUltimaFecha() {
		return asiServicio.obtenerUltimaFecha();
	}

	@GetMapping("/valcompro")
	public ResponseEntity<Boolean> validarCompro(@RequestParam Integer tipcom, @RequestParam Long compro) {
		boolean esComproValido = asiServicio.valCompro(tipcom, compro);
		return ResponseEntity.ok(esComproValido);
	}

	@PostMapping
	public Asientos saveAsiento(@RequestBody Asientos asiento) {
		return asiServicio.save(asiento);
	}

	@PutMapping("/{idasiento}")
	public ResponseEntity<Asientos> updateAsiento(@PathVariable Long idasiento, @RequestBody Asientos x) {
		Asientos y = asiServicio.findById(idasiento)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idasiento));
		y.setAsiento(x.getAsiento());
		y.setFecha(x.getFecha());
		y.setTipasi(x.getTipasi());
		y.setTipcom(x.getTipcom());
		y.setCompro(x.getCompro());
		y.setNumcue(x.getNumcue());
		y.setTotdeb(x.getTotdeb());
		y.setTotcre(x.getTotcre());
		y.setGlosa(x.getGlosa());
		y.setNumdoc(x.getNumdoc());
		y.setCerrado(x.getCerrado());
		y.setSwretencion(x.getSwretencion());
		y.setTotalspi(x.getTotalspi());
		y.setIddocumento(x.getIddocumento());
		y.setIdbene(x.getIdbene());
		y.setIdcueban(x.getIdcueban());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());
		Asientos updateAsiento = asiServicio.save(y);
		return ResponseEntity.ok(updateAsiento);
	}

	@PatchMapping("/totales")
	public void updateTotdebAndTotcre(@Param(value = "idasiento") Long idasiento,
			@Param(value = "totdeb") BigDecimal totdeb,
			@Param(value = "totcre") BigDecimal totcre) {
		asiServicio.updateTotdebAndTotcre(idasiento, totdeb, totcre);
	}

	@DeleteMapping(value = "/{idasiento}")
	public ResponseEntity<Boolean> deleteAsientos(@PathVariable("idasiento") Long idasiento) {
		asiServicio.deleteById(idasiento);
		return ResponseEntity.ok(!(asiServicio.findById(idasiento) != null));
	}

}
