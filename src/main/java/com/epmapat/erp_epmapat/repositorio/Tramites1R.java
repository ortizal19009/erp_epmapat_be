package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.Tramites1M;

public interface Tramites1R extends JpaRepository<Tramites1M, Long>{

    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "idabonado_abonados.idresponsable",
            "idabonado_abonados.idcliente_clientes",
            "idabonado_abonados.idcategoria_categorias",
            "rubrosSeleccionados"
    })
    @Override
    List<Tramites1M> findAll();

    @EntityGraph(attributePaths = {
            "idcliente_clientes",
            "idabonado_abonados",
            "idabonado_abonados.idresponsable",
            "idabonado_abonados.idcliente_clientes",
            "idabonado_abonados.idcategoria_categorias",
            "rubrosSeleccionados"
    })
    @Override
    Optional<Tramites1M> findById(Long id);
}
