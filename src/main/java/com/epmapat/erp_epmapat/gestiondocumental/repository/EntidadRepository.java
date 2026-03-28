package com.epmapat.erp_epmapat.gestiondocumental.repository;

import com.epmapat.erp_epmapat.gestiondocumental.model.Entidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntidadRepository extends JpaRepository<Entidad, UUID> {
    List<Entidad> findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCaseOrderByCodigo(String codigo, String nombre);
    List<Entidad> findAllByOrderByCodigo();
    Optional<Entidad> findByCodigo(String codigo);
}
