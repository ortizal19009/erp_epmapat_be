package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Ccertificaciones;
import com.epmapat.erp_epmapat.repositorio.CcertificacionesR;

@Service
public class CcertificacionServicio {

	@Autowired
	private CcertificacionesR dao;

	public List<Ccertificaciones> findDesdeHasta(Long desde, Long hasta) {
		if (desde != null || hasta != null) {
			return dao.findDesdeHasta(desde, hasta);
		} else {
			return null;
		}
	}

	// Busca por Cliente
	public List<Ccertificaciones> findByCliente(String cliente) {
		return dao.findByCliente(cliente);
	}

	public Ccertificaciones ultima() {
		return dao.findFirstByOrderByIdcertificacionDesc();
	}

	public List<Ccertificaciones> findAllById(Iterable<Long> ids) {
		return null;
	}

	public <S extends Ccertificaciones> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	public void flush() {
	}

	public <S extends Ccertificaciones> S saveAndFlush(S entity) {
		return null;
	}

	public <S extends Ccertificaciones> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Ccertificaciones> findById(Long id) {
		return dao.findById(id);
	}

	public long count() {
		return 0;
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(Ccertificaciones entity) {
		dao.delete(entity);
	}

	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	public void deleteAll(Iterable<? extends Ccertificaciones> entities) {
	}

	public void deleteAll() {
	}

	public <S extends Ccertificaciones> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	public <S extends Ccertificaciones> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	public <S extends Ccertificaciones> long count(Example<S> example) {
		return 0;
	}

	public <S extends Ccertificaciones> boolean exists(Example<S> example) {
		return false;
	}

}
