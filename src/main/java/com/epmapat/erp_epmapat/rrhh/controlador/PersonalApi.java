package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.Personal;
import com.epmapat.erp_epmapat.rrhh.servicio.PersonalServicio;

import lombok.RequiredArgsConstructor;

@RestController("rrhhLegacyPersonalApi")
@RequestMapping("/api/personal")
@RequiredArgsConstructor
public class PersonalApi {
    private final PersonalServicio personalServicio;

    @GetMapping
    public ResponseEntity<List<Personal>> getAll() {
        return ResponseEntity.ok(personalServicio.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Personal>> search(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(personalServicio.search(q, estado, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personal> getById(@PathVariable Long id) {
        return ResponseEntity.ok(personalServicio.findById(id));
    }

    @PostMapping
    public ResponseEntity<Personal> save(@RequestBody Personal p) {
        return ResponseEntity.ok(personalServicio.save(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Personal> update(@PathVariable Long id, @RequestBody Personal p) {
        return ResponseEntity.ok(personalServicio.update(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivar(@PathVariable Long id,
            @RequestParam(required = false) Long usumodi) {
        personalServicio.inactivar(id, usumodi);
        return ResponseEntity.noContent().build();
    }
}
