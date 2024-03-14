package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.FacturasI;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.repositorio.FacturasR;

@Service
public class FacturaServicio {

	@Autowired
	private FacturasR dao;

	public Facturas validarUltimafactura(String codrecaudador) {
		return dao.validarUltimafactura(codrecaudador);
	}

	public List<Facturas> findByUsucobro(Long idusuario, Date dfecha, Date hfecha) {
		return dao.findByUsucobro(idusuario, dfecha, hfecha);
	}

	public List<FacturasI> findByFechacobro(Date fechacobro) {
		return dao.findByFechacobro(fechacobro);
	}
	
	/* ================================================================= */

	public List<Facturas> findAll() {
		return dao.findAll();
	}

	public List<Facturas> findDesde(Long desde, Long hasta) {
		return dao.findDesde(desde, hasta);
	}

	@SuppressWarnings("null")
	public Optional<Facturas> findById(Long idfactura) {
		return dao.findById(idfactura);
	}

	// Planillas por Cliente
	public List<Facturas> findByIdcliente(Long idcliente) {
		return dao.findByIdcliente(idcliente);
	}

	// Planillas por Abonado
	public List<Facturas> findByIdabonado(Long idabonado) {
		return dao.findByIdabonado(idabonado);
	}

	// Una Planilla (como lista)
	public List<Facturas> buscarPlanilla(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	//Planillas por Abonado y Fecha
	public List<Facturas> buscarPorAbonadoYFechaCreacionRange(Long idabonado, LocalDate fechaDesde,
			LocalDate fechaHasta) {
		return dao.findByAbonadoAndFechaCreacionRange(idabonado, fechaDesde, fechaHasta);
	}

	// Planillas Sin Cobrar de un Cliente
	public List<Facturas> findSinCobro(Long idcliente) {
		return dao.findSinCobro(idcliente);
	}

	// Planillas Sin Cobrar de un Abonado (para Multas)
	public List<Long> findSinCobroAbo(Long idabonado) {
		return dao.findSinCobroAbo(idabonado);
	}

	// Planillas Sin Cobrar de un Abonado (Para convenios)
	public List<Facturas> findSinCobrarAbo(Long idabonado) {
		return dao.findSinCobrarAbo(idabonado);
	}

	@SuppressWarnings("null")
	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public List<Facturas> findByIdFactura(Long idabonado) {
		return dao.findByIdFactura(idabonado);
	}

	public List<Facturas> findByNrofactura(String nrofactura) {
		return dao.findByNrofactura(nrofactura);
	}

	@SuppressWarnings("null")
	public <S extends Facturas> S save(S entity) {
		return dao.save(entity);
	}

	public FacturasR getDao() {
		return dao;
	}

	public void setDao(FacturasR dao) {
		this.dao = dao;
	}
}
