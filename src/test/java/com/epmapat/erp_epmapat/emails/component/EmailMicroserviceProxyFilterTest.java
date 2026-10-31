package com.epmapat.erp_epmapat.emails.component;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
class EmailMicroserviceProxyFilterTest {
    @Test void erpAdministrationUsesTheMicroserviceAndForwardsAdministrativeHeaders()throws Exception{
        var filter=new EmailMicroserviceProxyFilter();
        var access=org.mockito.Mockito.mock(EmailAdminAccessService.class);
        org.mockito.Mockito.when(access.require(org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(1L);
        ReflectionTestUtils.setField(filter,"access",access);
        ReflectionTestUtils.setField(filter,"operationsKey","test-key");ReflectionTestUtils.setField(filter,"emailUrl","http://emails.test");ReflectionTestUtils.setField(filter,"sriUrl","http://sri.test");
        var server=MockRestServiceServer.bindTo((RestTemplate)ReflectionTestUtils.getField(filter,"client")).build();
        server.expect(requestTo("http://emails.test/api/v1/email-accounts/1/test-connection")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"success\":true}",MediaType.APPLICATION_JSON));
        var req=new MockHttpServletRequest("POST","/api/v1/email-accounts/1/test-connection");req.setServletPath(req.getRequestURI());req.setContent("{}".getBytes());req.setContentType("application/json");
        var response=new MockHttpServletResponse();filter.doFilter(req,response,new MockFilterChain());
        assertEquals(200,response.getStatus());assertTrue(response.getContentAsString().contains("true"));server.verify();
    }
    @Test void monitorUsesSriAndPreservesAuthorizationFailures()throws Exception{
        var filter=new EmailMicroserviceProxyFilter();
        var access=org.mockito.Mockito.mock(EmailAdminAccessService.class);
        org.mockito.Mockito.when(access.require(org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(1L);
        ReflectionTestUtils.setField(filter,"access",access);
        ReflectionTestUtils.setField(filter,"operationsKey","test-key");ReflectionTestUtils.setField(filter,"emailUrl","http://emails.test");ReflectionTestUtils.setField(filter,"sriUrl","http://sri.test");
        var server=MockRestServiceServer.bindTo((RestTemplate)ReflectionTestUtils.getField(filter,"client")).build();
        server.expect(requestTo("http://sri.test/api/v1/facturas/monitor/1/consultar")).andExpect(header("X-Operations-Key","test-key"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN).body("{\"error\":\"denied\"}"));
        var req=new MockHttpServletRequest("POST","/api/v1/sri-control/1/consultar");req.setServletPath(req.getRequestURI());req.addHeader("X-Operations-Key","test-key");
        var response=new MockHttpServletResponse();filter.doFilter(req,response,new MockFilterChain());
        assertEquals(403,response.getStatus());server.verify();
    }
}
