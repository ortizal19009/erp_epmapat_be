package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Recargosxcuenta;
import com.epmapat.erp_epmapat.servicio.RecargosxcuentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recargosxcuenta")
public class RecargosxcuentaApi {
    private final RecargosxcuentaService recargosxcuentaService;

    @GetMapping("/byEmision")
    public List<Recargosxcuenta> getRecargosxcuentaByEmision(Long emision) {
        return recargosxcuentaService.findAllByEmision(emision);
    }
    @PostMapping
    public Recargosxcuenta createRecargosxcuenta(Recargosxcuenta recargosxcuenta) {
        return recargosxcuentaService.save(recargosxcuenta);
    }
    
}
