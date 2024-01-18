package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.TpCertifica;
import com.epmapat.erp_epmapat.repositorio.TpCertificaR;

@Service
public class TpCertificaServicio {
   
   @Autowired
	TpCertificaR dao;

   public List<TpCertifica> findAll() {
		return dao.findAll();
	}
}
