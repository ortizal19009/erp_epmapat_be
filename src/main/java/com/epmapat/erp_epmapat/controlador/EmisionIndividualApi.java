package com.epmapat.erp_epmapat.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
