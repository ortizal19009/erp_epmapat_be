package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles;
import com.epmapat.erp_epmapat.servicio.Fec_factura_detallesService;

@RestController
@RequestMapping("/facturadetalles")
@CrossOrigin(origins = "*")
public class Fec_factura_detallesApi {
	@Autowired
	private Fec_factura_detallesService fecfdetService;

	@GetMapping("/detalle")
	public ResponseEntity<List<Fec_factura_detalles>> getMethodName() {
		return ResponseEntity.ok(fecfdetService.findAll());
	}

	@PostMapping
	public ResponseEntity<Fec_factura_detalles> saveFacturaDetalle(@RequestBody Fec_factura_detalles fecfdetalle) {
		return ResponseEntity.ok(fecfdetService.save(fecfdetalle));
	}

	@GetMapping("/factura")
	public ResponseEntity<List<Fec_factura_detalles>> getFecDetalleByIdFactura(Long idfactura) {
		return ResponseEntity.ok(fecfdetService.findFecDetalleByIdFactura(idfactura));
	}

}
