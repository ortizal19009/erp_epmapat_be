package com.epmapat.erp_epmapat.controlador.administracion;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.servicio.AbonadoServicio;
import com.epmapat.erp_epmapat.servicio.administracion.StorageService;

@RestController
@RequestMapping("/api/storage")
public class StorageApi {

    private final StorageService storageService;
    private final AbonadoServicio abonadoServicio;

    @Value("${storage.type:local}")
    private String storageType;

    @Value("${nextcloud.base-url:}")
    private String nextcloudBaseUrl;

    @Value("${nextcloud.username:}")
    private String nextcloudUsername;

    @Value("${nextcloud.base-folder:}")
    private String nextcloudBaseFolder;

    public StorageApi(StorageService storageService, AbonadoServicio abonadoServicio) {
        this.storageService = storageService;
        this.abonadoServicio = abonadoServicio;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "storageType", storageType,
                "implementation", storageService.getClass().getSimpleName(),
                "nextcloudBaseUrl", nextcloudBaseUrl,
                "nextcloudUsername", nextcloudUsername,
                "nextcloudBaseFolder", nextcloudBaseFolder
        ));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "general") String folder) throws Exception {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("detail", "Archivo requerido"));
        }

        String relativePath = storageService.store(file, folder);
        String filename = file.getOriginalFilename() == null ? "archivo" : file.getOriginalFilename();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("filename", filename);
        body.put("originalFilename", filename);
        body.put("contentType", file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        body.put("size", file.getSize());
        body.put("folder", folder);
        body.put("path", relativePath);
        body.put("relativePath", relativePath);
        body.put("storagePath", relativePath);
        body.put("downloadUrl", "/api/storage/file?path=" + URLEncoder.encode(relativePath, StandardCharsets.UTF_8));

        return ResponseEntity.ok(body);
    }

    @PostMapping(value = "/upload/abonado", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAbonadoFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idabonado") Long idabonado,
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "usumodi", required = false, defaultValue = "0") Long usumodi,
            @RequestParam(value = "observacion", required = false, defaultValue = "Upload foto de abonado") String observacion,
            @RequestParam(value = "accion", required = false, defaultValue = "MODIFICACION") String accion) throws Exception {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("detail", "Archivo requerido"));
        }

        Abonados abonado = abonadoServicio.findById(idabonado)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe el Abonado con Id: " + idabonado));

        String tipoNormalizado = tipo == null ? "" : tipo.trim().toLowerCase();
        String folder;
        String fotocasaPath = null;
        String fotomedidorPath = null;

        if ("fotocasa".equals(tipoNormalizado) || "casa".equals(tipoNormalizado)) {
            folder = "abonados/casas";
            fotocasaPath = storageService.store(file, folder);
        } else if ("fotomedidor".equals(tipoNormalizado) || "medidor".equals(tipoNormalizado)) {
            folder = "abonados/medidores";
            fotomedidorPath = storageService.store(file, folder);
        } else {
            return ResponseEntity.badRequest().body(Map.of("detail", "tipo debe ser fotocasa o fotomedidor"));
        }

        abonado = abonadoServicio.actualizarFotosAbonadoConAuditoria(
                idabonado,
                fotocasaPath,
                fotomedidorPath,
                usumodi,
                observacion,
                accion);

        String storedPath = fotocasaPath != null ? fotocasaPath : fotomedidorPath;
        String filename = file.getOriginalFilename() == null ? "archivo" : file.getOriginalFilename();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("idabonado", idabonado);
        body.put("tipo", tipoNormalizado);
        body.put("filename", filename);
        body.put("path", storedPath);
        body.put("relativePath", storedPath);
        body.put("storagePath", storedPath);
        body.put("fotocasaPath", abonado.getFotocasaPath());
        body.put("fotomedidorPath", abonado.getFotomedidorPath());

        return ResponseEntity.ok(body);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> getFile(@RequestParam("path") String path) throws Exception {
        Resource resource = storageService.load(path);
        if (resource == null || !resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String filename = resource.getFilename();
        if (!StringUtils.hasText(filename)) {
            filename = StringUtils.getFilename(path);
        }

        MediaType contentType = MediaTypeFactory.getMediaType(filename)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> delete(@RequestParam("path") String path) throws Exception {
        storageService.delete(path);
        return ResponseEntity.ok(Map.of("ok", true, "path", path));
    }
}
