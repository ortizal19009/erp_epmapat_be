package com.epmapat.erp_epmapat.servicio.administracion;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.modelo.administracion.MobileAppVersion;
import com.epmapat.erp_epmapat.repositorio.administracion.MobileAppVersionR;
import com.epmapat.erp_epmapat.servicio.RuntimeEnvironmentService;

@Service
public class MobileAppVersionService {

    private final MobileAppVersionR repository;
    private final RuntimeEnvironmentService runtimeEnvironmentService;

    @Value("${mobile.apk.base-path:}")
    private String basePath;

    @Value("${mobile.apk.windows-base-path:C:/ERP/apk-updates}")
    private String windowsBasePath;

    @Value("${mobile.apk.linux-base-path:/opt/epmapat/apk-updates}")
    private String linuxBasePath;

    public MobileAppVersionService(
            MobileAppVersionR repository,
            RuntimeEnvironmentService runtimeEnvironmentService) {
        this.repository = repository;
        this.runtimeEnvironmentService = runtimeEnvironmentService;
    }

    public List<MobileAppVersion> findAll() {
        return repository.findAllOrdered();
    }

    public Optional<MobileAppVersion> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<MobileAppVersion> findLatestActiveByPackageName(String packageName) {
        return repository.findLatestActiveByPackageName(normalizePackageName(packageName));
    }

    public boolean deleteById(Long id) throws Exception {
        MobileAppVersion version = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la version APK con id: " + id));

        if (StringUtils.hasText(version.getArchivoRuta())) {
            Files.deleteIfExists(resolveStoredPath(version.getArchivoRuta()));
        }

        repository.delete(version);
        return !repository.existsById(id);
    }

    public MobileAppVersion saveVersion(
            MultipartFile file,
            String packageName,
            String versionName,
            Long versionCode,
            String descripcion,
            Boolean forceUpdate,
            Long usucrea) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo APK es obligatorio");
        }
        if (!StringUtils.hasText(versionName)) {
            throw new IllegalArgumentException("La version es obligatoria");
        }
        if (versionCode == null) {
            throw new IllegalArgumentException("El versionCode es obligatorio");
        }

        String normalizedPackageName = normalizePackageName(packageName);
        String storedRelativePath = storeApk(file, normalizedPackageName, versionCode, versionName);

        MobileAppVersion entity = new MobileAppVersion();
        entity.setPackageName(normalizedPackageName);
        entity.setVersionName(versionName.trim());
        entity.setVersionCode(versionCode);
        entity.setArchivoNombre(StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename().trim() : "app-release.apk");
        entity.setArchivoRuta(storedRelativePath);
        entity.setContentType(StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/vnd.android.package-archive");
        entity.setSizeBytes(file.getSize());
        entity.setChecksumSha256(calculateSha256(file));
        entity.setDescripcion(StringUtils.hasText(descripcion) ? descripcion.trim() : null);
        entity.setEntorno(runtimeEnvironmentService.getEnvironmentLabel());
        entity.setHostName(runtimeEnvironmentService.getHostName());
        entity.setForceUpdate(Boolean.TRUE.equals(forceUpdate));
        entity.setActivo(Boolean.TRUE);
        entity.setUsucrea(usucrea);
        entity.setFeccrea(ZonedDateTime.now());

        return repository.save(entity);
    }

    public Resource loadAsResource(MobileAppVersion version) throws Exception {
        Path path = resolveStoredPath(version.getArchivoRuta());
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("No se encontro el APK solicitado");
        }
        return resource;
    }

    private String storeApk(MultipartFile file, String packageName, Long versionCode, String versionName) throws Exception {
        String originalName = StringUtils.hasText(file.getOriginalFilename()) ? StringUtils.cleanPath(file.getOriginalFilename()) : "app-release.apk";
        String safeName = originalName.replace("\\", "_").replace("/", "_").replace(" ", "_");
        String folder = packageName + "/v" + versionCode;
        String filename = UUID.randomUUID() + "_" + versionName.replace(" ", "_") + "_" + safeName;
        String relativePath = folder + "/" + filename;

        Path target = resolveStoredPath(relativePath);
        Files.createDirectories(target.getParent());

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return relativePath.replace("\\", "/");
    }

    private Path resolveStoredPath(String relativePath) {
        return Paths.get(resolveBasePath()).resolve(relativePath).normalize();
    }

    private String resolveBasePath() {
        if (StringUtils.hasText(basePath)) {
            return basePath.trim();
        }
        if (runtimeEnvironmentService.isWindows()) {
            return windowsBasePath;
        }
        return linuxBasePath;
    }

    private String normalizePackageName(String packageName) {
        return StringUtils.hasText(packageName) ? packageName.trim() : "com.erp.epmpatmobile";
    }

    private String calculateSha256(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
