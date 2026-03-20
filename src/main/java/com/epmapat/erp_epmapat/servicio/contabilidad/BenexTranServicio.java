package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.BenexTran;
import com.epmapat.erp_epmapat.repositorio.contabilidad.BenexTranR;

@Service
public class BenexTranServicio {

	@Autowired
	private BenexTranR dao;

	public List<BenexTran> findAll() {
		return dao.findAll();
	}

	public <S extends BenexTran> S save(S entity) {
		return dao.save(entity);
	}

	public List<BenexTran> getEgresos(String codcue) {
		return dao.getEgresos(codcue);
	}

	public List<BenexTran> getByIdBene(Long idbene, Date desde, Date hasta) {
		return dao.getByIdBeneDesdeHasta(idbene, desde, hasta);
	}

	public List<BenexTran> getCxP() {
		return dao.getCxP();
	}

	//ACFP sin liquidar
	public List<BenexTran> getACFP(Date hasta, String nomben, Integer tiptran, String codcue) {
		return dao.getACFP(hasta, nomben, tiptran, codcue);
	}

	// Verifica si un Beneficiario tiene benextran
	public boolean existeByIdbene(Long idbene) {
		return dao.existeByIdbene(idbene);
	}

	//BenexTran por idbenxtra
	public Optional<BenexTran> findById(Long id) {
		return dao.findById(id);
	}

	// Actualizar
   public BenexTran actualizar(Long idbenxtra, BenexTran x) {
      Optional<BenexTran> y = dao.findById(idbenxtra);
      if (y.isPresent()) {
         BenexTran benextran = y.get();

         benextran.setIdbene(x.getIdbene());
         benextran.setIntdoc(x.getIntdoc());
         benextran.setNumdoc(x.getNumdoc());
         benextran.setValor(x.getValor());
         benextran.setTotpagcob(x.getTotpagcob());
         benextran.setIdpagcob(x.getIdpagcob());
			benextran.setIntpre(x.getIntpre());
			benextran.setCodparreci(x.getCodparreci());
			benextran.setCodcuereci(x.getCodcuereci());
			benextran.setAsierefe(x.getAsierefe());
         return dao.save(benextran);
      } else {
         throw new RuntimeException("Benextran no encontrado con id " + idbenxtra);
      }
   }

}
