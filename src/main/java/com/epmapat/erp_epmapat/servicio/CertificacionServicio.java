package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Certificaciones;
import com.epmapat.erp_epmapat.repositorio.CertificacionesR;

@Service
public class CertificacionServicio {

	@Autowired
	private CertificacionesR dao;

	public List<Certificaciones> findDesdeHasta(Long desde, Long hasta) {
		if (desde != null || hasta != null) {
			return dao.findDesdeHasta(desde, hasta);
		} else {
			return null;
		}
	}

	// Busca por Cliente
	public List<Certificaciones> findByCliente(String cliente) {
		return dao.findByCliente(cliente);
	}

	public Certificaciones ultima() {
		return dao.findFirstByOrderByIdcertificacionDesc();
	}

	public List<Certificaciones> findAllById(Iterable<Long> ids) {
		return null;
	}

	public <S extends Certificaciones> List<S> saveAll(Iterable<S> entities) {
		return null;
	}

	public void flush() {
	}

	public <S extends Certificaciones> S saveAndFlush(S entity) {
		return null;
	}

	public <S extends Certificaciones> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Certificaciones> findById(Long id) {
		return dao.findById(id);
	}

	public long count() {
		return 0;
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(Certificaciones entity) {
		dao.delete(entity);
	}

	public void deleteAllById(Iterable<? extends Long> ids) {
	}

	public void deleteAll(Iterable<? extends Certificaciones> entities) {
	}

	public void deleteAll() {
	}

	public <S extends Certificaciones> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	public <S extends Certificaciones> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	public <S extends Certificaciones> long count(Example<S> example) {
		return 0;
	}

	public <S extends Certificaciones> boolean exists(Example<S> example) {
		return false;
	}

}
