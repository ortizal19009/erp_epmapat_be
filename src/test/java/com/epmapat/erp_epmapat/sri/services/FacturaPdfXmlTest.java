package com.epmapat.erp_epmapat.sri.services;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla15R;
class FacturaPdfXmlTest {
    static final String CLAVE = "1509202601176000000000120010010000001231234567811";
    static final String FACTURA = "<factura><infoTributaria><razonSocial>EMPRESA PRUEBA</razonSocial><ruc>1760000000001</ruc><claveAcceso>"+CLAVE+"</claveAcceso><estab>001</estab><ptoEmi>001</ptoEmi><secuencial>000000123</secuencial><ambiente>2</ambiente><dirMatriz>Direccion matriz</dirMatriz></infoTributaria><infoFactura><fechaEmision>15/09/2026</fechaEmision><razonSocialComprador>CLIENTE PRUEBA</razonSocialComprador><identificacionComprador>9999999999999</identificacionComprador><direccionComprador>Direccion cliente</direccionComprador><totalSinImpuestos>10.00</totalSinImpuestos><totalDescuento>0.00</totalDescuento><importeTotal>10.00</importeTotal><propina>0</propina><totalConImpuestos><totalImpuesto><codigo>2</codigo><codigoPorcentaje>0</codigoPorcentaje><baseImponible>10.00</baseImponible><valor>0</valor></totalImpuesto></totalConImpuestos><pagos><pago><formaPago>01</formaPago><total>10.00</total></pago></pagos></infoFactura><detalles><detalle><codigoPrincipal>1</codigoPrincipal><descripcion>AGUA PRUEBA</descripcion><cantidad>2</cantidad><precioUnitario>5.00</precioUnitario><precioTotalSinImpuesto>10.00</precioTotalSinImpuesto></detalle></detalles></factura>";
    static String sobre(String contenido) { return "<autorizacion><estado>AUTORIZADO</estado><numeroAutorizacion>AUTORIZACION-PRUEBA</numeroAutorizacion><fechaAutorizacion>2026-09-15T12:00:00</fechaAutorizacion><comprobante>"+contenido+"</comprobante></autorizacion>"; }
    @Test void conservaSobreConCdataYXmlEscapado() throws Exception {
        for (String contenido : new String[]{"<![CDATA["+FACTURA+"]]>",FACTURA.replace("<","&lt;").replace(">","&gt;"),FACTURA}) {
            FacturaPdfXml xml = FacturaPdfXml.parse(sobre(contenido));
            assertEquals("AUTORIZACION-PRUEBA",xml.numeroAutorizacion);
            assertEquals("2026-09-15T12:00:00",xml.fechaAutorizacion);
            assertEquals(CLAVE,FacturaPdfXml.text(xml.factura,"claveAcceso"));
        }
    }
    @Test void soportaNamespacesYNoInventaAutorizacion() throws Exception {
        String factura = FACTURA.replaceAll("<(?!/)([A-Za-z]+)","<f:$1").replaceAll("</([A-Za-z]+)","</f:$1").replace("<f:factura>","<f:factura xmlns:f='urn:prueba'>");
        FacturaPdfXml xml = FacturaPdfXml.parse(factura);
        assertEquals(CLAVE,FacturaPdfXml.text(xml.factura,"claveAcceso"));
        assertEquals("",xml.numeroAutorizacion);
    }
    @Test void rechazaClaveAusente() {
        assertThrows(IllegalArgumentException.class,()->FacturaPdfXml.parse(FACTURA.replace(CLAVE,"")));
    }
    @Test void pdfImprimeClaveAutorizacionFechaYDetalle() throws Exception {
        XmlToPdfService service = new XmlToPdfService();
        Tabla15R tabla = mock(Tabla15R.class); when(tabla.getNombre("01")).thenReturn("SIN SISTEMA FINANCIERO");
        ReflectionTestUtils.setField(service,"tabla15r",tabla);
        String xml = sobre("<![CDATA["+FACTURA+"]]>" );
        for (byte[] bytes : new byte[][] {service.generarFacturaPDF(xml).toByteArray(),service.generarFacturaPDF_v2(xml).toByteArray()}) {
        Path output = Path.of("target/pdf-check/factura-prueba.pdf"); Files.createDirectories(output.getParent()); Files.write(output,bytes);
        try (PDDocument pdf = PDDocument.load(bytes)) {
            javax.imageio.ImageIO.write(new org.apache.pdfbox.rendering.PDFRenderer(pdf).renderImageWithDPI(0, 130), "png", output.resolveSibling("factura-prueba.png").toFile());
            String text = new PDFTextStripper().getText(pdf);
            assertTrue(text.contains(CLAVE)); assertTrue(text.contains("AUTORIZACION-PRUEBA"));
            assertTrue(text.contains("2026-09-15T12:00:00")); assertTrue(text.contains("AGUA PRUEBA"));
            assertTrue(text.contains("10.00"));
            assertFalse(text.contains("null"));
            assertTrue(text.contains("Precio Unitario"));
        }
        }
    }
}
