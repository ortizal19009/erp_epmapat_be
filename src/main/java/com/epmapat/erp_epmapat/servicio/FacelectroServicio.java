package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Facelectro;
import com.epmapat.erp_epmapat.repositorio.FacelectroR;

@Service
public class FacelectroServicio {
   
   @Autowired
	private FacelectroR dao;

	public List<Facelectro> findByNrofac(String nrofac) {
      return dao.findByNrofac(nrofac);
   }

	//Solo para brobar en Postman
	public List<Facelectro> find20() {
		return dao.find20();
	}
	// facelectro.idfacelectro no es clave foranea de ningún Tabla (Se usa solo para probar en Postman )
	public Optional<Facelectro> findById(Long id) {
		return dao.findById(id);
	}

    public List<Facelectro> findByIdcliente(Long idcliente) {
        return dao.findByIdcliente(idcliente) ;
    }

}
