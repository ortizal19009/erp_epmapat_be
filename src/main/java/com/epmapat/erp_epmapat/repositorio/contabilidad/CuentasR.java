package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;

public interface CuentasR extends JpaRepository<Cuentas, Long> {

    // Lista de Cuentas por Codigo y/o Nombre
    @Query(value = "SELECT * FROM cuentas where codcue like ?1% and LOWER(nomcue) like %?2% order by codcue", nativeQuery = true)
    List<Cuentas> findByCodigoyNombre(String codcue, String nomcue);

    // Bancos
    @Query(value = "SELECT * FROM cuentas WHERE codcue LIKE '111%' and codcue >= '111.02' and movcue = 2 order by codcue", nativeQuery = true)
    public List<Cuentas> findBancos();

    @SuppressWarnings("null")
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

    // Cuentas por tiptran para los DataList
    @Query(value = "SELECT * FROM cuentas where tiptran = ?1 and codcue like ?2 and movcue = 2 order by codcue", nativeQuery = true)
    List<Cuentas> findByTiptran(Integer tiptran, String codcue);

    // Una cuenta por codcue
    @Query("SELECT c FROM Cuentas c WHERE c.codcue = :codcue")
    Cuentas findCuentaByCodcue(String codcue);

    // Nombre de una cuenta
    @Query("SELECT nomcue FROM Cuentas c WHERE c.codcue = :codcue")
    Object[] findNomCueByCodcue(String codcue);

    // Valida codcue
    @Query("SELECT COUNT(c) > 0 FROM Cuentas c WHERE c.codcue = :codcue")
    boolean valCodcue(@Param("codcue") String codcue);

    // Valida el Nombre de la Cuenta
    @Query("SELECT COUNT(c) > 0 FROM Cuentas c WHERE LOWER(c.nomcue) = :nomcue")
    boolean valNomcue(@Param("nomcue") String nomcue);

    // Verifica si tiene Desagregación
    @Query(value = "SELECT EXISTS (SELECT 1 FROM Cuentas WHERE codcue LIKE ?1% AND nivcue > ?2)", nativeQuery = true)
    boolean valDesagrega(String codcue, Integer nivcue);

    // Cuentas de costos
    @Query(value = "SELECT * FROM cuentas where (codcue like '133%' or codcue like '62%') and movcue=2 and (asodebe >= '0' or asohaber >= '0') order by codcue", nativeQuery = true)
    List<Cuentas> findCuecostos();

}
