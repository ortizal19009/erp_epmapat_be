package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Partixcerti;
import com.epmapat.erp_epmapat.repositorio.contabilidad.CertipresuR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EjecucioR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PartixcertiR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PresupueR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PartixcertiServicio {

	final private PartixcertiR dao;
	private final CertipresuR certipresuDao;
	private final PresupueR prespueDao;
	private final CertipresuServicio certiServicio;
	private final PresupueServicio presuServicio;
	private final EjecucioR ejecucioDao;

	// Partidas de una Certipresu
	public List<Partixcerti> findByIdcerti(Long idcerti) {
		return dao.findByIdcerti_IdcertiOrderByIntpre_Codpar(idcerti);
	}

	// Partixcerti por idparxcer
	public Optional<Partixcerti> findById(Long idparxcer) {
		return dao.findDetailedById(idparxcer);
	}

	// Cuenta las partidas de una certipresu
	public short contarPorIdCerti(Long idcerti) {
		return dao.countByIdcerti_Idcerti(idcerti);
	}

	// Partixcerti por intpre (Certificaciones de una partida)
	public List<Partixcerti> buscaPorIntpreDesdeHasta(Long intpre, LocalDate desde, LocalDate hasta) {
		return dao.findByIntpreDesdeHasta(intpre, desde, hasta);
	}

	// Guarda: Las Tablas foraneas se crean aqui, el front solo envia el ID
	@Transactional
	public Partixcerti save(Partixcerti nueva) {
		nueva.setIdcerti(certipresuDao.getReferenceById(nueva.getIdcerti().getIdcerti()));
		nueva.setIntpre(prespueDao.getReferenceById(nueva.getIntpre().getIntpre()));
		Partixcerti saved = dao.save(nueva);
		// Actualiza certipresu.valor (el total)
		certiServicio.recalcularValorCertificacion(nueva.getIdcerti().getIdcerti());
		// Actualiza presupue.totcerti
		presuServicio.recalculaValorTotcerti(nueva.getIntpre().getIntpre());
		// Si viene idparxcer_ => reintegro => en el registro original swreinte=1
		if (nueva.getIdparxcer_() != null) {
			dao.actualizaSwreinte(nueva.getIdparxcer_(), (short) 1);
		}
		return saved;
	}

	// Actualiza partixcerti.totprmisos
	// @Transactional Ya se usa en la llamada
	public void actualizaTotprmisos(Long idparxcer) {
		BigDecimal totprmisos = ejecucioDao.sumaPrmisoPorIdparxcer(idparxcer);
		Partixcerti partixcerti = dao.findById(idparxcer)
				.orElseThrow(() -> new IllegalArgumentException("No existe Partixcerti con id " + idparxcer));
		partixcerti.setTotprmisos(totprmisos);
		dao.save(partixcerti);
	}

	// Actualiza partixcerti (idcerti e intpre nunca se actualizan)
	@Transactional
	public Partixcerti updatePartixcerti(Long idparxcer, Partixcerti data) {
		Partixcerti partixcerti = findById(idparxcer)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						"No se encuentra el Id " + idparxcer));
		// Coloca campos modificados
		boolean swmodifi = false;
		if (data.getValor() != null) {
			partixcerti.setValor(data.getValor());
			swmodifi = true;
		}
		if (data.getDescripcion() != null)
			partixcerti.setDescripcion(data.getDescripcion());
		partixcerti.setUsumodi(data.getUsumodi());
		partixcerti.setFecmodi(data.getFecmodi());
		Partixcerti updated = dao.save(partixcerti);
		if (swmodifi) {
			// Actualiza certipresu.valor (el total de la Certificación)
			certiServicio.recalcularValorCertificacion(updated.getIdcerti().getIdcerti());
			// Actualiza presupue.totcerti
			presuServicio.recalculaValorTotcerti(updated.getIntpre().getIntpre());
		}
		return updated;
	}

	// Elimina: antes de eliminar verifica
	@Transactional
	public void deleteById(Long idparxcer) {
		Partixcerti partixcerti = dao.findById(idparxcer)
				.orElseThrow(() -> new EntityNotFoundException(
						"Partixcerti no encontrada: " + idparxcer));
		Long idCerti = partixcerti.getIdcerti().getIdcerti();
		Long idPresu = partixcerti.getIntpre().getIntpre();
		// Si es reintegro => tiene idparxcer_ => actualiza swreinte
		if (partixcerti.getIdparxcer_() != null) {
			dao.actualizaSwreinte(partixcerti.getIdparxcer_(), (short) 0);
		}
		dao.deleteById(idparxcer);
		certiServicio.recalcularValorCertificacion(idCerti);
		presuServicio.recalculaValorTotcerti(idPresu);
	}

}
