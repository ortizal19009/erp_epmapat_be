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
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla5;
import com.epmapat.erp_epmapat.servicio.contabilidad.Tabla5Servicio;

@RestController
@RequestMapping("/tabla5")
@CrossOrigin("*")

public class Tabla5Api {
    @Autowired
    Tabla5Servicio tabla5Servicio;

    @PostMapping
    public Tabla5 updateOrSave(@RequestBody Tabla5 x) {
        return tabla5Servicio.save(x);
    }

    @GetMapping
    public List<Tabla5> findAll() {
        return tabla5Servicio.findAll();
    }

    @PutMapping("/{idtabla5}")
    public ResponseEntity<Tabla5> updateTabla5(@PathVariable Long idtabla5, @RequestBody Tabla5 x) {
        Tabla5 y = tabla5Servicio.findById(idtabla5)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idtabla5));

        y.setCodporcentaje(x.getCodporcentaje());
        y.setPorcentaje(x.getPorcentaje());
        y.setTipoiva(x.getTipoiva());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Tabla5 updateTabla5 = tabla5Servicio.save(y);
        return ResponseEntity.ok(updateTabla5);
    }

    @GetMapping("/{idtabla5}")
    public ResponseEntity<Tabla5> getByIdNovedad(@PathVariable Long idtabla5) {
        Tabla5 x = tabla5Servicio.findById(idtabla5)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Cuenta con Id: " + idtabla5)));
        return ResponseEntity.ok(x);
    }

    @DeleteMapping("/{idtabla5}")
    private ResponseEntity<Boolean> deleteTabla5(@PathVariable("idtabla5") Long idtabla5) {
        tabla5Servicio.deleteById(idtabla5);
        return ResponseEntity.ok(!(tabla5Servicio.findById(idtabla5) != null));
    }
}
