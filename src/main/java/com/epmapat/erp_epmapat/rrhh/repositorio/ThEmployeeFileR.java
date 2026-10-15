package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.rrhh.modelo.ThEmployeeFile;

public interface ThEmployeeFileR extends JpaRepository<ThEmployeeFile, Long> {

    @org.springframework.data.jpa.repository.Lock(javax.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM ThEmployeeFile f WHERE f.idfile = :id")
    java.util.Optional<ThEmployeeFile> findForUpdate(@Param("id") Long id);

    @Query("""
            SELECT f
            FROM ThEmployeeFile f
            WHERE f.idpersonal_personal.idpersonal = :idpersonal
            ORDER BY f.version_doc DESC
            """)
    List<ThEmployeeFile> findByPersonal(@Param("idpersonal") Long idpersonal);
}
