package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Rubros;

public interface RubrosR extends JpaRepository<Rubros, Long>{

	//Rubros de un Módulo
	@Query(value="SELECT * FROM rubros WHERE idmodulo_modulos=?1 order by descripcion", nativeQuery = true)
	public List<Rubros> findByIdmodulo(Long idmodulo);

	//Por Módulo y Descripción
	@Query(value="SELECT * FROM rubros WHERE idmodulo_modulos=?1 and LOWER(descripcion) like %?2% and estado=true order by descripcion", nativeQuery = true)
	public List<Rubros> findByModulo(Long idmodulo, String descripcion);

	//Validar por Nombre y Sección
	@Query(value="SELECT * FROM rubros WHERE idmodulo_modulos=?1 and LOWER(descripcion)=?2", nativeQuery = true)
	public List<Rubros> findByNombre(Long idmodulo, String descripcion);

	//Rubros de Emisión
	@Query(value="SELECT * FROM rubros WHERE idmodulo_modulos=4 and estado order by idrubro", nativeQuery = true)
	public List<Rubros> findEmision();

}
