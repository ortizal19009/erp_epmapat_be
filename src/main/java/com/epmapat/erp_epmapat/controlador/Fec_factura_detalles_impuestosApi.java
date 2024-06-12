package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles_impuestos;
import com.epmapat.erp_epmapat.servicio.Fec_factura_detalles_impuestosService;

@RestController
@RequestMapping("/facdetallesimpuestos")
@CrossOrigin("*")
public class Fec_factura_detalles_impuestosApi {
	@Autowired
	private Fec_factura_detalles_impuestosService fecfdetimpService;

	@PutMapping
	public ResponseEntity<Fec_factura_detalles_impuestos> saveFacImpuestos(
			@RequestBody Fec_factura_detalles_impuestos fecfdetimp) {
		Long id = fecfdetimp.getIdfacturadetalleimpuestos();
		Fec_factura_detalles_impuestos fecimpuestos = fecfdetimpService.findById(id)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe ese abonado con ese Id: " + id));
		if (fecimpuestos != null) {
			fecimpuestos.setIdfacturadetalleimpuestos(fecfdetimp.getIdfacturadetalleimpuestos());
			fecimpuestos.setIdfacturadetalle(fecfdetimp.getIdfacturadetalle());
			fecimpuestos.setCodigoporcentaje(fecfdetimp.getCodigoporcentaje());
			fecimpuestos.setCodigoimpuesto(fecfdetimp.getCodigoimpuesto());
			fecimpuestos.setBaseimponible(fecfdetimp.getBaseimponible());
			Fec_factura_detalles_impuestos upd = fecfdetimpService.save(fecimpuestos);
			return ResponseEntity.ok(upd);
		}
		return ResponseEntity.ok(fecfdetimpService.save(fecfdetimp));

	}

	@GetMapping("/factura_detalle")
	public ResponseEntity<List<Fec_factura_detalles_impuestos>> getByIdDetalle(
			@RequestParam("iddetalle") Long iddetalle) {
		return ResponseEntity.ok(fecfdetimpService.findByIdDetalle(iddetalle));
	}

	@DeleteMapping
	public ResponseEntity<Boolean> deleteImpuesto(@RequestParam("idimpuesto") Long idimpuesto) {
		fecfdetimpService.deleteById(idimpuesto);
		return ResponseEntity.ok(!(fecfdetimpService.findById(idimpuesto) != null));

	}
}
