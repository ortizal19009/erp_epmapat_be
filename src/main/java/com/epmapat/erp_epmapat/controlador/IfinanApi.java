package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Ifinan;
import com.epmapat.erp_epmapat.servicio.IfinanServicio;
@RestController
@RequestMapping("/ifinan")
public class IfinanApi {

    @Autowired
    IfinanServicio ifinanServicio;
    
    @GetMapping
    public List<Ifinan> getIfinan(@RequestParam(required = false) String codifinan,
            @RequestParam(required = false) String nomifinan) {
        if (codifinan != null) {
            return ifinanServicio.findByCodifinan(codifinan);
        }
        if (nomifinan != null) {
            return ifinanServicio.findByNomifinan(nomifinan.toLowerCase());
        }
        return ifinanServicio.findAll(Sort.by(Sort.Direction.ASC, "codifinan"));
    }

    @PostMapping
    public Ifinan updateOrSave(@RequestBody Ifinan x) {
        return ifinanServicio.save(x);
    }

    @GetMapping("/{idifinan}")
    public ResponseEntity<Ifinan> getByIdNovedad(@PathVariable Long idifinan) {
        Ifinan x = ifinanServicio.findById(idifinan)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Institución con Id: " + idifinan)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idifinan}")
    public ResponseEntity<Ifinan> update(@PathVariable Long idifinan, @RequestBody Ifinan x) {
        Ifinan y = ifinanServicio.findById(idifinan)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Institución con Id: " + idifinan)));
        // y.setCodifinan(x.getCodifinan());
        // y.setNomifinan(x.getNomifinan());
        // y.setFoto(x.getFoto());

        Ifinan actualizar = ifinanServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    // @DeleteMapping("/{idifinan}")
    // private ResponseEntity<Boolean> deleteIfinan(@PathVariable("idifinan") Long
    // idifinan) {
    // ifinanServicio.deleteById(idifinan);
    // return ResponseEntity.ok(!(ifinanServicio.findById(idifinan) != null));
    // }

}
