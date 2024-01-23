package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Formacobro;
import com.epmapat.erp_epmapat.repositorio.FormacobroR;

@Service
public class FormacobroServicio {

   @Autowired
	private FormacobroR dao;

    public List<Formacobro> findAll() {
        return dao.findAll();
    }
    
}
