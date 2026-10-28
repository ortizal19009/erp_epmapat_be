package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.emails.dtos.SendEmailRequest;
import com.epmapat.erp_epmapat.emails.model.EmailMessage;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.repository.EmailMessageR;
import com.epmapat.erp_epmapat.emails.service.EmailComposerService;
import com.epmapat.erp_epmapat.emails.service.EmailBlacklistService;
import com.epmapat.erp_epmapat.interfaces.ResumenPendientesCuenta;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;

class CierreRutaNotificacionServicioTest {
    RutasxemisionR rutas = mock(RutasxemisionR.class);
    LecturasR lecturas = mock(LecturasR.class);
    FacturaServicio facturas = mock(FacturaServicio.class);
    EmailMessageR mensajes = mock(EmailMessageR.class);
    EmailComposerService composer = mock(EmailComposerService.class);
    MockEnvironment ambiente = new MockEnvironment();
    EmailBlacklistService blacklist = mock(EmailBlacklistService.class);
    CierreRutaNotificacionServicio servicio;
    Clientes responsable;
    Lecturas lectura;
    Rutasxemision ruta;

    @BeforeEach void preparar() {
        servicio = new CierreRutaNotificacionServicio(rutas, lecturas, facturas, mensajes, composer, ambiente, blacklist);
        Emisiones emision = new Emisiones(); emision.setEmision("202610");
        ruta = new Rutasxemision(); ruta.setIdrutaxemision(10L); ruta.setEstado(1); ruta.setIdemision_emisiones(emision);
        responsable = new Clientes(); responsable.setNombre("Ana <Pago>"); responsable.setEmail("responsable@example.com");
        Abonados abonado = new Abonados(); abonado.setIdabonado(100L); abonado.setIdresponsable(responsable);
        lectura = new Lecturas(); lectura.setIdlectura(1L); lectura.setEstado(1); lectura.setIdfactura(200L);
        lectura.setTotal1(new BigDecimal("12.35")); lectura.setIdabonado_abonados(abonado);
        when(rutas.lockParaNotificar(10L)).thenReturn(Optional.of(ruta));
        when(lecturas.findByIdrutaxemision(10L)).thenReturn(List.of(lectura));
        when(facturas.resumenPendientesCierrePorCuentas(anyList(), anyList())).thenReturn(List.of());
        when(mensajes.findFirstByCorrelationIdOrderByCreatedAtDesc(anyString())).thenReturn(Optional.empty());
    }

    @Test void localSiempreDirigeAlCorreoDePruebaYDestacaValorActual() {
        responsable.setEmail("correo invalido");
        ResumenPendientesCuenta resumen = mock(ResumenPendientesCuenta.class);
        when(resumen.getCuenta()).thenReturn(100L);
        when(resumen.getTotalPendiente()).thenReturn(new BigDecimal("50.00"));
        when(facturas.resumenPendientesCierrePorCuentas(List.of(100L), List.of(200L))).thenReturn(List.of(resumen));
        assertEquals(1, servicio.notificar(10L).encolados());
        ArgumentCaptor<SendEmailRequest> captor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(composer).enqueue(eq(EmailType.NOTIFICACION), captor.capture());
        var solicitud = captor.getValue();
        assertEquals(List.of("alexis.ortiz81@outlook.com"), solicitud.to);
        assertNull(solicitud.cc); assertNull(solicitud.bcc);
        assertEquals("emails/cierre-ruta", solicitud.templateKey);
        String html = CierreRutaCorreoPlantillaTest.render(solicitud.variables);
        assertTrue(html.contains("VALOR ACTUAL GENERADO"));
        assertTrue(html.contains("12.35"));
        assertTrue(solicitud.text.contains("USD 62.35"));
        assertTrue(html.contains("Ana &lt;Pago&gt;"));
    }

    @Test void produccionUsaSoloElResponsable() {
        ambiente.setActiveProfiles("prod");
        assertTrue(servicio.notificar(10L).produccion());
        ArgumentCaptor<SendEmailRequest> captor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(composer).enqueue(eq(EmailType.NOTIFICACION), captor.capture());
        assertEquals(List.of("responsable@example.com"), captor.getValue().to);
        assertFalse(captor.getValue().subject.contains("PRUEBA"));
    }

    @Test void produccionOmiteCorreosInvalidosSinUsarCorreoDelTitular() {
        ambiente.setActiveProfiles("prod");
        responsable.setEmail("invalido");
        assertEquals(1, servicio.notificar(10L).sinCorreoValido());
        verifyNoInteractions(composer);
    }

    @Test void noRepiteUnaNotificacionYaEncolada() {
        when(mensajes.findFirstByCorrelationIdOrderByCreatedAtDesc(anyString())).thenReturn(Optional.of(new EmailMessage()));
        assertEquals(1, servicio.notificar(10L).duplicados());
        verifyNoInteractions(composer);
    }

    @Test void noNotificaLecturasSinCalculoYRechazaRutasAbiertas() {
        lectura.setTotal1(null);
        assertEquals(1, servicio.notificar(10L).sinCalcular());
        verifyNoInteractions(composer);
        ruta.setEstado(0);
        assertThrows(ResponseStatusException.class, () -> servicio.notificar(10L));
    }

    @Test void omiteCorreosBloqueadosSinDetenerLaRuta() {
        doThrow(new com.epmapat.erp_epmapat.emails.service.EmailBlacklistViolationException("bloqueado"))
            .when(blacklist).validateRecipients(anyList());
        assertEquals(1, servicio.notificar(10L).bloqueados());
        verifyNoInteractions(composer);
    }

    @Test void validaUnSoloCorreoSinInyeccionNiDestinatariosAdicionales() {
        assertEquals("ana@example.com", CierreRutaNotificacionServicio.correoValido(" ana@example.com "));
        for (String valor : List.of("ana", "ana@localhost", "ana@example.com,otro@example.com", "ana@example.com;otro@example.com", "ana@example.com\r\nBcc:otro@example.com", "Ana <ana@example.com>")) {
            assertNull(CierreRutaNotificacionServicio.correoValido(valor), valor);
        }
    }
}
