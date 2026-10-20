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
        String reportXml=net.sf.jasperreports.engine.xml.JRXmlWriter.writeReport(result,"UTF-8");
        assertTrue(reportXml.contains("RAZÓN ELIMINACIÓN"));
        assertFalse(reportXml.contains("RAZÃ"));
        assertTrue(reportXml.contains("N/D"));
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
                st.execute("CREATE TABLE facturas(idfactura bigint primary key, fechaeliminacion timestamp, razoneliminacion text, totaltarifa numeric);"
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
                    + "INSERT INTO facturas(idfactura,fechaeliminacion,razoneliminacion) VALUES(1,'2026-09-01','PRUEBA'),(2,null,null),(3,'2026-09-30 23:59:59','PRUEBA'),(4,null,null),"
                    + "(5,'2026-10-01',null),(6,'2026-08-31 23:59:59',null);"
                    + "INSERT INTO lecturas VALUES(1,1,1,1),(2,2,1,1),(3,3,1,1),(4,4,1,1),(5,5,1,1),(6,6,1,1);"
                    + "INSERT INTO emisionindividual VALUES(1,2),(3,4);"
                    + "INSERT INTO rubroxfac VALUES(1,1,1,10,null),(2,1,1,7,1),(3,1,1,20,1),(4,1,1,15,null),"
                    + "(1,5,1,999,1),(1,165,1,999,1),(1,1,1,500,0),(5,1,1,1000,1),(6,1,1,1000,1);"
                    + "UPDATE facturas SET idfactura=idfactura+200000; UPDATE lecturas SET idfactura=idfactura+200000;"
                    + "UPDATE rubroxfac SET idfactura_facturas=idfactura_facturas+200000;"
                    + "INSERT INTO facturas(idfactura,fechaeliminacion,razoneliminacion) SELECT 1000+g, TIMESTAMP '2025-01-01', 'HISTORICO' FROM generate_series(1,100000) g;"
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
            try (var st=db.createStatement()) {
                st.execute("INSERT INTO rubros VALUES(46,'Intereses'),(153,'INTERESES'),(1010,'Interés (GAD-MT)'),(2064,'Interferencia en medidores');"
                    + "INSERT INTO rubroxfac VALUES(200001,46,1,1000,1),(200001,153,1,2000,1),(200001,1010,1,3000,1),"
                    + "(200002,46,1,4000,1),(200002,153,1,5000,1),(200002,1010,1,6000,1),"
                    + "(200001,2064,1,0,1),(200002,2064,1,0,1);");
            }
            assertEquals(invoices,rows(db,template.getDatasetMap().get("RefFacturas").getQuery().getText()),"Los intereses no deben cambiar ningun importe");
            var rubrosSinInteres=rows(db,template.getDatasetMap().get("RefRubros").getQuery().getText());
            assertFalse(rubrosSinInteres.stream().anyMatch(r -> List.of(46L,153L,1010L).contains(r.get("codigo_rubro"))));
            assertTrue(rubrosSinInteres.stream().anyMatch(r -> Long.valueOf(2064).equals(r.get("codigo_rubro"))),"Interferencia no es un interes");
            explain(db, "original", original);
            explain(db, "optimized", template.getDatasetMap().get("RefFacturas").getQuery().getText());
            var params = new HashMap<String,Object>();
            params.put("desde", java.sql.Date.valueOf("2026-09-01")); params.put("hasta", java.sql.Date.valueOf("2026-09-30")); params.put("idusuario", 1);
            var print = JasperFillManager.fillReport(JasperCompileManager.compileReport(template), params, db);
            assertFalse(print.getPages().isEmpty());
            byte[] pdf=JasperExportManager.exportReportToPdf(print);
            assertTrue(pdf.length > 1000);
            try (var document=org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
                String text=new org.apache.pdfbox.text.PDFTextStripper().getText(document).replace(',', '.');
                String compact = text.replaceAll("\\s+", " ");
                assertTrue(compact.contains("RESUMEN DE REFACTURACIONES"), text);
                assertTrue(compact.contains("Cuentas incluidas 1 Facturas eliminadas 2 Rubros distintos 2"), text);
                assertTrue(compact.contains("Total facturado 30.00 Total refacturado 22.00 Diferencia -8.00"), text);
                assertTrue(text.contains("7.00"), text);
                assertTrue(text.contains("15.00"), text);
                assertTrue(text.contains("-3.00"), text);
                assertTrue(text.contains("-5.00"), text);
                assertTrue(text.contains("RAZÓN"), text);
                assertFalse(text.contains("RAZÃ"), text);
            }
            var emptyParams = new HashMap<String,Object>(params);
            emptyParams.put("desde", java.sql.Date.valueOf("2027-09-01"));
            emptyParams.put("hasta", java.sql.Date.valueOf("2027-09-30"));
            assertFalse(JasperFillManager.fillReport(JasperCompileManager.compileReport(template), emptyParams, db).getPages().isEmpty());
            try (var st=db.createStatement()) {
                st.execute("INSERT INTO facturas(idfactura,fechaeliminacion,razoneliminacion) VALUES(400001,'2026-09-10','SIN VINCULO'),(400002,'2026-09-10','CERO REAL'),(400003,null,null),(400004,'2026-09-10','SIN RUBROS'),(400005,null,null);"
                    + "INSERT INTO lecturas VALUES(400001,400001,1,1),(400002,400002,1,1),(400003,400003,1,1),(400004,400004,1,1),(400005,400005,1,1);"
                    + "INSERT INTO rubroxfac VALUES(400001,1,1,10,1),(400002,1,1,10,1),(400003,1,1,0,1),(400004,1,1,10,1);"
                    + "INSERT INTO emisionindividual VALUES(400002,400003),(400004,400005);");
            }
            var edgeRows=rows(db,template.getDatasetMap().get("RefFacturas").getQuery().getText());
            var deleted=edgeRows.stream().filter(r -> Long.valueOf(400001).equals(r.get("factura_original"))).findFirst().orElseThrow();
            assertNull(deleted.get("factura_nueva"));
            assertEquals(0, ((java.math.BigDecimal)deleted.get("valornuevo")).signum());
            assertEquals(new java.math.BigDecimal("-10"), deleted.get("diferencia"));
            var rubrosBajas=rows(db,template.getDatasetMap().get("RefRubros").getQuery().getText());
            assertEquals(0, rubrosBajas.stream().map(r -> (java.math.BigDecimal)r.get("valor_original")).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add).compareTo(new java.math.BigDecimal("60")));

            var missing=edgeRows.stream().filter(r -> Long.valueOf(400004).equals(r.get("factura_original"))).findFirst().orElseThrow();
            assertNull(missing.get("valornuevo")); assertNull(missing.get("diferencia"));
            var zero=edgeRows.stream().filter(r -> Long.valueOf(400002).equals(r.get("factura_original"))).findFirst().orElseThrow();
            assertEquals(0,((java.math.BigDecimal)zero.get("valornuevo")).signum());
            assertEquals(new java.math.BigDecimal("-10"),zero.get("diferencia"));
            try (var st=db.createStatement()) {
                st.execute("INSERT INTO facturas(idfactura,fechaeliminacion,razoneliminacion,totaltarifa) VALUES(400006,'2026-09-10','CABECERA CERO SIN RUBROS',0),(400007,'2026-09-10','CABECERA NO CERO SIN RUBROS',100);"
                    + "INSERT INTO lecturas VALUES(400006,400006,1,1),(400007,400007,1,1);"
                    + "INSERT INTO emisionindividual VALUES(400006,400003),(400007,400003);");
            }
            var fallbackRows=rows(db,template.getDatasetMap().get("RefFacturas").getQuery().getText());
            var zeroHeader=fallbackRows.stream().filter(r -> Long.valueOf(400006).equals(r.get("factura_original"))).findFirst().orElseThrow();
            assertEquals(0,((java.math.BigDecimal)zeroHeader.get("valoranterior")).signum());
            var unknown=fallbackRows.stream().filter(r -> Long.valueOf(400007).equals(r.get("factura_original"))).findFirst().orElseThrow();
            assertNull(unknown.get("valoranterior"),"No usar totaltarifa no cero porque puede incluir intereses u otros conceptos");
            try (var st=db.createStatement()) {
                st.execute("INSERT INTO facturas(idfactura) VALUES(400008); INSERT INTO lecturas VALUES(400008,400008,1,1);"
                    + "INSERT INTO rubros VALUES(1009,'SERVICIO NUEVO'); INSERT INTO rubroxfac VALUES(400008,1009,1,8.83,1);"
                    + "UPDATE emisionindividual SET idlecturanueva=400008 WHERE idlecturaanterior=400006;");
            }
            var newOnly = rows(db,template.getDatasetMap().get("RefRubros").getQuery().getText()).stream()
                .filter(r -> Long.valueOf(1009).equals(r.get("codigo_rubro"))).findFirst().orElseThrow();
            assertEquals(0, ((java.math.BigDecimal)newOnly.get("valor_original")).signum());
            assertEquals(new java.math.BigDecimal("8.83"), newOnly.get("valor_nuevo"));
            assertEquals(new java.math.BigDecimal("8.83"), newOnly.get("diferencia"));
            var withMissing=JasperFillManager.fillReport(JasperCompileManager.compileReport(template),params,db);
            byte[] resultPdf=JasperExportManager.exportReportToPdf(withMissing);
            java.nio.file.Files.write(java.nio.file.Path.of("target/refacturaciones-verificacion.pdf"),resultPdf);
            try (var document=org.apache.pdfbox.pdmodel.PDDocument.load(resultPdf)) {
                String text=new org.apache.pdfbox.text.PDFTextStripper().getText(document).replace(',', '.');
                assertTrue(text.contains("N/D"),text); assertTrue(text.contains("-10.00"),text);
            }
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
