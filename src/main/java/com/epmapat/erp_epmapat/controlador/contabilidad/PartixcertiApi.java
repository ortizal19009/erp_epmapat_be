package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Partixcerti;
import com.epmapat.erp_epmapat.servicio.contabilidad.PartixcertiServicio;

@RestController
@RequestMapping("/partixcerti")
@CrossOrigin("*")

public class PartixcertiApi {

	@Autowired
	private PartixcertiServicio parxcerServicio;
	
	@GetMapping("/idcerti/{idcerti}")
	public List<Partixcerti> getByIdCerti(@PathVariable Long idcerti){
		return parxcerServicio.findByIdcerti(idcerti);
	}

	@GetMapping("/{idparxcer}")
	public Optional<Partixcerti> findById(@PathVariable Long idparxcer ) {
		return parxcerServicio.findById( idparxcer );
	}

	@PostMapping
	public Partixcerti savePartiCerti(@RequestBody Partixcerti partixcerti) {
		return parxcerServicio.save(partixcerti);
	}
	@PutMapping("/{idparxcer}")
	public ResponseEntity<Partixcerti> updatePartixCerti(@PathVariable Long idparxcer, @RequestBody Partixcerti partixcertim){
		Partixcerti partixcerti = parxcerServicio.findById(idparxcer).
				orElseThrow(()-> new ResourceNotFoundExcepciones("No existe ese Id: "+idparxcer));
		partixcerti.setDescripcion(partixcertim.getDescripcion());
		partixcerti.setValor(partixcertim.getValor());
		partixcerti.setSaldo(partixcertim.getSaldo());
		partixcerti.setTotprmisos(partixcertim.getTotprmisos());
		partixcerti.setUsucrea(partixcertim.getUsucrea());
		partixcerti.setFeccrea(partixcertim.getFeccrea());
		partixcerti.setUsumodi(partixcertim.getUsumodi());
		partixcerti.setFecmodi(partixcertim.getFecmodi());
		partixcerti.setInteje(partixcertim.getInteje());
		partixcerti.setIntpre(partixcertim.getIntpre());
		partixcerti.setIdcerti(partixcertim.getIdcerti());
		partixcerti.setIdparxcer_(partixcertim.getIdparxcer_());
		Partixcerti updateParxCer = parxcerServicio.save(partixcerti);
		return ResponseEntity.ok(updateParxCer);
	}

}
