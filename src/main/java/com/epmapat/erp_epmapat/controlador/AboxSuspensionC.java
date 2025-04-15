package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.AboxSuspensionM;
import com.epmapat.erp_epmapat.servicio.AboxSuspensionS;

@RestController
@RequestMapping("/aboxsuspension")
@CrossOrigin(origins = "*")
public class AboxSuspensionC {

	@Autowired
	private AboxSuspensionS aboxsuspensionS;

	@GetMapping
	public List<AboxSuspensionM> getAllAboxSuspension(){
		return aboxsuspensionS.findAll();
	}

	@PostMapping
	public AboxSuspensionM saveAboxSuspension(@RequestBody AboxSuspensionM aboxsuspensionM) {
		return aboxsuspensionS.save(aboxsuspensionM);
	}

	@GetMapping("suspension/{idsuspension}")
	public List<AboxSuspensionM> getByIdsuspension(@PathVariable Long idsuspension){
		return aboxsuspensionS.findByIdsuspension(idsuspension);
	}
}
