package com.epmapat.erp_epmapat.controlador.sri.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.epmapat.erp_epmapat.controlador.sri.configuratios.SRIProperties;
import com.epmapat.erp_epmapat.controlador.sri.dtos.*;
import com.epmapat.erp_epmapat.controlador.sri.exceptions.FacturaElectronicaException;
import com.epmapat.erp_epmapat.controlador.sri.models.Factura;
import com.epmapat.erp_epmapat.controlador.sri.repositories.FacturaR;

@Service
@Transactional
public class FacturaSRIService {

    private static final Logger logger = LoggerFactory.getLogger(FacturaSRIService.class);
    
    private final FacturaR facturaRepository;
    private final JAXBContext jaxbContext;
    private final Schema schema;
    private final SRIProperties sriProperties;

    public FacturaSRIService(FacturaR facturaRepository, SRIProperties sriProperties) 
            throws FacturaElectronicaException {
        this.facturaRepository = facturaRepository;
        this.sriProperties = sriProperties;
        
        try {
            this.jaxbContext = JAXBContext.newInstance(FacturaElectronica.class);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.schema = sf.newSchema(new File(sriProperties.getXsdPath()));
        } catch (Exception e) {
            throw new FacturaElectronicaException("Error inicializando servicio de facturación electrónica", e);
        }
    }

    public List<ResultadoGeneracion> generarXmlPorLote(List<Long> idsFacturas) {
        return idsFacturas.stream()
                .map(this::procesarFactura)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ResultadoGeneracion procesarFactura(Long idFactura) {
        try {
            Factura factura = facturaRepository.findById(idFactura)
                    .orElseThrow(() -> new FacturaElectronicaException("Factura no encontrada con ID: " + idFactura));

            validarFactura(factura);
            
            FacturaElectronica facturaElectronica = mapearFacturaElectronica(factura);
            byte[] xmlBytes = generarXml(facturaElectronica);
            byte[] xmlFirmado = firmarXml(xmlBytes, factura.getClaveacceso());
            
            validarXsd(xmlFirmado);

            actualizarEstadoFactura(factura, xmlFirmado, "GENERADA");

            return ResultadoGeneracion.(idFactura, "XML generado correctamente", xmlFirmado);
            
        } catch (Exception e) {
            logger.error("Error procesando factura ID: " + idFactura, e);
            return ResultadoGeneracion.error(idFactura, e.getMessage());
        }
    }

    private FacturaElectronica mapearFacturaElectronica(Factura factura) {
        FacturaElectronica fe = new FacturaElectronica();
        
        // InfoTributaria
        fe.setInfoTributaria(mapearInfoTributaria(factura));
        
        // InfoFactura
        fe.setInfoFactura(mapearInfoFactura(factura));
        
        // Detalles
        fe.setDetalles(mapearDetalles(factura));
        
        // Pagos
        fe.setPagos(mapearPagos(factura));
        
        // Totales
        fe.setTotalImpuestos(calcularTotalImpuestos(factura));
        fe.setImporteTotal(calcularImporteTotal(factura));
        
        return fe;
    }

    private InfoTributaria mapearInfoTributaria(Factura factura) {
        InfoTributaria it = new InfoTributaria();
        it.setAmbiente(sriProperties.getAmbiente());
        it.setTipoEmision(sriProperties.getTipoEmision());
        it.setRazonSocial(factura.getRazonsocialcomprador());
        it.setRuc(sriProperties.getRuc());
        it.setClaveAcceso(factura.getClaveacceso());
        it.setCodDoc("01"); // 01=Factura
        it.setEstab(factura.getEstablecimiento());
        it.setPtoEmi(factura.getPuntoemision());
        it.setSecuencial(factura.getSecuencial());
        it.setDirMatriz(sriProperties.getDirMatriz());
        return it;
    }

    private InfoFactura mapearInfoFactura(Factura factura) {
        InfoFactura info = new InfoFactura();
        info.setFechaEmision(factura.getFechaemision().format(DateTimeFormatter.ISO_DATE_TIME));
        info.setDirEstablecimiento(factura.getDireccionestablecimiento());
        info.setContribuyenteEspecial(sriProperties.getContribuyenteEspecial());
        info.setObligadoContabilidad(sriProperties.getObligadoContabilidad());
        info.setTipoIdentificacionComprador(factura.getTipoidentificacioncomprador());
        info.setRazonSocialComprador(factura.getRazonsocialcomprador());
        info.setIdentificacionComprador(factura.getGuiaremision());
        info.setTotalSinImpuestos(calcularTotalSinImpuestos(factura));
        info.setTotalDescuento(calcularTotalDescuentos(factura));
        return info;
    }

    private List<Detalle> mapearDetalles(Factura factura) {
        return factura.getDetalles().stream()
                .map(d -> new Detalle(
                        d.getCodigoprincipal(),
                        d.getDescripcion(),
                        d.getCantidad(),
                        d.getPreciounitario(),
                        d.getDescuento(),
                        d.getPreciototalsinimpuesto(),
                        d.getImpuestos()))
                .collect(Collectors.toList());
    }

    private List<Pago> mapearPagos(Factura factura) {
        return factura.getPagos().stream()
                .map(p -> new Pago(
                        p.getFormapago(),
                        p.getTotal(),
                        p.getPlazo(),
                        p.getUnidadtiempo()))
                .collect(Collectors.toList());
    }

    private void actualizarEstadoFactura(Factura factura, byte[] xmlFirmado, String estado) {
        factura.setXmlautorizado(xmlFirmado);
        factura.setEstado(estado);
        facturaRepository.save(factura);
    }

    private void validarFactura(Factura factura) throws FacturaElectronicaException {
        if (factura == null) {
            throw new FacturaElectronicaException("La factura no puede ser nula");
        }
        
        if (factura.getClaveacceso() == null || factura.getClaveacceso().isEmpty()) {
            throw new FacturaElectronicaException("Clave de acceso es requerida");
        }
        
        // Agregar más validaciones según necesidades
    }

    // Implementaciones de cálculos
    private BigDecimal calcularTotalSinImpuestos(Factura factura) {
        return factura.getDetalles().stream()
                .map(d -> d.getPreciounitario().multiply(d.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularTotalDescuentos(Factura factura) {
        return factura.getDetalles().stream()
                .map(d -> Optional.ofNullable(d.getDescuento()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularTotalImpuestos(Factura factura) {
        return factura.getDetalles().stream()
                .flatMap(d -> d.getImpuestos().stream())
                .map(i -> i.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularImporteTotal(Factura factura) {
        BigDecimal subtotal = calcularTotalSinImpuestos(factura);
        BigDecimal descuentos = calcularTotalDescuentos(factura);
        BigDecimal impuestos = calcularTotalImpuestos(factura);
        return subtotal.subtract(descuentos).add(impuestos);
    }

    // Resto de métodos (generarXml, firmarXml, validarXsd) permanecen iguales
    // ...
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

}
