package com.epmapat.erp_epmapat.jasperReports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.sql.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "RESUMEN_TEST_POSTGRES", matches = "true")
class ResumenFacturacionTest {
    static JasperDesign design;
    static JasperReport report;
    Connection db;
    @BeforeAll static void compile() throws Exception {
        try (var in = ResumenFacturacionTest.class.getResourceAsStream("/reports/ResumenFacturacion.jrxml")) {
            assertNotNull(in);
            design = JRXmlLoader.load(in);
            report = JasperCompileManager.compileReport(design);
        }
    }
    @BeforeEach void database() throws Exception {
        db = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:55440/resumen_test", "resumen_test", "");
        execute("DROP SCHEMA IF EXISTS resumen_fixture CASCADE; CREATE SCHEMA resumen_fixture; SET search_path TO resumen_fixture");
        execute("CREATE TABLE formacobro (idformacobro bigint PRIMARY KEY, descripcion text); " +
            "INSERT INTO formacobro VALUES (1,'Contado'),(3,'Nota Credito'),(4,'SPI - Transferencia'),(5,'Tarjeta de Credito'),(6,'Recaudacion Externa'),(7,'Cheque'),(8,'NO USAR ')");
        execute("CREATE TABLE facturas (idfactura bigserial PRIMARY KEY, estado integer, formapago bigint, " +
            "feccrea date DEFAULT DATE '2026-09-30', fechacobro date DEFAULT DATE '2026-09-30', " +
            "fechaanulacion date, fechaeliminacion date, pagado integer DEFAULT 1, " +
            "totaltarifa double precision DEFAULT 10, nrofactura text, usuarioanulacion bigint, " +
            "razonanulacion text, usuariocobro bigint DEFAULT 11)");
    }
    @AfterEach void close() throws Exception { if (db != null) db.close(); }
    void execute(String sql) throws Exception { try (var st = db.createStatement()) { st.execute(sql); } }
    void insert(int estado, int forma, int count) throws Exception {
        execute("INSERT INTO facturas(estado,formapago) SELECT " + estado + "," + forma + " FROM generate_series(1," + count + ")");
    }
    List<Map<String,Object>> rows(String inicio, String fin) throws Exception {
        String sql = design.getQuery().getText().replace("$P{fechaInicio}", "?").replace("$P{fechaFin}", "?");
        List<Map<String,Object>> rows = new ArrayList<>();
        try (var stmt = db.prepareStatement(sql)) {
            stmt.setString(1,inicio); stmt.setString(2,fin);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) { var row = new HashMap<String,Object>();
                    for (String key : List.of("seccion","concepto","cantidad","indicador")) row.put(key,rs.getObject(key));
                    rows.add(row);
                }
            }
        }
        return rows;
    }
    List<Map<String,Object>> rows() throws Exception { return rows("2026-09-30","2026-09-30"); }
    long cantidad(String section, String concept) throws Exception {
        var selected = rows().stream().filter(r -> section.equals(r.get("seccion")) && concept.equals(r.get("concepto"))).toList();
        assertEquals(1,selected.size(),"Una sola fila de " + concept);
        return ((Number)selected.get(0).get("cantidad")).longValue();
    }
    @Test void exampleConsolidatesCashAndSeparatesSpiAndCredit() throws Exception {
        insert(1,1,17701); insert(2,1,113); insert(1,4,185); insert(3,4,45); insert(1,6,1466); insert(1,3,2);
        String section = "RECAUDACIÓN POR FORMA DE COBRO";
        assertEquals(17814,cantidad(section,"EFECTIVO"));
        assertEquals(185,cantidad(section,"TRANSFERENCIA"));
        assertEquals(45,cantidad(section,"TRANSFERENCIA NO COBRADA"));
        assertEquals(1466,cantidad(section,"RECAUDACIÓN EXTERNA"));
        assertEquals(2,cantidad(section,"NOTAS CRÉDITO"));
        assertEquals(19465,cantidad(section,"TOTAL COBRADO"));
    }
    @Test void sameDayIncludesDateAndExcludesAdjacentDays() throws Exception {
        insert(1,1,1);
        execute("INSERT INTO facturas(estado,formapago,feccrea,fechacobro) VALUES (1,1,'2026-09-29','2026-09-29'),(1,1,'2026-10-01','2026-10-01')");
        assertEquals(1,cantidad("RECAUDACIÓN POR FORMA DE COBRO","TOTAL COBRADO"));
    }
    @Test void emptyPeriodRendersZeroTotalsAndPdf() throws Exception {
        assertEquals(0,cantidad("RECAUDACIÓN POR FORMA DE COBRO","TOTAL COBRADO"));
        var print = fill(); assertFalse(print.getPages().isEmpty());
        assertTrue(JasperExportManager.exportReportToPdf(print).length > 1000);
        assertTrue(rows().stream().anyMatch(r -> "N/A".equals(r.get("indicador"))));
    }
    @Test void unknownAndDisabledFormsRemainVisibleWithoutIncreasingTotal() throws Exception {
        insert(1,99,1); insert(1,8,1); insert(9,1,1);
        execute("DELETE FROM formacobro WHERE idformacobro=5"); insert(1,5,1);
        assertEquals(0,cantidad("RECAUDACIÓN POR FORMA DE COBRO","TOTAL COBRADO"));
        assertEquals(3,cantidad("CONTROL DE INCONSISTENCIAS","Forma inexistente o deshabilitada (NO USAR)"));
        assertEquals(1,cantidad("CONTROL DE INCONSISTENCIAS","Estados no reconocidos"));
    }
    @Test void pendingPaidFlagDoesNotIncreaseCollected() throws Exception {
        insert(3,4,3); insert(1,4,2); insert(2,4,1);
        assertEquals(2,cantidad("RECAUDACIÓN POR FORMA DE COBRO","TOTAL COBRADO"));
        assertEquals(3,cantidad("RECAUDACIÓN POR FORMA DE COBRO","TOTAL TRANSFERENCIAS PENDIENTES"));
        assertEquals(1,cantidad("CONTROL DE INCONSISTENCIAS","Cobros con estados incompatibles"));
    }
    @Test void missingDatesDuplicatesAndNonpositiveValuesAreAudited() throws Exception {
        insert(1,1,2);
        execute("UPDATE facturas SET nrofactura='001-001-1',totaltarifa=0; INSERT INTO facturas(estado,formapago,fechacobro) VALUES(1,NULL,NULL)");
        assertEquals(1,cantidad("CONTROL DE INCONSISTENCIAS","Números repetidos dentro del alcance"));
        assertEquals(2,cantidad("CONTROL DE INCONSISTENCIAS","Tarifa nula o <= 0 (no es valor final)"));
        assertEquals(1,cantidad("CONTROL DE INCONSISTENCIAS","Pagadas sin fecha (excluye SPI pendiente)"));
    }
    @Test void emissionCohortAndDailyCountsAreIndependentOfCollectionCohort() throws Exception {
        execute("INSERT INTO facturas(estado,formapago,feccrea,fechacobro) VALUES (1,1,'2026-08-01','2026-09-30'),(1,1,'2026-09-30','2026-10-01'); INSERT INTO facturas(estado,formapago,fechacobro,pagado) VALUES(1,1,NULL,0)");
        assertEquals(2,cantidad("RESUMEN EJECUTIVO","FACTURAS EMITIDAS (fecha de creación)"));
        assertEquals(1,cantidad("RESUMEN EJECUTIVO","TOTAL COBRADO (por fecha de cobro)"));
        assertEquals(1,cantidad("RESUMEN EJECUTIVO","PENDIENTES DE LAS EMITIDAS (estado actual)"));
        assertEquals(2,cantidad("EVOLUCIÓN DIARIA (cantidades)","2026-09-30"));
    }
    @Test void deletedInvoicesAreExcludedAndMultiPageReportHasInstitutionalHeader() throws Exception {
        insert(1,1,1); execute("UPDATE facturas SET fechaeliminacion='2026-09-30',estado=0");
        assertEquals(0,cantidad("RECAUDACIÓN POR FORMA DE COBRO","TOTAL COBRADO"));
        assertEquals(1,cantidad("ANULACIONES Y ELIMINACIONES","Eliminaciones registradas en el período"));
        insert(1,1,10); insert(3,4,2); insert(1,3,1);
        var print = fill(); assertTrue(print.getPages().size() >= 2);
        Path dir = Path.of("target/resumen-facturacion"); Files.createDirectories(dir);
        JasperExportManager.exportReportToPdfFile(print,dir.resolve("ResumenFacturacion-ejemplo.pdf").toString());
        for (int i=0;i<print.getPages().size();i++) {
            BufferedImage image = (BufferedImage)JasperPrintManager.printPageToImage(print,i,1.5f);
            ImageIO.write(image,"png",dir.resolve("pagina-"+(i+1)+".png").toFile());
        }
    }
    JasperPrint fill() throws Exception {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("fechaInicio","2026-09-30"); parameters.put("fechaFin","2026-09-30");
        parameters.put("razonSocial","EPMAPA-T"); parameters.put("ruc","RUC INSTITUCIONAL");
        parameters.put("dirMatriz","TULCÁN");
        return JasperFillManager.fillReport(report,parameters,db);
    }
}
