package com.epmapat.erp_epmapat.controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.SuspensionesM;
import com.epmapat.erp_epmapat.servicio.SuspensionesS;

@RestController
@RequestMapping("/suspensiones")
@CrossOrigin("*")

public class SuspensionesC {

	@Autowired
	private SuspensionesS suspensionesS;

	@GetMapping
	public List<SuspensionesM> getAllSuspensiones(){
		return suspensionesS.findAll();
	}
	
	@PostMapping
	public SuspensionesM saveSuspencion(@RequestBody SuspensionesM suspensionesM) {
		return suspensionesS.save(suspensionesM);
	}
	@GetMapping("/susp/{desde}/{hasta}")
	public List<SuspensionesM> getByFecha(@PathVariable String desde, @PathVariable String hasta){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		Date h = null;		
		try {
			d = (Date) dateFormat.parse(desde);
			h = (Date) dateFormat.parse(hasta);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return suspensionesS.findByFecha(d, h);
	}
	@GetMapping("/habbydate/{desde}/{hasta}")
	public List<SuspensionesM> getByFechaHabilitacion(@PathVariable String desde, @PathVariable String hasta){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		Date h = null;		
		try {
			d = (Date) dateFormat.parse(desde);
			h = (Date) dateFormat.parse(hasta);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return suspensionesS.findByFechaHabilitaciones(d, h);
	}
	@GetMapping("/susp/{numero}")
	public List<SuspensionesM> getByNumero(@PathVariable Long numero){
		return suspensionesS.findByNumero(numero);
	}
	@GetMapping("/habilitaciones")
	public List<SuspensionesM> getByHabilitaciones(){
		return suspensionesS.findHabilitaciones();
	}
	@GetMapping("/ultimo")
	public SuspensionesM getUltima(){
		return suspensionesS.findFirstByOrderByIdsuspensionDesc();
	}

}
