package com.epmapat.erp_epmapat.controlador;

import com.epmapat.erp_epmapat.modelo.Usrxrutas;
import com.epmapat.erp_epmapat.interfaces.UsrxrutasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usrxrutas")
@RequiredArgsConstructor
public class UsrxrutasApi {

    private final UsrxrutasService usrxrutasService;

    @PostMapping
    public ResponseEntity<Usrxrutas> crear(@RequestBody Usrxrutas body) {
        return ResponseEntity.ok(usrxrutasService.crear(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usrxrutas> actualizar(@PathVariable Long id, @RequestBody Usrxrutas body) {
        return ResponseEntity.ok(usrxrutasService.actualizar(id, body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usrxrutas> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usrxrutasService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Usrxrutas>> listar() {
        return ResponseEntity.ok(usrxrutasService.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usrxrutasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idusuario}")
    public ResponseEntity<List<Usrxrutas>> listarPorUsuario(@PathVariable Long idusuario) {
        return ResponseEntity.ok(usrxrutasService.listarPorUsuario(idusuario));
    }

    @GetMapping("/emision/{idemision}")
    public ResponseEntity<List<Usrxrutas>> listarPorEmision(@PathVariable Long idemision) {
        return ResponseEntity.ok(usrxrutasService.listarPorEmision(idemision));
    }

    @GetMapping("/usuario/{idusuario}/emision/{idemision}")
    public ResponseEntity<Usrxrutas> obtenerPorUsuarioYEmision(
            @PathVariable Long idusuario,
            @PathVariable Long idemision) {

        Usrxrutas resultado = usrxrutasService
                .findByUsuarioAndEmision(idusuario, idemision)
                .orElseGet(Usrxrutas::new);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/emision/{idemision}/rutas-ocupadas")
    public ResponseEntity<List<Long>> rutasOcupadas(@PathVariable Long idemision) {
        return ResponseEntity.ok(usrxrutasService.rutasOcupadasEnEmision(idemision));
    }

}
