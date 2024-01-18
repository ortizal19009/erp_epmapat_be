package com.epmapat.erp_epmapat.repositorio;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Recaudaxcaja;

public interface RecaudaxcajaR extends JpaRepository<Recaudaxcaja, Serializable> {

  	//Busca por Caja
 	@Query(value = "SELECT * FROM recaudaxcaja WHERE idcaja_cajas=?1 AND fechainiciolabor BETWEEN (?2) AND (?3) order by fechainiciolabor", nativeQuery = true)
 	public List<Recaudaxcaja> findByCaja(Long idcaja, Date desde, Date hasta); 
   
}
