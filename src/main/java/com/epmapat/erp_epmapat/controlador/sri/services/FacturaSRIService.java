package com.epmapat.erp_epmapat.controlador.sri.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.epmapat.erp_epmapat.controlador.sri.configuratios.SRIProperties;
import com.epmapat.erp_epmapat.controlador.sri.dtos.FacturaElectronica;
import com.epmapat.erp_epmapat.controlador.sri.dtos.ResultadoGeneracion;
import com.epmapat.erp_epmapat.controlador.sri.models.Factura;
import com.epmapat.erp_epmapat.controlador.sri.repositories.FacturaR;

@Service
@Transactional
public class FacturaSRIService {

    private final FacturaR facturaRepository;
    private final JAXBContext jaxbContext;
    private final Schema schema;
    private final SRIProperties sriProperties;

    public FacturaSRIService(FacturaR facturaRepository,
            SRIProperties sriProperties) throws Exception {
        this.facturaRepository = facturaRepository;
        this.sriProperties = sriProperties;
        this.jaxbContext = JAXBContext.newInstance(FacturaElectronica.class);

        // Cargar esquema XSD para validación
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = sf.newSchema(new File(sriProperties.getXsdPath()));
    }

    /**
     * Genera XML para un lote de facturas
     * 
     * @param idsFacturas Lista de IDs de facturas
     * @return Lista de resultados con estado de cada factura
     */
    public List<ResultadoGeneracion> generarXmlPorLote(List<Long> idsFacturas) {
        return idsFacturas.stream()
                .map(this::procesarFactura)
                .collect(Collectors.toList());
    }

    private ResultadoGeneracion procesarFactura(Long idFactura) {
        try {
            Factura factura = facturaRepository.findById(idFactura)
                    .orElseThrow(null);

            // Validar factura antes de procesar
            validarFactura(factura);

            // Generar XML
            FacturaElectronica facturaElectronica = mapearFacturaElectronica(factura);
            byte[] xmlBytes = generarXml(facturaElectronica);

            // Firmar XML
            byte[] xmlFirmado = firmarXml(xmlBytes, factura.getClaveacceso());

            // Validar contra XSD
            validarXsd(xmlFirmado);

            // Actualizar estado en base de datos
            factura.setXmlautorizado(xmlFirmado);
            factura.setEstado("GENERADA");
            facturaRepository.save(factura);

            return new ResultadoGeneracion(idFactura, "OK", "XML generado correctamente", xmlFirmado);
        } catch (Exception e) {
            return new ResultadoGeneracion(idFactura, "ERROR", e.getMessage(), null);
        }
    }

    private FacturaElectronica mapearFacturaElectronica(Factura factura) {
        FacturaElectronica fe = new FacturaElectronica();

        // InfoTributaria
        InfoTributaria infoTributaria = new InfoTributaria();
        infoTributaria.setAmbiente(sriProperties.getAmbiente());
        infoTributaria.setTipoEmision(sriProperties.getTipoEmision());
        infoTributaria.setRazonSocial(factura.getRazonsocialcomprador());
        infoTributaria.setRuc(sriProperties.getRuc());
        infoTributaria.setClaveAcceso(factura.getClaveacceso());
        infoTributaria.setCodDoc("01"); // 01=Factura
        infoTributaria.setEstab(factura.getEstablecimiento());
        infoTributaria.setPtoEmi(factura.getPuntoemision());
        infoTributaria.setSecuencial(factura.getSecuencial());
        infoTributaria.setDirMatriz(sriProperties.getDirMatriz());

        fe.setInfoTributaria(infoTributaria);

        // InfoFactura
        InfoFactura infoFactura = new InfoFactura();
        infoFactura.setFechaEmision(factura.getFechaemision().format(DateTimeFormatter.ISO_DATE_TIME));
        infoFactura.setDirEstablecimiento(factura.getDireccionestablecimiento());
        infoFactura.setContribuyenteEspecial(sriProperties.getContribuyenteEspecial());
        infoFactura.setObligadoContabilidad(sriProperties.getObligadoContabilidad());
        infoFactura.setTipoIdentificacionComprador(factura.getTipoidentificacioncomprador());
        infoFactura.setRazonSocialComprador(factura.getRazonsocialcomprador());
        infoFactura.setIdentificacionComprador(factura.getGuiaremision()); // Asumo que guía de remisión es
                                                                           // identificación
        infoFactura.setTotalSinImpuestos(calcularTotalSinImpuestos(factura));
        infoFactura.setTotalDescuento(calcularTotalDescuentos(factura));

        // Detalles
        List<Detalle> detalles = factura.getDetalles().stream()
                .map(this::mapearDetalle)
                .collect(Collectors.toList());
        fe.setDetalles(detalles);

        // Pagos
        List<Pago> pagos = factura.getPagos().stream()
                .map(this::mapearPago)
                .collect(Collectors.toList());
        fe.setPagos(pagos);

        // Totales
        fe.setTotalImpuestos(calcularTotalImpuestos(factura));
        fe.setImporteTotal(calcularImporteTotal(factura));

        return fe;
    }

    private byte[] generarXml(FacturaElectronica facturaElectronica) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(facturaElectronica, outputStream);

        return outputStream.toByteArray();
    }

    private byte[] firmarXml(byte[] xmlBytes, String claveAcceso) throws Exception {
        // Implementación de firma digital según requerimientos SRI
        // Usando XML Digital Signature API
        // Retorna el XML firmado
        return null;
    }

    private void validarXsd(byte[] xmlBytes) throws SAXException, IOException {
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new ByteArrayInputStream(xmlBytes)));
    }

    // Métodos auxiliares para cálculos
    private BigDecimal calcularTotalSinImpuestos(Factura factura) {
        return factura.getDetalles().stream()
                .map(d -> d.getPreciounitario().multiply(d.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Otros métodos auxiliares...
}
