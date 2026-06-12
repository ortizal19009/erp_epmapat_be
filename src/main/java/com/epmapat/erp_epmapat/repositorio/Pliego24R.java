package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Pliego24;

public interface Pliego24R  extends JpaRepository<Pliego24, Long> {

   //Nuevo Pliego Trarifario
   @EntityGraph(attributePaths = { "idcategoria" })
   @Query("SELECT p FROM Pliego24 p LEFT JOIN FETCH p.idcategoria ORDER BY p.idcategoria.idcategoria, p.desde")
	public List<Pliego24> findTodos();

   //Tarifas de todas las Categorias de un determinado consumo (m3) Se usa solo en  la Simulación
   @EntityGraph(attributePaths = { "idcategoria" })
   @Query("SELECT p FROM Pliego24 p LEFT JOIN FETCH p.idcategoria WHERE p.desde <= ?1 AND p.hasta >= ?1 ORDER BY p.idcategoria.idcategoria, p.desde")
	public List<Pliego24> findConsumos(Long m3);

   //Tarifa de un determinado conusmo(m3) de una Categoria y de una Gradualidad
   @EntityGraph(attributePaths = { "idcategoria" })
   @Query("SELECT p FROM Pliego24 p LEFT JOIN FETCH p.idcategoria WHERE p.idcategoria.idcategoria = ?1 AND p.desde <= ?2 AND p.hasta >= ?2")
	public List<Pliego24> findBloque(Long idcategoria, Long m3);

   @EntityGraph(attributePaths = { "idcategoria" })
   @Query("SELECT p FROM Pliego24 p LEFT JOIN FETCH p.idcategoria WHERE p.idcategoria.idcategoria = ?1 AND p.desde <= ?2 AND p.hasta >= ?2")
public Pliego24 _findBloque(int idcategoria, int m3);

}
