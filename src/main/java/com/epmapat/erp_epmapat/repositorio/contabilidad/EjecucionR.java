package com.epmapat.erp_epmapat.repositorio.contabilidad;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucion;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;

public interface EjecucionR extends JpaRepository<Ejecucion, Long> {

    @Query(value = "SELECT * FROM ejecucion order by codpar", nativeQuery = true)
    List<Ejecucion> findAll();

    @Query(value = "SELECT * FROM ejecucion where idrefo = ?1 order by codpar", nativeQuery = true)
    List<Ejecucion> buscaByIdrefo(Long idrefo);

    @Query(value = "SELECT * FROM ejecucion  WHERE (codpar like ?1) AND (fecha_eje BETWEEN (?2) AND (?3)) ORDER BY codpar, fecha_eje, idejecu ", nativeQuery = true)
    public List<Ejecucion> findByCodparFecha(String codpar, Date desdeFecha, Date hastaFecha);

    // Verifica si una partida tiene Ejecución
    @Query("SELECT COUNT(e) > 0 FROM Ejecucion e WHERE e.codpar = :codpar")
    boolean tieneEjecucion(@Param("codpar") String codpar);

    //Busca la Ejecución de una Partida (para actualizar codpar)
    // List<Ejecucion> findByidpresupue(Long idpresupue);
    List<Ejecucion> findByidpresupue(Presupue presupue);

    //Actualizar codpar
    @Query("UPDATE Ejecucion e SET e.codpar = :codpar WHERE e.idpresupue = :idpresupue")
    void updateCodpar(@Param("codpar") String codpar, @Param("idpresupue") Long idpresupue);
    
}