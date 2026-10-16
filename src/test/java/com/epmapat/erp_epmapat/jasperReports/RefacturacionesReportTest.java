package com.epmapat.erp_epmapat.jasperReports;

import com.epmapat.erp_epmapat.jasperReports.utils.RefacturacionesReportOptimizer;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RefacturacionesReportTest {
    @BeforeAll static void quietJasper() {
        ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME))
                .setLevel(ch.qos.logback.classic.Level.WARN);
    }
    static JasperDesign design() throws Exception {
        try (var in = RefacturacionesReportTest.class.getResourceAsStream("/reports/Refacturaciones.jrxml")) {
            return JRXmlLoader.load(in);
        }
    }
    static byte[] bytes(JasperReport report) throws Exception {
        var out = new ByteArrayOutputStream();
        try (var objects = new ObjectOutputStream(out)) { objects.writeObject(report); }
        return out.toByteArray();
    }
    @Test void keepsStoredLayoutAndCachesCompilationUntilTemplateChanges() throws Exception {
        var custom = design(); custom.setPageWidth(650);
        ((net.sf.jasperreports.engine.design.JRDesignQuery) custom.getQuery()).setText("select 0::bigint as count");
        var source = bytes(JasperCompileManager.compileReport(custom));
        var optimizer = new RefacturacionesReportOptimizer();
        var result = optimizer.load(source);
        assertEquals(650, result.getPageWidth());
        assertTrue(result.getQuery().getText().contains("INTERVAL '1 day'"));
        assertSame(result, optimizer.load(source));
        custom.setPageWidth(660);
        var changed = optimizer.load(bytes(JasperCompileManager.compileReport(custom)));
        assertEquals(660, changed.getPageWidth()); assertNotSame(result, changed);
    }

    @Test
    @EnabledIfEnvironmentVariable(named="REFACTURACIONES_TEST_POSTGRES", matches="true")
    void monthlyTotalsExcludeHistoricalRubrosAndIncludeEntireLastDay() throws Exception {
        try (var db = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:55441/refacturaciones_test", "refacturaciones_test", "")) {
            try (var st = db.createStatement()) {
                st.execute("CREATE TABLE facturas(idfactura bigint primary key, fechaeliminacion timestamp, razoneliminacion text);"
                    + "CREATE TABLE lecturas(idlectura bigint primary key,idfactura bigint,idemision bigint,idabonado_abonados bigint);"
                    + "CREATE TABLE emisionindividual(idlecturaanterior bigint,idlecturanueva bigint);"
                    + "CREATE TABLE emisiones(idemision bigint primary key,emision text);"
                    + "CREATE TABLE abonados(idabonado bigint primary key,idresponsable bigint);"
                    + "CREATE TABLE clientes(idcliente bigint primary key,nombre text);"
                    + "CREATE TABLE rubros(idrubro bigint primary key,descripcion text);"
                    + "CREATE TABLE rubroxfac(idfactura_facturas bigint,idrubro_rubros bigint,cantidad numeric,valorunitario numeric,estado integer);"
                    + "CREATE TABLE usuarios(idusuario bigint primary key,nomusu text);"
                    + "INSERT INTO emisiones VALUES(1,'Septiembre'); INSERT INTO clientes VALUES(1,'CLIENTE PRUEBA');"
                    + "INSERT INTO abonados VALUES(1,1); INSERT INTO usuarios VALUES(1,'ADMIN PRUEBA');"
                    + "INSERT INTO rubros VALUES(1,'AGUA'),(5,'EXCLUIDO'),(165,'EXCLUIDO');"
                    + "INSERT INTO facturas VALUES(1,'2026-09-01','PRUEBA'),(2,null,null),(3,'2026-09-30 23:59:59','PRUEBA'),(4,null,null),"
                    + "(5,'2026-10-01',null),(6,'2026-08-31 23:59:59',null);"
                    + "INSERT INTO lecturas VALUES(1,1,1,1),(2,2,1,1),(3,3,1,1),(4,4,1,1),(5,5,1,1),(6,6,1,1);"
                    + "INSERT INTO emisionindividual VALUES(1,2),(3,4);"
                    + "INSERT INTO rubroxfac VALUES(1,1,1,10,null),(2,1,1,7,1),(3,1,1,20,1),(4,1,1,15,null),"
                    + "(1,5,1,999,1),(1,165,1,999,1),(1,1,1,500,0),(5,1,1,1000,1),(6,1,1,1000,1);"
                    + "UPDATE facturas SET idfactura=idfactura+200000; UPDATE lecturas SET idfactura=idfactura+200000;"
                    + "UPDATE rubroxfac SET idfactura_facturas=idfactura_facturas+200000;"
                    + "INSERT INTO facturas SELECT 1000+g, TIMESTAMP '2025-01-01', 'HISTORICO' FROM generate_series(1,100000) g;"
                    + "INSERT INTO lecturas SELECT 1000+g,1000+g,1,1 FROM generate_series(1,100000) g;"
                    + "INSERT INTO rubroxfac SELECT 1000+g,1,1,100,1 FROM generate_series(1,100000) g;");
                st.execute("CREATE INDEX ON rubroxfac(idfactura_facturas); CREATE INDEX ON lecturas(idfactura);"
                    + "CREATE INDEX ON emisionindividual(idlecturaanterior); CREATE INDEX ON facturas(fechaeliminacion); ANALYZE;");
            }
            var template = design();
            var invoices = rows(db, template.getDatasetMap().get("RefFacturas").getQuery().getText());
            assertEquals(2, invoices.size());
            assertEquals(new java.math.BigDecimal("30"), invoices.stream().map(r -> (java.math.BigDecimal) r.get("valoranterior")).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
            var rubros = rows(db, template.getDatasetMap().get("RefRubros").getQuery().getText());
            assertEquals(1, rubros.size());
            assertEquals(new java.math.BigDecimal("30"), rubros.get(0).get("valor_original"));
            assertEquals(new java.math.BigDecimal("22"), rubros.get(0).get("valor_nuevo"));
            assertEquals(new java.math.BigDecimal("-8"), rubros.get(0).get("diferencia"));
            String original;
            try (var in = getClass().getResourceAsStream("/reports/refacturaciones-original.sql")) {
                original = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
            // Use identical date semantics to compare the optimization independently from the last-day fix.
            original = original.replace("f.fechaeliminacion BETWEEN $P{desde} AND $P{hasta}",
                    "f.fechaeliminacion >= CAST($P{desde} AS date) AND f.fechaeliminacion < CAST($P{hasta} AS date) + INTERVAL '1 day'");
            assertEquals(rows(db, original), invoices);
            explain(db, "original", original);
            explain(db, "optimized", template.getDatasetMap().get("RefFacturas").getQuery().getText());
            var params = new HashMap<String,Object>();
            params.put("desde", java.sql.Date.valueOf("2026-09-01")); params.put("hasta", java.sql.Date.valueOf("2026-09-30")); params.put("idusuario", 1);
            var print = JasperFillManager.fillReport(JasperCompileManager.compileReport(template), params, db);
            assertFalse(print.getPages().isEmpty());
            assertTrue(JasperExportManager.exportReportToPdf(print).length > 1000);
        }
    }
    static void explain(Connection db, String name, String query) throws Exception {
        String sql = query.replace("$P{desde}", "DATE '2026-09-01'").replace("$P{hasta}", "DATE '2026-09-30'");
        var lines = new ArrayList<String>();
        try (var st=db.createStatement(); var rs=st.executeQuery("EXPLAIN (ANALYZE, BUFFERS) " + sql)) {
            while(rs.next()) lines.add(rs.getString(1));
        }
        java.nio.file.Files.write(java.nio.file.Path.of("target/refacturaciones-" + name + "-plan.txt"), lines);
    }
    static List<Map<String,Object>> rows(Connection db, String query) throws Exception {
        String sql = query.replace("$P{desde}", "DATE '2026-09-01'").replace("$P{hasta}", "DATE '2026-09-30'");
        var result = new ArrayList<Map<String,Object>>();
        try (var st=db.createStatement(); var rs=st.executeQuery(sql)) {
            while(rs.next()) {
                var row = new HashMap<String,Object>();
                for(int i=1;i<=rs.getMetaData().getColumnCount();i++) row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                result.add(row);
            }
        }
        return result;
    }
}
