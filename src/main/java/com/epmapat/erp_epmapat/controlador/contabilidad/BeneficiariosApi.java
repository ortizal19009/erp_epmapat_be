package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.servicio.contabilidad.BeneficiarioServicio;

@RestController
@RequestMapping("/beneficiarios")
@CrossOrigin("*")

public class BeneficiariosApi {

	@Autowired
	private BeneficiarioServicio beneServicio;

	// Lista de beneficiarios por nombre, codigo o ruc
	@GetMapping
	public List<Beneficiarios> findBeneficiarios(@Param(value = "nomben") String nomben,
			@Param(value = "codben") String codben, @Param(value = "rucben") String rucben,
			@Param(value = "ciben") String ciben) {
		return beneServicio.findBeneficiarios(nomben.toLowerCase(), codben, rucben, ciben);
	}

	// Busca por Nombre para los datalist
	@GetMapping("/nomben")
	public List<Beneficiarios> findByNomben(@Param(value = "nomben") String nomben) {
		return beneServicio.findByNomben(nomben.toLowerCase());
	}

	// Busca por Nombre y grupo para los datalist
	@GetMapping("/nombengru")
	public List<Beneficiarios> findByGrupoBene(@Param(value = "nomben") String nomben,
			@Param(value = "idgrupo") Long idgrupo) {
		return beneServicio.findByNombenGru(nomben.toLowerCase(), idgrupo);
	}

	// Valida el nombre del Beneficiario
	@GetMapping("/valnomben")
	public ResponseEntity<Boolean> valNomben(@Param(value = "nomben") String nomben) {
		boolean rtn = beneServicio.valNomben(nomben.toLowerCase());
		return ResponseEntity.ok(rtn);
	}

	// Ultimo código de beneficiario (por grupo)
	@GetMapping("/ultcodigo")
	public Beneficiarios findUltCodigo(@Param(value = "idgrupo") Long idgrupo) {
		return beneServicio.findUltCodigo(idgrupo);
	}

	@GetMapping("/siguicodigo")
	public String getSiguienteAsiento(@Param(value = "idgrupo") Long idgrupo) {
		return beneServicio.siguienteCodigo(idgrupo);
	}

	// Valida el codben del Beneficiario
	@GetMapping("/valcodben")
	public ResponseEntity<Boolean> valCodben(@Param(value = "codben") String codben) {
		boolean f = beneServicio.valCodben(codben);
		return ResponseEntity.ok(f);
	}

	// Valida rucben del Beneficiario
	@GetMapping("/valrucben")
	public ResponseEntity<Boolean> valRucben(@Param(value = "rucben") String rucben) {
		boolean f = beneServicio.valRucben(rucben);
		return ResponseEntity.ok(f);
	}

	// Valida ciben del Beneficiario
	@GetMapping("/valciben")
	public ResponseEntity<Boolean> valCiben(@Param(value = "ciben") String ciben) {
		boolean f = beneServicio.valCiben(ciben);
		return ResponseEntity.ok(f);
	}

	@GetMapping("/{idbene}")
	public Optional<Beneficiarios> findByIdBene(@PathVariable Long idbene) {
		return beneServicio.findById(idbene);
	}

	// Cuenta por Idifinan
	@GetMapping("/countByIdifinan")
	public Long countByIdifinan(@Param(value = "idifinan") Long idifinan) {
		return beneServicio.countByIdifinan(idifinan);
	}

	@PostMapping
	public ResponseEntity<Beneficiarios> save(@RequestBody Beneficiarios x) {
		return ResponseEntity.ok(beneServicio.save(x));
	}

	@PutMapping("/{idbene}")
	public ResponseEntity<Beneficiarios> updateBeneficiario(@PathVariable Long idbene, @RequestBody Beneficiarios x) {
		Beneficiarios y = beneServicio.findById(idbene)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta idbene: " + idbene));
		y.setNomben(x.getNomben());
		y.setTpidben(x.getTpidben());
		y.setCodben(x.getCodben());
		y.setRucben(x.getRucben());
		y.setCiben(x.getCiben());
		y.setTlfben(x.getTlfben());
		y.setDirben(x.getDirben());
		y.setMailben(x.getMailben());
		y.setTpcueben(x.getTpcueben());
		y.setCuebanben(x.getCuebanben());
		y.setIdgrupo(x.getIdgrupo());
		y.setIdifinan(x.getIdifinan());
		// y.setUsucrea(x.getUsucrea());
		// y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());
		Beneficiarios z = beneServicio.save(y);
		return ResponseEntity.ok(z);
	}

	@DeleteMapping(value = "/{idbene}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("idbene") Long idbene) {
		beneServicio.deleteById(idbene);
		return ResponseEntity.ok(!(beneServicio.findById(idbene) != null));
	}

}
