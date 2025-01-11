package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Asientos;
import com.epmapat.erp_epmapat.repositorio.contabilidad.AsientosR;

@Service
public class AsientoServicio {

	@Autowired
	private AsientosR dao;

	public Asientos findFirstByOrderByAsientoDesc() {
		return dao.findFirstByOrderByAsientoDesc();
	}

	public Asientos findByTipcomAndCompro(Long tipcom, Long compro) {
		return dao.findByTipcomAndCompro(tipcom, compro);
	}

	public Long findLastComproByTipcom(Integer tipcom) {
		return dao.findLastComproByTipcom(tipcom);
	}

	public Optional<Asientos> findById(Long id) {
		return dao.findById(id);
	}

	//Busca por número de Asiento
	public List<Asientos> findAsientos(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
		return dao.findAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	//Busca por número de Cpmprobante
	public List<Asientos> findComprobantes(Integer tipcom, Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
		return dao.findComprobantes(tipcom, desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	// Un Asiento
	public Asientos obtenerAsientoPorId(Long idasiento) {
		return dao.findByIdasiento(idasiento);
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

	public <S extends Asientos> S save(S entity) {
		return dao.save(entity);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

}