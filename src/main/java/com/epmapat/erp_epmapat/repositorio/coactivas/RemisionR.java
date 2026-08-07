package com.epmapat.erp_epmapat.repositorio.coactivas;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.coactivas.Remision;

public interface RemisionR extends JpaRepository<Remision, Long>{
    @Override
    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "iddocumento_documentos"
    })
    Page<Remision> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "iddocumento_documentos"
    })
    @Query("SELECT r FROM Remision r WHERE r.feccrea BETWEEN ?1 AND ?2 ORDER BY r.idremision DESC")
    List<Remision> findRemisionesByFeccrea(LocalDate d, LocalDate h);

    @Override
    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "iddocumento_documentos"
    })
    Optional<Remision> findById(Long idremision);
    
}
