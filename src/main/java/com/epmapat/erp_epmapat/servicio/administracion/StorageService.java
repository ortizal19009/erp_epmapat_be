package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, String folder) throws Exception;

    Resource load(String relativePath) throws Exception;

    void delete(String relativePath) throws Exception;

    Map<String, Object> health() throws Exception;
}
