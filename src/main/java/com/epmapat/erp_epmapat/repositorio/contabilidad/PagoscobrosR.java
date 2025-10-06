package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Pagoscobros;

public interface PagoscobrosR extends JpaRepository<Pagoscobros, Long>{

   @Query(value = "SELECT * FROM pagoscobros WHERE idbenxtra = ?1", nativeQuery = true)
	public List<Pagoscobros> getByIdbenxtra(Long Idbenxtra);

}
