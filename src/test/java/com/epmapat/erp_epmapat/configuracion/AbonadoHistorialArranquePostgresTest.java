package com.epmapat.erp_epmapat.configuracion;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.util.PropertyPlaceholderHelper;

@EnabledIfSystemProperty(named = "epmapat.test.localPostgres", matches = "true")
class AbonadoHistorialArranquePostgresTest {
    @Test void esquemaInstaladoNoEsperaElBloqueoCompartidoDeUnRespaldo() throws Exception {
        var properties = new Properties();
        try (var input = getClass().getResourceAsStream("/application-local.properties")) { properties.load(input); }
        var helper = new PropertyPlaceholderHelper("${", "}", ":", true);
        String url = helper.replacePlaceholders(properties.getProperty("spring.datasource.url"), key -> null);
        assertTrue(url.startsWith("jdbc:postgresql://localhost:") || url.startsWith("jdbc:postgresql://127.0.0.1:"));
        var ds = new DriverManagerDataSource(url,
            helper.replacePlaceholders(properties.getProperty("spring.datasource.username"), key -> null),
            helper.replacePlaceholders(properties.getProperty("spring.datasource.password"), key -> null));
        try (Connection respaldo = ds.getConnection(); Connection arranque = ds.getConnection()) {
            respaldo.setAutoCommit(false); respaldo.setReadOnly(true);
            arranque.setAutoCommit(false); arranque.setReadOnly(true);
            try {
                try (var stmt = respaldo.createStatement()) {
                    stmt.execute("LOCK TABLE public.abonados IN ACCESS SHARE MODE");
                }
                try (var stmt = arranque.createStatement()) { stmt.execute("SET LOCAL statement_timeout = '3s'"); }
                var migration = new AbonadoHistorialSchemaMigration(new SingleConnectionDataSource(arranque, true));
                assertTimeout(Duration.ofSeconds(4), () -> migration.run(null));
            } finally {
                arranque.rollback(); respaldo.rollback();
            }
        }
    }
}
