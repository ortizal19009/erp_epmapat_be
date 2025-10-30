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
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla17;
import com.epmapat.erp_epmapat.servicio.contabilidad.Tabla17Servicio;

@RestController
@RequestMapping("/tabla17")
@CrossOrigin("*")

public class Tabla17Api {
    @Autowired
    Tabla17Servicio tabla17Servicio;

    @GetMapping
    public List<Tabla17> findAll() {
        return tabla17Servicio.findAll();
    }

    @GetMapping("/retenciones")
    public ResponseEntity<List<Tabla17>> obtenerRegistros() {
        List<Tabla17> registros = tabla17Servicio.obtenerRegistrosConPorcivaMayorACero();
        return ResponseEntity.ok(registros);
    }

    @PostMapping
    public Tabla17 updateOrSave(@RequestBody Tabla17 x) {
        return tabla17Servicio.save(x);
    }

    @PutMapping("/{idtabla17}")
    public ResponseEntity<Tabla17> updateTabla17(@PathVariable Long idtabla17, @RequestBody Tabla17 x) {
        Tabla17 y = tabla17Servicio.findById(idtabla17)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idtabla17));

        y.setPorcentaje(x.getPorcentaje());
        y.setCodigo(x.getCodigo());
        y.setPorciva(x.getPorciva());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Tabla17 updateTabla17 = tabla17Servicio.save(y);
        return ResponseEntity.ok(updateTabla17);
    }

    @GetMapping("/{idtabla17}")
    public ResponseEntity<Tabla17> getByIdNovedad(@PathVariable Long idtabla17) {
        Tabla17 x = tabla17Servicio.findById(idtabla17)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Cuenta con Id: " + idtabla17)));
        return ResponseEntity.ok(x);
    }

    @DeleteMapping("/{idtabla17}")
    private ResponseEntity<Boolean> deleteTabla17(@PathVariable("idtabla17") Long idtabla17) {
        tabla17Servicio.deleteById(idtabla17);
        return ResponseEntity.ok(!(tabla17Servicio.findById(idtabla17) != null));
    }
}
