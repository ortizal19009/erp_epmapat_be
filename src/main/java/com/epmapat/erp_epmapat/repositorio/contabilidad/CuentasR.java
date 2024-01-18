package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;

public interface CuentasR extends JpaRepository<Cuentas, Long>{

   // Lista de Cuentas por Codigo y/o Nombre
   @Query(value = "SELECT * FROM cuentas where codcue like ?1% and LOWER(nomcue) like %?2% order by codcue", nativeQuery = true)
   List<Cuentas> findByCodigoyNombre( String codcue, String nomcue );

	@Query(value= "SELECT * FROM cuentas WHERE codcue LIKE %?1% ", nativeQuery= true)
	public List<Cuentas> findBancos(String codcue);

	@Query(value = "SELECT * FROM cuentas order by codcue", nativeQuery = true)
    List<Cuentas> findAll();

    @Query(value = "SELECT * FROM cuentas where codcue like ?1% order by codcue", nativeQuery = true)
    List<Cuentas> findByCodcue(String codcue);

    @Query(value = "SELECT * FROM cuentas where LOWER(nomcue) like ?1% order by codcue", nativeQuery = true)
    List<Cuentas> findByNomcue(String nomcue);

    @Query(value = "SELECT * FROM cuentas where grucue like ?1% order by codcue", nativeQuery = true)
    List<Cuentas> findByGrucue(String grucue);

    @Query(value = "SELECT * FROM cuentas where asohaber = ?1 order by codcue", nativeQuery = true)
    List<Cuentas> findByAsohaber(String asohaber);

    @Query(value = "SELECT * FROM cuentas where asodebe = ?1 order by codcue", nativeQuery = true)
    List<Cuentas> findByAsodebe(String asodebe);
	
    //Cuentas por tiptran
    @Query(value = "SELECT * FROM cuentas where tiptran = ?1 and codcue like ?2% and movcue order by codcue", nativeQuery = true)
    List<Cuentas> findByTiptran(Integer tiptran, String codcue);

}
