package com.epmapat.erp_epmapat.controlador.administracion;

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
import com.epmapat.erp_epmapat.modelo.administracion.Documentos;
import com.epmapat.erp_epmapat.servicio.administracion.DocumentoServicio;

@RestController
@RequestMapping("/documentos")
@CrossOrigin("*")

public class DocumentosApi {

    @Autowired
    DocumentoServicio docuServicio;

    @GetMapping
    public List<Documentos> getAllLista(@Param(value = "nomdoc") String nomdoc) {
        if (nomdoc != null) {
            return docuServicio.findByNomdoc(nomdoc.toLowerCase());
        } else {
            return docuServicio.findAll();
        }
    }

    @GetMapping("/{iddocumento}")
    public ResponseEntity<Documentos> getByIddocumento(@PathVariable Long iddocumento) {
        Documentos documento = docuServicio.findById(iddocumento)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe el Documento con Id: " + iddocumento)));
        return ResponseEntity.ok(documento);
    }

    @PostMapping
    public ResponseEntity<Documentos> save(@RequestBody Documentos x) {
        return ResponseEntity.ok(docuServicio.save(x));
    }

    @PutMapping("/{iddocumento}")
    public ResponseEntity<Documentos> update(@PathVariable Long iddocumento, @RequestBody Documentos x) {
        Documentos y = docuServicio.findById(iddocumento)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Documento con Id: " + iddocumento)));
        y.setNomdoc(x.getNomdoc());
        y.setTipdoc(x.getTipdoc());
        y.setIdtabla4(x.getIdtabla4());
        y.setTipocomprobante(x.getTipocomprobante());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Documentos actualizar = docuServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{iddocumento}")
    private ResponseEntity<Boolean> deleteDocumento(@PathVariable("iddocumento") Long iddocumento) {
        docuServicio.deleteById(iddocumento);
        return ResponseEntity.ok(!(docuServicio.findById(iddocumento) != null));
    }

}
