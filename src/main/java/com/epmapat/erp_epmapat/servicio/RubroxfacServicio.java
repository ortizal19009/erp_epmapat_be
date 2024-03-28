package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;

@Service
public class RubroxfacServicio {
	@Autowired
	private RubroxfacR dao;

	// Campos Rubro y valor de una Planilla
	public List<Map<String, Object>> rubrosByIdfactura(Long idfactura) {
		return dao.rubrosByIdfactura(idfactura);
	}

	public Double findRubroxfac(Long idfactura) {
		return dao.findSuma(idfactura);
	}

	public List<RubroxfacI> getByFechaCobro(Date d, Date h) {
		return dao.getByFechaCobro(d, h);
	}

	public List<Rubroxfac> findByFecha(Date d, Date h) {
		return dao.findByFecha(d, h);
	}

	public List<Rubroxfac> findSinCobroRF(Long cuenta) {
		return dao.findSinCobroRF(cuenta);
	}

	// Rubros de una Planilla
	public List<Rubroxfac> getByIdfactura(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	// Rubros de una Planilla
	public List<Rubroxfac> getByIdfactura1(Long idfactura) {
		return dao.findByIdfactura1(idfactura);
	}

	// Campos Rubro.descripcion y rubroxfac.valorunitario de una Planilla
	public List<Object[]> findRubros(Long idFactura) {
		return dao.findRubros(idFactura);
	}

	// Movimientos de un Rubro
	public List<Rubroxfac> getByIdrubro(Long idrubro) {
		return dao.findByIdrubro(idrubro);
	}

	// Multa de una Factura
	public boolean getMulta(Long idfactura) {
		return dao.findMulta(idfactura);
	}

	// Recaudacion diaria - Total por Rubros (Todos)
	public List<Object[]> getRubroTotalsByFechaCobro(LocalDate fechaCobro) {
		return dao.findRubroTotalByRubroxfacAndFechacobro(fechaCobro);
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
	public List<Object[]> totalRubrosAnterior(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
		List<Object[]> resultados = dao.totalRubrosAnterior(d_fecha, h_fecha, hasta);
		return resultados;
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
	public List<Object[]> totalRubrosActual(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
		List<Object[]> resultados = dao.totalRubrosActual(d_fecha, h_fecha, hasta);
		return resultados;
	}
	// Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
	public List<Object[]> totalRubrosAnteriorByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta, Long idrecaudador) {
		List<Object[]> resultados = dao.totalRubrosAnteriorByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
		return resultados;
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
	public List<Object[]> totalRubrosActualByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta, Long idrecaudador) {
		List<Object[]> resultados = dao.totalRubrosActualByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
		return resultados;
	}

	// Grabar
	@SuppressWarnings("null")
	public <S extends Rubroxfac> S save(S entity) {
		return dao.save(entity);
	}

	@SuppressWarnings("null")
	public Optional<Rubroxfac> findById(Long id) {
		return dao.findById(id);
	}
}
