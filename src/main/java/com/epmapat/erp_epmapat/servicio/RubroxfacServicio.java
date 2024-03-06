package com.epmapat.erp_epmapat.servicio;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;

@Service
public class RubroxfacServicio {
	@Autowired
	private RubroxfacR dao;

	// Rubros de una Planilla
	public List<Rubroxfac> getByIdfactura(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	// Movimientos de un Rubro
	public List<Rubroxfac> getByIdrubro(Long idrubro) {
		return dao.findByIdrubro(idrubro);
	}

	// Grabar
	public <S extends Rubroxfac> S save(S entity) {
		return dao.save(entity);
	}

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

}
