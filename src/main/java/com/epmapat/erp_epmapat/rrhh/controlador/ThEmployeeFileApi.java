package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.ThEmployeeFile;
import com.epmapat.erp_epmapat.rrhh.servicio.ThEmployeeFileServicio;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.epmapat.erp_epmapat.rrhh.servicio.ThEmployeeFileAccessService;

@RestController
@RequestMapping("/api/th-files")
@RequiredArgsConstructor
public class ThEmployeeFileApi {

    private final ThEmployeeFileServicio service;
    private final ThEmployeeFileAccessService access;

    @GetMapping("/permissions")
    public java.util.Map<String,Object> permissions(HttpServletRequest request) {
        Long actor = access.require(request,false);
        boolean write = false;
        try { access.require(request,true); write = true; }
        catch (org.springframework.web.server.ResponseStatusException ex) { if (ex.getStatus() != HttpStatus.FORBIDDEN) throw ex; }
        return java.util.Map.of("userId",actor,"canWrite",write);
    }

    @PostMapping
    public ResponseEntity<ThEmployeeFile> save(@RequestBody ThEmployeeFile f, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(f,access.require(request,true)));
    }

    @GetMapping("/persona/{idpersonal}")
    public ResponseEntity<List<ThEmployeeFile>> byPersonal(@PathVariable Long idpersonal, HttpServletRequest request) {
        access.require(request,false);
        return ResponseEntity.ok().header("Cache-Control","no-store").body(service.byPersonal(idpersonal));
    }

    @PostMapping(value="/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ThEmployeeFile> upload(@RequestPart("metadata") ThEmployeeFile metadata,
            @RequestPart("file") MultipartFile file, HttpServletRequest request) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(metadata,file,access.require(request,true)));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) throws Exception {
        access.require(request,false);
        ThEmployeeFile f = service.find(id);
        Resource resource = service.download(f);
        return ResponseEntity.ok().header("Cache-Control","no-store")
            .header("X-Content-Type-Options","nosniff")
            .header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.attachment().filename(f.getNombre_original(),java.nio.charset.StandardCharsets.UTF_8).build().toString())
            .contentType(MediaType.parseMediaType(f.getContent_type())).body(resource);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ThEmployeeFile> state(@PathVariable Long id, @RequestBody java.util.Map<String,String> body, HttpServletRequest request) {
        return ResponseEntity.ok(service.state(id,body.get("estado"),access.require(request,true)));
    }
}
