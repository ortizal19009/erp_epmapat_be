package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.epmapat.erp_epmapat.repositorio.FacturasR;

@Testcontainers(disabledWithoutDocker = true)
class TitularidadDeudasPostgresTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test void soloExcluyeConsumoDeEmisionAbiertaSinConvenio() throws Exception {
        var jdbc = new JdbcTemplate(new DriverManagerDataSource(
            postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()));
        jdbc.execute("CREATE TABLE emisiones (idemision bigint, emision text, estado integer, fechaanulacion date)");
        jdbc.execute("CREATE TABLE lecturas (idfactura bigint, idabonado_abonados bigint, idemision bigint)");
        jdbc.execute("CREATE TABLE facturas (idfactura bigint, idabonado bigint, totaltarifa numeric, "
            + "fechaeliminacion date, fechaanulacion date, estado integer, fechacobro date, "
            + "idmodulo bigint, fechaconvenio date, estadoconvenio integer, conveniopago integer, pagado integer DEFAULT 0)");
        jdbc.update("INSERT INTO emisiones VALUES (1, '202609', 1, NULL), (2, '202610', 0, NULL)");
        jdbc.update("INSERT INTO facturas VALUES (10, 123, 20, NULL, NULL, 1, NULL, 4, NULL, 0, 0, 0)");
        jdbc.update("INSERT INTO lecturas VALUES (10, 123, 2)");
        String sql = FacturasR.class.getMethod("contarDeudasParaCambioTitularidad", Long.class)
            .getAnnotation(Query.class).value().replace("?1", "?");
        // La factura corriente puede existir y seguir pendiente.
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE lecturas SET idemision=1");
        assertEquals(1L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE lecturas SET idemision=2");
        jdbc.update("UPDATE facturas SET idmodulo=27");
        assertEquals(1L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET idmodulo=4, conveniopago=1");
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET conveniopago=0");
        jdbc.update("UPDATE emisiones SET estado=1 WHERE idemision=2");
        assertEquals(1L, jdbc.queryForObject(sql, Long.class, 123L));
        // Cada marca de una factura original convenida debe excluirla de la deuda.
        jdbc.update("UPDATE facturas SET fechaconvenio=CURRENT_DATE");
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET fechaconvenio=NULL, estadoconvenio=1");
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
        // Las cuotas nuevas siguen bloqueando aunque tengan marcas del convenio.
        jdbc.update("INSERT INTO facturas VALUES (11, 123, 20, NULL, NULL, 1, NULL, 27, CURRENT_DATE, 1, 1, 0)");
        assertEquals(1L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET pagado=1 WHERE idfactura=11");
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET pagado=0, fechacobro=CURRENT_DATE WHERE idfactura=11");
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET estadoconvenio=0 WHERE idfactura=10");
        assertEquals(1L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET fechacobro=CURRENT_DATE");
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
        jdbc.update("UPDATE facturas SET fechacobro=NULL, fechaanulacion=CURRENT_DATE");
        assertEquals(0L, jdbc.queryForObject(sql, Long.class, 123L));
    }
}
