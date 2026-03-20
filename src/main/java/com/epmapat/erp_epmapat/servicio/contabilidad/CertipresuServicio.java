package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;
import com.epmapat.erp_epmapat.repositorio.administracion.DocumentosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.BeneficiariosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.CertipresuR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PartixcertiR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CertipresuServicio {

	private final CertipresuR dao;
	private final DocumentosR daoDocumentos;
	private final BeneficiariosR daoBeneficiarios;
	private final PartixcertiR daoPartixcerti;

	// Busca Certificaciones o Reintegradas (desde/hasta)
	public List<Certipresu> findDesdeHasta(int tipo, Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
		return dao.findDesdeHasta(tipo, desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	// Ultima (certificacion o reintegro)
	public Certipresu findFirstByTipoOrderByNumeroDesc(int tipo) {
		return dao.findFirstByTipoOrderByNumeroDesc(tipo);
	}

	// Valida el número de la Certificacion o la reintegrada
	public boolean existsByNumeroAndTipo(Long numero, int tipo) {
		return dao.existsByNumeroAndTipo(numero, tipo);
	}

	// Busca una Certificacion (Para reintegrada no se necesita)
	public Certipresu findByNumeroAndTipo(Long numero, int tipo) {
		return dao.findByNumeroAndTipo(numero, tipo);
	}

	// BUsca la última certificacion hasta una fecha (para el navegador)
	public Long obtenerUltimoNumeroTipo1HastaFecha(LocalDate fecha) {
		return dao.findUltimoNumeroTipo1HastaFecha(fecha);
	}

	public Optional<Certipresu> findById(Long id) {
		return dao.findById(id);
	}

	// Guarda: Las Tablas foraneas se crean aqui, el front solo envia el ID
	public Certipresu save(Certipresu nueva) {
		nueva.setIntdoc(daoDocumentos.getReferenceById(nueva.getIntdoc().getIntdoc()));
		nueva.setIdbene(daoBeneficiarios.getReferenceById(nueva.getIdbene().getIdbene()));
		nueva.setIdbeneres(daoBeneficiarios.getReferenceById(nueva.getIdbeneres().getIdbene()));
		return dao.save(nueva);
	}

	// Actualiza (solo los campos modificados)
	public Certipresu updateCertipresu(Long idcerti, Certipresu data) {
		Certipresu certipresu = findById(idcerti)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						"No se encuentra este Id " + idcerti));
		// Coloca campos (solo los modificados)
		if (data.getNumero() != null)
			certipresu.setNumero(data.getNumero());
		if (data.getFecha() != null)
			certipresu.setFecha(data.getFecha());
		if (data.getIntdoc() != null)
			certipresu.setIntdoc(data.getIntdoc());
		if (data.getNumdoc() != null)
			certipresu.setNumdoc(data.getNumdoc());
		if (data.getIdbene() != null)
			certipresu.setIdbene(data.getIdbene());
		if (data.getIdbeneres() != null)
			certipresu.setIdbeneres(data.getIdbeneres());
		if (data.getDescripcion() != null)
			certipresu.setDescripcion(data.getDescripcion());
		certipresu.setUsumodi(data.getUsumodi());
		certipresu.setFecmodi(data.getFecmodi());
		return save(certipresu);
	}

	// Suma partixcerti.valor y lo guarda en certipresu.valor
	public void recalcularValorCertificacion(Long idcerti) {
		BigDecimal total = daoPartixcerti.sumarValoresPorCertificacion(idcerti);
		Certipresu certipresu = dao.findById(idcerti)
				.orElseThrow(() -> new RuntimeException("Certificación no encontrada: " + idcerti));
		certipresu.setValor(total);
		dao.save(certipresu);
	}

	// Antes de eliminar busca (otro usuario pudo eliminar)
	public void deleteById(Long idcerti) {
		dao.findById(idcerti)
				.orElseThrow(() -> new EntityNotFoundException("Certificación no encontrada: " + idcerti));
		dao.deleteById(idcerti);
	}

}
