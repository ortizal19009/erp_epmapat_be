package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;

public interface BeneficiariosR extends JpaRepository<Beneficiarios, Long> {

	// Lista de beneficiarios por nombre, codigo y (RUC o CI)
	// @Query(value = "SELECT * FROM beneficiarios WHERE LOWER(nomben) LIKE CONCAT('%', nomben, '%') and codben LIKE CONCAT('%', codben, '%') and (rucben LIKE CONCAT('%', rucben, '%') OR ciben LIKE CONCAT('%', ciben, '%')) and idbene > 1 order by nomben", nativeQuery = true)
	// public List<Beneficiarios> findBeneficiarios(String nomben, String codben, String rucben, String ciben);
	@Query(value = "SELECT * FROM beneficiarios WHERE LOWER(nomben) LIKE %?1% and codben LIKE %?2% and (rucben LIKE ?3% OR ciben LIKE ?4%) and idbene > 1 order by nomben", nativeQuery = true)
	public List<Beneficiarios> findBeneficiarios(String nomben, String codben, String rucben, String ciben);

	// Lista de beneficiarios por nombre
	@Query(value = "SELECT * FROM beneficiarios WHERE LOWER(nomben) LIKE %?1% order by nomben", nativeQuery = true)
	public List<Beneficiarios> findByNomben(String nomben);

	// Lista de beneficiarios por nombre y grupo
	@Query(value = "SELECT * FROM Beneficiarios WHERE LOWER(nomben) LIKE %?1% AND idgrupo = ?2  order by nomben", nativeQuery = true)
	public List<Beneficiarios> findByNombenGru(String nomben, Long idgrupo);

	// Valida el Nombre del Beneficiario
	@Query("SELECT COUNT(b) > 0 FROM Beneficiarios b WHERE LOWER(b.nomben) = :nomben")
	boolean valNomben(@Param("nomben") String nomben);

	// Ultimo código del Beneficiario (por grupo)
	@Query(value = "SELECT * FROM beneficiarios where idgrupo=?1 ORDER BY codben DESC LIMIT 1", nativeQuery = true)
	Beneficiarios findUltCodigo(Long idgrupo);

	// Valida el Código del beneficiario
	@Query("SELECT COUNT(b) > 0 FROM Beneficiarios b WHERE b.codben = :codben")
	boolean valCodben(@Param("codben") String codben);

	// Valida el RUC del Beneficiario
	@Query("SELECT COUNT(b) > 0 FROM Beneficiarios b WHERE b.rucben =:rucben")
	boolean valRucben(@Param("rucben") String rucben);

	// Valida la CI del Beneficiario
	@Query("SELECT COUNT(b) > 0 FROM Beneficiarios b WHERE b.ciben =:ciben")
	boolean valCiben(@Param("ciben") String ciben);
	
	// Cuenta los Beneficiariops por Institucion financiera
	@Query(value = "SELECT count(*) FROM beneficiarios where idifinan=?1", nativeQuery = true)
	Long countByIdifinan(Long idifinan);

}
