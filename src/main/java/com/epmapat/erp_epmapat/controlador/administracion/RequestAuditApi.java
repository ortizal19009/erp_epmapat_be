package com.epmapat.erp_epmapat.controlador.administracion;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.servlet.http.HttpServletRequest;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.epmapat.erp_epmapat.modelo.administracion.RequestAudit;
import com.epmapat.erp_epmapat.repositorio.administracion.RequestAuditR;
import com.epmapat.erp_epmapat.seguridad.WebAccessGuard;
@RestController @RequestMapping("/request-audit")
public class RequestAuditApi {
 private final RequestAuditR repository; private final WebAccessGuard guard;
 public RequestAuditApi(RequestAuditR repository, WebAccessGuard guard) { this.repository=repository; this.guard=guard; }
 @GetMapping public Page<RequestAudit> list(HttpServletRequest request,
  @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="25") int size,
  @RequestParam(required=false) String metodo, @RequestParam(required=false) String ruta,
  @RequestParam(required=false) Integer estado,
  @RequestParam(required=false) Long usuarioId, @RequestParam(required=false) String ip,
  @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
  @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta) {
  guard.requireAdmin(request);
  if (desde != null && hasta != null && desde.isAfter(hasta)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Rango de fechas invalido");
  return repository.findAll((root, query, cb) -> {
   List<Predicate> filters = new ArrayList<>();
   if (metodo != null && !metodo.isBlank()) filters.add(cb.equal(root.get("metodo"),metodo.toUpperCase(java.util.Locale.ROOT)));
   if (ruta != null && !ruta.isBlank()) filters.add(cb.greaterThan(cb.locate(root.get("ruta"),ruta),0));
   if (estado != null) filters.add(cb.equal(root.get("estado"),estado));
   if (usuarioId != null) filters.add(cb.equal(root.get("usuarioId"),usuarioId));
   if (ip != null && !ip.isBlank()) filters.add(cb.equal(root.get("ip"),ip.trim()));
   ZoneId zone=ZoneId.of("America/Guayaquil");
   if (desde != null) filters.add(cb.greaterThanOrEqualTo(root.get("fecha"),desde.atStartOfDay(zone).toInstant()));
   if (hasta != null) filters.add(cb.lessThan(root.get("fecha"),hasta.plusDays(1).atStartOfDay(zone).toInstant()));
   return cb.and(filters.toArray(new Predicate[0]));
  },PageRequest.of(Math.max(0,page),Math.max(1,Math.min(size,100)),Sort.by(Sort.Direction.DESC,"id")));
 }
}
