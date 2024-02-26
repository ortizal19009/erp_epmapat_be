package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.servicio.FacturaServicio;

@RestController
@RequestMapping("/facturas")
@CrossOrigin(origins = "*")

public class FacturasApi {

	@Autowired
	private FacturaServicio facServicio;

	@GetMapping
	public List<Facturas> getAll(@Param(value = "desde") Long desde, @Param(value = "hasta") Long hasta,
			@Param(value = "idcliente") Long idcliente) {
		if (desde != null)
			return facServicio.findDesde(desde, hasta);
		else {
			if (idcliente != null)
				return facServicio.findByIdcliente(idcliente);
			else
				return facServicio.findAll();
		}
	}

	@GetMapping("/idabonado/{idabonado}")
	public List<Facturas> getByIdabonado(@PathVariable("idabonado") Long idabonado) {
		return facServicio.findByIdabonado(idabonado);
	}

	@GetMapping("/{idfactura}")
	public ResponseEntity<Facturas> getById(@PathVariable Long idfactura) {
		Facturas x = facServicio.findById(idfactura)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe la Factura  Id: " + idfactura)));
		return ResponseEntity.ok(x);
	}

	// Planillas por Cliente sin cobro
	@GetMapping("/idcliente/{idcliente}")
	public List<Facturas> getSinCobro(@PathVariable("idcliente") Long idcliente) {
		return facServicio.findSinCobro(idcliente);
	}

	@GetMapping("/f_abonado/{idabonado}")
	public List<Facturas> getFacturaByAbonado(@PathVariable("idabonado") Long idabonado) {
		return facServicio.findByIdFactura(idabonado);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Facturas saveFacturas(@RequestBody Facturas x) {
		return facServicio.save( x );
	}

	@PutMapping("/{idfactura}")
	public ResponseEntity<Facturas> updateFacturas(@PathVariable long idfactura, @RequestBody Facturas x){
		Facturas y = facServicio.findById(idfactura)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No existe esa factura con ese id"+idfactura));
		y.setConveniopago(x.getConveniopago());
		y.setEstado(x.getEstado());
		y.setEstadoconvenio(x.getEstadoconvenio());
		y.setFeccrea(x.getFeccrea());
		y.setFechaanulacion(x.getFechaanulacion());
		y.setFechacobro(x.getFechacobro());
		y.setFechaconvenio(x.getFechaconvenio());
		y.setFechaeliminacion(x.getFechaeliminacion());
		y.setFechatransferencia(x.getFechatransferencia());
		y.setFecmodi(x.getFecmodi());
		y.setFormapago(x.getFormapago());
		y.setHoracobro(x.getHoracobro());
		y.setIdabonado(x.getIdabonado());
		y.setIdcliente(x.getIdcliente());
		y.setIdmodulo(x.getIdmodulo());
		y.setNrofactura(x.getNrofactura());
		y.setPagado(x.getPagado());
		y.setPorcexoneracion(x.getPorcexoneracion());
		y.setRazonanulacion(x.getRazonanulacion());
		y.setRazoneliminacion(x.getRazoneliminacion());
		y.setRazonexonera(x.getRazonexonera());
		y.setRefeformapago(x.getRefeformapago());
		y.setTotaltarifa(x.getTotaltarifa());
		y.setUsuarioanulacion(x.getUsuarioanulacion());
		y.setUsuariocobro(x.getUsuariocobro());
		y.setUsuarioeliminacion(x.getUsuarioeliminacion());
		y.setUsuariotransferencia(x.getUsuariotransferencia());
		y.setUsucrea(x.getUsucrea());
		y.setUsumodi(x.getUsumodi());
		y.setValorbase(x.getValorbase());
		Facturas updateFacturas = facServicio.save( y );
		return ResponseEntity.ok(updateFacturas);		
		}
	
	@GetMapping("/validador/{codrecaudador}")
	public ResponseEntity<Facturas> validarUltimaFactura(@PathVariable("codrecaudador")String codrecaudador){
		Facturas factura = facServicio.validarUltimafactura(codrecaudador);
		return ResponseEntity.ok(factura);
	}
	

}
