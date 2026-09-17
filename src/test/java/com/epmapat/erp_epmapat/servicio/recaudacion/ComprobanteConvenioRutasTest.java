package com.epmapat.erp_epmapat.servicio.recaudacion;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.commons.JasperReportManager;
import com.epmapat.erp_epmapat.jasperReports.utils.JasperReportLoader;
import com.epmapat.erp_epmapat.jasperReports.utils.ReportCache;

class ComprobanteConvenioRutasTest {
    private String totales(JasperReport report) {
        return Arrays.stream(report.getDatasets())
                .filter(dataset -> "FacturaTotales".equals(dataset.getName()))
                .findFirst().orElseThrow().getQuery().getText();
    }

    @Test void todasLasRutasUsanLosTotalesDeLaFuenteVigente() throws Exception {
        String expected = totales(new ReportCache().getCompiled("CompPagoConvenios"));
        assertTrue(expected.contains("CEIL("));
        JasperReportManager manager = new JasperReportManager();
        JasperReport recaudacion = manager.getCompiledReport("CompPagoConvenios");
        assertEquals(expected, totales(recaudacion));
        assertEquals(expected, totales(new JasperReportLoader().load("CompPagoConvenios")));
        assertSame(recaudacion, manager.getCompiledReport("CompPagoConvenios"));
    }
}
