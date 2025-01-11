package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.Unicostos;
import com.epmapat.erp_epmapat.servicio.contabilidad.UnicostosServicio;

@RestController
@RequestMapping("/unicostos")
@CrossOrigin(origins = "*")

public class UnicostosApi {

   @Autowired
   UnicostosServicio unicostServicio;

   // Todas ordendas por mes (sin necesidad de select en el repositorio)
   @GetMapping
   public List<Unicostos> getAllIfinans() {
      Sort sort = Sort.by(Sort.Direction.ASC, "mes");
      return unicostServicio.findAll(sort);
   }

}
