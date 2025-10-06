package com.epmapat.erp_epmapat.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.CondMultasIntereses;
import com.epmapat.erp_epmapat.servicio.CondMultasInteresesServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/condmultasintereses")
@CrossOrigin("*")
public class CondMultasInteresesApi {
    @Autowired
    CondMultasInteresesServicio codservice;

    @PostMapping
    public ResponseEntity<CondMultasIntereses> SaveCondonacion(@RequestBody CondMultasIntereses entity) {
        return ResponseEntity.ok(codservice.save(entity));
    }

}
