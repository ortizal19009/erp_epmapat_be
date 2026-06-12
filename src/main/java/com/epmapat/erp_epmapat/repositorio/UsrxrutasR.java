package com.epmapat.erp_epmapat.repositorio;

import com.epmapat.erp_epmapat.modelo.Usrxrutas;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsrxrutasR extends JpaRepository<Usrxrutas, Long> {

    @EntityGraph(attributePaths = { "idusuario_usuarios", "idemision_emisiones" })
    @Query("""
        SELECT u
        FROM Usrxrutas u
        WHERE u.idusuario_usuarios.idusuario = :idusuario
          AND u.idemision_emisiones.idemision = :idemision
    """)
    Optional<Usrxrutas> findByUsuarioAndEmision(
            @Param("idusuario") Long idusuario,
            @Param("idemision") Long idemision
    );

    @EntityGraph(attributePaths = { "idusuario_usuarios", "idemision_emisiones" })
    @Query("""
        SELECT u
        FROM Usrxrutas u
        WHERE u.idemision_emisiones.idemision = :idemision
    """)
    List<Usrxrutas> findByEmision(@Param("idemision") Long idemision);

    @EntityGraph(attributePaths = { "idusuario_usuarios", "idemision_emisiones" })
    @Query("""
        SELECT u
        FROM Usrxrutas u
        WHERE u.idusuario_usuarios.idusuario = :idusuario
    """)
    List<Usrxrutas> findByUsuario(@Param("idusuario") Long idusuario);

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM Usrxrutas u
        WHERE u.idusuario_usuarios.idusuario = :idusuario
          AND u.idemision_emisiones.idemision = :idemision
    """)
    boolean existsByUsuarioAndEmision(
            @Param("idusuario") Long idusuario,
            @Param("idemision") Long idemision
    );

    // ✅ Rutas ocupadas en una emisión por OTROS usuarios
    // ✅ OJO: SIN "::bigint" -> usar CAST()
    @Query(value = """
        SELECT DISTINCT CAST(r->>'idruta' AS bigint) AS idruta
        FROM usrxrutas u
        CROSS JOIN LATERAL jsonb_array_elements(u.rutas) r
        WHERE u.idemision_emisiones = :idemision
          AND u.idusuario_usuarios <> :idusuario
          AND CAST(r->>'idruta' AS bigint) IN (:idrutas)
    """, nativeQuery = true)
    List<Long> findRutasOcupadasEnEmisionPorOtros(
            @Param("idemision") Long idemision,
            @Param("idusuario") Long idusuario,
            @Param("idrutas") List<Long> idrutas
    );

    // ✅ Todas las rutas ocupadas en una emisión (para pintar UI)
    // ✅ OJO: SIN "::bigint"
    @Query(value = """
        SELECT DISTINCT CAST(r->>'idruta' AS bigint) AS idruta
        FROM usrxrutas u
        CROSS JOIN LATERAL jsonb_array_elements(u.rutas) r
        WHERE u.idemision_emisiones = :idemision
    """, nativeQuery = true)
    List<Long> findRutasOcupadasEnEmision(@Param("idemision") Long idemision);
}
