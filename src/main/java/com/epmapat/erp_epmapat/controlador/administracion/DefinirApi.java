package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Definir> update(@PathVariable Long iddefinir, @RequestBody Definir x) throws Exception {
        Definir y = defServicio.findById(iddefinir)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe: " + iddefinir)));
        y.setRazonsocial(x.getRazonsocial());
        y.setNombrecomercial(x.getNombrecomercial());
        y.setRuc(x.getRuc());
        y.setDireccion(x.getDireccion());
        y.setTipoambiente(x.getTipoambiente());
        y.setIva(x.getIva());
        y.setFirma(x.getFirma());
        y.setClave_firma(x.getClave_firma());
        y.setEmail(x.getEmail());
        y.setRbu(x.getRbu());

        if (x.getClave_email() == y.getClave_email()) {
            y.setClave_email(x.getClave_email());

        } else {
            y.setClave_email(defServicio.encriptar(x.getClave_email()));

        }

        Definir z = defServicio.save(y);
        return ResponseEntity.ok(z);
    }

    @PutMapping("/subir-firma/{id}")
    public ResponseEntity<Object> subirFirma(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("clave") String clave) {
        Map<String, Object> response = new HashMap<>();
        try {

            defServicio.guardarFirma(id, archivo, clave);
            response.put("message", "Firma cargada correctamente.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/desEncriptar")
    public ResponseEntity<Object> desencriptar(@RequestParam Long id) throws Exception {
        return ResponseEntity.ok(defServicio.desEncriptar(id));
    }

    @GetMapping("/encriptar")
    public ResponseEntity<Object> encriptar(@RequestParam String clave) throws Exception {
        return ResponseEntity.ok(defServicio.encriptar(clave));
    }

}
