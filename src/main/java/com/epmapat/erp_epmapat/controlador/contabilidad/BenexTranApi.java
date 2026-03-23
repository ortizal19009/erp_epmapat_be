package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	// @GetMapping("/acfp") // Anticipos, CxC, F.Terceros o CxP sin liquidar
	// public ResponseEntity<List<BenexTran>> getACFP(@RequestParam("hasta")
	// LocalDate hasta,
	// @RequestParam("nomben") String nomben, @RequestParam("tiptran") Integer
	// tiptran,
	// @RequestParam("codcue") String codcue) {
	// return ResponseEntity.ok(benxtraServicio.getACFP(hasta, nomben.toLowerCase(),
	// tiptran, codcue));
	// }

	@GetMapping("/acfp")
	public ResponseEntity<List<BenexTran>> getACFP(
			// @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			@RequestParam("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
			@RequestParam("nomben") String nomben,
			@RequestParam("tiptran") Integer tiptran,
			@RequestParam("codcue") String codcue) {

		return ResponseEntity.ok(
				benxtraServicio.getACFP(hasta, nomben.toLowerCase(), tiptran, codcue));
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

	// BenexTran de una transaci.inttra
	@GetMapping("/inttra/{inttra}")
	public ResponseEntity<List<BenexTran>> getPorInttra(@PathVariable Long inttra) {
		List<BenexTran> lista = benxtraServicio.obtenerPorInttra(inttra);
		return ResponseEntity.ok(lista);
	}

	// Cuenta los Benextran de una transaci.inttra
	@GetMapping("/count/inttra/{inttra}")
	public ResponseEntity<Short> countByInttra(@PathVariable Long inttra) {
		short count = benxtraServicio.countByInttra(inttra);
		return ResponseEntity.ok(count);
	}

	// Guardar nuevo
	@PostMapping
	public BenexTran saveBenexTran(@RequestBody BenexTran x) {
		return benxtraServicio.save(x);
	}

	// Guarda lote de registros de benextarn
	@PostMapping("/registros")
	public ResponseEntity<List<BenexTran>> guardarLote(@RequestBody List<BenexTran> lista) {
		List<BenexTran> guardados = benxtraServicio.guardarLote(lista);
		return ResponseEntity.ok(guardados);
	}

	// @PutMapping("/{idbenxtra}")
	// public BenexTran updateBenextran(@PathVariable Long idbenxtra, @RequestBody
	// BenexTran benextran) {
	// return benxtraServicio.updateBenextran(idbenxtra, benextran);
	// }
	// Actualiza solo campos modificados
	@PutMapping("/{idbenxtra}")
	public ResponseEntity<BenexTran> updateBenextran(@PathVariable Long idbenxtra, @RequestBody BenexTran benextran) {
		BenexTran actualizada = benxtraServicio.updateBenextran(idbenxtra, benextran);
		return ResponseEntity.ok(actualizada);
	}

	// Elimina (Devuelve: 200, 204 o 500 )
	@DeleteMapping("/{idbenxtra}")
	public ResponseEntity<?> deleteBenextran(@PathVariable Long idbenxtra) {
		try {
			boolean eliminado = benxtraServicio.deleteById(idbenxtra);
			if (eliminado) {
				return ResponseEntity.ok(true); // 200 OK
			} else {
				return ResponseEntity.noContent().build(); // 204 No Content
			}
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
		}
	}

}
