package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;
import com.epmapat.erp_epmapat.servicio.contabilidad.TramipresuServicio;

@RestController
@RequestMapping("/tramipresu")
@CrossOrigin("*")

public class TramipresuApi {

   @Autowired
   private TramipresuServicio tramiServicio;

   @GetMapping
   public List<Tramipresu> getAllLista(@Param(value = "desdeNum") Long desdeNum,
         @Param(value = "hastaNum") Long hastaNum,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
      if (desdeNum != null) {
         return tramiServicio.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
      } else
         // return certiServicio.findAll();
         return null;
   }

   @GetMapping("/max")
   public ResponseEntity<Tramipresu> getMax() {
      return ResponseEntity.ok(tramiServicio.findFirstByOrderByNumeroDesc());
   }

   //Validar número
   @GetMapping("/numero")
   public boolean valNumero( @Param(value = "numero") Long numero ) {
      return tramiServicio.valNumero( numero );
   }

   @PostMapping
   public ResponseEntity<Tramipresu> saveTramipresu(@RequestBody Tramipresu x) {
      return ResponseEntity.ok(tramiServicio.save(x));
   }

   @GetMapping("/idtrami")
   public ResponseEntity<Optional<Tramipresu>> findById(@RequestParam("idtrami") Long idtrami) {
      return ResponseEntity.ok(tramiServicio.findById(idtrami));
   }

}
