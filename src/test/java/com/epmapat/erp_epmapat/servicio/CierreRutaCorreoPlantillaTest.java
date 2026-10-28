package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class CierreRutaCorreoPlantillaTest {
    static String render(Map<String, Object> variables) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML"); resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine(); engine.setTemplateResolver(resolver);
        Context contexto = new Context(); contexto.setVariables(variables);
        return engine.process("emails/cierre-ruta", contexto);
    }

    static Map<String, Object> ejemplo() {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("esPrueba", false);
        v.put("logoUrl", "https://epmapatulcan.gob.ec/emapat/wp-content/uploads/2026/03/LOGO-blanco-1.png");
        v.put("nombre", "Maria Ejemplo"); v.put("cuenta", 12345L); v.put("emision", "octubre de 2026");
        v.put("actual", "12.35"); v.put("anterior", "50.00"); v.put("total", "62.35");
        v.put("consumo", "35.00"); v.put("servicios", "10.00"); v.put("convenios", "5.00");
        v.put("intereses", "1.20"); v.put("facturasPendientes", 3L);
        v.put("direccion", "Juan Ram\u00f3n Arellano y Bol\u00edvar, esquina. Tulc\u00e1n.");
        v.put("telefono", "(06) 2980 021"); v.put("telefonoUrl", "tel:+59362980021");
        v.put("correoContacto", "info@epmapatulcan.gob.ec"); v.put("horario", "07:30 a 16:30");
        return v;
    }

    @Test void renderizaPlantillaConLogoValoresYContactosYGeneraVistaPrevia() throws Exception {
        String html = render(ejemplo());
        assertTrue(html.contains("VALOR ACTUAL GENERADO"));
        assertTrue(html.contains("12.35")); assertTrue(html.contains("62.35"));
        assertTrue(html.contains("LOGO-blanco-1.png"));
        assertTrue(html.contains("tel:+59362980021"));
        assertTrue(html.contains("mailto:info@epmapatulcan.gob.ec"));
        assertTrue(html.contains("https://epmapatulcan.gob.ec/puntos-de-recaudacion/"));
        assertFalse(html.contains("AMBIENTE DE PRUEBAS"));
        assertFalse(html.contains("${"));
        Path archivo = Path.of("target/previews/cierre-ruta-ejemplo.html");
        Files.createDirectories(archivo.getParent());
        Files.writeString(archivo, html, StandardCharsets.UTF_8);
    }

    @Test void escapaDatosDelUsuarioYDistingueElEntornoDePrueba() {
        Map<String, Object> variables = ejemplo();
        variables.put("nombre", "Ana <script>alert(1)</script>"); variables.put("esPrueba", true);
        String html = render(variables);
        assertTrue(html.contains("&lt;script&gt;")); assertFalse(html.contains("<script>"));
        assertTrue(html.contains("AMBIENTE DE PRUEBAS"));
    }

    @Test void convierteCodigoDeEmisionALecturaNaturalSinFallarEnCodigosAntiguos() {
        assertEquals("octubre de 2026", CierreRutaNotificacionServicio.periodo("2610"));
        assertEquals("antigua", CierreRutaNotificacionServicio.periodo("antigua"));
        assertEquals("2613", CierreRutaNotificacionServicio.periodo("2613"));
    }
}
