package com.epmapat.erp_epmapat.controlador;

import java.io.FileNotFoundException;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Nacionalidad;
import com.epmapat.erp_epmapat.servicio.NacionalidadServicio;

import net.sf.jasperreports.engine.JRException;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/nacionalidades")
@CrossOrigin(origins = "*")

public class NacionalidadApi {

    @Autowired
    private NacionalidadServicio nacService;
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
	public List<Nacionalidad> getAllNacionalidades(@Param(value = "idused") Long idused, @Param(value = "descripcion") String descripcion) {
		if(descripcion != null){
			return nacService.findByDescription(descripcion.toLowerCase());
			
		}else if (idused != null){
			return nacService.used(idused);
		}
		else {
			return nacService.findAll( Sort.by(Sort.Order.asc("descripcion")));
		}
	}

    @PostMapping
    public Nacionalidad saveNacionalidad(@RequestBody Nacionalidad nacionalidadM) {
    	return nacService.save(nacionalidadM);
    }

    @GetMapping("/{idnacionalidad}")
    public ResponseEntity<Nacionalidad> getByIdNacionalidad(@PathVariable Long idnacionalidad){
    	Nacionalidad nacionalidadM = nacService.findById(idnacionalidad)
    			.orElseThrow(()-> new ResourceNotFoundExcepciones(("No existe esa nacionalidad con ese Id: "+ idnacionalidad)) );
    	return ResponseEntity.ok(nacionalidadM);
    }

    @PutMapping(value = "/{idnacionalidad}")
    public ResponseEntity<Nacionalidad> updateNacionalidad(@PathVariable Long idnacionalidad, @RequestBody Nacionalidad descripcion){
    	Nacionalidad nacionalidadM = nacService.findById(idnacionalidad)
    			.orElseThrow(()-> new ResourceNotFoundExcepciones(("No existe esa nacionalidad con ese Id: "+ idnacionalidad)) );
    	nacionalidadM.setDescripcion(descripcion.getDescripcion());
    	Nacionalidad updaNacionalidad = nacService.save(nacionalidadM);
    	return ResponseEntity.ok(updaNacionalidad);
    }

    // @PutMapping(value = "/{idnacionalidad}")
    // public ResponseEntity<NacionalidadM> update(@PathVariable Long idnacionalidad, @RequestBody NacionalidadM x) {
    //     NacionalidadM y = nacService.findById(idnacionalidad)
    //             .orElseThrow(() -> new ResourceNotFoundExcepciones(
    //                     ("No existe Nacionalidad Id: " + idnacionalidad)));
    //     y.setDescripcion(x.getDescripcion());

    //     NacionalidadM actualizar = nacService.save(y);
    //     return ResponseEntity.ok(actualizar);
    // }

    @DeleteMapping(value = "/{idnacionalidad}" )
    private ResponseEntity<Boolean > deleteNacionalidad(@PathVariable ("idnacionalidad") Long idnacionalidad){
    	nacService.deleteById(idnacionalidad);
    	return ResponseEntity.ok(!(nacService.findById(idnacionalidad) != null));
    	
    }
    @GetMapping("/export/{format}")
    private String exportReport(@PathVariable String format) throws FileNotFoundException, JRException {
    	return nacService.exportNacionalidades(format);
    }
    
}
