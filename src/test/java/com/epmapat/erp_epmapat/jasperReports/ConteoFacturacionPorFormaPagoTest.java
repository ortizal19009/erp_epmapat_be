package com.epmapat.erp_epmapat.jasperReports;

import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConteoFacturacionPorFormaPagoTest {
    @Test void compilaYGeneraConteoConTotalYPeriodoVacio() throws Exception {
        JasperReport report;
        try (var in = getClass().getResourceAsStream("/reports/ConteoFacturacionPorFormaPago.jrxml")) {
            assertNotNull(in);
            report = JasperCompileManager.compileReport(in);
        }
        Map<String,Object> params = new HashMap<>();
        params.put("desde", java.sql.Date.valueOf("2026-09-01"));
        params.put("hasta", java.sql.Date.valueOf("2026-09-30"));
        params.put("idusuario", 1);
        params.put(JRParameter.REPORT_LOCALE, Locale.US);
        assertEquals(Set.of("desde", "hasta", "idusuario"),
            java.util.Arrays.stream(report.getParameters()).filter(p -> !p.isSystemDefined())
                .map(JRParameter::getName).collect(java.util.stream.Collectors.toSet()));
        Collection<Map<String,?>> rows = new ArrayList<>();
        rows.add(Map.of("formapago",1,"forma","Contado","clasificacion","","anuladas",2L,"eliminadas",4L,"refacturadas",1L,"cantidad",10L,"valor_total",new java.math.BigDecimal("100.25")));
        rows.add(Map.of("formapago",4,"forma","SPI","clasificacion","EFECTIVIZADAS","anuladas",2L,"eliminadas",4L,"refacturadas",1L,"cantidad",3L,"valor_total",new java.math.BigDecimal("30.50")));
        rows.add(Map.of("formapago",4,"forma","SPI","clasificacion","NO EFECTIVIZADAS","anuladas",2L,"eliminadas",4L,"refacturadas",1L,"cantidad",2L,"valor_total",new java.math.BigDecimal("20.00")));
        var print = JasperFillManager.fillReport(report,params,new JRMapCollectionDataSource(rows));
        String text = net.sf.jasperreports.engine.JasperExportManager.exportReportToXml(print);
        assertTrue(text.contains("EFECTIVIZADAS"));
        assertTrue(text.contains("NO EFECTIVIZADAS"));
        assertTrue(text.contains("<![CDATA[15]]>"));
        assertTrue(text.contains("150.75"));
        assertTrue(text.contains("Facturas anuladas: 2"));
        assertTrue(text.contains("Facturas eliminadas: 4"));
        assertTrue(text.contains("Facturas refacturadas: 1"));
        var empty = JasperFillManager.fillReport(report,params,new JREmptyDataSource(0));
        assertFalse(empty.getPages().isEmpty());
        assertTrue(JasperExportManager.exportReportToXml(empty).contains("<![CDATA[0]]>"));
        assertTrue(JasperExportManager.exportReportToPdf(print).length > 0);
        var images = print.getPages().get(0).getElements().stream()
            .filter(e -> e instanceof JRPrintImage).map(e -> (JRPrintImage)e).toList();
        assertEquals(2, images.size(), "Logo y fondo presentes");
        images.forEach(i -> assertNotNull(i.getRenderer()));
        var dir = java.nio.file.Path.of("target", "conteo-preview");
        java.nio.file.Files.createDirectories(dir);
        java.nio.file.Files.write(dir.resolve("conteo.pdf"), JasperExportManager.exportReportToPdf(print));
        var rendered = JasperPrintManager.printPageToImage(print, 0, 1.2f);
        var png = new java.awt.image.BufferedImage(rendered.getWidth(null), rendered.getHeight(null), java.awt.image.BufferedImage.TYPE_INT_RGB);
        var graphics = png.createGraphics(); graphics.drawImage(rendered,0,0,null); graphics.dispose();
        javax.imageio.ImageIO.write(png,"png",dir.resolve("conteo.png").toFile());
    }
}
