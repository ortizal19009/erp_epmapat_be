package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.repositorio.FacturasR;

@Service
public class FacturaServicio {

	@Autowired
	private FacturasR dao;

	public List<Facturas> findAll() {
		return dao.findAll();
	}

	public List<Facturas> findDesde(Long desde, Long hasta) {
		return dao.findDesde( desde, hasta );
	}

	public Optional<Facturas> findById(Long idfactura) {
		return dao.findById(idfactura);
	}
	//Planillas por Cliente
	public List<Facturas> findByIdcliente(Long idcliente){
		return dao.findByIdcliente(idcliente);
	}
	//Planillas por Abonado
	public List<Facturas> findByIdabonado(Long idabonado){
		return dao.findByIdabonado(idabonado);
	}
	//Planillas por Cliente sin Cobro
	public List<Facturas> findSinCobro(Long idcliente){
	return dao.findSinCobro(idcliente);
	}
	
	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public List<Facturas> findByIdFactura(Long idabonado) {
		return dao.findByIdFactura(idabonado);
	}

	// public List<Facturas> findByNroFactura(String nfactura){
	// 	return dao.findByNroFactura(nfactura);
	// }

	public <S extends Facturas> S save(S entity) {
		return dao.save(entity);
	}

	public FacturasR getDao() {
		return dao;
	}

	public void setDao(FacturasR dao) {
		this.dao = dao;
	}
	public Facturas validarUltimafactura(String codrecaudador) {
		return dao.validarUltimafactura(codrecaudador);
	}

}
