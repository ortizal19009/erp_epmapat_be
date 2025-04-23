package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Facxnc;

public interface FacxncR extends JpaRepository<Facxnc, Long>{
    @Query(value = "SELECT * FROM facxnc fnc WHERE fnc.idvaloresnc_valoresnc ",nativeQuery = true)
    List<Facxnc> findByIdvalnc(Long idvalnc);

}
