package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.epmapat.erp_epmapat.modelo.administracion.Tabla4;
import com.epmapat.erp_epmapat.servicio.administracion.Tabla4Servicio;

@RestController
@RequestMapping("/tabla4")
@CrossOrigin("*")

public class Tabla4Api {

	@Autowired
	private Tabla4Servicio tab4Servicio;

	@GetMapping
	public List<Tabla4> getAllTabla(@Param(value = "tipocomprobante") String tipocomprobante,
			@Param(value = "nomcomprobante") String nomcomprobante) {
		if (tipocomprobante != null) {
			return tab4Servicio.findByTipocomprobante(tipocomprobante);
		} else if (nomcomprobante != null) {
			return tab4Servicio.findByNomcomprobante(nomcomprobante.toLowerCase());
		} else
			return tab4Servicio.findAll();
	}

	@GetMapping("/{idtabla4}")
	public ResponseEntity<Tabla4> getByIdtabla4(@PathVariable Long idtabla4) {
		Tabla4 x = tab4Servicio.findById(idtabla4)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe el Comprobante Id: " + idtabla4)));
		return ResponseEntity.ok(x);
	}

	@PostMapping
	public Tabla4 saveTabla4(@RequestBody Tabla4 tabla4m) {
		return tab4Servicio.save(tabla4m);
	}

	@PutMapping("/{idtabla4}")
    public ResponseEntity<Tabla4> update(@PathVariable Long idtabla4, @RequestBody Tabla4 x) {
        Tabla4 y = tab4Servicio.findById(idtabla4)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Comprobante SRI Id: " + idtabla4)));
        y.setTipocomprobante(x.getTipocomprobante());
        y.setNomcomprobante(x.getNomcomprobante());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Tabla4 actualizar = tab4Servicio.save(y);
        return ResponseEntity.ok(actualizar);
    }
	 
	@DeleteMapping("/{idtabla4}")
    private ResponseEntity<Boolean> deleteTabla4(@PathVariable("idtabla4") Long idtabla4) {
        tab4Servicio.deleteById(idtabla4);
        return ResponseEntity.ok(!(tab4Servicio.findById(idtabla4) != null));
    }
	 
}
