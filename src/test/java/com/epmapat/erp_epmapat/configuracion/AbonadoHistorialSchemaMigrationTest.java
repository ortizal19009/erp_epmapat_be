package com.epmapat.erp_epmapat.configuracion;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

class AbonadoHistorialSchemaMigrationTest {
    @Test void instaladoNoRecreaFuncionesNiTriggerNiPideOtraConexion() {
        var jdbc = mock(JdbcTemplate.class);
        var dataSource = mock(DataSource.class);
        when(jdbc.queryForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        new AbonadoHistorialSchemaMigration(dataSource, jdbc).run(null);
        verify(jdbc).queryForObject(anyString(), eq(Boolean.class));
        verify(jdbc, never()).execute(anyString());
        verifyNoInteractions(dataSource);
    }
}
