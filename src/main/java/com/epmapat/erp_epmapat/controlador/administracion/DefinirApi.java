package com.epmapat.erp_epmapat.controlador.administracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.servicio.administracion.DefinirServicio;

@RestController
@RequestMapping("/definir")
@CrossOrigin("*")

public class DefinirApi {

    @Autowired
    DefinirServicio defServicio;

    @GetMapping("/{iddefinir}")
    public ResponseEntity<Definir> getByIddefinir(@PathVariable Long iddefinir) {
        Definir definir = defServicio.findById(iddefinir).orElse(null);
        return definir != null ? ResponseEntity.ok(definir) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{iddefinir}")
    public ResponseEntity<Definir> update(@PathVariable Long iddefinir, @RequestBody Definir x) {
        Definir y = defServicio.findById(iddefinir)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe: " + iddefinir)));
        y.setRazonsocial(x.getRazonsocial());
        y.setNombrecomercial(x.getNombrecomercial());
        y.setRuc(x.getRuc());
        y.setDireccion(x.getDireccion());
        y.setTipoambiente(x.getTipoambiente());
        y.setIva(x.getIva());

        Definir z = defServicio.save(y);
        return ResponseEntity.ok(z);
    }

}
