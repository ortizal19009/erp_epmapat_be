package com.epmapat.erp_epmapat.repositorio;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Recaudaxcaja;

public interface RecaudaxcajaR extends JpaRepository<Recaudaxcaja, Serializable> {

  	//Busca por Caja
 	@Query(value = "SELECT * FROM recaudaxcaja WHERE idcaja_cajas=?1 AND fechainiciolabor BETWEEN (?2) AND (?3) order by fechainiciolabor desc", nativeQuery = true)
 	public List<Recaudaxcaja> findByCaja(Long idcaja, Date desde, Date hasta);  
 	//Ultima conexión 
 	@Query(value = "SELECT * FROM recaudaxcaja WHERE idcaja_cajas=?1 order by idrecaudaxcaja desc limit 1", nativeQuery = true)
 	public Recaudaxcaja findLastConexion(Long idcaja); 

	@Query(value = """
			SELECT rx.*
			FROM recaudaxcaja rx
			JOIN (
				SELECT idcaja_cajas, MAX(idrecaudaxcaja) AS idrecaudaxcaja
				FROM recaudaxcaja
				GROUP BY idcaja_cajas
			) ult ON ult.idrecaudaxcaja = rx.idrecaudaxcaja
			WHERE rx.estado = 1
			""", nativeQuery = true)
	List<Recaudaxcaja> findCajasAbiertas();
   
}
