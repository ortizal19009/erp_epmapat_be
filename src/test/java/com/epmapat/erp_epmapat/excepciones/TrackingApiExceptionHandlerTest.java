package com.epmapat.erp_epmapat.excepciones;

import java.net.SocketTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.epmapat.erp_epmapat.controlador.TrackingController;
import com.epmapat.erp_epmapat.servicio.TrackingServicio;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrackingApiExceptionHandlerTest {
    @Test
    void partialJsonDoesNotPersistOrAcknowledgePoints() throws Exception {
        var service = mock(TrackingServicio.class);
        var controller = new TrackingController();
        ReflectionTestUtils.setField(controller, "trackingServicio", service);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TrackingApiExceptionHandler()).build();
        mvc.perform(post("/tracking/points/batch").contentType("application/json")
                .content("{\"trackingSessionId\":\"session-1\",\"points\":[{"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    private HttpMessageNotReadableException unreadable(Throwable cause) {
        return new HttpMessageNotReadableException("Incomplete GPS request", cause,
                new ServletServerHttpRequest(new MockHttpServletRequest()));
    }

    @Test
    void incompleteUploadReturnsRetryableTimeout() {
        var response = new MockHttpServletResponse();
        var body = new TrackingApiExceptionHandler().handleIncompleteBody(
                unreadable(new RuntimeException(new SocketTimeoutException())), response);
        assertEquals(408, response.getStatus());
        assertTrue(body.get("message").contains("pendientes"));
    }

    @Test
    void malformedJsonReturnsBadRequest() {
        var response = new MockHttpServletResponse();
        new TrackingApiExceptionHandler().handleIncompleteBody(unreadable(null), response);
        assertEquals(400, response.getStatus());
    }

    @Test
    void closedResponseIsNotWrittenAgain() throws Exception {
        var response = new MockHttpServletResponse();
        response.setStatus(408);
        response.flushBuffer();
        assertNull(new TrackingApiExceptionHandler().handleIncompleteBody(
                unreadable(new SocketTimeoutException()), response));
        assertEquals(408, response.getStatus());
        assertEquals("", response.getContentAsString());
    }
}
