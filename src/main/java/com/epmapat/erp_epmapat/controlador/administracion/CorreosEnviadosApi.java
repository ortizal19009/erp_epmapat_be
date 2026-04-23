package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.administracion.CorreosEnviados;
import com.epmapat.erp_epmapat.servicio.administracion.CorreosEnviadosServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/correos-enviados")
@RequiredArgsConstructor
public class CorreosEnviadosApi {

    private final CorreosEnviadosServicio servicio;

    @GetMapping
    public ResponseEntity<List<CorreosEnviados>> listar(
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(servicio.listar(modulo, estado));
    }

    @PostMapping
    public ResponseEntity<CorreosEnviados> registrar(@RequestBody CorreosEnviadosRequest request) {
        CorreosEnviados envio = servicio.registrarEnvio(
                request.modulo(),
                request.documentoid(),
                request.documento(),
                request.destinatarios(),
                request.asunto(),
                request.remitente(),
                request.archivoadjunto(),
                request.estado(),
                request.detalle());
        return ResponseEntity.ok(envio);
    }

    public record CorreosEnviadosRequest(
            String modulo,
            Long documentoid,
            String documento,
            String destinatarios,
            String asunto,
            String remitente,
            String archivoadjunto,
            String estado,
            String detalle) {}
}
