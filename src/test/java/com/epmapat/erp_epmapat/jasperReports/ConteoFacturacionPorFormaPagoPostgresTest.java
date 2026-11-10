package com.epmapat.erp_epmapat.jasperReports;

import java.sql.*;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "RESUMEN_TEST_POSTGRES", matches = "true")
class ConteoFacturacionPorFormaPagoPostgresTest {
    @Test void cuentaUnaVezPorFacturaYSumaRubrosEnConjunto() throws Exception {
        try (var db = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:55440/resumen_test", "resumen_test", "");
             var st = db.createStatement()) {
            st.execute("SET statement_timeout = '15s'");
            st.execute("CREATE TEMP TABLE facturas (idfactura bigint, formapago integer, estado integer, fechacobro timestamp, interescobrado numeric, swiva numeric, swmulta boolean, swcondonar boolean, fechatransferencia timestamp, fechaanulacion timestamp, fechaeliminacion timestamp)");
            st.execute("CREATE TEMP TABLE rubroxfac (idfactura_facturas bigint, idrubro_rubros integer, cantidad numeric, valorunitario numeric, estado integer)");
            st.execute("CREATE TEMP TABLE formacobro (idformacobro integer, descripcion text)");
            st.execute("CREATE TEMP TABLE usuarios (idusuario integer, nomusu text)");
            st.execute("INSERT INTO usuarios VALUES (1,'Operador'); INSERT INTO formacobro VALUES (1,'Contado'),(4,'SPI')");
            st.execute("INSERT INTO facturas(idfactura,formapago,estado,fechacobro,interescobrado,swiva,swmulta,swcondonar) SELECT n, CASE WHEN n % 2 = 0 THEN 1 ELSE 4 END, CASE WHEN n % 4 = 1 THEN 3 ELSE 1 END, TIMESTAMP '2026-09-30 23:59:59', 1, 2, false, false FROM generate_series(1,20000) n");
            st.execute("INSERT INTO rubroxfac SELECT n, 10, 1, 5, 1 FROM generate_series(1,20000) n CROSS JOIN generate_series(1,3) r");
            st.execute("INSERT INTO facturas(idfactura,formapago,estado,fechacobro,interescobrado,swiva,swmulta,swcondonar) VALUES (20001,1,1,'2026-10-01',1,2,false,false),(20002,1,1,NULL,1,2,false,false)");
            st.execute("UPDATE facturas SET fechatransferencia = '2026-09-30' WHERE formapago = 4");
            st.execute("CREATE TEMP TABLE lecturas (idlectura bigint, idfactura bigint)");
            st.execute("CREATE TEMP TABLE emisionindividual (idlecturaanterior bigint, idlecturanueva bigint)");
            st.execute("ANALYZE facturas; ANALYZE rubroxfac");
            String sql;
            try (var in = getClass().getResourceAsStream("/reports/ConteoFacturacionPorFormaPago.jrxml")) {
                sql = JRXmlLoader.load(in).getQuery().getText()
                    .replace("$P{desde}", "?").replace("$P{hasta}", "?").replace("$P{idusuario}", "?");
            }
            long inicio = System.nanoTime();
            try (var ps = db.prepareStatement(sql)) {
                ps.setDate(1,Date.valueOf("2026-09-01"));
                ps.setDate(2,Date.valueOf("2026-09-30"));
                ps.setInt(3,1);
                try (var rs = ps.executeQuery()) {
                    long count = 0; java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                    int groups = 0;
                    while (rs.next()) {
                        count += rs.getLong("cantidad");
                        total = total.add(rs.getBigDecimal("valor_total"));
                        assertEquals("Operador",rs.getString("usuarioimpresion"));
                        groups++;
                    }
                    assertEquals(3,groups);
                    assertEquals(20000,count);
                    assertEquals(0,total.compareTo(new java.math.BigDecimal("360000")));
                }
            }
            // Una transferencia pendiente al cierre conserva su fila historica
            // incluso despues de cambiar estado y fecha de cobro en octubre.
            st.execute("INSERT INTO facturas(idfactura,formapago,estado,fechatransferencia,fechacobro,interescobrado,swiva) VALUES (30000,4,3,'2026-09-20','2026-09-20',0,0),(30001,4,3,'2026-08-20','2026-09-20',0,0)");
            st.execute("INSERT INTO rubroxfac VALUES (30000,10,1,123.45,1),(30001,10,1,999,1)");
            var antes = rows(db,sql,"2026-09-01","2026-09-30");
            st.execute("UPDATE facturas SET estado = 1, fechacobro = '2026-10-05' WHERE idfactura = 30000");
            assertEquals(antes,rows(db,sql,"2026-09-01","2026-09-30"));
            var octubre = rows(db,sql,"2026-10-01","2026-10-31");
            assertTrue(octubre.stream().anyMatch(r -> "EFECTIVIZADAS".equals(r.get("clasificacion"))
                && new java.math.BigDecimal("123.45").compareTo((java.math.BigDecimal)r.get("valor_total")) == 0));
            st.execute("INSERT INTO facturas(idfactura,fechaanulacion,fechaeliminacion) VALUES (40000,'2026-09-10','2026-10-01'),(40001,NULL,'2026-09-15'),(40002,NULL,'2026-09-16'),(40003,'2026-07-20',NULL)");
            st.execute("INSERT INTO lecturas VALUES (1,40001),(2,50000),(3,50001); INSERT INTO emisionindividual VALUES (1,2),(1,3)");
            var septiembre = rows(db,sql,"2026-09-01","2026-09-30");
            assertEquals(3,septiembre.size());
            for (var r : septiembre) {
                assertEquals(1L,r.get("anuladas"));
                assertEquals(2L,r.get("eliminadas"));
                assertEquals(1L,r.get("refacturadas"));
            }
            var julio = rows(db,sql,"2026-07-01","2026-07-31");
            assertEquals(1,julio.size());
            assertEquals(0L,julio.get(0).get("cantidad"));
            assertEquals(1L,julio.get(0).get("anuladas"));
            System.out.println("Historico antes/despues de efectivizar, fechas de transferencia, estadisticas y periodo sin cobros: OK");
            System.out.println("Conteo: 20000 facturas / 60000 rubros + controles historicos sin indices: " + ((System.nanoTime()-inicio)/1000000) + " ms");
        }
    }
    private java.util.List<java.util.Map<String,Object>> rows(Connection db, String sql, String desde, String hasta) throws Exception {
        var result = new java.util.ArrayList<java.util.Map<String,Object>>();
        try (var ps = db.prepareStatement(sql)) {
            ps.setDate(1,Date.valueOf(desde)); ps.setDate(2,Date.valueOf(hasta)); ps.setInt(3,1);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var row = new java.util.HashMap<String,Object>();
                    for (String key : java.util.List.of("formapago","clasificacion","cantidad","valor_total","anuladas","eliminadas","refacturadas")) row.put(key,rs.getObject(key));
                    result.add(row);
                }
            }
        }
        return result;
    }

}
