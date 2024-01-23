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
import com.epmapat.erp_epmapat.modelo.Tipopago;
import com.epmapat.erp_epmapat.servicio.TipopagoServicio;

@RestController
@RequestMapping("/tipopago")
@CrossOrigin("*")

public class TipopagoApi {
    
    @Autowired
    TipopagoServicio TipopagoServicio;

    @PostMapping
    public Tipopago updateOrSave(@RequestBody Tipopago x) {
        return TipopagoServicio.save(x);
    }

    @GetMapping
    public List<Tipopago> getAll() {
        return TipopagoServicio.findAll();
    }

    @GetMapping("/{idtipopago}")
    public ResponseEntity<Tipopago> getByIdNovedad(@PathVariable Long idtipopago) {
        Tipopago x = TipopagoServicio.findById(idtipopago)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Tipo de Pago Id: " + idtipopago)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idtipopago}")
    public ResponseEntity<Tipopago> update(@PathVariable Long idtipopago, @RequestBody Tipopago x) {
        Tipopago y = TipopagoServicio.findById(idtipopago)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Tipo de Pago Id: " + idtipopago)));
        y.setDescripcion(x.getDescripcion());
        y.setUsucrea(x.getUsucrea());

        Tipopago actualizar = TipopagoServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{idtipopago}")
    private ResponseEntity<Boolean> delete(@PathVariable("idtipopago") Long idtipopago) {
        TipopagoServicio.deleteById(idtipopago);
        return ResponseEntity.ok(!(TipopagoServicio.findById(idtipopago) != null));
    }

}
