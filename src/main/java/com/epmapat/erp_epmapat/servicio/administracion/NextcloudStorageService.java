package com.epmapat.erp_epmapat.servicio.administracion;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import com.github.sardine.impl.SardineException;

import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "nextcloud")
public class NextcloudStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(NextcloudStorageService.class);

    @Value("${nextcloud.base-url}")
    private String baseUrl;

    @Value("${nextcloud.username}")
    private String username;

    @Value("${nextcloud.password}")
    private String password;

    @Value("${nextcloud.base-folder:/epmapat}")
    private String baseFolder;

    @Value("${nextcloud.ssl.trust-all:true}")
    private boolean trustAllSslCertificates;

    private Sardine sardine;

    @PostConstruct
    public void init() throws Exception {
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("nextcloud.base-url es obligatorio cuando storage.type=nextcloud");
        }
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("nextcloud.username es obligatorio cuando storage.type=nextcloud");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("nextcloud.password o NEXTCLOUD_APP_TOKEN es obligatorio cuando storage.type=nextcloud");
        }

        sardine = createSardineClient();
        log.info("Nextcloud storage initialized: baseUrl={}, httpsConfigured={}, trustAllSslCertificates={}, baseFolder={}",
                baseUrl,
                isHttps(baseUrl),
                trustAllSslCertificates,
                baseFolder);
        log.info("Nextcloud WebDAV base resolved to {}", davBase());
    }

    private String davBase() {
        // Usar la ruta WebDAV universal de Nextcloud
        return trimTrailingSlash(baseUrl) + "/remote.php/webdav";
    }

    private Sardine sardine() {
        return sardine;
    }

    private Sardine createSardineClient() throws Exception {
        SardineImpl client;
        if (!trustAllSslCertificates) {
            client = new SardineImpl(username, password);
        } else {
            TrustStrategy trustAllStrategy = (X509Certificate[] chain, String authType) -> true;
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, trustAllStrategy)
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE);

            client = new SardineImpl(username, password) {
                @Override
                protected ConnectionSocketFactory createDefaultSecureSocketFactory() {
                    return sslSocketFactory;
                }
            };
        }

        client.ignoreCookies();
        return client;
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
        } catch (SardineException ex) {
            if (isIgnorableDirectoryStatus(ex.getStatusCode())) {
                log.warn("Retrying Nextcloud upload after status {} for {}", ex.getStatusCode(), relativePath);
                ensureDirectoryExists(sardine, getParentRelativePath(relativePath));
                sardine.put(buildRemoteUrl(relativePath), file.getBytes(), file.getContentType());
            } else {
                throw new IllegalStateException("No se pudo subir el archivo a Nextcloud en " + buildRemoteUrl(relativePath), ex);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo subir el archivo a Nextcloud en " + buildRemoteUrl(relativePath), ex);
        }

        return relativePath;
    }

    @Override
    public Resource load(String relativePath) throws Exception {
        String normalizedPath = normalizeRelativePath(relativePath);
        Sardine sardine = sardine();
        long startedAt = System.currentTimeMillis();
        InputStream inputStream;
        try {
            inputStream = sardine.get(buildRemoteUrl(normalizedPath));
        } catch (SardineException ex) {
            if (ex.getStatusCode() == 404) {
                log.warn("Nextcloud file not found: {}", normalizedPath);
                return null;
            }
            throw new IllegalStateException("No se pudo leer el archivo desde Nextcloud: " + normalizedPath, ex);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo leer el archivo desde Nextcloud: " + normalizedPath, ex);
        }
        byte[] content;
        try {
            content = inputStream.readAllBytes();
        } finally {
            inputStream.close();
        }

        log.debug("Loaded Nextcloud file {} in {} ms ({} bytes)", normalizedPath, System.currentTimeMillis() - startedAt, content.length);

        return new ByteArrayResource(content) {
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
        String probeFolder = normalizeRelativePath(baseFolder);
        String probeUrl = buildDirectoryRemoteUrl(probeFolder);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("mode", "nextcloud");
        out.put("baseUrl", baseUrl);
        out.put("username", username);
        out.put("baseFolder", baseFolder);
        out.put("httpsConfigured", isHttps(baseUrl));
        out.put("trustAllSslCertificates", trustAllSslCertificates);
        out.put("probePath", probeUrl);
        try {
            boolean exists = sardine.exists(probeUrl);
            out.put("ok", exists);
            out.put("readBack", exists ? "exists" : "missing");
        } catch (SardineException ex) {
            out.put("ok", false);
            out.put("readBack", "error");
            out.put("statusCode", ex.getStatusCode());
            out.put("reason", ex.getResponsePhrase());
            log.warn("Nextcloud health check failed for {} with status {}", probeUrl, ex.getStatusCode());
        }
        return out;
    }

    private void ensureDirectoryExists(Sardine sardine, String relativeDirectory) throws Exception {
        List<String> segments = splitSegments(relativeDirectory);
        String current = "";
        for (String segment : segments) {
            current = current + "/" + segment;
            String currentUrl = buildDirectoryRemoteUrl(current);
            log.debug("Ensuring Nextcloud directory exists: segment={}, url={}", segment, currentUrl);
            if (!sardine.exists(currentUrl)) {
                tryCreateDirectory(sardine, currentUrl);
            }
        }
    }

    private void tryCreateDirectory(Sardine sardine, String currentUrl) throws Exception {
        try {
            sardine.createDirectory(currentUrl);
            log.debug("Created Nextcloud directory: {}", currentUrl);
            return;
        } catch (SardineException ex) {
            if (isIgnorableDirectoryStatus(ex.getStatusCode())) {
                log.debug("Ignoring Nextcloud directory status {} for {}", ex.getStatusCode(), currentUrl);
                return;
            }
            String alternateUrl = currentUrl.endsWith("/") ? currentUrl.substring(0, currentUrl.length() - 1) : currentUrl + "/";
            log.debug("Retrying Nextcloud directory creation with alternate URL: {}", alternateUrl);
            try {
                sardine.createDirectory(alternateUrl);
                log.debug("Created Nextcloud directory with alternate URL: {}", alternateUrl);
                return;
            } catch (SardineException retryEx) {
                if (retryEx.getStatusCode() == 404 && (sardine.exists(currentUrl) || sardine.exists(alternateUrl))) {
                    log.debug("Nextcloud directory already exists after 404 response: {}", alternateUrl);
                    return;
                }
                if (isIgnorableDirectoryStatus(retryEx.getStatusCode())) {
                    log.debug("Ignoring Nextcloud directory status {} for {}", retryEx.getStatusCode(), alternateUrl);
                    return;
                }
                throw new IllegalStateException(
                        "No se pudo crear el directorio en Nextcloud. URL=" + alternateUrl
                                + ", status=" + retryEx.getStatusCode()
                                + ", reason=" + retryEx.getResponsePhrase(),
                        retryEx);
            }
        }
    }

    private boolean isIgnorableDirectoryStatus(int statusCode) {
        return statusCode == 301 || statusCode == 405 || statusCode == 409;
    }

    private String buildRemoteUrl(String relativePath) {
        return davBase() + "/" + normalizeRelativePath(relativePath);
    }

    private String buildDirectoryRemoteUrl(String relativePath) {
        return buildRemoteUrl(relativePath) + "/";
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

    private boolean isHttps(String value) {
        return StringUtils.hasText(value) && value.trim().toLowerCase().startsWith("https://");
    }

    private String buildFileName(String originalName) {
        String cleanName = StringUtils.hasText(originalName) ? StringUtils.cleanPath(originalName) : "archivo";
        cleanName = cleanName.replace("\\", "_").replace("/", "_").replace(" ", "_");
        return UUID.randomUUID() + "_" + cleanName;
    }
}
