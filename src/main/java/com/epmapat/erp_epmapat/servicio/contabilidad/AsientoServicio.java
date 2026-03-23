package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Asientos;
import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;
import com.epmapat.erp_epmapat.repositorio.administracion.DocumentosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.AsientosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TransaciR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AsientoServicio {

	private final AsientosR dao;
	private final DocumentosR daoDocumentos;
	private final TransaciR daoTransaci;

	public Asientos findFirstByOrderByAsientoDesc() {
		return dao.findFirstByOrderByAsientoDesc();
	}

	// Un Asiento por Número
	public Asientos buscarPorNumero(Long numeroAsiento) {
		return dao.findByAsiento(numeroAsiento)
				.orElse(null);
	}

	// Un Asiento por Comprobante
	public Asientos findByTipcomAndCompro(Integer tipcom, Long compro) {
		return dao.findByTipcomAndCompro(tipcom, compro)
				.orElse(null);
	}

	// Ultimo comprobante
	public Long findLastComproByTipcom(Integer tipcom) {
		return dao.findLastComproByTipcom(tipcom);
	}

	// Busca primer comprobante de un tipcom (para navegador)
	public Long obtenerPrimerComprobante(Integer tipcom) {
		Long compro = dao.findFirstComproByTipcom(tipcom);
		return (compro == null) ? 1L : compro;
	}

	// Busca por número de Asiento
	public List<Asientos> findAsientos(Long desdeNum, Long hastaNum, LocalDate desdeFecha, LocalDate hastaFecha) {
		return dao.findAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	// Busca por número de Comprobante
	public List<Asientos> findComprobantes(Integer tipcom, Long desdeNum, Long hastaNum, LocalDate desdeFecha,
			LocalDate hastaFecha) {
		return dao.findComprobantes(tipcom, desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	// Un Asiento por idasiento
	public Optional<Asientos> findById(Long id) {
		return dao.findById(id);
	}

	// Siguiente asiento
	public Long obtenerSiguienteNumeroAsiento() {
		Asientos ultimaAsiento = dao.findTopByOrderByNumeroDesc();
		if (ultimaAsiento != null) {
			Long ultimoNumeroAsiento = ultimaAsiento.getAsiento();
			return ultimoNumeroAsiento + 1;
		} else {
			return 1L; // Si no hay Asiento existentes, se genera el número 1
		}
	}

	// Ultima Fecha
	public LocalDate obtenerUltimaFecha() {
		return dao.findUltimaFecha();
	}

	// Validar número de Comprobante
	public boolean valCompro(Integer tipcom, Long compro) {
		return dao.valCompro(tipcom, compro);
	}

	// Actualizar Totales del Asiento
	public void updateTotdebAndTotcre(Long idasiento, BigDecimal totdeb, BigDecimal totcre) {
		dao.updateTotdebAndTotcre(totdeb, totcre, idasiento);
	}

	// public <S extends Asientos> S save(S entity) {
	// return dao.save(entity);
	// }

	// Guarda: Las Tablas foraneas se crean aqui, el front solo envia el ID
	public Asientos save(Asientos nuevo) {
		return dao.save(nuevo);
	}

	// Actualiza (solo los campos modificados)
	public Asientos updateAsiento(Long idasiento, Asientos data) {
		Asientos asiento = findById(idasiento)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						"No se encuentra este Id " + idasiento));
		// Coloca campos (solo los modificados)
		if (data.getFecha() != null)
			asiento.setFecha(data.getFecha());
		if (data.getTipasi() != null)
			asiento.setTipasi(data.getTipasi());
		if (data.getTipcom() != null)
			asiento.setTipcom(data.getTipcom());
		if (data.getCompro() != null)
			asiento.setCompro(data.getCompro());
		if (data.getIntdoc() != null)
			asiento.setIntdoc(daoDocumentos.getReferenceById(data.getIntdoc().getIntdoc()));
		if (data.getNumdoc() != null)
			asiento.setNumdoc(data.getNumdoc());
		if (data.getIdbene() != null)
			asiento.setIdbene(data.getIdbene());
		if (data.getGlosa() != null)
			asiento.setGlosa(data.getGlosa());
		asiento.setUsumodi(data.getUsumodi());
		asiento.setFecmodi(data.getFecmodi());
		return save(asiento);
	}

	public void recalcularTotalesAsiento(Long idAsiento) {
		Asientos asiento = dao.findById(idAsiento)
				.orElseThrow(() -> new RuntimeException("Asiento no encontrado"));
		BigDecimal totalDeb = BigDecimal.ZERO;
		BigDecimal totalCre = BigDecimal.ZERO;
		List<Transaci> trans = daoTransaci.findTransaci(asiento.getIdasiento());
		for (Transaci t : trans) {
			if (t.getDebcre() == 1) {
				totalDeb = totalDeb.add(t.getValor());
			} else if (t.getDebcre() == 2) {
				totalCre = totalCre.add(t.getValor());
			}
		}
		totalDeb = totalDeb.setScale(2, RoundingMode.HALF_UP);
		totalCre = totalCre.setScale(2, RoundingMode.HALF_UP);
		asiento.setTotdeb(totalDeb);
		asiento.setTotcre(totalCre);
		dao.save(asiento);
	}

	// Antes de eliminar busca (otro usuario pudo eliminar)
	public void deleteById(Long idasiento) {
		dao.findById(idasiento)
				.orElseThrow(() -> new EntityNotFoundException("Asiento no encontrado: " + idasiento));
		dao.deleteById(idasiento);
	}


}