package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TransaciR;

@Service
public class TransaciServicio {

	@Autowired
	private TransaciR dao;

	// Cuenta tiene Transacciones
	public boolean tieneTransaci(String codcue) {
		return dao.tieneTransaci(codcue);
	}

	// Asiento tiene Transacciones
	public boolean existsByIdasiento(Long idasiento) {
		return dao.existsByIdasiento(idasiento);
	}

	// Bancos
	public List<Transaci> findMovibank(Long idcuenta, Integer mes) {
		return dao.findMovibank(idcuenta, mes);
	}

	// Transacciones de un Asiento
	public List<Transaci> findTransaci(Long idasiento) {
		return dao.findTransaci(idasiento);
	}

	// Mayor de una Cuenta
	public List<Transaci> findByCodcue(String codcue, Date desde, Date hasta) {
		return dao.findByCodcue(codcue, desde, hasta);
	}

	// Suma débitos o créditos de una cuenta
	public BigDecimal sumValor(String codcue, Integer debcre, Date desde, Date hasta) {
		return dao.sumValor(codcue, debcre, desde, hasta);
	}

	// Saldo anterior cuenta deudora
	public BigDecimal saldo(String codcue, Date hasta) {
		List<Transaci> transacciones = dao.findByCodcueHasta(codcue, hasta);
		BigDecimal saldo = BigDecimal.ZERO;
		for (Transaci transaccion : transacciones) {
			BigDecimal valor = transaccion.getValor();
			String cuenta1 = transaccion.getCodcue().substring(0, 1);
			String cuenta2 = transaccion.getCodcue().substring(0, 2);
			if (cuenta1.equals("1") || cuenta2.equals("63") || cuenta2.equals("91")) {
				if (transaccion.getDebcre() == 1)
					saldo = saldo.add(valor);
				else
					saldo = saldo.subtract(valor);
			} else {
				if (transaccion.getDebcre() == 1)
					saldo = saldo.subtract(valor);
				else
					saldo = saldo.add(valor);
			}
		}
		return saldo;
	}

	//Busca Transacciones por número y fechas de los Asientos
	public List<Transaci> tranAsientos(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
		return dao.tranAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	public <S extends Transaci> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Transaci> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	// Balance de comprobación
	public List<Map<String, Object>> obtenerBalance(Date desdeFecha, Date hastaFecha) {
		return dao.obtenerBalance(desdeFecha, hastaFecha);
	}

	// Estado de situación
	public List<Map<String, Object>> obtenerEstados(Long intgrupo, Date desdeFecha, Date hastaFecha) {
		return dao.obtenerEstados(intgrupo, desdeFecha, hastaFecha);
	}

	// Flujo del efectivo
	public Double totalFlujo(String codcue, Date desdeFecha, Date hastaFecha, Long debcre) {
		Double tflujo = dao.totalFlujo(codcue, desdeFecha, hastaFecha, debcre);
		return tflujo;
	}


	// public ResponseEntity<List<Transaci>> getByTipAsi(@RequestParam("tipasi") Long tipasi) {
	// 	return ResponseEntity.ok(dao.findByTipAsi(tipasi));
	// }
	public List<Transaci> getByTipAsi( Long tipasi) {
		return dao.findByTipAsi(tipasi);
	}
	public List<Transaci> aperInicial(String codcue) {
		// TODO Auto-generated method stub
		return dao.aperInicial(codcue);
	}
}
