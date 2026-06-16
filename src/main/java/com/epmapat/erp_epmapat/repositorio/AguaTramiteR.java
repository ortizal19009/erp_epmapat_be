package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;

//import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epmapat.erp_epmapat.modelo.AguaTramite;

//@Repository
public interface AguaTramiteR extends JpaRepository<AguaTramite, Long>, JpaSpecificationExecutor<AguaTramite> {

   @Override
   @EntityGraph(attributePaths = { "idcliente_clientes", "idtipotramite_tipotramite" })
   Page<AguaTramite> findAll(Specification<AguaTramite> spec, Pageable pageable);

   @Query("""
         SELECT a
         FROM AguaTramite a
         LEFT JOIN FETCH a.idcliente_clientes
         LEFT JOIN FETCH a.idtipotramite_tipotramite
         WHERE a.idaguatramite = ?1
         """)
   Optional<AguaTramite> findById(Long idaguatramite);

   @Query("""
         SELECT a
         FROM AguaTramite a
         LEFT JOIN FETCH a.idcliente_clientes
         LEFT JOIN FETCH a.idtipotramite_tipotramite
         ORDER BY a.idaguatramite DESC
         """)
   public List<AguaTramite> findAll();

   @Query("""
         SELECT a
         FROM AguaTramite a
         LEFT JOIN FETCH a.idcliente_clientes
         LEFT JOIN FETCH a.idtipotramite_tipotramite
         WHERE a.idaguatramite >= ?1 AND a.idaguatramite <= ?2
         ORDER BY a.idaguatramite DESC
         """)
   public List<AguaTramite> findAll(Long desde, Long hasta);

   @Query(value = "SELECT * FROM aguatramite AS a JOIN clientes as c ON a.idcliente_clientes=c.idcliente WHERE c.nombre like %?1% OR LOWER(c.nombre) like %?1% or UPPER(c.nombre) like %?1% OR INITCAP(c.nombre) like %?1% ORDER by c.nombre", nativeQuery = true)
	public List<AguaTramite> findByCliente(String nombre);

	@Query("""
	      SELECT a
	      FROM AguaTramite a
	      LEFT JOIN FETCH a.idcliente_clientes
	      LEFT JOIN FETCH a.idtipotramite_tipotramite
	      WHERE a.idtipotramite_tipotramite = ?1
	        AND a.estado = ?2
	        AND ((a.feccrea BETWEEN ?3 AND ?4) OR (a.fechaterminacion BETWEEN ?5 AND ?6))
	      ORDER BY a.feccrea DESC
	      """)
	public List<AguaTramite> findByIdTipTramite(Long idtipotramite, Long estado, Date d, Date h, Date td, Date th);

}
