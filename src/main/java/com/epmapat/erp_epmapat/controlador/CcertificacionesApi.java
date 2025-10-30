package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.epmapat.erp_epmapat.modelo.Ccertificaciones;
import com.epmapat.erp_epmapat.servicio.CcertificacionServicio;

@RestController
@RequestMapping("/ccertificaciones")
@CrossOrigin(origins = "*")

public class CcertificacionesApi {

	@Autowired
	private CcertificacionServicio certiServicio;

	@GetMapping
	public List<Ccertificaciones> getCertificaciones(@Param(value = "desde") Long desde,
			@Param(value = "hasta") Long hasta, @Param(value = "cliente") String cliente) {
		if (desde != null && hasta != null) {
			return certiServicio.findDesdeHasta(desde, hasta);
		} else if (cliente != null) {
			return certiServicio.findByCliente(cliente.toLowerCase());
		} else
			return null;
	}

	@GetMapping("/ultima")
	public Ccertificaciones ultima() {
		return certiServicio.ultima();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Ccertificaciones saveCertificaciones(@RequestBody Ccertificaciones x) {
		return certiServicio.save(x);
	}

	@GetMapping("/{idcertificacion}")
	public ResponseEntity<Ccertificaciones> getByIdCertificaciones(@PathVariable Long idcertificacion) {
		Ccertificaciones x = certiServicio.findById(idcertificacion)
				.orElseThrow(
						() -> new ResourceNotFoundExcepciones("No existe ese Certificado con ese id: " + idcertificacion));
		return ResponseEntity.ok(x);
	}

	@PutMapping(value = "/{idcertificacion}")
	public ResponseEntity<Ccertificaciones> updateCertificaciones(@PathVariable Long idcertificacion,
			@RequestBody Ccertificaciones certificacionesm) {
		Ccertificaciones certificacionesM = certiServicio.findById(idcertificacion)
				.orElseThrow(
						() -> new ResourceNotFoundExcepciones("No existe ese Certificado con ese id: " + idcertificacion));
		certificacionesM.setNumero(certificacionesm.getNumero());
		certificacionesM.setAnio(certificacionesm.getAnio());
		certificacionesM.setReferenciadocumento(certificacionesm.getReferenciadocumento());
		certificacionesM.setIdtpcertifica_tpcertifica(certificacionesm.getIdtpcertifica_tpcertifica());
		certificacionesM.setIdfactura_facturas(certificacionesm.getIdfactura_facturas());
		certificacionesM.setUsucrea(certificacionesm.getUsucrea());
		certificacionesM.setFeccrea(certificacionesm.getFeccrea());
		certificacionesM.setFecmodi(certificacionesm.getFecmodi());
		Ccertificaciones updateCertificaciones = certiServicio.save(certificacionesM);
		return ResponseEntity.ok(updateCertificaciones);
	}

	@DeleteMapping(value = "/{idcertificacion}")
	public ResponseEntity<Boolean> deleteCertificaciones(@PathVariable("idcertificacion") Long idcertificacion) {
		certiServicio.deleteById(idcertificacion);
		return ResponseEntity.ok(!(certiServicio.findById(idcertificacion) != null));
	}

}
