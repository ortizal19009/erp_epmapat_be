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
import com.epmapat.erp_epmapat.modelo.Tpreclamo;
import com.epmapat.erp_epmapat.servicio.TpreclamoServicio;

@RestController
@RequestMapping("/tpreclamo")
@CrossOrigin("*")

public class TpreclamoApi {

    @Autowired
    TpreclamoServicio TpreclamoServicio;

    @GetMapping
    public List<Tpreclamo> getAllTpreclamoes() {
        return TpreclamoServicio.findAll();
    }

    @PostMapping
    public Tpreclamo updateOrSave(@RequestBody Tpreclamo Tpreclamo) {
        return TpreclamoServicio.save(Tpreclamo);
    }

    @GetMapping("/{idtpreclamo}")
    public ResponseEntity<Tpreclamo> getByidtpreclamo(@PathVariable Long idtpreclamo) {
        Tpreclamo x = TpreclamoServicio.findById(idtpreclamo)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe el Tipo de Reclamo Id: " + idtpreclamo)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idtpreclamo}")
    public ResponseEntity<Tpreclamo> updateTpreclamo(@PathVariable Long idtpreclamo, @RequestBody Tpreclamo x) {
        Tpreclamo y = TpreclamoServicio.findById(idtpreclamo)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe el Tipo de Reclamo Id: " + idtpreclamo)));
        y.setDescripcion(x.getDescripcion());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Tpreclamo updaTpreclamo = TpreclamoServicio.save(y);
        return ResponseEntity.ok(updaTpreclamo);
    }

    @DeleteMapping("/{idtpreclamo}")
    private ResponseEntity<Boolean> deleteTpreclamo(@PathVariable("idtpreclamo") Long idtpreclamo) {
        TpreclamoServicio.deleteById(idtpreclamo);
        return ResponseEntity.ok(!(TpreclamoServicio.findById(idtpreclamo) != null));
    }

}
