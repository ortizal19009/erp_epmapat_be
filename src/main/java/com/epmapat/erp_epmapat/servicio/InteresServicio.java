package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.repositorio.InteresesR;

@Service
public class InteresServicio {
	
	@Autowired
	private InteresesR dao;
	@Autowired
	private FacturaServicio s_factura; 
	@Autowired
	private LecturaServicio s_lectura; 

	public List<Intereses> findAll() {
		return dao.findAll();
	}

	public List<Intereses> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	public List<Intereses> findByAnioMes(Number anio, Number mes) {
		return dao.findByAnioMes( anio, mes);
	}

	public List<Intereses> findUltimo() {
		return dao.findUltimo();
	}

	public <S extends Intereses> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Intereses> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(Intereses entity) {
		dao.delete(entity);
	}
	public Object facturaid(Long idfactura) {
		
		List<FacIntereses>lectura = s_lectura.getForIntereses(idfactura); 
		
		if(lectura.isEmpty()) {
			System.out.println("Es null");
			return s_factura.getForIntereses(idfactura); 
		}else {
			System.out.println("No es null");
			return lectura; 
		}
		
		 
	}

}
