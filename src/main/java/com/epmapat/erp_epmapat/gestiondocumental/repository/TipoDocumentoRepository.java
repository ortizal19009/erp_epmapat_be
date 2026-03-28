package com.epmapat.erp_epmapat.gestiondocumental.repository;

import com.epmapat.erp_epmapat.gestiondocumental.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, UUID> {
    List<TipoDocumento> findByEntidadIdOrderByCodigo(UUID entidadId);
}
