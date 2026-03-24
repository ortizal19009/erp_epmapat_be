package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.RecargoXCtaReq;
import com.epmapat.erp_epmapat.DTO.ValidarRecargosRequest;
import com.epmapat.erp_epmapat.DTO.ValidarRecargosResponse;
import com.epmapat.erp_epmapat.modelo.Recargosxcuenta;
import com.epmapat.erp_epmapat.servicio.RecargosxcuentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recargosxcuenta")
public class RecargosxcuentaApi {
    private final RecargosxcuentaService recargosxcuentaService;

    @GetMapping("/byEmision")
    public List<Recargosxcuenta> getRecargosxcuentaByEmision(Long idemision) {
        return recargosxcuentaService.findAllByEmision(idemision);
    }

    @PostMapping("/validar")
    public ResponseEntity<ValidarRecargosResponse> validar(@RequestBody ValidarRecargosRequest req) {
        return ResponseEntity.ok(recargosxcuentaService.validar(req));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Recargosxcuenta>> guardarBatch(@RequestBody List<RecargoXCtaReq> reqs) {
        return ResponseEntity.ok(recargosxcuentaService.guardarBatchConValidaciones(reqs));
    }

}
