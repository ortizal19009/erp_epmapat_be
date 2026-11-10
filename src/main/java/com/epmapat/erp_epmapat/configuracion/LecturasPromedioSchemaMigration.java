package com.epmapat.erp_epmapat.configuracion;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Ensures the mapped snapshot column exists on installations predating November 2026. */
@Component
public class LecturasPromedioSchemaMigration implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public LecturasPromedioSchemaMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Historical readings must retain NULL: their original average is unknown.
        // The optional reporting index remains in the deployment SQL script.
        jdbc.execute("ALTER TABLE public.lecturas ADD COLUMN IF NOT EXISTS promedio numeric(14,2)");
    }
}
