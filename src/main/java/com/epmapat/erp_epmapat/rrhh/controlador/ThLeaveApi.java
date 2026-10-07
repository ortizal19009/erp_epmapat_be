package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveAccessService;

import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveMovementServicio;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveMovementResponse;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveReconciliationResponse;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveReconciliationCsv;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveReconciliationServicio;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveInboxServicio;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveHistoryResponse;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveHistoryServicio;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveBalanceStateServicio;
import com.epmapat.erp_epmapat.rrhh.dto.ThLeaveBalanceStateResponse;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/th-leave")
public class ThLeaveApi {

    private final ThLeaveAccessService access;
    private final ThLeaveBalanceServicio balanceServicio;
    private final ThLeaveRequestServicio requestServicio;
    private final ThLeaveMovementServicio movementServicio;
    private final ThLeaveReconciliationServicio reconciliationServicio;
    private final ThLeaveInboxServicio inboxServicio;
    private final ThLeaveHistoryServicio historyServicio;
    private final ThLeaveBalanceStateServicio balanceStateServicio;

    @PostMapping("/balances/{idbalance}/estado")
    public ResponseEntity<Void> cambiarEstadoSaldo(@PathVariable Long idbalance, @RequestBody EstadoSaldoBody body,
            HttpServletRequest request) {
        balanceStateServicio.cambiar(idbalance, body.getActivo(), body.getVersion(), body.getMotivo(), access.require(request, true));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balances/{idbalance}/historial-estado")
    public ResponseEntity<ThLeaveBalanceStateResponse> historialEstadoSaldo(@PathVariable Long idbalance, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(balanceStateServicio.historial(idbalance));
    }

    @Data
    public static class EstadoSaldoBody {
        private Boolean activo;
        private Long version;
        private String motivo;
    }

    @GetMapping("/requests/{idrequest}/historial")
    public ResponseEntity<ThLeaveHistoryResponse> historial(@PathVariable Long idrequest, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(historyServicio.consultar(idrequest));
    }

    @GetMapping("/requests/bandeja")
    public ResponseEntity<ThLeaveInboxResponse> bandeja(
            @RequestParam(defaultValue = "SOLICITADA") String estado,
            @RequestParam(required = false) String tipo, @RequestParam(required = false) Long idpersonal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamano,
            HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .body(inboxServicio.consultar(estado, tipo, idpersonal, desde, hasta, pagina, tamano));
    }

    @GetMapping("/requests/calendario")
    public ResponseEntity<ThLeaveInboxResponse> calendario(
            @RequestParam(required = false) String tipo, @RequestParam(required = false) Long idpersonal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int pagina, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .body(inboxServicio.calendario(tipo, idpersonal, desde, hasta, pagina));
    }

    @GetMapping("/balances/persona/{idpersonal}/conciliacion")
    public ResponseEntity<ThLeaveReconciliationResponse> conciliacion(@PathVariable Long idpersonal,
            @RequestParam(required = false) Integer anio, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(reconciliationServicio.verificar(idpersonal, anio));
    }

    @GetMapping("/requests/bandeja.csv")
    public ResponseEntity<byte[]> exportarBandeja(
            @RequestParam(defaultValue = "SOLICITADA") String estado,
            @RequestParam(required = false) String tipo, @RequestParam(required = false) Long idpersonal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamano,
            HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"solicitudes-rrhh-pagina-" + (pagina + 1) + ".csv\"")
                .body(com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxCsv.render(
                        inboxServicio.consultar(estado, tipo, idpersonal, desde, hasta, pagina, tamano)));
    }

    @GetMapping("/balances/persona/{idpersonal}/conciliacion.csv")
    public ResponseEntity<byte[]> exportarConciliacion(@PathVariable Long idpersonal,
            @RequestParam(required = false) Integer anio, HttpServletRequest request) {
        access.require(request, false);
        ThLeaveReconciliationResponse report = reconciliationServicio.verificar(idpersonal, anio);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"conciliacion-rrhh-" + idpersonal + ".csv\"")
                .header("Cache-Control", "no-store")
                .body(ThLeaveReconciliationCsv.render(report));
    }

    @GetMapping("/permissions")
    public Map<String, Object> permissions(HttpServletRequest request) {
        Long actor = access.require(request, false);
        boolean write;
        try { access.require(request, true); write = true; }
        catch (org.springframework.web.server.ResponseStatusException ex) {
            if (ex.getStatus() != org.springframework.http.HttpStatus.FORBIDDEN) throw ex;
            write = false;
        }
        return Map.of("userId", actor, "canWrite", write);
    }

    @PostMapping("/balances")
    public ResponseEntity<ThLeaveBalance> createBalance(@RequestBody ThLeaveBalance b, HttpServletRequest request) {
        b.setUsucrea(access.require(request, true));
        return ResponseEntity.ok(balanceServicio.save(b));
    }

    @PostMapping("/balances/{idbalance}/ajustar")
    public ResponseEntity<Void> ajustar(@PathVariable Long idbalance, @RequestBody AjusteBody body, HttpServletRequest request) {
        movementServicio.ajustar(idbalance, body.getDias(), body.getMotivo(), body.getClave(), access.require(request, true));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/balances/{idbalance}/abrir-libro")
    public ResponseEntity<Void> abrirLibro(@PathVariable Long idbalance, HttpServletRequest request) {
        movementServicio.abrirLibro(idbalance, access.require(request, true));
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class AjusteBody {
        private java.math.BigDecimal dias;
        private String motivo;
        private String clave;
    }

    @GetMapping("/balances/persona/{idpersonal}")
    public ResponseEntity<List<ThLeaveBalance>> balancesByPersona(@PathVariable Long idpersonal, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok(balanceServicio.byPersonal(idpersonal));
    }

    @PostMapping("/requests")
    public ResponseEntity<ThLeaveRequest> createRequest(@RequestBody ThLeaveRequest r, HttpServletRequest request) {
        r.setUsucrea(access.require(request, true));
        return ResponseEntity.ok(requestServicio.save(r));
    }

    @PostMapping("/requests/{idrequest}/aprobar")
    public ResponseEntity<ThLeaveRequest> aprobar(@PathVariable Long idrequest, @RequestBody AprobacionBody b, HttpServletRequest request) {
        Long actor = access.require(request, true);
        return ResponseEntity.ok(requestServicio.aprobar(idrequest, actor, b.getObservacion()));
    }

    @PostMapping("/requests/{idrequest}/rechazar")
    public ResponseEntity<ThLeaveRequest> rechazar(@PathVariable Long idrequest, @RequestBody AprobacionBody b, HttpServletRequest request) {
        Long actor = access.require(request, true);
        return ResponseEntity.ok(requestServicio.rechazar(idrequest, actor, b.getObservacion()));
    }

    @GetMapping("/requests/persona/{idpersonal}")
    public ResponseEntity<List<ThLeaveRequest>> requestsByPersona(@PathVariable Long idpersonal, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok(requestServicio.byPersonal(idpersonal));
    }

    @PostMapping("/requests/{idrequest}/cancelar")
    public ResponseEntity<ThLeaveRequest> cancelar(@PathVariable Long idrequest, @RequestBody ResolucionBody body,
            HttpServletRequest request) {
        return ResponseEntity.ok(requestServicio.cancelar(idrequest, access.require(request, true), body.getMotivo()));
    }

    @PostMapping("/requests/{idrequest}/revertir")
    public ResponseEntity<ThLeaveRequest> revertir(@PathVariable Long idrequest, @RequestBody ResolucionBody body,
            HttpServletRequest request) {
        return ResponseEntity.ok(requestServicio.revertir(idrequest, access.require(request, true), body.getMotivo()));
    }

    @GetMapping("/movements/persona/{idpersonal}")
    public ResponseEntity<List<ThLeaveMovementResponse>> movementsByPersona(@PathVariable Long idpersonal,
            @RequestParam(required = false) Integer anio, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok(movementServicio.byPersonal(idpersonal, anio));
    }

    @GetMapping("/movements/persona/{idpersonal}/libro.csv")
    public ResponseEntity<byte[]> exportarLibro(@PathVariable Long idpersonal, @RequestParam Integer anio,
            HttpServletRequest request) {
        access.require(request, false);
        if (idpersonal <= 0 || anio < 1900 || anio > 9999)
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Indique empleado y año válidos para exportar el libro");
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"libro-rrhh-" + idpersonal + "-" + anio + ".csv\"")
                .body(com.epmapat.erp_epmapat.rrhh.dto.ThLeaveMovementCsv.render(idpersonal, movementServicio.byPersonal(idpersonal, anio)));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ThLeaveRequest>> requestsByEstado(@RequestParam String estado, HttpServletRequest request) {
        access.require(request, false);
        return ResponseEntity.ok(requestServicio.byEstado(estado));
    }

    @Data
    public static class AprobacionBody {
        private Long aprobadorId;
        private String observacion;
    }

    @Data
    public static class ResolucionBody {
        private String motivo;
    }
}
