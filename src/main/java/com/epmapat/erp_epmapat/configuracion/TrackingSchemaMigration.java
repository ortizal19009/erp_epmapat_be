package com.epmapat.erp_epmapat.configuracion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Ensures deployments created before GPS point deduplication have the required column. */
@Component
public class TrackingSchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TrackingSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public TrackingSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("ALTER TABLE IF EXISTS tracking_points "
                + "ADD COLUMN IF NOT EXISTS client_point_id VARCHAR(100)");
        jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uk_tracking_point_client "
                + "ON tracking_points (client_point_id) WHERE client_point_id IS NOT NULL");
        log.info("Esquema de trazabilidad GPS verificado");
    }
}
