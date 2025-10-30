package com.epmapat.erp_epmapat.controlador;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRI;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRia;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRin;
import com.epmapat.erp_epmapat.interfaces.FacEliminadas;
import com.epmapat.erp_epmapat.interfaces.IemiIndividual;
import com.epmapat.erp_epmapat.interfaces.R_refacturacion_int;
import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.EmisionIndividual;
import com.epmapat.erp_epmapat.servicio.EmisionIndividualServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/emisionindividual")
@CrossOrigin("*")
public class EmisionIndividualApi {
    @Autowired
    private EmisionIndividualServicio sei;

    @PostMapping
    public ResponseEntity<EmisionIndividual> postMethodName(@RequestBody EmisionIndividual emiIndi) {
        return ResponseEntity.ok(sei.save(emiIndi));
    }

    @GetMapping("/idemision")
    public ResponseEntity<List<EmisionIndividual>> getByIdEmision(@RequestParam("idemision") Long idemision) {
        return ResponseEntity.ok(sei.findByIdEmision(idemision));
    }

    @GetMapping("/nuevas")
    public ResponseEntity<List<IemiIndividual>> getLecturasNuevas(@RequestParam("idemision") Long idemision) {
        return ResponseEntity.ok(sei.findLecturasNuevas(idemision));
    }

    @GetMapping("/anteriores")
    public ResponseEntity<List<IemiIndividual>> getLecturasAnteriores(@RequestParam("idemision") Long idemision) {
        return ResponseEntity.ok(sei.findLecturasAnteriores(idemision));
    }

    @GetMapping("/reportes/emisiones")
    public ResponseEntity<List<EmisionIndividualRI>> findLecReport(@RequestParam("idemision") Integer idemision) {
        return ResponseEntity.ok(sei.getLecReport(idemision));
    }

    @GetMapping("/reportes/anteriores")
    public ResponseEntity<List<EmisionIndividualRia>> emisionIndividualAnterior(
            @RequestParam Integer idemision) {
        return ResponseEntity.ok(sei.emisionIndividualAnterior(idemision));
    }

    @GetMapping("/reportes/nuevas")
    public ResponseEntity<List<EmisionIndividualRin>> emisionIndividualNueva(
            @RequestParam("idemision") Integer idemision) {
        return ResponseEntity.ok(sei.emisionIndividualNueva(idemision));
    }

    @GetMapping("/reportes/xemision")
    public ResponseEntity<List<R_refacturacion_int>> getRefacturacionxEmision(@RequestParam Long idemision) {
        return ResponseEntity.ok(sei.getRefacturacionxEmision(idemision));
    }

    @GetMapping("/reportes/xfecha")
    public ResponseEntity<List<R_refacturacion_int>> getRefacturacionxFecha(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
        return ResponseEntity.ok(sei.getRefacturacionxFecha(d, h));
    }

    @GetMapping("/reporte/refacturacion/rubros/anterior")
    public ResponseEntity<List<RubroxfacI>> getRefacturacionxEmisionRubrosAnteriores(Long idemision) {
        return ResponseEntity.ok(sei.getRefacturacionxEmisionRubrosAnteriores(idemision));
    }

    @GetMapping("/reporte/refacturacion/rubros/nuevo")
    public ResponseEntity<List<RubroxfacI>> getRefacturacionxEmisionRubrosNuevos(Long idemision) {
        return ResponseEntity.ok(sei.getRefacturacionxEmisionRubrosNuevos(idemision));
    }

    @GetMapping("/reporte/refacturacion/fecha/anterior")
    public List<RubroxfacI> getRefacturacionxFechaRubrosAnteriores(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
        return sei.getRefacturacionxFechaRubrosAnteriores(d, h);
    }

    @GetMapping("/reporte/refacturacion/fecha/nuevo")
    public List<RubroxfacI> getRefacturacionxFechaRubrosNuevos(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
        return sei.getRefacturacionxFechaRubrosNuevos(d, h);
    }

    @GetMapping("/reporte/eliminadas/fechaeliminacion")
    public ResponseEntity<List<FacEliminadas>> getFacElimByFechaElimina(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h) {
        return ResponseEntity.ok(sei.getFacElimByFechaElimina(d, h));
    }

    @GetMapping("/reporte/eliminadas/emision")
    public ResponseEntity<List<FacEliminadas>> getFacElimByEmision(@RequestParam Long idemision) {
        return ResponseEntity.ok(sei.getFacElimByEmision(idemision));
    }
}
