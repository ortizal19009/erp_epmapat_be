package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Usrxrutas;
import com.epmapat.erp_epmapat.servicio.UsrxrutaService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/usrxrutas")
public class UsrxrutasApi {

    private final UsrxrutaService usrxrutaService;

    @GetMapping
    public List<Usrxrutas> findAll() {
        return usrxrutaService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Usrxrutas> findById(@PathVariable Long id) {
        return usrxrutaService.findById(id);
    }

    @PostMapping
    public Usrxrutas save(@RequestBody Usrxrutas usrxrutas) {
        return usrxrutaService.save(usrxrutas);
    }

    @PutMapping("/{id}")
    public Usrxrutas update(@PathVariable Long id, @RequestBody Usrxrutas usrxrutas) {
        usrxrutas.setIdusrxruta(id);

        return usrxrutaService.save(usrxrutas);
    }

    /*
     * @DeleteMapping("/{id}")
     * public void delete(@PathVariable Long id) {
     * usrxrutaService.delete(id);
     * }
     */
    @GetMapping("/usuario/{idusuario}/emision/{idemision}")
    public List<Usrxrutas> findByUsuarioAndEmision(@PathVariable Long idusuario, @PathVariable Long idemision) {
        return usrxrutaService.findByUsuarioAndEmision(idusuario, idemision);
    }
}
