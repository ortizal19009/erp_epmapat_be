package com.epmapat.erp_epmapat.controlador.coactivas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.coactivas.Remision;
import com.epmapat.erp_epmapat.servicio.coactivas.RemisionServicio;

@RestController
@RequestMapping("/remisiones")
@CrossOrigin("*")
public class RemisionApi {
    @Autowired
    private RemisionServicio remisionServicio;

    @GetMapping
    public ResponseEntity<Page<Remision>> findAllPageable(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(remisionServicio.findAllPageable(page, size));
    }

    @PostMapping
    public ResponseEntity<Remision> saveRemision(@RequestBody Remision remision) {
        return ResponseEntity.ok(remisionServicio.saveRemision(remision));
    }

}
