package com.epmapat.erp_epmapat.controlador;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.servicio.BackupService;

@RestController
@RequestMapping("/api/backup")
@CrossOrigin("*")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping
    public ResponseEntity<String> crearBackup() throws InterruptedException {
        try {
            backupService.generarBackup();
            return ResponseEntity.ok("Backup generado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el backup: " + e.getMessage());
        }
    }
}