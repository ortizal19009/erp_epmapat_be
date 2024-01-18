package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.Facelectro;

//@Repository
public interface FacelectroR extends JpaRepository<Facelectro, Long>{ 
   
   @Query(value = "SELECT * FROM facelectro order by idfacelectro DESC LIMIT 20", nativeQuery = true)
	public List<Facelectro> find20();

   @Query(value = "SELECT * FROM facelectro AS f WHERE f.nrofac=?1", nativeQuery = true)
	List<Facelectro> findByNrofac(String nrofac);

   //Facturas (electronicas) por Cliente
//   @Query(value = "SELECT idfacelectro, idcliente, idfactura, nrofac, idfactura_facturas, concepto, total FROM facelectro AS fe JOIN facturas AS f ON fe.idfactura_facturas = f.idfactura WHERE f.idcliente=?1 ORDER BY idfacelectro DESC LIMIT 10", nativeQuery=true)
   @Query(value = "SELECT * FROM facelectro AS fe JOIN facturas AS f ON fe.idfactura_facturas = f.idfactura WHERE f.idcliente=?1 ORDER BY idfacelectro DESC LIMIT 10", nativeQuery=true)
	public List<Facelectro> findByIdcliente(Long idcliente);

}
