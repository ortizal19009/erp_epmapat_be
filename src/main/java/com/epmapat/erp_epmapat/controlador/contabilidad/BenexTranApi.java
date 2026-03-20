package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.BenexTran;
import com.epmapat.erp_epmapat.servicio.contabilidad.BenexTranServicio;

@RestController
@RequestMapping("/benextran")

public class BenexTranApi {

	@Autowired
	private BenexTranServicio benxtraServicio;

	@GetMapping
	public ResponseEntity<List<BenexTran>> getAllBenexTran() {
		return ResponseEntity.ok(benxtraServicio.findAll());
	}

	@GetMapping("/egresos")
	private ResponseEntity<List<BenexTran>> getEgresos(@RequestParam("codcue") String codcue) {
		return ResponseEntity.ok(benxtraServicio.getEgresos(codcue));
	}

	@GetMapping("/movibenefi")
	public ResponseEntity<List<BenexTran>> getByIdBene(@Param("idbene") Long idbene,
			@Param("desde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
			@Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta) {
		return ResponseEntity.ok(benxtraServicio.getByIdBene(idbene, desde, hasta));
	}

	@GetMapping("/cxp")
	public ResponseEntity<List<BenexTran>> getCxP() {
		return ResponseEntity.ok(benxtraServicio.getCxP());
	}

	@GetMapping("/acfp") // Anticipos, CxC, F.Terceros o CxP sin liquidar
	public ResponseEntity<List<BenexTran>> getACFP(@RequestParam("hasta") Date hasta,
			@RequestParam("nomben") String nomben, @RequestParam("tiptran") Integer tiptran,
			@RequestParam("codcue") String codcue) {
		return ResponseEntity.ok(benxtraServicio.getACFP(hasta, nomben.toLowerCase(), tiptran, codcue));
	}

	// Verifica si un Beneficiario tiene benextran
	@GetMapping("/existeByIdbene")
	public ResponseEntity<Boolean> existeByIdbene(@Param(value = "idbene") Long idbene) {
		boolean f = benxtraServicio.existeByIdbene(idbene);
		return ResponseEntity.ok(f);
	}

	// benextran por idbenxtra
	@GetMapping("/{idbenxtra}")
	public ResponseEntity<BenexTran> getByIdbenxtra(@PathVariable Long idbenxtra) {
		BenexTran benextran = benxtraServicio.findById(idbenxtra)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe benextran Id: " + idbenxtra)));
		return ResponseEntity.ok(benextran);
	}

	// Guardar nuevo
	@PostMapping
	public BenexTran saveBenexTran(@RequestBody BenexTran x) {
		return benxtraServicio.save(x);
	}

	// Actualizar
	@PutMapping("/{idbenxtra}")
	public BenexTran actualizar(@PathVariable Long idbenxtra, @RequestBody BenexTran x) {
		return benxtraServicio.actualizar(idbenxtra, x);
	}

}
