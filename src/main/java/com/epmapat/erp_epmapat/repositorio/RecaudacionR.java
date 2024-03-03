package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Recaudacion;

public interface RecaudacionR extends JpaRepository<Recaudacion, Long> {
    @Query(value = "SELECT SUM(valor) FROM recaudacion WHERE recaudador = ?1 and fechacobro = ?2 ", nativeQuery = true)
    Double totalRecaudado(Long idrecaudador, Date fechacobro);

}
