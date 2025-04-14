package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.interfaces.NtaCreditoSaldos;
import com.epmapat.erp_epmapat.modelo.Ntacredito;
import com.epmapat.erp_epmapat.servicio.NtacreditoServicio;

@RestController
@RequestMapping("/ntacredito")
@CrossOrigin("*")
public class NtacreditoApi {
    @Autowired
    private NtacreditoServicio ntacreditoServicio;

    @GetMapping
    public ResponseEntity<List<Ntacredito>> getAll() {
        return ResponseEntity.ok(ntacreditoServicio.findAll());
    }

    @GetMapping("/{idntacredito}")
    public ResponseEntity<Optional<Ntacredito>> getById(@PathVariable Long idntacredito) {
        return ResponseEntity.ok(ntacreditoServicio.findById(idntacredito));
    }

    @PostMapping
    public ResponseEntity<Ntacredito> save(@RequestBody Ntacredito ntacredito) {
        return ResponseEntity.ok(ntacreditoServicio.save(ntacredito));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Ntacredito>> getAllPageable(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(ntacreditoServicio.findAllNtaCredito(page, size));
    }

    @GetMapping("/saldosNC")
    public ResponseEntity<List<NtaCreditoSaldos>> getSaldosByCuenta(@RequestParam Long cuenta) {
        return ResponseEntity.ok(ntacreditoServicio.findSaldosByCuenta(cuenta));
    }

}
