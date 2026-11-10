package com.epmapat.erp_epmapat.jasperReports;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.jasperReports.services.BuildReports;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;

class BuildReportsDateParametersTest {
    @Test void fechasTimestampSeNormalizanYElPeriodoSePuedeEvaluar() throws Exception {
        var design = new JasperDesign();
        design.setName("Periodo");
        for (String name : new String[]{"desde", "hasta"}) {
            var parameter = new JRDesignParameter();
            parameter.setName(name); parameter.setValueClass(java.sql.Date.class);
            design.addParameter(parameter);
        }
        var title = new JRDesignBand(); title.setHeight(30);
        var field = new JRDesignTextField(); field.setWidth(500); field.setHeight(25);
        field.setExpression(new JRDesignExpression("\"Periodo: \" + $P{desde} + \" al \" + $P{hasta}"));
        title.addElement(field); design.setTitle(title);
        var report = JasperCompileManager.compileReport(design);
        var builder = new BuildReports(null, null);
        Map<String, Object> input = Map.of("desde", Timestamp.valueOf("2026-11-01 00:00:00"),
            "hasta", Timestamp.valueOf("2026-11-10 00:00:00"));
        Map<String, Object> normalized = ReflectionTestUtils.invokeMethod(builder, "normalizeParametersForReport", report, input);
        assertEquals(java.sql.Date.class, normalized.get("desde").getClass());
        assertEquals(java.sql.Date.class, normalized.get("hasta").getClass());
        assertEquals(Timestamp.class, input.get("desde").getClass());
        var print = JasperFillManager.fillReport(report, normalized, new JREmptyDataSource());
        assertTrue(JasperExportManager.exportReportToXml(print).contains("Periodo: 2026-11-01 al 2026-11-10"));
    }

    @Test void conservaFechasYaTipadasYConversionesDeOtrosReportes() {
        var builder = new BuildReports(null, null);
        var date = java.sql.Date.valueOf("2026-11-10");
        assertSame(date, ReflectionTestUtils.invokeMethod(builder, "convertValue", date, java.sql.Date.class));
        var timestamp = (Timestamp) ReflectionTestUtils.invokeMethod(builder, "convertValue", date, Timestamp.class);
        assertEquals(date.getTime(), timestamp.getTime());
        var converted = (java.sql.Date) ReflectionTestUtils.invokeMethod(builder, "convertValue", new java.util.Date(date.getTime()), java.sql.Date.class);
        assertEquals(date, converted);
        assertNull(ReflectionTestUtils.invokeMethod(builder, "convertValue", null, java.sql.Date.class));
    }
}
