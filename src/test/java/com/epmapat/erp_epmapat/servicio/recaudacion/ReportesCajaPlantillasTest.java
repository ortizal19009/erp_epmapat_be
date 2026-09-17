package com.epmapat.erp_epmapat.servicio.recaudacion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import net.sf.jasperreports.engine.JasperCompileManager;
class ReportesCajaPlantillasTest {
    @Test void compilanReportesDeCaja() throws Exception {
        for (String name : new String[]{"FacturasCobradas", "FacturasCobradasRec", "RubrosCobrados", "RubrosCobradosRec", "FacturasCobradasFacilito", "FacturasCobradasFacilitoHoras"}) {
            try (java.io.InputStream in = getClass().getResourceAsStream("/reports/" + name + ".jrxml")) {
                assertNotNull(JasperCompileManager.compileReport(in), name);
            }
        }
    }
}
