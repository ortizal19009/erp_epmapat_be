package com.epmapat.erp_epmapat.seguridad;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.mockito.ArgumentCaptor;
import com.epmapat.erp_epmapat.modelo.administracion.RequestAudit;
import com.epmapat.erp_epmapat.repositorio.administracion.RequestAuditR;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class RequestAuditFilterTest {
 @Test void recordsResponseWithoutCredentialsOrQuery() throws Exception {
  RequestAuditR repository=mock(RequestAuditR.class);
  MockHttpServletRequest request=new MockHttpServletRequest("POST","/erp/clientes");
  request.setContextPath("/erp"); request.setQueryString("password=secret");
  request.addHeader("Authorization","Bearer secret");
  MockHttpServletResponse response=new MockHttpServletResponse();
  new RequestAuditFilter(repository).doFilter(request,response,(req,res)->{
   req.setAttribute("jwtUserId",7L); ((javax.servlet.http.HttpServletResponse)res).setStatus(201);
  });
  ArgumentCaptor<RequestAudit> captor=ArgumentCaptor.forClass(RequestAudit.class);
  verify(repository).save(captor.capture()); RequestAudit audit=captor.getValue();
  assertEquals("/clientes",audit.getRuta()); assertEquals(201,audit.getEstado());
  assertEquals(7L,audit.getUsuarioId()); assertNotNull(audit.getFecha());
 }
 @Test void auditFailureDoesNotBreakRequest() throws Exception {
  RequestAuditR repository=mock(RequestAuditR.class);
  when(repository.save(any())).thenThrow(new IllegalStateException());
  MockHttpServletResponse response=new MockHttpServletResponse();
  new RequestAuditFilter(repository).doFilter(new MockHttpServletRequest("GET","/clientes"),response,(req,res)->res.getWriter().write("ok"));
  assertEquals("ok",response.getContentAsString());
 }
 @Test void excludesAuditQueries() throws Exception {
  RequestAuditR repository=mock(RequestAuditR.class);
  new RequestAuditFilter(repository).doFilter(new MockHttpServletRequest("GET","/request-audit"),new MockHttpServletResponse(),(req,res)->{});
  verifyNoInteractions(repository);
 }
 @Test void recordsUnhandledFailureAndPreservesException() {
  RequestAuditR repository=mock(RequestAuditR.class);
  assertThrows(javax.servlet.ServletException.class,()->new RequestAuditFilter(repository).doFilter(new MockHttpServletRequest("GET","/clientes"),new MockHttpServletResponse(),(req,res)->{throw new javax.servlet.ServletException("failure");}));
  ArgumentCaptor<RequestAudit> captor=ArgumentCaptor.forClass(RequestAudit.class);
  verify(repository).save(captor.capture()); assertEquals(500,captor.getValue().getEstado());
 }
}
