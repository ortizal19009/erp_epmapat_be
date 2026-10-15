package com.epmapat.erp_epmapat.configuracion;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackingSchemaMigrationTest {
    @Test void startupIncludesHistoricalSessionMigrationAndRemainsRepeatable() {
        var jdbc = mock(JdbcTemplate.class);
        var migration = new TrackingSchemaMigration(jdbc);
        migration.run(null);
        migration.run(null);
        var statements = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(jdbc, times(8)).execute(statements.capture());
        var sql = statements.getAllValues();
        assertEquals(sql.subList(0, 4), sql.subList(4, 8));
        assertEquals("ALTER TABLE tracking_sessions ADD COLUMN IF NOT EXISTS source_session_id VARCHAR(50)", sql.get(0));
        assertEquals("CREATE INDEX IF NOT EXISTS tracking_sessions_source_idx ON tracking_sessions (source_session_id)", sql.get(1));
        assertTrue(sql.stream().allMatch(statement -> statement.contains("IF NOT EXISTS")));
    }
    @Test void missingSchemaPermissionsFailStartupInsteadOfReportingSuccess() {
        var jdbc = mock(JdbcTemplate.class);
        doThrow(new org.springframework.dao.PermissionDeniedDataAccessException("DDL denied", null))
                .when(jdbc).execute(anyString());
        assertThrows(org.springframework.dao.PermissionDeniedDataAccessException.class,
                () -> new TrackingSchemaMigration(jdbc).run(null));
    }
}
