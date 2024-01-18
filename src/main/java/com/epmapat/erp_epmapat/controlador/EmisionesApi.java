package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.servicio.EmisionServicio;

@RestController
@RequestMapping("/emisiones")
@CrossOrigin("*")

public class EmisionesApi {

    @Autowired
    private EmisionServicio emiServicio;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)

    public List<Emisiones> getAll(@Param(value = "desde") String desde,
            @Param(value = "hasta") String hasta) {
        if (desde != null && hasta != null)
            return emiServicio.findByDesdeHasta(desde, hasta);
        else
            return null;
    }

    @GetMapping("/ultimo")
    public Emisiones ultimo() {
        return emiServicio.findFirstByOrderByEmisionDesc();
    }

    @GetMapping("/{idemision}")
    public ResponseEntity<Emisiones> getByIdEmision(@PathVariable Long idemision) {
        Emisiones x = emiServicio.findById(idemision)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Emisión Id: " + idemision)));
        return ResponseEntity.ok(x);
    }

    @PostMapping
    public ResponseEntity<Emisiones> save(@RequestBody Emisiones x) {
    return ResponseEntity.ok(emiServicio.save(x));
    }

    @PutMapping("/{idemision}")
    public ResponseEntity<Emisiones> update(@PathVariable Long idemision, @RequestBody Emisiones x) {
        Emisiones y = emiServicio.findById(idemision)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe la Emisión Id: " + idemision)));
        y.setEmision(x.getEmision());
        y.setEstado(x.getEstado());
        y.setObservaciones(x.getObservaciones());
        y.setUsuariocierre(x.getUsuariocierre());
        y.setFechacierre(x.getFechacierre());
        y.setM3(x.getM3());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Emisiones actualizar = emiServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

}
