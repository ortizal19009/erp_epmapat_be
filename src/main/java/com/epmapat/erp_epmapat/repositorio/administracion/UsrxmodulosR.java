package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.ErpModulosI;
import com.epmapat.erp_epmapat.modelo.administracion.Usrxmodulos;

public interface UsrxmodulosR extends JpaRepository<Usrxmodulos, Long> {
        @Query(value = """
                        SELECT
                                em.iderpmodulo AS iderpmodulo,
                                em.descripcion AS descripcion,
                                um.enabled AS enabled,
                                COALESCE(um.platform, em.platform, 'WEB') AS plataform
                        FROM usrxmodulos um
                        JOIN erpmodulos em ON um.iderpmodulo_erpmodulos = em.iderpmodulo
                        WHERE um.idusuario_usuarios = ?1
                          AND um.platform = ?2
                        ORDER BY um.iderpmodulo_erpmodulos ASC
                        """, nativeQuery = true)
        public List<ErpModulosI> findModulosEnabledByUser(Long iduser, String platform);

        @Query(value = """
                        SELECT
                                em.iderpmodulo AS iderpmodulo,
                                em.descripcion AS descripcion,
                                um.enabled AS enabled,
                                COALESCE(um.platform, em.platform, 'WEB') AS plataform
                        FROM usrxmodulos um
                        JOIN erpmodulos em ON um.iderpmodulo_erpmodulos = em.iderpmodulo
                        WHERE um.idusuario_usuarios = ?1
                        ORDER BY um.iderpmodulo_erpmodulos ASC
                        """, nativeQuery = true)
        public List<ErpModulosI> findByUser(Long iduser);

        @Query(value = "SELECT * FROM usrxmodulos um where um.idusuario_usuarios = ?1 and um.iderpmodulo_erpmodulos = ?2", nativeQuery = true)
        public Usrxmodulos findModulos(Long iduser, Long iderpmodulo);

        @Query(value = """
                        SELECT m.descripcion
                        FROM usrxmodulos um
                        JOIN erpmodulos m ON m.iderpmodulo = um.iderpmodulo_erpmodulos
                        WHERE um.idusuario_usuarios = :userId
                            AND um.enabled = true
                            AND um.platform IN (:platform, 'BOTH')
                        """, nativeQuery = true)
        List<String> findEnabledModule(@Param("userId") Long userId, @Param("platform") String platform);

        @Query(value = "SELECT * from usrxmodulos um " +
                        "where um.idusuario_usuarios = :iduser " +
                        "and um.platform IN (:platform, 'BOTH') " +
                        "order by um.iderpmodulo_erpmodulos asc", nativeQuery = true)
        List<Usrxmodulos> findByUserPlatform(
                        @Param("iduser") Long iduser,
                        @Param("platform") String platform);

}
