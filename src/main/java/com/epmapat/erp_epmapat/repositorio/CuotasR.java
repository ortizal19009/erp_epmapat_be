package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Cuotas;

public interface CuotasR extends JpaRepository<Cuotas, Long>{

   @Query(value = "SELECT * FROM cuotas order by idcuota LIMIT 10", nativeQuery = true)
	public List<Cuotas> find10();

   @Query(value = "SELECT * FROM cuotas WHERE idconvenio_convenios=?1", nativeQuery = true)
	public List<Cuotas> findByIdconvenio(Long idconvenio);

}
