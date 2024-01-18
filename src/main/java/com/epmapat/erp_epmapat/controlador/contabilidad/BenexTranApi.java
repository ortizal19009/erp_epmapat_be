// package com.epmapat.erp_epmapat.controlador.contabilidad;

// public class BenexTranApi {
   
// }

package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.BenexTran;
import com.epmapat.erp_epmapat.servicio.contabilidad.BenexTranServicio;

@RestController
@RequestMapping("/benextran")
@CrossOrigin(origins = "*")
public class BenexTranApi {

	@Autowired
	private BenexTranServicio benextranServicio;

	@GetMapping
	public ResponseEntity<List<BenexTran>> getAllBenexTran() {
		return ResponseEntity.ok(benextranServicio.findAll());
	}

	@GetMapping("/egresos")
	private ResponseEntity<List<BenexTran>> getEgresos(@RequestParam ("codcue" ) String codcue){
		return ResponseEntity.ok(benextranServicio.getEgresos(codcue));
	}

	@GetMapping("/beneficiario")
	public ResponseEntity<List<BenexTran>> getByIdBene(@RequestParam("idbene") Long idbene){
		return ResponseEntity.ok(benextranServicio.getByIdBene(idbene));
	}

	@GetMapping("/cxp")
	public ResponseEntity<List<BenexTran>> getCxP(){
		return ResponseEntity.ok(benextranServicio.getCxP());
	}

	@GetMapping("/acfp")	//Anticipos, CxC, F.Terceros o CxP
	public ResponseEntity<List<BenexTran>> getACFP(@RequestParam("hasta") Date hasta, 
		@RequestParam("nomben") String nomben, @RequestParam("tiptran") Integer tiptran, @RequestParam("codcue") String codcue){
		return ResponseEntity.ok(benextranServicio.getACFP( hasta, nomben, tiptran, codcue));
	}

   @PostMapping
	public BenexTran saveBenexTran(@RequestBody BenexTran x) {
		return benextranServicio.save( x );
	}
   
}
