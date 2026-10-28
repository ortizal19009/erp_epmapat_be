package com.epmapat.erp_epmapat.jasperReports;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.*;
import java.io.*;
import java.util.zip.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.jasperReports.services.ReporteExportService;
import com.epmapat.erp_epmapat.jasperReports.services.ReporteSqlService;

class CajaExcelNumerosTest {
    JasperPrint ejemplo() throws Exception {
        JasperDesign d=new JasperDesign(); d.setName("ImportesCaja");d.setPageWidth(400);d.setPageHeight(200);
        d.setColumnWidth(360);d.setLeftMargin(20);d.setRightMargin(20);d.setTopMargin(20);d.setBottomMargin(20);
        JRDesignParameter importe=new JRDesignParameter();importe.setName("importe");importe.setValueClass(BigDecimal.class);d.addParameter(importe);
        JRDesignParameter codigo=new JRDesignParameter();codigo.setName("codigo");codigo.setValueClass(String.class);d.addParameter(codigo);
        JRDesignBand banda=new JRDesignBand();banda.setHeight(30);
        JRDesignTextField monto=new JRDesignTextField();monto.setX(0);monto.setY(0);monto.setWidth(150);monto.setHeight(25);
        monto.setPattern("#,##0.00 '$'");monto.setExpression(new JRDesignExpression("$P{importe}"));banda.addElement(monto);
        JRDesignTextField identificacion=new JRDesignTextField();identificacion.setX(170);identificacion.setY(0);identificacion.setWidth(150);identificacion.setHeight(25);
        identificacion.setExpression(new JRDesignExpression("$P{codigo}"));banda.addElement(identificacion);
        ((JRDesignSection)d.getDetailSection()).addBand(banda);
        return JasperFillManager.fillReport(JasperCompileManager.compileReport(d),new HashMap<>(Map.of("importe",new BigDecimal("1234.56"),"codigo","0012345678")),new JREmptyDataSource(1));
    }
    void verificar(byte[] excel) throws Exception {
        Map<String,String> archivos=new HashMap<>();
        try(ZipInputStream zip=new ZipInputStream(new ByteArrayInputStream(excel))) {
            ZipEntry e;while((e=zip.getNextEntry())!=null)archivos.put(e.getName(),new String(zip.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8));
        }
        String hoja=archivos.get("xl/worksheets/sheet1.xml");assertNotNull(hoja);
        var dom=javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(hoja.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        var celdas=dom.getElementsByTagName("c");boolean numerico=false;
        for(int i=0;i<celdas.getLength();i++) {
            var celda=(org.w3c.dom.Element)celdas.item(i);
            if(celda.getTextContent().equals("1234.56")) {
                assertNotEquals("s",celda.getAttribute("t"));assertNotEquals("inlineStr",celda.getAttribute("t"));numerico=true;
            }
        }
        assertTrue(numerico,"El importe debe ser numerico para poder sumar en Excel");
        assertTrue(archivos.get("xl/sharedStrings.xml").contains("0012345678"),"Conservar ceros iniciales de identificaciones");
    }
    @Test void exportadorPrincipalConservaNumerosYTextos() throws Exception {
        verificar(new ReporteExportService().export("xlsx",ejemplo()).toByteArray());
    }
    @Test void exportadorSqlConservaNumerosYTextos() throws Exception {
        verificar(new ReporteSqlService(null,null).export("xlsx",ejemplo()).toByteArray());
    }
}
