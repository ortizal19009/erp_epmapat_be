package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.Recargosxcuenta;

public interface RecargosxcuentaR extends JpaRepository<Recargosxcuenta, Long> {

    @Query("""
        SELECT r
        FROM Recargosxcuenta r
        WHERE r.idemision_emisiones.idemision = :idemision
    """)
    List<Recargosxcuenta> findByIdEmision(@Param("idemision") long idemision);
}
