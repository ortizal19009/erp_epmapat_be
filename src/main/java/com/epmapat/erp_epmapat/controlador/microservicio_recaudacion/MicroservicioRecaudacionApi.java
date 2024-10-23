package com.epmapat.erp_epmapat.controlador.microservicio_recaudacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.servicio.microservicio_recaudacion.RecaudacionMicroservice;

@RestController
@RequestMapping("/mrecaudacion")
@CrossOrigin("*")
public class MicroservicioRecaudacionApi {
    @Autowired
    private RecaudacionMicroservice sRecaudacionMicroservice;

    @GetMapping("/sincobro/cuenta")
    public ResponseEntity<List<Object>> getSincobroByCuenta(@RequestParam Long cuenta) {
        return ResponseEntity.ok(sRecaudacionMicroservice.sinCobrarByCuenta(cuenta));
    }

    @GetMapping("/sincobro/cliente")
    public ResponseEntity<List<Object>> getSincobroByCliente(@RequestParam Long idcliente) {
        return ResponseEntity.ok(sRecaudacionMicroservice.sinCobrarByCliente(idcliente));
    }
}
