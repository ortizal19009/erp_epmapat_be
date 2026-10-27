package com.epmapat.erp_epmapat.seguridad;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class JwtAuditIdentityTest {
 @Test void identifiesValidSessionOnLegacyRoute() throws Exception {
  JwtService service=mock(JwtService.class);
  when(service.validateWebToken("valid")).thenReturn(Map.of("sub","7","platform","WEB"));
  MockHttpServletRequest request=new MockHttpServletRequest("GET","/lecturas/fecEmision");
  request.addHeader("Authorization","Bearer valid");
  new JwtAuthenticationFilter(service).doFilter(request,new MockHttpServletResponse(),(req,res)->assertEquals(7L,req.getAttribute("jwtUserId")));
 }
 @Test void legacyTokenStillPassesWithoutClaimingAnIdentity() throws Exception {
  JwtService service=mock(JwtService.class);
  when(service.validateWebToken("legacy")).thenThrow(new IllegalArgumentException());
  MockHttpServletRequest request=new MockHttpServletRequest("GET","/tmpinteresxfac/getByIdfactura");
  request.addHeader("Authorization","Bearer legacy");
  MockHttpServletResponse response=new MockHttpServletResponse();
  new JwtAuthenticationFilter(service).doFilter(request,response,(req,res)->{assertNull(req.getAttribute("jwtUserId"));res.getWriter().write("ok");});
  assertEquals("ok",response.getContentAsString());
 }
 @Test void protectedRouteStillRejectsInvalidToken() throws Exception {
  JwtService service=mock(JwtService.class);
  when(service.validateWebToken("invalid")).thenThrow(new IllegalArgumentException());
  MockHttpServletRequest request=new MockHttpServletRequest("GET","/request-audit");
  request.addHeader("Authorization","Bearer invalid");
  MockHttpServletResponse response=new MockHttpServletResponse();
  new JwtAuthenticationFilter(service).doFilter(request,response,(req,res)->fail("Must not continue"));
  assertEquals(401,response.getStatus());
 }
 @Test void downstreamErrorsAreNotReportedAsInvalidSessions() {
  JwtService service=mock(JwtService.class);
  when(service.validateWebToken("valid")).thenReturn(Map.of("sub","7","platform","WEB"));
  MockHttpServletRequest request=new MockHttpServletRequest("GET","/request-audit");
  request.addHeader("Authorization","Bearer valid");
  assertThrows(IllegalArgumentException.class,()->new JwtAuthenticationFilter(service).doFilter(request,new MockHttpServletResponse(),(req,res)->{throw new IllegalArgumentException("business error");}));
 }
}
