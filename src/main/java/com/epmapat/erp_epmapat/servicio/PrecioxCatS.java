package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.PrecioxCatM;
import com.epmapat.erp_epmapat.repositorio.PrecioxCatR;

@Service
public class PrecioxCatS {
	
	@Autowired
	private PrecioxCatR dao;

	public List<PrecioxCatM> findAll(Long c, Long dm, Long hm) {
		if(c != null && dm != null || hm != null) {
			return dao.findAll(c, dm,hm);
		}
		else {
			return dao.findAll();
		}
	}

	public List<PrecioxCatM> findConsumo(Long idcategoria, Long m3 ) {
		if(idcategoria != null && m3 != null ) {
			return dao.findConsumo(idcategoria, m3);
		}
		else {
			return null;
		}
	}

	public <S extends PrecioxCatM> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<PrecioxCatM> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(PrecioxCatM entity) {
		dao.delete(entity);		
	}
}
