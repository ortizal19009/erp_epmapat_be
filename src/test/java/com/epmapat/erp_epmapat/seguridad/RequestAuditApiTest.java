package com.epmapat.erp_epmapat.seguridad;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.controlador.administracion.RequestAuditApi;
import com.epmapat.erp_epmapat.repositorio.administracion.RequestAuditR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class RequestAuditApiTest {
 @Test void rejectsAnonymousAndNonAdminWithoutReadingAudit() {
  RequestAuditR repository=mock(RequestAuditR.class);
  RequestAuditApi api=new RequestAuditApi(repository,new WebAccessGuard());
  MockHttpServletRequest request=new MockHttpServletRequest();
  ResponseStatusException anonymous=assertThrows(ResponseStatusException.class,()->api.list(request,0,25,null,null,null,null,null,null,null));
  assertEquals(401,anonymous.getRawStatusCode());
  request.setAttribute("jwtUserId",2L);
  ResponseStatusException forbidden=assertThrows(ResponseStatusException.class,()->api.list(request,0,25,null,null,null,null,null,null,null));
  assertEquals(403,forbidden.getRawStatusCode()); verifyNoInteractions(repository);
 }
 @Test void rejectsReversedDates() {
  RequestAuditR repository=mock(RequestAuditR.class);
  MockHttpServletRequest request=new MockHttpServletRequest(); request.setAttribute("jwtUserId",1L);
  ResponseStatusException error=assertThrows(ResponseStatusException.class,()->new RequestAuditApi(repository,new WebAccessGuard()).list(request,0,25,null,null,null,null,null,java.time.LocalDate.of(2026,10,27),java.time.LocalDate.of(2026,10,26)));
  assertEquals(400,error.getRawStatusCode()); verifyNoInteractions(repository);
 }
}
