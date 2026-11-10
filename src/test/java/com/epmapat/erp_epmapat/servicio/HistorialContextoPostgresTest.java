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
        try (var factory = localFactory(); var session = factory.openSession()) {
            var tx = session.beginTransaction();
            try {
                verificarContexto(session);
            } finally {
                tx.rollback();
            }
        }
    }

    private org.hibernate.SessionFactory localFactory() throws Exception {
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
        return cfg.buildSessionFactory();
    }

    @Test void respaldoSriYEliminacionFuncionanEnHibernateSinCambiarOtrasFacturas() throws Exception {
        try (var factory = localFactory(); var session = factory.openSession()) {
            var tx = session.beginTransaction();
            try {
                for (String tabla : java.util.List.of("evento_outbox", "comprobante_documento", "sri_autorizacion_control")) {
                    session.createNativeQuery("CREATE TEMP TABLE " + tabla + " (factura_id bigint, xml text, ride bytea) ON COMMIT DROP").executeUpdate();
                    session.createNativeQuery("INSERT INTO pg_temp." + tabla + " VALUES (123, '<xml>original</xml>', decode('504446','hex')), (999, 'conservar', NULL)").executeUpdate();
                }
                var em = mock(EntityManager.class);
                when(em.createNativeQuery(anyString())).thenAnswer(invocation -> {
                    String sql = invocation.getArgument(0, String.class);
                    // Execute the production SQL against isolated temporary tables only.
                    sql = sql.replace("to_regclass(:tabla)", "to_regclass(replace(:tabla, 'public.', 'pg_temp.'))")
                        .replace("FROM public.", "FROM pg_temp.");
                    return session.createNativeQuery(sql);
                });
                var auditoria = mock(AuditoriaGenericaService.class);
                var servicio = new FacturaServicio();
                ReflectionTestUtils.setField(servicio, "entityManager", em);
                ReflectionTestUtils.setField(servicio, "auditoriaGenericaService", auditoria);
                ReflectionTestUtils.invokeMethod(servicio, "eliminarDependenciasSri", 123L, 7L, "Prueba");
                for (String tabla : java.util.List.of("evento_outbox", "comprobante_documento", "sri_autorizacion_control")) {
                    verify(auditoria).saveAuditAtomic(eq(tabla), eq(123L), argThat(payload ->
                        payload.toString().contains("<xml>original</xml>") && payload.toString().contains("504446")), eq(7L), eq("Prueba"), eq("ELIMINACION"));
                    assertEquals(1L, ((Number)session.createNativeQuery("SELECT count(*) FROM pg_temp." + tabla + " WHERE factura_id = 999").getSingleResult()).longValue());
                    assertEquals(0L, ((Number)session.createNativeQuery("SELECT count(*) FROM pg_temp." + tabla + " WHERE factura_id = 123").getSingleResult()).longValue());
                }
                // Empty snapshots must also evaluate to valid JSON.
                ReflectionTestUtils.invokeMethod(servicio, "eliminarDependenciasSri", 123L, 7L, "Prueba vacia");
                verify(auditoria, times(3)).saveAuditAtomic(anyString(), eq(123L), eq(java.util.Map.of("registrosJson", "[]")), eq(7L), eq("Prueba vacia"), eq("ELIMINACION"));
            } finally {
                tx.rollback();
            }
        }
    }

    private void verificarContexto(org.hibernate.Session session) {
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
    }

    private void assertContexto(org.hibernate.Session session, String modulo, String usuario, String observacion) {
        Object[] valores = (Object[]) session.createNativeQuery(
            "SELECT current_setting('epmapat.historial_modulo') AS modulo, "
            + "current_setting('epmapat.historial_usuario') AS usuario, "
            + "current_setting('epmapat.historial_observacion') AS observacion").getSingleResult();
        assertArrayEquals(new Object[]{modulo, usuario, observacion}, valores);
    }
}
