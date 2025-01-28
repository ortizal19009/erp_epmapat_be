package com.epmapat.erp_epmapat.controlador.coactivas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.coactivas.Facxremi;
import com.epmapat.erp_epmapat.servicio.coactivas.FacxremiServicio;

@RestController
@RequestMapping("/facxremi")
@CrossOrigin("*")
public class FacxremiApi {
    @Autowired
    private FacxremiServicio facxremiServicio;

    @PostMapping
    public ResponseEntity<Facxremi> save(@RequestBody Facxremi facxremi) {
        return ResponseEntity.ok(facxremiServicio.savefacxremi(facxremi));
    }

}
