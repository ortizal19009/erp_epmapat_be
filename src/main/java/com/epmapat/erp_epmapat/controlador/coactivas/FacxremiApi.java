package com.epmapat.erp_epmapat.controlador.coactivas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Facxrecauda;
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

    @GetMapping("/byremision")
    public ResponseEntity<List<Facxremi>> getByRemision(@RequestParam Long idremision, @RequestParam Long tipfac) {
        return ResponseEntity.ok(facxremiServicio.findByRemision(idremision, tipfac));
    }

}
