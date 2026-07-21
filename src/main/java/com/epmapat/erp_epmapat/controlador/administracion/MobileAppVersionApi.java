package com.epmapat.erp_epmapat.controlador.administracion;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.administracion.MobileAppVersion;
import com.epmapat.erp_epmapat.servicio.administracion.MobileAppVersionService;

@RestController
@RequestMapping("/mobile-app-versions")
public class MobileAppVersionApi {

    private final MobileAppVersionService service;

    public MobileAppVersionApi(MobileAppVersionService service) {
        this.service = service;
    }

    @GetMapping
    public List<MobileAppVersion> listAll() {
        return service.findAll();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "packageName", required = false) String packageName,
            @RequestParam("versionName") String versionName,
            @RequestParam("versionCode") Long versionCode,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "forceUpdate", required = false, defaultValue = "false") Boolean forceUpdate,
            @RequestParam(value = "usucrea", required = false) Long usucrea) throws Exception {

        MobileAppVersion saved = service.saveVersion(
                file,
                packageName,
                versionName,
                versionCode,
                descripcion,
                forceUpdate,
                usucrea);

        return ResponseEntity.ok(buildVersionPayload(saved));
    }

    @GetMapping("/public/latest")
    public ResponseEntity<Map<String, Object>> getLatest(
            @RequestParam(value = "packageName", required = false) String packageName) {
        MobileAppVersion version = service.findLatestActiveByPackageName(packageName)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe una version activa publicada"));
        return ResponseEntity.ok(buildVersionPayload(version));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {
        MobileAppVersion version = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe la version APK con id: " + id));
        Resource resource = service.loadAsResource(version);
        MediaType contentType = MediaTypeFactory.getMediaType(version.getArchivoNombre())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + version.getArchivoNombre() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) throws Exception {
        boolean deleted = service.deleteById(id);
        return ResponseEntity.ok(Map.of("ok", deleted, "id", id));
    }

    private Map<String, Object> buildVersionPayload(MobileAppVersion version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", version.getId());
        body.put("packageName", version.getPackageName());
        body.put("versionName", version.getVersionName());
        body.put("versionCode", version.getVersionCode());
        body.put("descripcion", version.getDescripcion());
        body.put("forceUpdate", version.getForceUpdate());
        body.put("activo", version.getActivo());
        body.put("archivoNombre", version.getArchivoNombre());
        body.put("archivoRuta", version.getArchivoRuta());
        body.put("contentType", version.getContentType());
        body.put("sizeBytes", version.getSizeBytes());
        body.put("checksumSha256", version.getChecksumSha256());
        body.put("entorno", version.getEntorno());
        body.put("hostName", version.getHostName());
        body.put("feccrea", version.getFeccrea());
        body.put("downloadUrl", "/mobile-app-versions/download/" + version.getId());
        body.put("downloadUrlEncoded", "/mobile-app-versions/download/" + URLEncoder.encode(String.valueOf(version.getId()), StandardCharsets.UTF_8));
        return body;
    }
}
