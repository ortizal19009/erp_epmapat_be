package com.epmapat.erp_epmapat.controlador;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.servicio.RubroxfacServicio;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/rubroxfac")
@CrossOrigin(origins = "*")

public class RubroxfacApi {

   @Autowired
   private RubroxfacServicio rxfServicio;

   @GetMapping("/sumavalores")
   public Double findRubroxfac(@RequestParam("idfactura") Long idfactura) {
      return rxfServicio.findRubroxfac(idfactura);
   }

   @GetMapping("/reportes/fechaCobro")
   public List<RubroxfacI> getByFechaCobro(@RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return rxfServicio.getByFechaCobro(d, h);
   }

   @GetMapping("/reportes/fecha")
   public List<Rubroxfac> getByFecha(@RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return rxfServicio.findByFecha(d, h);
   }

   @GetMapping("/sincobro/rubxfa")
   public List<Rubroxfac> getSinCobroRF(@RequestParam("cuenta") Long cuenta) {
      return rxfServicio.findSinCobroRF(cuenta);
   }

   /* ============================================== */
   @GetMapping
   public List<Rubroxfac> getByIdfactura(@Param(value = "idfactura") Long idfactura) {
      return rxfServicio.getByIdfactura(idfactura);
   }

   @GetMapping("/esiva")
   public List<Rubroxfac> getByIdfactura1(@Param(value = "idfactura") Long idfactura) {
      return rxfServicio.getByIdfactura1(idfactura);
   }

   // Campos Rubro.descripcion y rubroxfac.valorunitario de una Planilla
   @GetMapping("/rubros")
   public ResponseEntity<List<Object[]>> findRubros(@Param(value = "idfactura") Long idfactura) {
      List<Object[]> x = rxfServicio.findRubros(idfactura);
      return ResponseEntity.ok(x);
   }

   @GetMapping("/rubro/{idrubro}")
   public List<Rubroxfac> getByIdrubro(@PathVariable Long idrubro) {
      return rxfServicio.getByIdrubro(idrubro);
   }

   // Verifica si una Factura tiene Multa
   @GetMapping("/multa")
   public boolean getMulta(@Param(value = "idfactura") Long idfactura) {
      return rxfServicio.getMulta(idfactura);
   }

   @GetMapping("/{idrubroxfac}")
   public Optional<Rubroxfac> findByIdRubroxfac(@PathVariable Long idrubroxfac) {
      return rxfServicio.findById(idrubroxfac);
   }

   @PostMapping
   public Rubroxfac save(@RequestBody Rubroxfac x) {
      return rxfServicio.save(x);
   }

   @PutMapping("/{idrubroxfac}")
	public ResponseEntity<Rubroxfac> updateRubroxfac(@PathVariable Long idrubroxfac, @RequestBody Rubroxfac x) {
		Rubroxfac y = rxfServicio.findById( idrubroxfac )
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta idrubroxfac: " + idrubroxfac));
		y.setValorunitario(x.getValorunitario());
		Rubroxfac z = rxfServicio.save(y);
		return ResponseEntity.ok( z );
	}

}
