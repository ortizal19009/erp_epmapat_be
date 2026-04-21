package com.epmapat.erp_epmapat.controlador.qztray;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.servicio.qztray.QzTrayFirmaServicio;

@RestController
@RequestMapping("/qz")
public class QzTrayApi {

    private final QzTrayFirmaServicio firmaServicio;

    public QzTrayApi(QzTrayFirmaServicio firmaServicio) {
        this.firmaServicio = firmaServicio;
    }

    @GetMapping(value = "/certificate", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> certificate() {
        return ResponseEntity.ok(firmaServicio.getCertificatePem());
    }

    @GetMapping(value = "/signature", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> signature(@RequestParam("request") String request) {
        return ResponseEntity.ok(firmaServicio.sign(request));
    }
}
