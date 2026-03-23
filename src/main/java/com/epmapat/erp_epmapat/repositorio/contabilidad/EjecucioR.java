package com.epmapat.erp_epmapat.repositorio.contabilidad;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucio;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;

public interface EjecucioR extends JpaRepository<Ejecucio, Long> {

    @SuppressWarnings("null")
    @Query(value = "SELECT * FROM ejecucio order by codpar", nativeQuery = true)
    List<Ejecucio> findAll();

    @Query(value = "SELECT * FROM ejecucio where idrefo = ?1 order by codpar", nativeQuery = true)
    List<Ejecucio> buscaByIdrefo(Long idrefo);

    @Query(value = "SELECT * FROM ejecucio  WHERE (codpar like ?1) AND (fecha_eje BETWEEN (?2) AND (?3)) ORDER BY codpar, fecha_eje, inteje", nativeQuery = true)
    public List<Ejecucio> findByCodparFecha(String codpar, Date desdeFecha, Date hastaFecha);

    // Verifica si una partida tiene Ejecucio
    @Query("SELECT COUNT(e) > 0 FROM Ejecucio e WHERE e.codpar = :codpar")
    boolean tieneEjecucio(@Param("codpar") String codpar);

    // Busca la Ejecución de una Partida (para actualizar codpar)
    List<Ejecucio> findByintpre(Presupue presupue);

    // Actualizar codpar
    @Query("UPDATE Ejecucio e SET e.codpar = :codpar WHERE e.intpre = :intpre")
    void updateCodpar(@Param("codpar") String codpar, @Param("intpre") Long intpre);

    // Reformas de una partida (desde/hasta) (El comodín % se agrega en el servicio)
    @Query(value = "SELECT sum(modifi) FROM ejecucio  WHERE codpar LIKE ?1  AND fecha_eje BETWEEN ?2 AND ?3 AND tipeje = 2", nativeQuery = true)
    Double totalModi(String codpar, Date desdeFecha, Date hastaFecha);

    // Devengado de una partida (desde/hasta)
    @Query(value = "SELECT sum(devengado) FROM ejecucio WHERE codpar LIKE ?1  AND fecha_eje BETWEEN ?2 AND ?3 AND tipeje = 3", nativeQuery = true)
    Double totalDeven(String codpar, Date desdeFecha, Date hastaFecha);

    // Cobrado/Pagado de una partida (desde/hasta)
    @Query(value = "SELECT sum(cobpagado) FROM ejecucio WHERE codpar LIKE ?1  AND fecha_eje BETWEEN ?2 AND ?3 AND tipeje = 4", nativeQuery = true)
    Double totalCobpagado(String codpar, Date desdeFecha, Date hastaFecha);

    // Cuenta los compromisos de una partixcerti (para no eliminar partixcerti)
    short countByIdparxcer(Long idparxcer);

    // Cuenta las Partidas de una ejecución para disabled eliminar partida
    @Query(value = "SELECT count(*) FROM ejecucio where intpre=?1", nativeQuery = true)
    Long countByIntpre(Long intpre);

    // Partidas de un Trámite
    @Query(value = "SELECT * FROM ejecucio  WHERE idtrami = ?1 ORDER BY inteje", nativeQuery = true)
    public List<Ejecucio> partixtrami(Long idtrami);

    // Ejecución de un asiento
    List<Ejecucio> findByIdasiento(Long idasiento);

    // Ejecución de un asiento por tippar
    // @Query("""
    // SELECT e
    // FROM Ejecucio e
    // WHERE e.idasiento = :idasiento
    // AND e.intpre.tippar = :tippar
    // """)
    // @Query("SELECT e FROM Ejecucio e WHERE e.idasiento = :idasiento AND
    // e.intpre.tippar = :tippar")
    // List<Ejecucio> findByIdasientoAndTippar(Long idasiento, Integer tippar);
    @Query("""
                SELECT e
                FROM Ejecucio e
                JOIN e.intpre p
                WHERE e.idasiento = :idasiento
                  AND p.tippar = :tippar
            """)
    List<Ejecucio> findByIdasientoAndTippar(Long idasiento, Integer tippar);

    // Ejecucion de una transaci.inttra
    Optional<Ejecucio> findByInttra(Long inttra);

    // Compromisos que tienen saldo pendiente (toLowerCase y los comodines se
    // agregan en el servicio)
    @Query(value = "select * from ejecucio e join tramites t on e.idtrami = t.idtrami join beneficiarios b on t.idbene = b.idbene where LOWER(b.nomben) like ?1 and e.fecha_eje <=?2 and e.prmiso > 0 and e.prmiso > e.totdeven order by b.nomben", nativeQuery = true)
    public List<Ejecucio> misosPendientes(String nomben, Date hasta);

    @Modifying
    @Transactional
    @Query("UPDATE Ejecucio SET totdeven = :totdeven WHERE inteje = :inteje")
    void updateTotdeven(@Param("inteje") Long inteje, @Param("totdeven") BigDecimal totdeven);

}