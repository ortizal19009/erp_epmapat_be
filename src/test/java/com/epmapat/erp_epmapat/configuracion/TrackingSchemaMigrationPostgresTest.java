package com.epmapat.erp_epmapat.configuracion;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

/** Upgrade the already installed schema, rather than generating the current JPA schema. */
@Testcontainers(disabledWithoutDocker = true)
class TrackingSchemaMigrationPostgresTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test void legacyGpsEventsSurviveRepeatedMigrationAndNewEventsAreDeduplicated() {
        var jdbc = new JdbcTemplate(new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()));
        jdbc.execute("CREATE TABLE tracking_sessions (id varchar(50) PRIMARY KEY)");
        jdbc.execute("CREATE TABLE tracking_points (id bigint PRIMARY KEY)");
        jdbc.execute("CREATE TABLE route_deviation_events (id bigint PRIMARY KEY, description text)");
        jdbc.execute("CREATE TABLE gps_gap_events (id bigint PRIMARY KEY, description text)");
        jdbc.update("INSERT INTO gps_gap_events VALUES (1, 'original GPS gap')");
        jdbc.update("INSERT INTO route_deviation_events VALUES (1, 'original deviation')");

        var migration = new TrackingSchemaMigration(jdbc);
        migration.run(null);
        migration.run(null);

        assertEquals("original GPS gap", jdbc.queryForObject("SELECT description FROM gps_gap_events WHERE id=1", String.class));
        assertEquals("original deviation", jdbc.queryForObject("SELECT description FROM route_deviation_events WHERE id=1", String.class));
        assertNull(jdbc.queryForObject("SELECT client_event_id FROM gps_gap_events WHERE id=1", String.class));
        assertNull(jdbc.queryForObject("SELECT client_event_id FROM route_deviation_events WHERE id=1", String.class));
        for (String table : new String[]{"gps_gap_events", "route_deviation_events"}) {
            jdbc.update("INSERT INTO " + table + " (id, client_event_id) VALUES (2, 'device-event-2')");
            assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM " + table + " WHERE client_event_id='device-event-2'", Integer.class));
            assertThrows(DataIntegrityViolationException.class,
                    () -> jdbc.update("INSERT INTO " + table + " (id, client_event_id) VALUES (3, 'device-event-2')"));
            // Historic records have no client key and must remain independently representable.
            jdbc.update("INSERT INTO " + table + " (id) VALUES (4)");
            assertEquals(3, jdbc.queryForObject("SELECT count(*) FROM " + table, Integer.class));
        }
    }
}
