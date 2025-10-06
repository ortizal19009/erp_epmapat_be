package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Ubicacionm;
import com.epmapat.erp_epmapat.servicio.UbicacionmServicio;

@RestController
@RequestMapping("/ubicacionm")
@CrossOrigin("*")

public class UbicacionmApi {

    @Autowired
    UbicacionmServicio ubimServicio;

    @GetMapping
    public List<Ubicacionm> getAllUbicacionm() {
        return ubimServicio.findAll();
    }

    @PostMapping
    public Ubicacionm updateOrSave(@RequestBody Ubicacionm x) {
        return ubimServicio.save(x);
    }

    @GetMapping("/{idubicacionm}")
    public ResponseEntity<Ubicacionm> getByIdUbicacionm(@PathVariable Long idubicacionm) {
        Ubicacionm x = ubimServicio.findById(idubicacionm)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Ubicacion de Medidor Id: " + idubicacionm)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idubicacionm}")
    public ResponseEntity<Ubicacionm> update(@PathVariable Long idubicacionm, @RequestBody Ubicacionm x) {
        Ubicacionm y = ubimServicio.findById(idubicacionm)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Ubicación de Medidor Id: " + idubicacionm)));
        y.setDescripcion(x.getDescripcion());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Ubicacionm actualizar = ubimServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{idubicacionm}")
    private ResponseEntity<Boolean> delete(@PathVariable("idubicacionm") Long idubicacionm) {
        ubimServicio.deleteById(idubicacionm);
        return ResponseEntity.ok(!(ubimServicio.findById(idubicacionm) != null));
    }

}
