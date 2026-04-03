package com.epmapat.erp_epmapat.servicio.administracion;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.servicio.RuntimeEnvironmentService;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${storage.local.base-path:}")
    private String basePath;

    private final RuntimeEnvironmentService runtimeEnvironmentService;

    public LocalStorageService(RuntimeEnvironmentService runtimeEnvironmentService) {
        this.runtimeEnvironmentService = runtimeEnvironmentService;
    }

    @Override
    public String store(MultipartFile file, String folder) throws Exception {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String normalizedFolder = normalizeFolder(folder);
        String filename = buildFileName(file.getOriginalFilename());
        String relativePath = normalizedFolder + "/" + filename;

        Path target = resolvePath(relativePath);
        Files.createDirectories(target.getParent());

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return relativePath;
    }

    @Override
    public Resource load(String relativePath) throws Exception {
        Path path = resolveExistingPath(relativePath);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("Archivo no encontrado: " + relativePath);
        }
        return resource;
    }

    @Override
    public void delete(String relativePath) throws Exception {
        Path path = resolvePath(relativePath);
        Files.deleteIfExists(path);
    }

    private Path resolveExistingPath(String relativePath) {
        Path candidate = Paths.get(relativePath);
        if (candidate.isAbsolute()) {
            return candidate;
        }
        return resolvePath(relativePath);
    }

    private Path resolvePath(String relativePath) {
        return Paths.get(resolveBasePath()).resolve(relativePath).normalize();
    }

    private String resolveBasePath() {
        if (StringUtils.hasText(basePath)) {
            return basePath;
        }

        if (runtimeEnvironmentService.isWindows()) {
            return "C:/ERP/nube-local";
        }

        return "/opt/epmapat/fotos";
    }

    private String normalizeFolder(String folder) {
        String value = StringUtils.hasText(folder) ? folder.trim() : "general";
        return value.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private String buildFileName(String originalName) {
        String cleanName = StringUtils.hasText(originalName) ? StringUtils.cleanPath(originalName) : "archivo";
        cleanName = cleanName.replace("\\", "_").replace("/", "_").replace(" ", "_");
        return UUID.randomUUID() + "_" + cleanName;
    }
}
