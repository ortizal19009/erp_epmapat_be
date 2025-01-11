package com.epmapat.erp_epmapat.controlador.contabilidad;

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
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla01;
import com.epmapat.erp_epmapat.servicio.contabilidad.Tabla01Servicio;

@RestController
@RequestMapping("/tabla01")
@CrossOrigin("*")

public class Tabla01Api {
    @Autowired
    Tabla01Servicio tabla01Servicio;

    @PostMapping
    public Tabla01 updateOrSave(@RequestBody Tabla01 x) {
        return tabla01Servicio.save(x);
    }

    @GetMapping
    public List<Tabla01> findAll() {
        return tabla01Servicio.findAll();
    }

    @PutMapping("/{idtabla01}")
    public ResponseEntity<Tabla01> updateTabla01(@PathVariable Long idtabla01, @RequestBody Tabla01 x) {
        Tabla01 y = tabla01Servicio.findById(idtabla01)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idtabla01));

        y.setCodsustento(x.getCodsustento());
        y.setNomsustento(x.getNomsustento());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Tabla01 updateTabla01 = tabla01Servicio.save(y);
        return ResponseEntity.ok(updateTabla01);
    }

    @GetMapping("/{idtabla01}")
    public ResponseEntity<Tabla01> getByIdNovedad(@PathVariable Long idtabla01) {
        Tabla01 x = tabla01Servicio.findById(idtabla01)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Cuenta con Id: " + idtabla01)));
        return ResponseEntity.ok(x);
    }

    @DeleteMapping("/{idtabla01}")
    private ResponseEntity<Boolean> deleteTabla01(@PathVariable("idtabla01") Long idtabla01) {
        tabla01Servicio.deleteById(idtabla01);
        return ResponseEntity.ok(!(tabla01Servicio.findById(idtabla01) != null));
    }
}
