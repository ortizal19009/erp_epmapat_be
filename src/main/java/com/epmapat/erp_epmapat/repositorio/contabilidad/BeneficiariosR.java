package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;

public interface BeneficiariosR extends JpaRepository<Beneficiarios, Long> {

	@Query(value = "SELECT * FROM beneficiarios WHERE LOWER(nomben) LIKE %?1% order by nomben", nativeQuery = true)
	public List<Beneficiarios> findByName(String nombre);

	@Query(value = "SELECT * FROM beneficiarios WHERE LOWER(nomben) LIKE %?1% AND idgrupo=?2 order by nomben", nativeQuery = true)
	public List<Beneficiarios> findByGrupoBene(String nombre, Long idgrupo);

}
