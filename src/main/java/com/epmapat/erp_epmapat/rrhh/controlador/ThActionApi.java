package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.ThAction;
import com.epmapat.erp_epmapat.rrhh.servicio.ThActionServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/th-actions")
@RequiredArgsConstructor
public class ThActionApi {

    private final ThActionServicio thActionServicio;

    @PostMapping
    public ResponseEntity<ThAction> save(@RequestBody ThAction action) {
        return ResponseEntity.ok(thActionServicio.save(action));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThAction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(thActionServicio.findById(id));
    }

    @GetMapping("/persona/{idpersonal}")
    public ResponseEntity<List<ThAction>> getByPersonal(@PathVariable Long idpersonal) {
        return ResponseEntity.ok(thActionServicio.findByPersonal(idpersonal));
    }
}
