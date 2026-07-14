package com.epmapat.erp_epmapat.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.SmartSyncRequestDto;
import com.epmapat.erp_epmapat.DTO.SmartSyncResponseDto;
import com.epmapat.erp_epmapat.servicio.MobileSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mobile/sync")
public class MobileSyncController {

    private final MobileSyncService mobileSyncService;

    @PostMapping("/download")
    public ResponseEntity<SmartSyncResponseDto> download(@RequestBody SmartSyncRequestDto request) {
        log.info("Iniciando Smart Sync para usuario: {} y emision: {}", request.getIdusuario(), request.getIdemision());
        SmartSyncResponseDto response = mobileSyncService.getSmartSyncData(
                request.getIdusuario(),
                request.getIdemision(),
                request.getModulos()
        );

        log.info("Smart Sync completado para usuario {}: {} lecturas, {} abonados, {} clientes, {} rutas",
                request.getIdusuario(),
                response.getLecturas().size(),
                response.getAbonados().size(),
                response.getClientes().size(),
                response.getRutas().size());

        return ResponseEntity.ok(response);
    }
}
