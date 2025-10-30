package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;

//import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.AguaTramite;

//@Repository
public interface AguaTramiteR extends JpaRepository<AguaTramite, Long>{

   @Query(value = "SELECT * FROM aguatramite order by idaguatramite DESC LIMIT 10", nativeQuery = true)
   public List<AguaTramite> findAll();

   @Query(value = "SELECT * FROM aguatramite AS a WHERE a.idaguatramite >= ?1 and a.idaguatramite <= ?2 ", nativeQuery = true)
   public List<AguaTramite> findAll(Long desde, Long hasta);

   @Query(value = "SELECT * FROM aguatramite AS a JOIN clientes as c ON a.idcliente_clientes=c.idcliente WHERE c.nombre like %?1% OR LOWER(c.nombre) like %?1% or UPPER(c.nombre) like %?1% OR INITCAP(c.nombre) like %?1% ORDER by c.nombre", nativeQuery = true)
	public List<AguaTramite> findByCliente(String nombre);

   @Query(value = "SELECT * FROM aguatramite WHERE idtipotramite_tipotramite = ?1 and estado = ?2 AND ((feccrea BETWEEN ?3 AND ?4)OR( fechaterminacion BETWEEN ?5 AND ?6) )ORDER BY feccrea DESC", nativeQuery = true)
	public List<AguaTramite> findByIdTipTramite(Long idtipotramite, Long estado, Date d, Date h, Date td, Date th);


}
