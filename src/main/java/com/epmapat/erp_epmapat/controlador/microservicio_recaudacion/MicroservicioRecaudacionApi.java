package com.epmapat.erp_epmapat.controlador.microservicio_recaudacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.microserviceRecaudacion.FacturaRequest;
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

    @PutMapping("/cobrar")
    public ResponseEntity<?> cobrarFacturas(@RequestBody FacturaRequest factura) {
        System.out.println("Recibido el objeto factura: " + factura);
        Object response = sRecaudacionMicroservice.cobrar(factura);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test_connection")
    public ResponseEntity<Object> testConection(@RequestParam Long user) {
        return ResponseEntity.ok(sRecaudacionMicroservice.testConnection(user));
    }
    @GetMapping("/login")
    public ResponseEntity<Object> loginCajas(@RequestParam String username, @RequestParam String password){
        return ResponseEntity.ok(sRecaudacionMicroservice.sinInCaja(username, password));
    }
    @PutMapping("/logout")
    public ResponseEntity<Object> logOutcajaResponseEntity(@RequestParam String username){
        return ResponseEntity.ok(sRecaudacionMicroservice.sinOutCaja(username));
    }
    @GetMapping("/interes")
    public ResponseEntity<Object> getInteresFac(@RequestParam Long idfactura) {
        return ResponseEntity.ok(sRecaudacionMicroservice.getInteresFac(idfactura));
    }
    @GetMapping("/impuestos")
    public ResponseEntity<Object> getImpuestosFac(@RequestParam Long idfactura) {
        return ResponseEntity.ok(sRecaudacionMicroservice.getImpuestosFac(idfactura));
    }
    
    
}
