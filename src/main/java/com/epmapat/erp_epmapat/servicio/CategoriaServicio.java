package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Categorias;
import com.epmapat.erp_epmapat.repositorio.CategoriaR;

@Service
public class CategoriaServicio {

	@Autowired
	private CategoriaR dao;

	//Categorias habilitadas
	public List<String> listaCategorias() {
		return dao.listaCategorias();
	 }


	@SuppressWarnings("null")
	public List<Categorias> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	@SuppressWarnings("null")
	public <S extends Categorias> S save(S entity) {
		return dao.save(entity);
	}

	@SuppressWarnings("null")
	public Optional<Categorias> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteByIdQ(id);
	}

	public List<Categorias> used(Long id) {
		return dao.used(id);
	}

	public List<Categorias> findByDescri(String descripcion) {
		return dao.findByDescri(descripcion);
	}

	//Suma totaltarifa
	public BigDecimal sumTotalTarifa() {
		return dao.sumTotalTarifa();
	}
}
