package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla10;
import com.epmapat.erp_epmapat.servicio.contabilidad.Tabla10Servicio;

@RestController
@RequestMapping("/tabla10")
@CrossOrigin("*")

public class Tabla10Api {
    @Autowired
    Tabla10Servicio tabla10Servicio;

    @PostMapping
    public Tabla10 updateOrSave(@RequestBody Tabla10 x) {
        return tabla10Servicio.save(x);
    }

    @GetMapping
    public List<Tabla10> getAllLista(@Param(value = "codretair") String codretair) {
        if (codretair != null) {
            return tabla10Servicio.findByCodretair(codretair);
        } else
            return tabla10Servicio.findAll();
    }

    @PutMapping("/{idtabla10}")
    public ResponseEntity<Tabla10> updateTabla10(@PathVariable Long idtabla10, @RequestBody Tabla10 x) {
        Tabla10 y = tabla10Servicio.findById(idtabla10)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idtabla10));

        y.setCodretair(x.getCodretair());
        y.setConceptoretair(x.getConceptoretair());
        y.setPorcretair(x.getPorcretair());
        y.setCodcue(x.getCodcue());

        Tabla10 updateTabla10 = tabla10Servicio.save(y);
        return ResponseEntity.ok(updateTabla10);
    }

    @GetMapping("/{idtabla10}")
    public ResponseEntity<Tabla10> getByIdNovedad(@PathVariable Long idtabla10) {
        Tabla10 x = tabla10Servicio.findById(idtabla10)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Cuenta con Id: " + idtabla10)));
        return ResponseEntity.ok(x);
    }

    @DeleteMapping("/{idtabla10}")
    private ResponseEntity<Boolean> deleteTabla10(@PathVariable("idtabla10") Long idtabla10) {
        tabla10Servicio.deleteById(idtabla10);
        return ResponseEntity.ok(!(tabla10Servicio.findById(idtabla10) != null));
    }
}
