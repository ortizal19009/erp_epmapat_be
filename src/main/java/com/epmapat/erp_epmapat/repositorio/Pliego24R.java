package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Pliego24;

public interface Pliego24R  extends JpaRepository<Pliego24, Long> {

   //Nuevo Pliego Trarifario
   @Query(value = "SELECT * FROM pliego24 order by idcategoria, desde", nativeQuery=true)
	public List<Pliego24> findTodos();

   //Tarifas de todas las Categorias de un determinado consumo (m3) Se usa solo en  la Simulación
   @Query(value = "SELECT * FROM pliego24 WHERE desde <=?1 and hasta >=?1 order by idcategoria, desde", nativeQuery=true)
	public List<Pliego24> findConsumos(Long m3);

   //Tarifa de un determinado conusmo(m3) de una Categoria y de una Gradualidad
   @Query(value = "SELECT * FROM pliego24 WHERE idcategoria=?1 and desde<=?2 and hasta>=?2", nativeQuery=true)
	public List<Pliego24> findBloque(Long idcategoria, Long m3);

   @Query(value = "SELECT * FROM pliego24 WHERE idcategoria=?1 and desde<=?2 and hasta>=?2 LIMIT 1", nativeQuery=true)
public Pliego24 _findBloque(int idcategoria, int m3);

}
