package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;

import com.epmapat.erp_epmapat.modelo.Novedad;
import com.epmapat.erp_epmapat.servicio.NovedadServicio;

@RestController
@RequestMapping("/novedades")
@CrossOrigin(origins = "*")

public class NovedadesApi {

    @Autowired
    NovedadServicio novServicio;

    // @GetMapping
    // public List<Novedad> getAllNovedades() {
    // return novServicio.findAll();
    // }

    @GetMapping
    public List<Novedad> getAll(@Param(value = "descripcion") String descripcion) {
        if (descripcion != null) {
            return novServicio.findByDescri(descripcion);
        } else {
            return novServicio.findAll();
        }
    }

    @PostMapping
    public Novedad updateOrSave(@RequestBody Novedad nov) {
        return novServicio.save(nov);
    }

    @GetMapping("/{idnovedad}")
    public ResponseEntity<Novedad> getByIdNovedad(@PathVariable Long idnovedad) {
        Novedad x = novServicio.findById(idnovedad)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Novedad Id: " + idnovedad)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idnovedad}")
    public ResponseEntity<Novedad> updateNovedad(@PathVariable Long idnovedad, @RequestBody Novedad x) {
        Novedad y = novServicio.findById(idnovedad)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Novedad Id: " + idnovedad)));
        y.setDescripcion(x.getDescripcion());
        y.setEstado(x.getEstado());
        y.setUsucrea(x.getUsucrea());
        // y.setFeccrea(x.getFeccrea());

        Novedad xy = novServicio.save(y);
        return ResponseEntity.ok(xy);
    }

    @PatchMapping("/{idnovedad}")
    public ResponseEntity<Novedad> updatePartially(@PathVariable Long idnovedad, @RequestBody Novedad x) {
        Novedad y = novServicio.findById(idnovedad)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Novedad Id: " + idnovedad)));
        y.setDescripcion(x.getDescripcion());
        if (y.getEstado() != x.getEstado()) {
            y.setEstado(x.getEstado());
        }

        Novedad xy = novServicio.save(y);
        return ResponseEntity.ok(xy);
    }

    @DeleteMapping("/{idnovedad}")
    private ResponseEntity<Boolean> deleteNovedad(@PathVariable("idnovedad") Long idnovedad) {
        novServicio.deleteById(idnovedad);
        return ResponseEntity.ok(!(novServicio.findById(idnovedad) != null));
    }

}