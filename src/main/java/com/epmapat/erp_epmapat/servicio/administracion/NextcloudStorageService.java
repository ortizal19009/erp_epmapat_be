package com.epmapat.erp_epmapat.servicio.administracion;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import javax.annotation.PostConstruct;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "nextcloud")
public class NextcloudStorageService implements StorageService {

    @Value("${nextcloud.base-url}")
    private String baseUrl;

    @Value("${nextcloud.username}")
    private String username;

    @Value("${nextcloud.password:${NEXTCLOUD_APP_TOKEN:}}")
    private String password;

    @Value("${nextcloud.base-folder:/epmapat}")
    private String baseFolder;

    @PostConstruct
    void validateConfig() {
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("nextcloud.base-url es obligatorio cuando storage.type=nextcloud");
        }
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("nextcloud.username es obligatorio cuando storage.type=nextcloud");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("nextcloud.password o NEXTCLOUD_APP_TOKEN es obligatorio cuando storage.type=nextcloud");
        }
    }

    private String davBase() {
        return trimTrailingSlash(baseUrl) + "/remote.php/dav/files/" + username;
    }

    private Sardine sardine() {
        return SardineFactory.begin(username, password);
    }

    @Override
    public String store(MultipartFile file, String folder) throws Exception {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String normalizedFolder = normalizeFolder(folder);
        String filename = buildFileName(file.getOriginalFilename());
        String relativePath = normalizeRelativePath(baseFolder + "/" + normalizedFolder + "/" + filename);

        Sardine sardine = sardine();
        ensureDirectoryExists(sardine, getParentRelativePath(relativePath));
        try {
            sardine.put(buildRemoteUrl(relativePath), file.getBytes(), file.getContentType());
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo subir el archivo a Nextcloud en " + buildRemoteUrl(relativePath), ex);
        }

        return relativePath;
    }

    @Override
    public Resource load(String relativePath) throws Exception {
        String normalizedPath = normalizeRelativePath(relativePath);
        Sardine sardine = sardine();
        InputStream inputStream;
        try {
            inputStream = sardine.get(buildRemoteUrl(normalizedPath));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo leer el archivo desde Nextcloud: " + normalizedPath, ex);
        }
        return new InputStreamResource(inputStream) {
            @Override
            public String getFilename() {
                int lastSlash = normalizedPath.lastIndexOf('/');
                return lastSlash >= 0 ? normalizedPath.substring(lastSlash + 1) : normalizedPath;
            }
        };
    }

    @Override
    public void delete(String relativePath) throws Exception {
        Sardine sardine = sardine();
        String normalizedPath = normalizeRelativePath(relativePath);
        try {
            sardine.delete(buildRemoteUrl(normalizedPath));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo eliminar el archivo en Nextcloud: " + normalizedPath, ex);
        }
    }

    @Override
    public Map<String, Object> health() throws Exception {
        Sardine sardine = sardine();
        String probeFolder = normalizeRelativePath(baseFolder + "/healthcheck");
        String probePath = probeFolder + "/probe-" + UUID.randomUUID() + ".txt";
        String probeUrl = buildRemoteUrl(probePath);
        String payload = "ok";

        ensureDirectoryExists(sardine, probeFolder);
        sardine.put(probeUrl, payload.getBytes(StandardCharsets.UTF_8), "text/plain");

        String readBack;
        try (InputStream inputStream = sardine.get(probeUrl)) {
            readBack = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        sardine.delete(probeUrl);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("ok", payload.equals(readBack));
        out.put("mode", "nextcloud");
        out.put("baseUrl", baseUrl);
        out.put("username", username);
        out.put("baseFolder", baseFolder);
        out.put("probePath", probePath);
        out.put("readBack", readBack);
        return out;
    }

    private void ensureDirectoryExists(Sardine sardine, String relativeDirectory) throws Exception {
        List<String> segments = splitSegments(relativeDirectory);
        String current = "";
        for (String segment : segments) {
            current = current + "/" + segment;
            String currentUrl = buildRemoteUrl(current);
            if (!sardine.exists(currentUrl)) {
                sardine.createDirectory(currentUrl);
            }
        }
    }

    private String buildRemoteUrl(String relativePath) {
        return davBase() + "/" + normalizeRelativePath(relativePath);
    }

    private String getParentRelativePath(String relativePath) {
        int lastSlash = relativePath.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        return relativePath.substring(0, lastSlash);
    }

    private List<String> splitSegments(String path) {
        List<String> segments = new ArrayList<>();
        for (String segment : normalizeRelativePath(path).split("/")) {
            if (StringUtils.hasText(segment)) {
                segments.add(segment);
            }
        }
        return segments;
    }

    private String normalizeFolder(String folder) {
        String value = StringUtils.hasText(folder) ? folder.trim() : "general";
        return value.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private String normalizeRelativePath(String path) {
        return path.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private String trimTrailingSlash(String value) {
        return value.replaceAll("/+$", "");
    }

    private String buildFileName(String originalName) {
        String cleanName = StringUtils.hasText(originalName) ? StringUtils.cleanPath(originalName) : "archivo";
        cleanName = cleanName.replace("\\", "_").replace("/", "_").replace(" ", "_");
        return UUID.randomUUID() + "_" + cleanName;
    }
}
