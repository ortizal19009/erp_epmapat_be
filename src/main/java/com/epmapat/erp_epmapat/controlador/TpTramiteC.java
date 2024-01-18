package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.TpTramiteM;
import com.epmapat.erp_epmapat.servicio.TpTramiteS;

@RestController
@RequestMapping("/tptramite")
@CrossOrigin("*")

public class TpTramiteC {

	@Autowired
	private TpTramiteS tptramiServicio;

	@GetMapping
	public List<TpTramiteM> getAllTpTramite(){
		return tptramiServicio.findAll();
	}
	
	@GetMapping("/{idtptramite}")
	public ResponseEntity<TpTramiteM> findByIdTptramite(@PathVariable Long idtptramite){
		TpTramiteM tptramiteM = tptramiServicio.findById(idtptramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No se encuentra este tptramtie con este id: "+idtptramite));
		return ResponseEntity.ok(tptramiteM);
	}

}
