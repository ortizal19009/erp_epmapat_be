package com.epmapat.erp_epmapat.configuracion;

import javax.sql.DataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AbonadoHistorialSchemaMigration implements ApplicationRunner {
    private final DataSource dataSource;
    private final JdbcTemplate jdbc;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbonadoHistorialSchemaMigration.class);
    @org.springframework.beans.factory.annotation.Autowired
    public AbonadoHistorialSchemaMigration(DataSource dataSource) { this(dataSource, new JdbcTemplate(dataSource)); }
    AbonadoHistorialSchemaMigration(DataSource dataSource, JdbcTemplate jdbc) {
        this.dataSource = dataSource;
        this.jdbc = jdbc;
    }
    @org.springframework.transaction.annotation.Transactional
    @Override public void run(ApplicationArguments args) {
        Boolean instalada = jdbc.queryForObject("""
            SELECT to_regclass('public.abonado_historial') IS NOT NULL
               AND to_regclass('public.abonado_historial_cuenta_fecha_idx') IS NOT NULL
               AND to_regprocedure('public.abonado_historial_valor(text,jsonb)') IS NOT NULL
               AND EXISTS (SELECT 1 FROM pg_trigger
                   WHERE tgrelid = to_regclass('public.abonados')
                     AND tgname = 'abonado_historial_update' AND NOT tgisinternal
                     AND tgfoid = to_regprocedure('public.registrar_abonado_historial()'))
            """, Boolean.class);
        if (Boolean.TRUE.equals(instalada)) {
            log.info("Historial de abonados ya instalado; no se ejecuta DDL de nuevo");
            return;
        }
        jdbc.execute("SET LOCAL lock_timeout = '5s'");
        jdbc.execute("SET LOCAL statement_timeout = '30s'");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            new ClassPathResource("sql/2026-11-06_abonado_historial.sql"));
        populator.setSeparator(";;");
        populator.execute(dataSource);
        log.info("Esquema de historial de abonados instalado");
    }
}
