package com.epmapat.erp_epmapat.controlador;
import java.util.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.RutaGuardarRequest;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.servicio.RutaGestionServicio;
import com.epmapat.erp_epmapat.repositorio.*;
@RestController
@RequestMapping("/rutas/gestion")
public class RutaGestionApi {
    private final RutaGestionServicio servicio;private final RutaCuentasConsulta cuentas;private final RutaHistorialR historial;
    public RutaGestionApi(RutaGestionServicio servicio,RutaCuentasConsulta cuentas,RutaHistorialR historial){this.servicio=servicio;this.cuentas=cuentas;this.historial=historial;}
    private Long actor(HttpServletRequest request,Long respaldo){Object id=request.getAttribute("jwtUserId");return id instanceof Number n?n.longValue():respaldo;}
    @GetMapping("/siguiente-codigo") public Map<String,Object> siguienteCodigo(){return Map.of("codigo",servicio.siguienteCodigo(),"orden",servicio.siguienteOrden());}
    @PostMapping public Rutas crear(@Valid @RequestBody RutaGuardarRequest datos,HttpServletRequest request){return servicio.guardar(null,datos,actor(request,datos.usuario()));}
    @PutMapping("/{id}") public Rutas editar(@PathVariable Long id,@Valid @RequestBody RutaGuardarRequest datos,HttpServletRequest request){return servicio.guardar(id,datos,actor(request,datos.usuario()));}
    @GetMapping("/cuentas") public Page<Map<String,Object>> cuentas(@RequestParam(required=false) Long origen,@RequestParam(required=false) Long destino,
      @RequestParam(defaultValue="") String filtro,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return cuentas.buscar(origen,destino,filtro.trim(),Math.max(0,page),Math.max(10,Math.min(100,size)));}
    @GetMapping("/historial") public Page<AuditoriaGenerica> historial(@RequestParam(required=false) Long ruta,@RequestParam(required=false) Long cuenta,
      @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return historial.buscar(ruta,cuenta,PageRequest.of(Math.max(0,page),Math.max(10,Math.min(100,size))));}
    @ExceptionHandler(ResponseStatusException.class) public ResponseEntity<Map<String,String>> error(ResponseStatusException e){return ResponseEntity.status(e.getStatus()).body(Map.of("message",e.getReason()==null?"No se pudo guardar":e.getReason()));}
}
