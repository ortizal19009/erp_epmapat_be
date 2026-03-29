package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.rrhh.modelo.ThEmployeeFile;

public interface ThEmployeeFileR extends JpaRepository<ThEmployeeFile, Long> {

    @Query("""
            SELECT f
            FROM ThEmployeeFile f
            WHERE f.idpersonal_personal.idpersonal = :idpersonal
            ORDER BY f.version_doc DESC
            """)
    List<ThEmployeeFile> findByPersonal(@Param("idpersonal") Long idpersonal);
}
