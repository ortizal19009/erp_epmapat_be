package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.CondonacionCreateRequest;
import com.epmapat.erp_epmapat.DTO.CondonacionAprobacionLoteRequest;
import com.epmapat.erp_epmapat.DTO.CondonacionAprobacionLoteResponse;
import com.epmapat.erp_epmapat.DTO.CondonacionDecisionRequest;
import com.epmapat.erp_epmapat.DTO.CondonacionResponse;
import com.epmapat.erp_epmapat.servicio.CondMultasInteresesServicio;

@RestController
@RequestMapping("/condmultasintereses")
public class CondMultasInteresesApi {
    @Autowired
    CondMultasInteresesServicio codservice;

    @PostMapping
    public ResponseEntity<List<CondonacionResponse>> saveCondonacion(
            @RequestBody CondonacionCreateRequest request,
            @RequestParam(name = "idusuario", required = false) Long idusuario,
            @RequestHeader(name = "X-User-Id", required = false) Long idusuarioHeader) {
        return ResponseEntity.ok(codservice.crearSolicitudes(request, resolverIdusuario(idusuario, idusuarioHeader)));
    }

    @GetMapping
    public ResponseEntity<List<CondonacionResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idfactura,
            @RequestParam(required = false) Long idcliente,
            @RequestParam(required = false) Long cuenta,
            @RequestParam(required = false) String nrofactura,
            @RequestParam(required = false) Long usucrea,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate feccreaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate feccreaHasta) {
        return ResponseEntity.ok(codservice.listar(
                estado,
                idfactura,
                idcliente,
                cuenta,
                nrofactura,
                usucrea,
                feccreaDesde,
                feccreaHasta));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<CondonacionResponse>> pendientes() {
        return ResponseEntity.ok(codservice.listar("PENDIENTE", null, null, null, null, null, null, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CondonacionResponse> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(codservice.obtener(id));
    }

    @PutMapping("/aprobar-lote")
    public ResponseEntity<CondonacionAprobacionLoteResponse> aprobarLote(
            @RequestBody CondonacionAprobacionLoteRequest request,
            @RequestParam(name = "idusuario", required = false) Long idusuario,
            @RequestHeader(name = "X-User-Id", required = false) Long idusuarioHeader) {
        return ResponseEntity.ok(codservice.aprobarLote(request, resolverIdusuario(idusuario, idusuarioHeader)));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<CondonacionResponse> aprobar(
            @PathVariable Long id,
            @RequestBody(required = false) CondonacionDecisionRequest request,
            @RequestParam(name = "idusuario", required = false) Long idusuario,
            @RequestHeader(name = "X-User-Id", required = false) Long idusuarioHeader) {
        return ResponseEntity.ok(codservice.aprobar(id, resolverIdusuario(idusuario, idusuarioHeader), request));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<CondonacionResponse> rechazar(
            @PathVariable Long id,
            @RequestBody CondonacionDecisionRequest request,
            @RequestParam(name = "idusuario", required = false) Long idusuario,
            @RequestHeader(name = "X-User-Id", required = false) Long idusuarioHeader) {
        return ResponseEntity.ok(codservice.rechazar(id, resolverIdusuario(idusuario, idusuarioHeader), request));
    }

    private Long resolverIdusuario(Long idusuario, Long idusuarioHeader) {
        if (idusuario != null && idusuario > 0) {
            return idusuario;
        }
        if (idusuarioHeader != null && idusuarioHeader > 0) {
            return idusuarioHeader;
        }
        return null;
    }
}
