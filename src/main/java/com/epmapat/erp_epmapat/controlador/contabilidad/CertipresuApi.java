package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;
import com.epmapat.erp_epmapat.servicio.contabilidad.CertipresuServicio;

@RestController
@RequestMapping("/certipresu")
@CrossOrigin("*")

public class CertipresuApi {

	@Autowired
	private CertipresuServicio certiServicio;

	@GetMapping
	public List<Certipresu> getAllLista(@Param(value = "desdeNum") Long desdeNum,
			@Param(value = "hastaNum") Long hastaNum,
			@Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
			@Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
		if (desdeNum != null) {
			return certiServicio.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
		} else
			return null;
	}

	@GetMapping("/ultimo")
	public Certipresu ultimo() {
		return certiServicio.findFirstByOrderByNumeroDesc();
	}

	@GetMapping("/numero/{numero}/tipo/{tipo}")
	public Certipresu valNumero(@PathVariable Long numero, @PathVariable Integer tipo) {
		return certiServicio.findByNumeroAndTipo(numero, tipo);
	}

	@PostMapping
	public Certipresu saveCertiPresu(@RequestBody Certipresu certipresu) {
		return certiServicio.save(certipresu);
	}

	@PutMapping("/{idcerti}")
	public ResponseEntity<Certipresu> updateCertiPresu(@PathVariable Long idcerti, @RequestBody Certipresu certipresum) {
		Certipresu certipresu = certiServicio.findById(idcerti)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idcerti));
		certipresu.setTipo(certipresum.getTipo());
		certipresu.setNumero(certipresum.getNumero());
		certipresu.setFecha(certipresum.getFecha());
		certipresu.setValor(certipresum.getValor());
		certipresu.setDescripcion(certipresum.getDescripcion());
		certipresu.setNumdoc(certipresum.getNumdoc());
		certipresu.setUsucrea(certipresum.getUsucrea());
		certipresu.setFeccrea(certipresum.getFeccrea());
		certipresu.setUsumodi(certipresum.getUsumodi());
		certipresu.setFecmodi(certipresum.getFecmodi());
		certipresu.setIdbene(certipresum.getIdbene());
		certipresu.setIdbeneres(certipresum.getIdbeneres());
		certipresu.setIddocumento(certipresum.getIddocumento());
		Certipresu updateCerti = certiServicio.save(certipresu);
		return ResponseEntity.ok(updateCerti);
	}

	@GetMapping("/{idcerti}")
	public Optional<Certipresu> findByIdCertiPresu(@PathVariable Long idcerti) {
		return certiServicio.findById(idcerti);
	}

	@DeleteMapping(value = "/{idcerti}")
	public ResponseEntity<Boolean> deleteCertipresu(@PathVariable("idcerti") Long idcerti) {
		certiServicio.deleteById(idcerti);
		return ResponseEntity.ok(!(certiServicio.findById(idcerti) != null));
	}

}
