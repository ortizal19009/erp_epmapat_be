package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Properties;
import javax.persistence.EntityManager;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.PropertyPlaceholderHelper;

/** Executes the actual native queries through Hibernate, without changing business records. */
@EnabledIfSystemProperty(named = "epmapat.test.localPostgres", matches = "true")
class HistorialContextoPostgresTest {
    @Test void consultasDeContextoTienenAliasesUnicosYConservanSusValores() throws Exception {
        var properties = new Properties();
        try (var in = getClass().getResourceAsStream("/application-local.properties")) {
            properties.load(in);
        }
        var placeholders = new PropertyPlaceholderHelper("${", "}", ":", true);
        String url = placeholders.replacePlaceholders(properties.getProperty("spring.datasource.url"), key -> null);
        assertTrue(url.startsWith("jdbc:postgresql://localhost:") || url.startsWith("jdbc:postgresql://127.0.0.1:"));
        var dataSource = new DriverManagerDataSource(url,
            placeholders.replacePlaceholders(properties.getProperty("spring.datasource.username"), key -> null),
            placeholders.replacePlaceholders(properties.getProperty("spring.datasource.password"), key -> null));
        var cfg = new Configuration();
        cfg.getProperties().put("hibernate.connection.datasource", dataSource);
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "none");
        try (var factory = cfg.buildSessionFactory(); var session = factory.openSession()) {
            var tx = session.beginTransaction();
            try {
                // Only the native context query reaches PostgreSQL. Account writes remain mocked.
                var caso = new AguaTramiteAplicacionTest();
                var request = caso.solicitud(2, 1);
                request.getTramite().setObservacion("Prueba de contexto");
                when(caso.em.createNativeQuery(anyString())).thenAnswer(invocation -> session.createNativeQuery(invocation.getArgument(0, String.class)));
                caso.servicio.aplicar(request);
                assertContexto(session, "TRAMITES_AGUA", "7", "Prueba de contexto");

                var em = mock(EntityManager.class);
                when(em.createNativeQuery(anyString())).thenAnswer(invocation -> session.createNativeQuery(invocation.getArgument(0, String.class)));
                var abonados = spy(new AbonadoServicio());
                ReflectionTestUtils.setField(abonados, "entityManager", em);
                doReturn(request.getAbonado()).when(abonados).actualizarAbonadoConAuditoria(eq(123L), any(), eq(8L), eq("Edicion"), eq("MODIFICACION"));
                abonados.actualizarAbonadoConAuditoria(123L, request.getAbonado(), 8L, "Edicion", "MODIFICACION", "ABONADOS");
                assertContexto(session, "ABONADOS", "8", "Edicion");
            } finally {
                tx.rollback();
            }
        }
    }

    private void assertContexto(org.hibernate.Session session, String modulo, String usuario, String observacion) {
        Object[] valores = (Object[]) session.createNativeQuery(
            "SELECT current_setting('epmapat.historial_modulo') AS modulo, "
            + "current_setting('epmapat.historial_usuario') AS usuario, "
            + "current_setting('epmapat.historial_observacion') AS observacion").getSingleResult();
        assertArrayEquals(new Object[]{modulo, usuario, observacion}, valores);
    }
}
