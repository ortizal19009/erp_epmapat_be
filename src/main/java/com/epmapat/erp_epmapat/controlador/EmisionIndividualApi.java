package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRI;
import com.epmapat.erp_epmapat.interfaces.IemiIndividual;
import com.epmapat.erp_epmapat.modelo.EmisionIndividual;
import com.epmapat.erp_epmapat.servicio.EmisionIndividualServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/emisionindividual")
@CrossOrigin("*")
public class EmisionIndividualApi {
    @Autowired
    private EmisionIndividualServicio sei;

    @PostMapping
    public ResponseEntity<EmisionIndividual> postMethodName(@RequestBody EmisionIndividual emiIndi) {
        return ResponseEntity.ok(sei.save(emiIndi));
    }

    @GetMapping("/idemision")
    public ResponseEntity<List<EmisionIndividual>> getByIdEmision(@RequestParam("idemision") Long idemision) {
        return ResponseEntity.ok(sei.findByIdEmision(idemision));
    }

    @GetMapping("/nuevas")
    public ResponseEntity<List<IemiIndividual>> getLecturasNuevas(@RequestParam("idemision") Long idemision) {
        return ResponseEntity.ok(sei.findLecturasNuevas(idemision));
    }

    @GetMapping("/anteriores")
    public ResponseEntity<List<IemiIndividual>> getLecturasAnteriores(@RequestParam("idemision") Long idemision) {
        return ResponseEntity.ok(sei.findLecturasAnteriores(idemision));
    }

    @GetMapping("/reportes/emisiones")
    public ResponseEntity<List<EmisionIndividualRI>> findLecReport(@RequestParam("idemision") Integer idemision) {
        return ResponseEntity.ok(sei.getLecReport(idemision));
    }

}
