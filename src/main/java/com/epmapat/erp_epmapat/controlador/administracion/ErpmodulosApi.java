package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulos;
import com.epmapat.erp_epmapat.servicio.administracion.ErpmodulosServicio;

@RestController
@RequestMapping("/erpmodulos")
@CrossOrigin("*")
public class ErpmodulosApi {
    @Autowired
    private ErpmodulosServicio emServicio;

    @GetMapping
    public ResponseEntity<List<Erpmodulos>> getAll() {
        return ResponseEntity.ok(emServicio.findAll());
    }
}
