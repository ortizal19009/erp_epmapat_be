package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TransaciR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransaciServicio {

	private final TransaciR dao;
	// private final AsientosR daoAsientos;
	// private final CuentasR daoCuentas;
	// private final DocumentosR daoDocumentos;
	// private final BeneficiariosR daoBeneficiarios;
	private final AsientoServicio asiServicio;
	private final EjecucioServicio ejecuServicio;

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

	// Cuenta las transacciones de un asiento
	public short countByIdasiento_Idasiento(Long idasiento) {
		return dao.countByIdasiento_Idasiento(idasiento);
	}

	// Mayor de una Cuenta
	public List<Transaci> findByCodcue(String codcue, LocalDate desde, LocalDate hasta) {
		return dao.findByCodcue(codcue, desde, hasta);
	}

	// Suma débitos o créditos de una cuenta
	public BigDecimal sumValor(String codcue, Integer debcre, LocalDate desde, LocalDate hasta) {
		return dao.sumValor(codcue, debcre, desde, hasta);
	}

	// Saldo anterior cuenta deudora
	public BigDecimal saldo(String codcue, LocalDate hasta) {
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

	// Busca Transacciones por número y fechas de los Asientos
	public List<Transaci> tranAsientos(Long desdeNum, Long hastaNum, LocalDate desdeFecha, LocalDate hastaFecha) {
		return dao.tranAsientos(desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	public Optional<Transaci> findById(Long id) {
		return dao.findById(id);
	}

	// public <S extends Transaci> S save(S entity) {
	// return dao.save(entity);
	// }

	// Guarda: Las Tablas foraneas se crean aqui, el front solo envia el ID
	// nueva.setIdasiento(daoAsientos.getReferenceById(nueva.getIdasiento().getIdasiento()));
	// nueva.setIdcuenta(daoCuentas.getReferenceById(nueva.getIdcuenta().getIdcuenta()));
	// nueva.setIntdoc(daoDocumentos.getReferenceById(nueva.getIntdoc().getIntdoc()));
	// nueva.setIdbene(daoBeneficiarios.getReferenceById(nueva.getIdbene().getIdbene()));
	public Transaci save(Transaci nueva) {
		Transaci guardada = dao.save(nueva);
		// Llamar al servicio de Asientos que actualiza totales
		asiServicio.recalcularTotalesAsiento(guardada.getIdasiento().getIdasiento());
		return guardada;
	}

	// Actualiza (solo los campos modificados)
	@Transactional
	public Transaci updateTransaci(Long inttra, Transaci data) {
		Transaci transaci = findById(inttra)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						"No se encuentra este Id " + inttra));
		boolean swmodifi = false;
		// Coloca campos (solo los modificados)
		if (data.getOrden() != null)
			transaci.setOrden(data.getOrden());
		if (data.getIdcuenta() != null)
			transaci.setIdcuenta(data.getIdcuenta());
		if (data.getCodcue() != null)
			transaci.setCodcue(data.getCodcue());
		if (data.getIdbene() != null)
			transaci.setIdbene(data.getIdbene());
		if (data.getIntdoc() != null)
			transaci.setIntdoc(data.getIntdoc());
		if (data.getNumdoc() != null)
			transaci.setNumdoc(data.getNumdoc());
		if (data.getDebcre() != null) {
			swmodifi = true;
			transaci.setDebcre(data.getDebcre());
		}
		if (data.getValor() != null) {
			swmodifi = true;
			transaci.setValor(data.getValor());
		}
		if (data.getDescri() != null)
			transaci.setDescri(data.getDescri());
		if (data.getTotbene() != null)
			transaci.setTotbene(data.getTotbene());
		transaci.setUsumodi(data.getUsumodi());
		transaci.setFecmodi(data.getFecmodi());
		Transaci actualizada = dao.save(transaci);
		// Si se modificó el valor, actualiza ejecucio.devengado si existe
		if (swmodifi) {
			ejecuServicio.buscarPorInttra(actualizada.getInttra())
					.ifPresent(ejec -> {
						Integer tipo = ejec.getTipeje();
						if (tipo == 3) {
							ejec.setDevengado(actualizada.getValor());
						} else if (tipo == 4 || tipo == 5) {
							ejec.setCobpagado(actualizada.getValor());
						}
						ejecuServicio.save(ejec);
					});
		}
		// Llama al servicio de Asientos que actualiza totales
		if (swmodifi)
			asiServicio.recalcularTotalesAsiento(actualizada.getIdasiento().getIdasiento());
		return actualizada;
	}

	// Antes de eliminar busca (otro usuario pudo eliminar)
	@Transactional
	public void deleteById(Long inttra) {
		Transaci transaci = dao.findById(inttra)
				.orElseThrow(() -> new EntityNotFoundException("Transaci no encontrada: " + inttra));
		Long idasiento = transaci.getIdasiento().getIdasiento();
		// Si tiene ejecución también la elimina
		ejecuServicio.buscarPorInttra(inttra).ifPresent(ejecu -> {
			ejecuServicio.deleteById(ejecu.getInteje()); // Elimina ejecucion si existe
		});
		dao.deleteById(inttra);
		asiServicio.recalcularTotalesAsiento(idasiento); // Recalcula total del asiento
	}

	// Balance de comprobación
	public List<Map<String, Object>> obtenerBalance(LocalDate desdeFecha, LocalDate hastaFecha) {
		return dao.obtenerBalance(desdeFecha, hastaFecha);
	}

	// Estado de situación
	public List<Map<String, Object>> obtenerEstados(Long intgrupo, LocalDate desdeFecha, LocalDate hastaFecha) {
		return dao.obtenerEstados(intgrupo, desdeFecha, hastaFecha);
	}

	// Flujo del efectivo
	public Double totalFlujo(String codcue, LocalDate desdeFecha, LocalDate hastaFecha, Long debcre) {
		Double tflujo = dao.totalFlujo(codcue, desdeFecha, hastaFecha, debcre);
		return tflujo;
	}

	@GetMapping("/tipasi")
	public List<Transaci> getByTipAsi(@RequestParam("tipasi") Long tipasi) {
		return dao.findByTipAsi(tipasi);
	}

}
