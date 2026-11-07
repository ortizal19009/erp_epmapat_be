package com.epmapat.erp_epmapat.configuracion;

import javax.sql.DataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Component
public class AbonadoHistorialSchemaMigration implements ApplicationRunner {
    private final DataSource dataSource;
    public AbonadoHistorialSchemaMigration(DataSource dataSource) { this.dataSource = dataSource; }
    @org.springframework.transaction.annotation.Transactional
    @Override public void run(ApplicationArguments args) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            new ClassPathResource("sql/2026-11-06_abonado_historial.sql"));
        populator.setSeparator(";;");
        populator.execute(dataSource);
    }
}
