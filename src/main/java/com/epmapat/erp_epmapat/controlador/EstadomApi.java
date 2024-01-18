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
import com.epmapat.erp_epmapat.modelo.Estadom;
import com.epmapat.erp_epmapat.servicio.EstadomServicio;

@RestController
@RequestMapping("/estadom")
@CrossOrigin(origins = "*")

public class EstadomApi {
       
    @Autowired
    EstadomServicio EstmServicio;

    @GetMapping
    public List<Estadom> getAllEstadoms() {
        return EstmServicio.findAll();
    }

    @PostMapping
    public Estadom updateOrSave(@RequestBody Estadom x) {
        return EstmServicio.save(x);
    }

    @GetMapping("/{idestadom}")
    public ResponseEntity<Estadom> getById(@PathVariable Long idestadom) {
        Estadom x = EstmServicio.findById(idestadom)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Estado del Medidor Id: " + idestadom)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idestadom}")
    public ResponseEntity<Estadom> update(@PathVariable Long idestadom, @RequestBody Estadom x) {
        Estadom y = EstmServicio.findById(idestadom)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Estado del Medidor Id: " + idestadom)));
        y.setDescripcion(x.getDescripcion());
        y.setUsucrea(x.getUsucrea());
    //    y.setFeccrea(x.getFeccrea());
    //    y.setUsumodi(x.getUsumodi());
    //    y.setFecmodi(x.getFecmodi());

        Estadom actualizar = EstmServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{idestadom}")
    private ResponseEntity<Boolean> delete(@PathVariable("idestadom") Long idestadom) {
        EstmServicio.deleteById(idestadom);
        return ResponseEntity.ok(!(EstmServicio.findById(idestadom) != null));
    }

}
