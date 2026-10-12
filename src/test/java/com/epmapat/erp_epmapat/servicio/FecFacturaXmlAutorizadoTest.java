package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.repositorio.Fec_facturaR;

class FecFacturaXmlAutorizadoTest {
    private static final String KEY = "0810202601046002881000120010150000291908329164211";
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><factura><razonSocial>A &amp; B</razonSocial><detalle>texto\\noriginal</detalle></factura>";
    private final Fec_facturaService service = new Fec_facturaService();
    private final Fec_facturaR dao = mock(Fec_facturaR.class);
    private MockRestServiceServer server;
    private Fec_factura invoice;

    @BeforeEach void setup() {
        RestTemplate rest = new RestTemplate();
        server = MockRestServiceServer.createServer(rest);
        ReflectionTestUtils.setField(service, "dao", dao);
        ReflectionTestUtils.setField(service, "restTemplate", rest);
        ReflectionTestUtils.setField(service, "logService", mock(FecFacturaLogService.class));
        ReflectionTestUtils.setField(service, "sriMicroserviceBaseUrl", "http://192.168.0.33:9096");
        invoice = new Fec_factura();
        invoice.setIdfactura(123L);
        invoice.setClaveacceso(" " + KEY + " ");
        when(dao.findById(123L)).thenReturn(Optional.of(invoice));
        when(dao.save(any(Fec_factura.class))).thenAnswer(call -> call.getArgument(0));
    }

    @Test void retrievesUsingStoredKeyAndPreservesSignedXml() throws Exception {
        expectResponse(new ObjectMapper().writeValueAsString(java.util.Map.of("xmlAutorizado", XML)), MediaType.APPLICATION_JSON);
        assertEquals(XML, service.recuperarXmlAutorizado(123L).orElseThrow().getXmlautorizado());
        assertEquals("X", invoice.getEstado());
        assertNull(invoice.getErrores());
        verify(dao).save(invoice);
        server.verify();
    }

    @Test void acceptsRawXmlWithoutChangingItsEntities() {
        expectResponse(XML, MediaType.APPLICATION_XML);
        assertEquals(XML, service.recuperarXmlAutorizado(123L).orElseThrow().getXmlautorizado());
        server.verify();
    }

    @Test void decodesXmlEnvelopeOnce() {
        String escaped = XML.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        expectResponse("<response><xmlAutorizado>" + escaped + "</xmlAutorizado></response>", MediaType.APPLICATION_XML);
        assertEquals(XML, service.recuperarXmlAutorizado(123L).orElseThrow().getXmlautorizado());
        server.verify();
    }

    @Test void missingXmlDoesNotReportSuccessfulRecovery() {
        expectResponse("{\"xmlAutorizado\":\"\"}", MediaType.APPLICATION_JSON);
        assertTrue(service.recuperarXmlAutorizado(123L).isEmpty());
        assertEquals("P", invoice.getEstado());
        server.verify();
    }

    private void expectResponse(String body, MediaType type) {
        server.expect(requestTo("http://192.168.0.33:9096/api/v1/autorizacion/" + KEY + "/xml"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Accept", "application/json"))
            .andRespond(withSuccess(body, type));
    }
}
