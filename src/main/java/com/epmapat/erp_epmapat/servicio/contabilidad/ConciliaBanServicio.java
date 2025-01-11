package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.ConciliaBan;
import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;
import com.epmapat.erp_epmapat.repositorio.contabilidad.ConciliaBanR;

@Service
public class ConciliaBanServicio {

	@Autowired
	public ConciliaBanR dao;

	public List<ConciliaBan> findAll() {
		return dao.findAll();
	}

	public ConciliaBan getConciliaBan(Cuentas idcuenta, Integer mes) {
		return dao.findByIdcuentaAndMes(idcuenta, mes);
	 }

	public <S extends ConciliaBan> S save(S entity) {
		return dao.save(entity);
	}

	public void deleteById(Long id) {
	}
	
}
