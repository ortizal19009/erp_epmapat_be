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
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla15;
import com.epmapat.erp_epmapat.servicio.contabilidad.Tabla15Servicio;

@RestController
@RequestMapping("/tabla15")
@CrossOrigin("*")

public class Tabla15Api {
    @Autowired
    Tabla15Servicio tabla15Servicio;

    @PostMapping
    public Tabla15 updateOrSave(@RequestBody Tabla15 x) {
        return tabla15Servicio.save(x);
    }

    @GetMapping
    public List<Tabla15> findAll() {
        return tabla15Servicio.findAll();
    }

    @PutMapping("/{idtabla15}")
    public ResponseEntity<Tabla15> updateTabla15(@PathVariable Long idtabla15, @RequestBody Tabla15 x) {
        Tabla15 y = tabla15Servicio.findById(idtabla15)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idtabla15));

        y.setCodtabla15(x.getCodtabla15());
        y.setNomtabla15(x.getNomtabla15());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Tabla15 updateTabla15 = tabla15Servicio.save(y);
        return ResponseEntity.ok(updateTabla15);
    }

    @GetMapping("/{idtabla15}")
    public ResponseEntity<Tabla15> getByIdNovedad(@PathVariable Long idtabla15) {
        Tabla15 x = tabla15Servicio.findById(idtabla15)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Cuenta con Id: " + idtabla15)));
        return ResponseEntity.ok(x);
    }

    @DeleteMapping("/{idtabla15}")
    private ResponseEntity<Boolean> deleteTabla15(@PathVariable("idtabla15") Long idtabla15) {
        tabla15Servicio.deleteById(idtabla15);
        return ResponseEntity.ok(!(tabla15Servicio.findById(idtabla15) != null));
    }
}
