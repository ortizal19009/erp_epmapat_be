package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.epmapat.erp_epmapat.repositorio.LecturasR;

@Testcontainers(disabledWithoutDocker = true)
class ValoresEmitidosPostgresTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test void incluyeObservacionesSinIncluirFacturasEliminadasNiRubrosInactivos() throws Exception {
        var jdbc = new JdbcTemplate(new DriverManagerDataSource(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()));
        jdbc.execute("CREATE TABLE clientes (idcliente bigint PRIMARY KEY, nombre text)");
        jdbc.execute("CREATE TABLE abonados (idabonado bigint)");
        jdbc.execute("CREATE TABLE categorias (idcategoria bigint, descripcion text)");
        jdbc.execute("CREATE TABLE facturas (idfactura bigint, estado integer, fechaeliminacion date, fechaanulacion date)");
        jdbc.execute("CREATE TABLE lecturas (idresponsable bigint, idabonado_abonados bigint, "
                + "idfactura bigint, idcategoria bigint, idemision bigint, lecturaactual integer, "
                + "lecturaanterior integer, observaciones text)");
        jdbc.execute("CREATE TABLE rubroxfac (idfactura_facturas bigint, estado integer, "
                + "idrubro_rubros bigint, cantidad numeric, valorunitario numeric)");
        jdbc.update("INSERT INTO clientes VALUES (1, 'Cliente')");
        jdbc.update("INSERT INTO categorias VALUES (1, 'Residencial')");
        for (int i = 1; i <= 8; i++) {
            jdbc.update("INSERT INTO abonados VALUES (?)", i);
            jdbc.update("INSERT INTO facturas VALUES (?, 1, NULL, NULL)", i);
            jdbc.update("INSERT INTO lecturas VALUES (1, ?, ?, 1, 251, 12, 2, ?)", i, i,
                    i == 1 ? null : i == 2 ? "" : i == 3 ? "   " : "Novedad de lectura");
            jdbc.update("INSERT INTO rubroxfac VALUES (?, 1, 1, 2, 5.125)", i);
            jdbc.update("INSERT INTO rubroxfac VALUES (?, 0, 1, 1, 999)", i);
            jdbc.update("INSERT INTO rubroxfac VALUES (?, 1, 6, 1, 999)", i);
        }
        jdbc.update("UPDATE facturas SET estado=0 WHERE idfactura=5");
        jdbc.update("UPDATE facturas SET fechaeliminacion=CURRENT_DATE WHERE idfactura=6");
        jdbc.update("UPDATE facturas SET fechaanulacion=CURRENT_DATE WHERE idfactura=7");
        jdbc.update("UPDATE lecturas SET idemision=250 WHERE idfactura=8");
        jdbc.update("UPDATE rubroxfac SET estado=NULL WHERE idfactura_facturas=1 AND valorunitario=5.125");
        String sql = LecturasR.class.getMethod("getReporteValEmitidosxEmision", Long.class)
                .getAnnotation(Query.class).value().replace("?1", "?");
        var filas = jdbc.queryForList(sql, 251L);
        assertEquals(4, filas.size());
        for (int i = 0; i < filas.size(); i++) {
            assertEquals((long) i + 1, filas.get(i).get("cuenta"));
            assertEquals(new BigDecimal("10.25"), filas.get(i).get("valemitido"));
            assertEquals(10, filas.get(i).get("m3"));
        }
    }
}
