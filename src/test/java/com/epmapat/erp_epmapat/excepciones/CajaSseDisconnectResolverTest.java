package com.epmapat.erp_epmapat.excepciones;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CajaSseDisconnectResolverTest {
    private final CajaSseDisconnectResolver resolver = new CajaSseDisconnectResolver();

    @Test
    void handlesCommittedDisconnectWithoutWritingAnotherResponse() {
        for (String path : new String[] { "/caja/stream", "/caja/stream/global" }) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/erp/recaudacion-cobro" + path);
            request.setContextPath("/erp");
            MockHttpServletResponse response = new MockHttpServletResponse();
            response.setCommitted(true);
            assertNotNull(resolver.resolveException(request, response, null,
                    new ServletException(new IOException("Broken pipe"))));
            assertEquals(200, response.getStatus());
            assertEquals(0, response.getContentAsByteArray().length);
        }
    }

    @Test
    void preservesUnexpectedIoAndApplicationErrors() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/recaudacion-cobro/caja/stream");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setCommitted(true);
        assertNull(resolver.resolveException(request, response, null, new IOException("Disk failure")));
        assertNull(resolver.resolveException(request, response, null, new IllegalStateException("Broken pipe")));
        assertNull(resolver.resolveException(request, response, null, new IOException()));
    }

    @Test
    void leavesOtherEndpointsAndUncommittedResponsesToNormalHandling() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/recaudacion-cobro/caja/stream");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertNull(resolver.resolveException(request, response, null, new IOException("Broken pipe")));
        request.setRequestURI("/clientes");
        response.setCommitted(true);
        assertNull(resolver.resolveException(request, response, null, new IOException("Broken pipe")));
    }
}
