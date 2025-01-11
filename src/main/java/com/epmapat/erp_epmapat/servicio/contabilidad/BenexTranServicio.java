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

	// @Override
	// public List<BenexTran> findAll(Sort sort) {
	// return null;
	// }

	// @Override
	// public List<BenexTran> findAllById(Iterable<Long> ids) {
	// return null;
	// }

	// @Override
	// public <S extends BenexTran> List<S> saveAll(Iterable<S> entities) {
	// return null;
	// }

	// @Override
	// public void flush() {
	// }

	// @Override
	// public <S extends BenexTran> S saveAndFlush(S entity) {
	// return null;
	// }

	// @Override
	// public <S extends BenexTran> List<S> saveAllAndFlush(Iterable<S> entities) {
	// return null;
	// }

	// @Override
	// public void deleteAllInBatch(Iterable<BenexTran> entities) {
	// }

	// @Override
	// public void deleteAllByIdInBatch(Iterable<Long> ids) {
	// }

	// @Override
	// public void deleteAllInBatch() {
	// }

	// @Override
	// public BenexTran getOne(Long id) {
	// return null;
	// }

	// @Override
	// public BenexTran getReferenceById(Long id) {
	// return null;
	// }

	// @Override
	// public <S extends BenexTran> List<S> findAll(Example<S> example) {
	// return null;
	// }

	// @Override
	// public <S extends BenexTran> List<S> findAll(Example<S> example, Sort sort) {
	// return null;
	// }

	// @Override
	// public Page<BenexTran> findAll(Pageable pageable) {
	// return null;
	// }

	// @Override
	// public Optional<BenexTran> findById(Long id) {
	// return Optional.empty();
	// }

	// @Override
	// public boolean existsById(Long id) {
	// return false;
	// }

	// @Override
	// public long count() {
	// return 0;
	// }

	// @Override
	// public void deleteById(Long id) {
	// }

	// @Override
	// public void delete(BenexTran entity) {
	// }

	// @Override
	// public void deleteAllById(Iterable<? extends Long> ids) {
	// }

	// @Override
	// public void deleteAll(Iterable<? extends BenexTran> entities) {
	// }

	// @Override
	// public void deleteAll() {
	// }

	// @Override
	// public <S extends BenexTran> Optional<S> findOne(Example<S> example) {
	// return Optional.empty();
	// }

	// @Override
	// public <S extends BenexTran> Page<S> findAll(Example<S> example, Pageable
	// pageable) {
	// return null;
	// }

	// @Override
	// public <S extends BenexTran> long count(Example<S> example) {
	// return 0;
	// }

	// @Override
	// public <S extends BenexTran> boolean exists(Example<S> example) {
	// return false;
	// }

	// @Override
	// public <S extends BenexTran, R> R findBy(Example<S> example,
	// Function<FetchableFluentQuery<S>, R> queryFunction) {
	// return null;
	// }

	// @Override

	// @Override

}
