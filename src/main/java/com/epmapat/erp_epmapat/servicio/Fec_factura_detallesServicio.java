package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_detallesR;
@Service
public class Fec_factura_detallesServicio {
    @Autowired
    private Fec_factura_detallesR dao; 
    public List<Fec_factura_detalles> findAll(){
        return dao.findAll(); 
    }
    public <S extends Fec_factura_detalles> S save(S entity) {
		return dao.save(entity);
	} 
}
