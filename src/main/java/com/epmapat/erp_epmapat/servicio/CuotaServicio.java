package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Cuotas;
import com.epmapat.erp_epmapat.repositorio.CuotasR;

@Service
public class CuotaServicio {
   
   @Autowired
	private CuotasR dao;

   //Solo para brobar en Postman
	public List<Cuotas> find10() {
		return dao.find10();
	}

    public List<Cuotas> findByIdconvenio(Long idconvenio) {
        return dao.findByIdconvenio( idconvenio );
    }

    public <S extends Cuotas> S save(S entity) {
    	return dao.save(entity);
    }
}
