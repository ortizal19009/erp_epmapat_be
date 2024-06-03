package com.epmapat.erp_epmapat.controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.CtramitesM;
import com.epmapat.erp_epmapat.servicio.CtramitesS;

@RestController
@RequestMapping("/ctramites")
@CrossOrigin("*")

public class CtramitesC {

	@Autowired
	private CtramitesS tramitesS;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<CtramitesM> getAllTramites() {
		return tramitesS.findAll();
	}

	@GetMapping("/tptramite/{idtptramite}")
	@ResponseStatus(HttpStatus.OK)
	public List<CtramitesM> getByTpTramite(@PathVariable("idtptramite") Long idtptramite) {
		return tramitesS.findByTpTramite(idtptramite);
	}

	@GetMapping("/descripcion/{descripcion}")
	@ResponseStatus(HttpStatus.OK)
	public List<CtramitesM> getByDescripcion(@PathVariable("descripcion") String descripcion) {
		return tramitesS.findByDescripcion(descripcion.toLowerCase());
	}

	@GetMapping("/feccrea/{feccrea}")
	@ResponseStatus(HttpStatus.OK)
	public List<CtramitesM> getByFeccrea(@PathVariable("feccrea") String feccrea) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fechaConvertida = null;
		try {
			fechaConvertida = (Date) dateFormat.parse(feccrea);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.print(feccrea);
		return tramitesS.findByfeccrea(fechaConvertida);
	}
	// Trámites por Cliente
	@GetMapping("/idcliente/{idcliente}")
	@ResponseStatus(HttpStatus.OK)
	public List<CtramitesM> getByIdcliente(@PathVariable("idcliente") Long idcliente) {
		return tramitesS.findByIdcliente(idcliente);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CtramitesM saveTramites(@RequestBody CtramitesM tramitesM) {
		return tramitesS.save(tramitesM);
	}

	@GetMapping("/{idtramite}")
	public ResponseEntity<CtramitesM> getByIdTramite(@PathVariable Long idtramite) {
		CtramitesM tramitesM = tramitesS.findById(idtramite)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe este tamite con este Id"));
		return ResponseEntity.ok(tramitesM);
	}

	@PutMapping("/{idtramite}")
	public ResponseEntity<CtramitesM> updateTramite(@PathVariable Long idtramite, @RequestBody CtramitesM tramitesm) {
		CtramitesM tramitesM = tramitesS.findById(idtramite)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe este tamite con este Id"));
		tramitesM.setEstado(tramitesm.getEstado());
		tramitesM.setTotal(tramitesm.getTotal());
		tramitesM.setDescripcion(tramitesm.getDescripcion());
		tramitesM.setCuotas(tramitesm.getCuotas());
		tramitesM.setValidohasta(tramitesm.getValidohasta());
		tramitesM.setIdtptramite_tptramite(tramitesm.getIdtptramite_tptramite());
		tramitesM.setIdcliente_clientes(tramitesm.getIdcliente_clientes());
		tramitesM.setUsucrea(tramitesm.getUsucrea());
		tramitesM.setFeccrea(tramitesm.getFeccrea());
		tramitesM.setUsumodi(tramitesm.getUsumodi());
		tramitesM.setFecmodi(tramitesm.getFecmodi());
		CtramitesM updateTramite = tramitesS.save(tramitesM);
		return ResponseEntity.ok(updateTramite);
	}

	@DeleteMapping(value = "/{idtramite}")
	public ResponseEntity<Boolean> deleteTramites(@PathVariable("idtramite") Long idtramite) {
		tramitesS.deleteById(idtramite);
		return ResponseEntity.ok(!(tramitesS.findById(idtramite) != null));
	}

}
