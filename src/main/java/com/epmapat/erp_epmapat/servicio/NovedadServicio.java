package com.epmapat.erp_epmapat.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.mobile.NovedadesMobile;
import com.epmapat.erp_epmapat.modelo.Novedad;
import com.epmapat.erp_epmapat.repositorio.NovedadR;

import java.util.List;
import java.util.Optional;

@Service
public class NovedadServicio {

    @Autowired
    NovedadR dao;
    public List<Novedad> findByDescri(String descripcion) {
        return dao.findByDescri(descripcion) ;
    }

    
    public List<Novedad> findAll() {
        return dao.findAll();
    }
    
    
    public <S extends Novedad> S save(S entity) {
        return dao.save(entity);
    }

    
    public Optional<Novedad> findById(Long id) {
        return dao.findById(id);
    }

    
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
// =======================================================================================


	
	public List<Novedad> findByEstado(Long estado) {
		
		return dao.findByEstado(estado);
	}
public List<NovedadesMobile> getNovedadesToMobile(){
    return dao.getNovedadesToMobile();
}


}