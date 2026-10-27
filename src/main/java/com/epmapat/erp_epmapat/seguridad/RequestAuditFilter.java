package com.epmapat.erp_epmapat.seguridad;
import java.io.IOException;
import java.time.Instant;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.epmapat.erp_epmapat.modelo.administracion.RequestAudit;
import com.epmapat.erp_epmapat.repositorio.administracion.RequestAuditR;
@Component @Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RequestAuditFilter extends OncePerRequestFilter {
 private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);
 private final RequestAuditR repository;
 public RequestAuditFilter(RequestAuditR repository) { this.repository = repository; }
 @Override protected boolean shouldNotFilter(HttpServletRequest request) {
  String path = request.getRequestURI().substring(request.getContextPath().length());
  return "OPTIONS".equals(request.getMethod()) || path.equals("/request-audit") || path.startsWith("/request-audit/") || path.startsWith("/actuator/");
 }
 @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
  Instant fecha = Instant.now(); long start = System.nanoTime(); boolean failed = false;
  try { chain.doFilter(request, response); }
  catch (IOException | ServletException | RuntimeException ex) { failed = true; throw ex; }
  finally {
   try {
    RequestAudit audit = new RequestAudit(); audit.setFecha(fecha);
    audit.setMetodo(cut(request.getMethod(),16));
    audit.setRuta(cut(request.getRequestURI().substring(request.getContextPath().length()),2048));
    audit.setEstado(failed ? 500 : response.getStatus());
    audit.setDuracionMs((System.nanoTime()-start)/1000000);
    String remoteIp = request.getRemoteAddr();
    audit.setIp(cut("0:0:0:0:0:0:0:1".equals(remoteIp) ? "::1" : remoteIp,64));
    Object user = request.getAttribute("jwtUserId");
    if (user instanceof Number) audit.setUsuarioId(((Number)user).longValue());
    repository.save(audit);
   } catch (Exception ex) { log.warn("No se pudo registrar auditoria de peticion: {}", ex.getClass().getSimpleName()); }
  }
 }
 private String cut(String value, int max) { return value == null ? null : value.substring(0,Math.min(value.length(),max)); }
}
