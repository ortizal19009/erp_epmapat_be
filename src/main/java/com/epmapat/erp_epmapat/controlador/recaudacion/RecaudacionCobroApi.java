package com.epmapat.erp_epmapat.controlador.recaudacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.LoginRequest;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCajaDTO;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCajaOperacionResponse;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCobroRequest;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCobroResponse;
import com.epmapat.erp_epmapat.servicio.recaudacion.RecaudacionCobroServicio;

@RestController
@RequestMapping("/recaudacion-cobro")
public class RecaudacionCobroApi {

    @Autowired
    private RecaudacionCobroServicio recaudacionCobroServicio;

    @GetMapping("/sincobro/cuenta")
    public ResponseEntity<List<ValorFactDTO>> getSincobroByCuenta(@RequestParam Long cuenta) {
        return ResponseEntity.ok(recaudacionCobroServicio.getSincobroByCuenta(cuenta));
    }

    @GetMapping("/sincobro/cliente")
    public ResponseEntity<List<ValorFactDTO>> getSincobroByCliente(@RequestParam Long idcliente) {
        return ResponseEntity.ok(recaudacionCobroServicio.getSincobroByCliente(idcliente));
    }

    @GetMapping("/caja/estado")
    public ResponseEntity<RecaudacionCajaDTO> getCajaEstado(@RequestParam Long idusuario) {
        return ResponseEntity.ok(recaudacionCobroServicio.getEstadoCaja(idusuario));
    }

    @GetMapping("/caja/abrir")
    public ResponseEntity<RecaudacionCajaOperacionResponse> abrirCaja(
            @RequestParam String username,
            @RequestParam String password) {
        return ResponseEntity.ok(recaudacionCobroServicio.abrirCaja(username, password));
    }

    @PostMapping("/caja/abrir")
    public ResponseEntity<RecaudacionCajaOperacionResponse> abrirCajaPost(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(recaudacionCobroServicio.abrirCaja(request.getUsername(), request.getPassword()));
    }

    @PutMapping("/caja/cerrar")
    public ResponseEntity<RecaudacionCajaOperacionResponse> cerrarCaja(@RequestParam String username) {
        return ResponseEntity.ok(recaudacionCobroServicio.cerrarCaja(username));
    }

    @PostMapping("/cobrar")
    public ResponseEntity<RecaudacionCobroResponse> cobrar(@RequestBody RecaudacionCobroRequest request) {
        return ResponseEntity.ok(recaudacionCobroServicio.cobrar(request));
    }

    @PutMapping("/cobrar")
    public ResponseEntity<RecaudacionCobroResponse> cobrarPut(@RequestBody RecaudacionCobroRequest request) {
        return ResponseEntity.ok(recaudacionCobroServicio.cobrar(request));
    }
}
