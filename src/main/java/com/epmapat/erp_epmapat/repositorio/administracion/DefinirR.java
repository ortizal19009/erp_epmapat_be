package com.epmapat.erp_epmapat.repositorio.administracion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.DefinirProjection;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;

public interface DefinirR extends JpaRepository<Definir, Long> {
    Definir findTopByOrderByIddefinirDesc();

    @Query("""
                SELECT d.iddefinir AS iddefinir,
                       d.razonsocial AS razonsocial,
                       d.nombrecomercial AS nombrecomercial,
                       d.ruc AS ruc,
                       d.direccion AS direccion,
                       d.tipoambiente AS tipoambiente,
                       d.iva AS iva,
                       d.empresa AS empresa,
                       d.ubirepo AS ubirepo,
                       d.posiacti AS posiacti,
                       d.longacti AS longacti,
                       d.naturaleza AS naturaleza,
                       d.fechap AS fechap,
                       d.nombre AS nombre,
                       d.ubicomprobantes AS ubicomprobantes,
                       d.asunto AS asunto,
                       d.textomail AS textomail,
                       d.dirmatriz AS dirmatriz,
                       d.fechacierre AS fechacierre,
                       d.f_i AS f_i,
                       d.f_g AS f_g,
                       d.porciva AS porciva,
                       d.ciudad AS ciudad,
                       d.idtabla17 AS idtabla17,
                       d.ubidigi AS ubidigi,
                       d.ubimagenes AS ubimagenes,
                       d.swpreingsin AS swpreingsin,
                       d.email AS email,
                       d.clave_email AS clave_email,
                       d.rbu AS rbu
                FROM Definir d
                WHERE d.iddefinir = :id
            """)
    DefinirProjection findDefinirWithoutFirma(@Param("id") Long id);

}
