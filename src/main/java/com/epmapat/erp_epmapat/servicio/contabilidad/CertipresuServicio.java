package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;
import com.epmapat.erp_epmapat.repositorio.contabilidad.CertipresuR;

@Service
public class CertipresuServicio {

	@Autowired
	private CertipresuR dao;

	public Certipresu findFirstByOrderByNumeroDesc() {
		return dao.findFirstByOrderByNumeroDesc();
	}

	public Certipresu findByNumeroAndTipo(Long numero, int tipo) {
		return dao.findByNumeroAndTipo(numero, tipo);
	}

	public Optional<Certipresu> findById(Long id) {
		return dao.findById(id);
	}

	public List<Certipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
		return dao.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	public <S extends Certipresu> S save(S entity) {
		return dao.save(entity);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}
	
}
