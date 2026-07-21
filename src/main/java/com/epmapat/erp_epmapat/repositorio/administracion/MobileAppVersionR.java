package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.administracion.MobileAppVersion;

public interface MobileAppVersionR extends JpaRepository<MobileAppVersion, Long> {

    @Query(value = """
            SELECT *
            FROM mobile_app_versions
            ORDER BY activo DESC, version_code DESC, feccrea DESC, id DESC
            """, nativeQuery = true)
    List<MobileAppVersion> findAllOrdered();

    @Query(value = """
            SELECT *
            FROM mobile_app_versions
            WHERE activo = true
              AND package_name = ?1
            ORDER BY version_code DESC, feccrea DESC, id DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<MobileAppVersion> findLatestActiveByPackageName(String packageName);
}
