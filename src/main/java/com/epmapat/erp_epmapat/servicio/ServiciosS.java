package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.ServiciosM;
import com.epmapat.erp_epmapat.repositorio.ServiciosR;

@Service
public class ServiciosS {
	@Autowired
	private ServiciosR serviciosR;

	public List<ServiciosM> findAll(Long q, Sort sort) {
		if (q == null) {
			return serviciosR.findAll(sort);
		} else {
			return serviciosR.findAll(q);
		}
	}

	public void deleteById(Long id) {
		serviciosR.deleteByIdQ(id);

	}

	public Optional<ServiciosM> findById(Long id) {

		return serviciosR.findById(id);
	}

	public <S extends ServiciosM> S save(S entity) {

		return serviciosR.save(entity);
	}
	public List<ServiciosM> used(Long id){
		return serviciosR.used(id);
	}

}
