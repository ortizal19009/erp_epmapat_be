package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.Usrxrutas;

public interface UsrxrutasR extends JpaRepository<Usrxrutas, Long> {
    @Query("SELECT u FROM Usrxrutas u WHERE u.idusuario_usuarios.idusuario = :idusuario AND u.idemision_emisiones.idemision = :idemision")
    List<Usrxrutas> findByUsuarioAndEmision(@Param("idusuario") Long idusuario, @Param("idemision") Long idemision);

}
