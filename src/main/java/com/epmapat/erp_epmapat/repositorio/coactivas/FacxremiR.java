package com.epmapat.erp_epmapat.repositorio.coactivas;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.coactivas.Facxremi;

public interface FacxremiR extends JpaRepository<Facxremi, Long> {
    @EntityGraph(attributePaths = {
            "idfactura_facturas",
            "idfactura_facturas.idmodulo",
            "idremision_remisiones",
            "idremision_remisiones.idcliente_clientes",
            "idremision_remisiones.idabonado_abonados"
    })
    @Query("SELECT fr FROM Facxremi fr WHERE fr.idremision_remisiones.idremision = ?1 AND fr.tipfactura = ?2 ORDER BY fr.idfactura_facturas.idfactura ASC")
    public List<Facxremi> findByRemision(Long idremision, Long tipfac);
}
