package com.epmapat.erp_epmapat.controlador;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Cajas;
import com.epmapat.erp_epmapat.servicio.CajaServicio;

@RestController
@RequestMapping("/cajas")
@CrossOrigin(origins = "*")

public class CajasApi {

	@Autowired
	private CajaServicio cajaServicio;

	@GetMapping
	public List<Cajas> getAll(@Param(value = "descripcion") String descripcion,
			@Param(value = "idptoemision_ptoemision") Long idptoemision, @Param(value = "codigo") String codigo) {
		if (descripcion != null) {
			return cajaServicio.findByDescri(descripcion);
		} else {
			if (idptoemision != null) {
				if (codigo != null) {
					return cajaServicio.findByCodigos(idptoemision, codigo);
				} else {
					return cajaServicio.findByIdptoemision(idptoemision);
				}
			} else {
				return cajaServicio.findAll();
			}
		}
	}

	@PostMapping
	private ResponseEntity<Cajas> saveCaja(@RequestBody Cajas cajaM) {
		try {
			Cajas cajaGuardada = cajaServicio.save(cajaM);
			return ResponseEntity.created(new URI("/caja" + cajaGuardada.getIdcaja())).body(cajaGuardada);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@GetMapping("/{idcaja}")
	public ResponseEntity<Cajas> getByIdCaja(@PathVariable Long idcaja) {
		Cajas cajaM = cajaServicio.findById(idcaja)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe esa caja con ese Id: " + idcaja)));
		return ResponseEntity.ok(cajaM);
	}

	@PutMapping("/{idcaja}")
	public ResponseEntity<Cajas> updateCaja(@PathVariable Long idcaja, @RequestBody Cajas cajam) {
		Cajas cajaM = cajaServicio.findById(idcaja)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe esa caja con ese Id: " + idcaja)));
		cajaM.setDescripcion(cajam.getDescripcion());
		cajaM.setCodigo(cajam.getCodigo());
		cajaM.setEstado(cajam.getEstado());
		cajaM.setIdptoemision_ptoemision(cajam.getIdptoemision_ptoemision());
		cajaM.setUsucrea(cajam.getUsucrea());
		cajaM.setFeccrea(cajam.getFeccrea());
		cajaM.setUsumodi(cajam.getUsumodi());
		cajaM.setFecmodi(cajam.getFecmodi());
		Cajas updateCaja = cajaServicio.save(cajaM);
		return ResponseEntity.ok(updateCaja);
	}

	@DeleteMapping(value = "/{idcaja}")
	public ResponseEntity<Boolean> deleteCaja(@PathVariable("idcaja") Long idcaja) {
		cajaServicio.deleteById(idcaja);
		return ResponseEntity.ok(!(cajaServicio.findById(idcaja) != null));
	}
	@GetMapping("usuario/{idusuario}")
	public ResponseEntity<Cajas> getByIdUsuario(@PathVariable("idusuario") Long idusuario){
		Cajas caja = cajaServicio.findCajaByIdUsuario(idusuario);
		if(caja != null) {
			return ResponseEntity.ok(caja);			
		}else {
			return ResponseEntity.noContent().build(); 
		}
	}

}
