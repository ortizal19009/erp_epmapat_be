package com.epmapat.erp_epmapat.configuracion;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
public class AguaTramiteSchemaMigration implements ApplicationRunner {
    private final DataSource dataSource;

    public AguaTramiteSchemaMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        new ResourceDatabasePopulator(new ClassPathResource(
            "sql/2026-10-22_add_aguatramite_idabonado.sql")).execute(dataSource);
    }
}
