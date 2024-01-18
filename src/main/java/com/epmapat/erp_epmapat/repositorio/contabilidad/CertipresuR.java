package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;

public interface CertipresuR extends JpaRepository<Certipresu, Long>{

	@Query(value = "SELECT * FROM certipresu ORDER BY idcerti DESC LIMIT 10 ", nativeQuery = true)
	public List<Certipresu> findLastTen();

	@Query(value="SELECT * FROM certipresu WHERE tipo = 2 AND fecha BETWEEN (?1) AND (?2)", nativeQuery = true)
	public List<Certipresu> findByFecha(Date desde, Date hasta);
   
	@Query(value = "SELECT * FROM certipresu ORDER BY numero DESC LIMIT 1", nativeQuery = true)
	public List<Certipresu> findMax();

	@Query(value="SELECT * FROM certipresu WHERE numero BETWEEN (?1) AND (?2)", nativeQuery = true)
	public List<Certipresu> findByNumero(Long desde, Long hasta);

	@Query(value ="SELECT * FROM certipresu WHERE fecha BETWEEN  (?3) AND (?4) OR numero BETWEEN (?1) AND (?2) ORDER BY numero ASC", nativeQuery = true)
	public List<Certipresu> findByDorN(Long d_num, Long h_num, Date d_date, Date h_date);

	@Query(value ="SELECT * FROM certipresu WHERE tipo= 1 and numero BETWEEN (?1) AND (?2) and fecha BETWEEN (?3) AND (?4) ORDER BY numero ASC", nativeQuery = true)
	public List<Certipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);

	//Ultima certificación
	Certipresu findFirstByOrderByNumeroDesc();
 
	//Validar por Número
	Certipresu findByNumeroAndTipo(Long numero, int tipo);

}
