package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.EmisionIndividual;

public interface EmisionIndividualR extends JpaRepository<EmisionIndividual, Long>{
    @Query(value = "select * from emisionindividual where idemision = ?1", nativeQuery = true)
    List<EmisionIndividual> findByIdEmision(Long idemision);
}
