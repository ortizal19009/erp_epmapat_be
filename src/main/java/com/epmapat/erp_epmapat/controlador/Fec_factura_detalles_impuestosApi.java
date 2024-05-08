package com.epmapat.erp_epmapat.controlador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles_impuestos;
import com.epmapat.erp_epmapat.servicio.Fec_factura_detalles_impuestosService;

@RestController
@RequestMapping("/facdetallesimpuestos")
@CrossOrigin("*")
public class Fec_factura_detalles_impuestosApi {
	@Autowired
	private Fec_factura_detalles_impuestosService fecfdetimpService;

	@PostMapping
	public ResponseEntity<Fec_factura_detalles_impuestos> saveFacImpuestos(
			@RequestBody Fec_factura_detalles_impuestos fecfdetimp) {
		return ResponseEntity.ok(fecfdetimpService.save(fecfdetimp));

	}
}
