package com.epmapat.erp_epmapat.controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.epmapat.erp_epmapat.modelo.SuspensionesM;
import com.epmapat.erp_epmapat.servicio.SuspensionesS;

@RestController
@RequestMapping("/suspensiones")


public class SuspensionesC {

	@Autowired
	private SuspensionesS suspensionesS;

    @Autowired
    private com.epmapat.erp_epmapat.servicio.RetiroMedidoresServicio retiros;

    @PostMapping("/{id}/retiros")
    public SuspensionesM retirarMedidores(@PathVariable Long id,
            @javax.validation.Valid @RequestBody com.epmapat.erp_epmapat.DTO.RetiroMedidoresRequest solicitud,
            javax.servlet.http.HttpServletRequest request) {
        Object autenticado = request.getAttribute("jwtUserId");
        Long usuario = autenticado instanceof Number numero ? numero.longValue() : solicitud.idusuario();
        return retiros.retirar(id, solicitud, usuario);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> errorValidacion(
            org.springframework.web.server.ResponseStatusException error) {
        return org.springframework.http.ResponseEntity.status(error.getStatus())
            .body(java.util.Map.of("message", error.getReason() == null ? "No se pudo completar la operacion" : error.getReason()));
    }

	@GetMapping
	public List<SuspensionesM> getAllSuspensiones(){
		return suspensionesS.conCuentas(suspensionesS.findAll());
	}
	
    @GetMapping("/{id}")
    public SuspensionesM getById(@PathVariable Long id) {
        SuspensionesM registro = suspensionesS.findById(id).orElseThrow(() ->
            new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "No existe el registro"));
        return suspensionesS.conCuentas(List.of(registro)).get(0);
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
		return suspensionesS.conCuentas(suspensionesS.findByFecha(d, h));
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
		return suspensionesS.conCuentas(suspensionesS.findByFechaHabilitaciones(d, h));
	}
	@GetMapping("/susp/{numero}")
	public List<SuspensionesM> getByNumero(@PathVariable Long numero){
		return suspensionesS.conCuentas(suspensionesS.findByNumero(numero));
	}
	@GetMapping("/habilitaciones")
	public List<SuspensionesM> getByHabilitaciones(){
		return suspensionesS.conCuentas(suspensionesS.findHabilitaciones());
	}
	@GetMapping("/ultimo")
	public SuspensionesM getUltima(){
		return suspensionesS.findFirstByOrderByIdsuspensionDesc();
	}

	@GetMapping("/ultimo/portipo")
	public SuspensionesM getUltimaPorTipo(@RequestParam Long tipo){
		return suspensionesS.findUltimaPorTipo(tipo);
	}

}
