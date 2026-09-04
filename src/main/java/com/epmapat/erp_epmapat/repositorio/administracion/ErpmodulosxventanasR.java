package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulosxventanas;

public interface ErpmodulosxventanasR extends JpaRepository<Erpmodulosxventanas, Long> {

   @Query("""
         SELECT mv
         FROM Erpmodulosxventanas mv
         JOIN FETCH mv.iderpmodulo
         WHERE mv.iderpmodulo.iderpmodulo IN :modulos
         ORDER BY mv.nombreventana
         """)
   List<Erpmodulosxventanas> findByModuleIds(@Param("modulos") List<Long> modulos);

   @Query("""
         SELECT mv
         FROM Erpmodulosxventanas mv
         JOIN FETCH mv.iderpmodulo
         ORDER BY mv.nombreventana
         """)
   List<Erpmodulosxventanas> findAllWithModule();
}
