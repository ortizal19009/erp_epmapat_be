package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveBalance;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveRequest;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveBalanceServicio;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveRequestServicio;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/th-leave")
public class ThLeaveApi {

    private final ThLeaveBalanceServicio balanceServicio;
    private final ThLeaveRequestServicio requestServicio;

    @PostMapping("/balances")
    public ResponseEntity<ThLeaveBalance> createBalance(@RequestBody ThLeaveBalance b) {
        return ResponseEntity.ok(balanceServicio.save(b));
    }

    @GetMapping("/balances/persona/{idpersonal}")
    public ResponseEntity<List<ThLeaveBalance>> balancesByPersona(@PathVariable Long idpersonal) {
        return ResponseEntity.ok(balanceServicio.byPersonal(idpersonal));
    }

    @PostMapping("/requests")
    public ResponseEntity<ThLeaveRequest> createRequest(@RequestBody ThLeaveRequest r) {
        return ResponseEntity.ok(requestServicio.save(r));
    }

    @PostMapping("/requests/{idrequest}/aprobar")
    public ResponseEntity<ThLeaveRequest> aprobar(@PathVariable Long idrequest, @RequestBody AprobacionBody b) {
        return ResponseEntity.ok(requestServicio.aprobar(idrequest, b.getAprobadorId(), b.getObservacion()));
    }

    @PostMapping("/requests/{idrequest}/rechazar")
    public ResponseEntity<ThLeaveRequest> rechazar(@PathVariable Long idrequest, @RequestBody AprobacionBody b) {
        return ResponseEntity.ok(requestServicio.rechazar(idrequest, b.getAprobadorId(), b.getObservacion()));
    }

    @GetMapping("/requests/persona/{idpersonal}")
    public ResponseEntity<List<ThLeaveRequest>> requestsByPersona(@PathVariable Long idpersonal) {
        return ResponseEntity.ok(requestServicio.byPersonal(idpersonal));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ThLeaveRequest>> requestsByEstado(@RequestParam String estado) {
        return ResponseEntity.ok(requestServicio.byEstado(estado));
    }

    @Data
    public static class AprobacionBody {
        private Long aprobadorId;
        private String observacion;
    }
}
