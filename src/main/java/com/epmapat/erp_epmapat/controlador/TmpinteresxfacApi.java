package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.epmapat.erp_epmapat.servicio.TmpinteresxfacService;

@RestController
@RequestMapping("/tmpinteresxfac")
@CrossOrigin("*")
public class TmpinteresxfacApi {
    @Autowired
    private TmpinteresxfacService tmpinteresxfacService;

    @GetMapping("/calcularInteres")
    private ResponseEntity<Map<String, Object>> updateFacturas() {
        return ResponseEntity.ok(tmpinteresxfacService.updateTmpInteresxfac());
    }

    @GetMapping("/getByIdfactura")
    public ResponseEntity<BigDecimal> getByIdFactura(@RequestParam Long idfactura) {
        try {
            BigDecimal interes = tmpinteresxfacService.findByIdFactura(idfactura);

            // Si el servicio devuelve null o 0 → retorna 0
            if (interes == null || interes.compareTo(BigDecimal.ZERO) == 0) {
                interes = BigDecimal.ZERO;
            }

            return ResponseEntity.ok(interes);

        } catch (ResponseStatusException ex) {
            // Si el servicio lanza 404 o similar → retorna 0 igualmente
            return ResponseEntity.ok(BigDecimal.ZERO);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok(BigDecimal.ZERO);
        }
    }

}
