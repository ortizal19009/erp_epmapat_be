package com.epmapat.erp_epmapat.servicio;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.servicio.administracion.StorageService;

@Service
public class LecturaFotoStorageService {

    private final StorageService storageService;

    public LecturaFotoStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String saveImageFile(Long idlectura, MultipartFile file, String subfolder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            return storageService.store(file, subfolder);
        } catch (Exception ex) {
            throw new IOException("No se pudo guardar la foto de la lectura", ex);
        }
    }

    public Resource loadAsResource(String storedPath) throws IOException {
        try {
            return storageService.load(storedPath);
        } catch (Exception ex) {
            throw new IOException("No se pudo cargar la foto de la lectura", ex);
        }
    }
}
