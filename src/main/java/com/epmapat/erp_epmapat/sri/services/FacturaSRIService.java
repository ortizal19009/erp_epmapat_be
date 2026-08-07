package com.epmapat.erp_epmapat.sri.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.sri.exceptions.FacturaElectronicaException;
import com.epmapat.erp_epmapat.sri.interfaces.TotalSinImpuestos;
import com.epmapat.erp_epmapat.sri.models.Comprobante;
import com.epmapat.erp_epmapat.sri.models.Detalle;
import com.epmapat.erp_epmapat.sri.models.Factura;
import com.epmapat.erp_epmapat.sri.models.FacturaDetalle;
import com.epmapat.erp_epmapat.sri.models.FacturaDetalleImpuesto;
import com.epmapat.erp_epmapat.sri.models.InfoFactura;
import com.epmapat.erp_epmapat.sri.models.InfoTributaria;
import com.epmapat.erp_epmapat.sri.models.TotalConImpuestos;
import com.epmapat.erp_epmapat.sri.models.Detalle.Impuesto;
import com.epmapat.erp_epmapat.sri.models.TotalConImpuestos.TotalImpuesto;
import com.epmapat.erp_epmapat.sri.repositories.FacturaDetalleR;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FacturaSRIService {
    private static final BigDecimal CIEN = new BigDecimal("100");

    @Autowired
    private DefinirR definirR;

    @Autowired
    private ClaveAccesoGenerator claveAccesoGenerator;

    @Autowired
    private FacturaDetalleR fDetalleR;

    private final String VERSION = "1.1.0";

    public String generarXmlFactura(Factura factura) throws FacturaElectronicaException {
        try {
            List<Detalle> detalles = mapearDetalles(factura.getDetalles());
            Comprobante comprobante = new Comprobante();
            comprobante.setVersion(VERSION);
            comprobante.setId("comprobante");
            comprobante.setInfoTributaria(crearInfoTributaria(factura));
            comprobante.setInfoFactura(crearInfoFactura(factura, detalles));
            comprobante.setDetalles(detalles);
            return convertirObjetoAXml(comprobante);
        } catch (Exception e) {
            throw new FacturaElectronicaException("Error al generar XML para el SRI", e);
        }
    }

    public static void saveXml(String xmlContent, String filePath) {
        try {
            Files.write(Paths.get(filePath), xmlContent.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InfoTributaria crearInfoTributaria(Factura factura) {
        Definir def = getDefinir();
        String claveAcceso = factura.getClaveacceso() == null
                ? claveAccesoGenerator.generarClaveAcceso(factura, def)
                : factura.getClaveacceso();

        InfoTributaria infoTributaria = new InfoTributaria();
        infoTributaria.setAmbiente(def.getTipoambiente());
        infoTributaria.setTipoEmision((byte) 1);
        infoTributaria.setRazonSocial(def.getRazonsocial());
        infoTributaria.setNombreComercial(def.getNombrecomercial());
        infoTributaria.setRuc(def.getRuc());
        infoTributaria.setClaveAcceso(claveAcceso);
        infoTributaria.setCodDoc("01");
        infoTributaria.setEstab(factura.getEstablecimiento());
        infoTributaria.setPtoEmi(factura.getPuntoemision());
        infoTributaria.setSecuencial(factura.getSecuencial());
        infoTributaria.setDirMatriz(factura.getDireccionestablecimiento());
        return infoTributaria;
    }

    private InfoFactura crearInfoFactura(Factura factura, List<Detalle> detalles) {
        BigDecimal totalSinImpuestos = calcularTotalSinImpuestos(detalles);
        BigDecimal totalDescuento = calcularTotalDescuento(detalles);
        TotalConImpuestos totalConImpuestos = crearTotalConImpuestos(detalles);
        BigDecimal totalImpuestos = totalConImpuestos.getTotalImpuestos() == null
                ? BigDecimal.ZERO
                : totalConImpuestos.getTotalImpuestos().stream()
                        .map(TotalImpuesto::getValor)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        InfoFactura infoFactura = new InfoFactura();
        infoFactura.setFechaEmision(formatToDDMMYYYY(factura.getFechaemision()));
        infoFactura.setObligadoContabilidad("SI");
        infoFactura.setTipoIdentificacionComprador(factura.getTipoidentificacioncomprador());
        infoFactura.setRazonSocialComprador(factura.getRazonsocialcomprador());
        infoFactura.setIdentificacionComprador(factura.getIdentificacioncomprador());
        infoFactura.setDireccionComprador(factura.getDireccioncomprador());
        infoFactura.setTotalSinImpuestos(totalSinImpuestos.setScale(2, RoundingMode.HALF_UP));
        infoFactura.setTotalDescuento(totalDescuento.setScale(2, RoundingMode.HALF_UP));
        infoFactura.setTotalConImpuestos(totalConImpuestos);
        infoFactura.setPropina(BigDecimal.ZERO);
        infoFactura.setImporteTotal(totalSinImpuestos.add(totalImpuestos)
                .setScale(2, RoundingMode.HALF_UP));
        infoFactura.setMoneda("DOLAR");
        return infoFactura;
    }

    private List<Detalle> mapearDetalles(List<FacturaDetalle> detallesFactura) {
        List<FacturaDetalle> detalles = detallesFactura == null ? Collections.emptyList() : detallesFactura;
        BigDecimal tarifaIva = obtenerTarifaIva();
        Set<String> codigosConsolidar = Set.of("1006", "1007");

        List<FacturaDetalle> aConsolidar = detalles.stream()
                .filter(d -> d.getCodigoprincipal() != null && codigosConsolidar.contains(d.getCodigoprincipal().trim()))
                .collect(Collectors.toList());

        List<FacturaDetalle> normales = detalles.stream()
                .filter(d -> d.getCodigoprincipal() == null || !codigosConsolidar.contains(d.getCodigoprincipal().trim()))
                .collect(Collectors.toList());

        List<Detalle> resultado = normales.stream().map(d -> {
            Detalle detalle = new Detalle();
            detalle.setCodigoPrincipal(d.getCodigoprincipal());
            detalle.setDescripcion(d.getDescripcion());
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPreciounitario());
            detalle.setDescuento(d.getDescuento());
            detalle.setPrecioTotalSinImpuesto(
                    calcularPrecioTotalSinImpuesto(d.getCantidad(), d.getPreciounitario(), d.getDescuento()));

            List<FacturaDetalleImpuesto> impuestos = d.getImpuestos() == null ? Collections.emptyList() : d.getImpuestos();

            detalle.setImpuestos(impuestos.stream().map(i -> {
                Impuesto impuesto = new Impuesto();
                impuesto.setCodigo(i.getCodigoimpuesto());
                impuesto.setCodigoPorcentaje(i.getCodigoporcentaje());
                impuesto.setTarifa(obtenerTarifaParaCodigo(i.getCodigoimpuesto(), i.getCodigoporcentaje(), tarifaIva));
                impuesto.setBaseImponible(i.getBaseimponible());
                impuesto.setValor(calcularValorImpuesto(i.getCodigoimpuesto(), i.getCodigoporcentaje(),
                        i.getBaseimponible(), tarifaIva));
                return impuesto;
            }).collect(Collectors.toList()));

            return detalle;
        }).collect(Collectors.toList());

        if (!aConsolidar.isEmpty()) {
            BigDecimal precioUnitarioTotal = aConsolidar.stream()
                    .map(FacturaDetalle::getPreciounitario)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal descuentoTotal = aConsolidar.stream()
                    .map(FacturaDetalle::getDescuento)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            FacturaDetalleImpuesto impRef = aConsolidar.stream()
                    .filter(d -> d.getImpuestos() != null && !d.getImpuestos().isEmpty())
                    .map(d -> d.getImpuestos().get(0))
                    .findFirst()
                    .orElse(null);

            String codigoImp = impRef != null ? impRef.getCodigoimpuesto() : null;
            String codigoPorc = impRef != null ? impRef.getCodigoporcentaje() : null;

            BigDecimal baseImponibleTotal = aConsolidar.stream()
                    .flatMap(d -> d.getImpuestos() == null ? Stream.empty() : d.getImpuestos().stream())
                    .map(FacturaDetalleImpuesto::getBaseimponible)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Impuesto impuestoUnico = new Impuesto();
            impuestoUnico.setCodigo(codigoImp);
            impuestoUnico.setCodigoPorcentaje(codigoPorc);
            impuestoUnico.setTarifa(obtenerTarifaParaCodigo(codigoImp, codigoPorc, tarifaIva));
            impuestoUnico.setBaseImponible(baseImponibleTotal);
            impuestoUnico.setValor(calcularValorImpuesto(codigoImp, codigoPorc, baseImponibleTotal, tarifaIva));

            Detalle consolidado = new Detalle();
            consolidado.setCodigoPrincipal("1004");
            consolidado.setDescripcion("Conservación de fuentes");
            consolidado.setCantidad(BigDecimal.ONE);
            consolidado.setPrecioUnitario(precioUnitarioTotal);
            consolidado.setDescuento(descuentoTotal);
            consolidado.setPrecioTotalSinImpuesto(
                    calcularPrecioTotalSinImpuesto(BigDecimal.ONE, precioUnitarioTotal, descuentoTotal));
            consolidado.setImpuestos(List.of(impuestoUnico));
            resultado.add(consolidado);
        }

        return resultado;
    }

    public static String formatToDDMMYYYY(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        return dateTime.format(formatter);
    }

    private TotalConImpuestos crearTotalConImpuestos(List<Detalle> detallesFactura) {
        TotalConImpuestos totalConImpuestos = new TotalConImpuestos();
        BigDecimal tarifaIva = obtenerTarifaIva();
        List<Detalle> detalles = detallesFactura == null ? Collections.emptyList() : detallesFactura;

        Map<String, ResumenImpuesto> totalesPorImpuesto = detalles.stream()
                .flatMap(d -> {
                    List<Impuesto> impuestos = d.getImpuestos() == null
                            ? Collections.emptyList()
                            : d.getImpuestos();
                    return impuestos.stream();
                })
                .collect(Collectors.groupingBy(
                        i -> i.getCodigo() + "|" + i.getCodigoPorcentaje(),
                        Collectors.collectingAndThen(Collectors.toList(), items -> {
                            Impuesto ref = items.get(0);
                            BigDecimal base = items.stream()
                                    .map(Impuesto::getBaseImponible)
                                    .filter(Objects::nonNull)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            return new ResumenImpuesto(
                                    ref.getCodigo(),
                                    ref.getCodigoPorcentaje(),
                                    base,
                                    calcularValorImpuesto(ref.getCodigo(), ref.getCodigoPorcentaje(), base, tarifaIva));
                        })));

        List<TotalImpuesto> totales = totalesPorImpuesto.values().stream()
                .map(resumen -> {
                    TotalImpuesto totalImpuesto = new TotalImpuesto();
                    totalImpuesto.setCodigo(resumen.codigoImpuesto());
                    totalImpuesto.setCodigoPorcentaje(resumen.codigoPorcentaje());
                    totalImpuesto.setBaseImponible(resumen.baseImponible());
                    totalImpuesto.setValor(resumen.valor());
                    return totalImpuesto;
                }).collect(Collectors.toList());

        totalConImpuestos.setTotalImpuestos(totales);
        return totalConImpuestos;
    }

    private String convertirObjetoAXml(Comprobante comprobante) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Comprobante.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter writer = new StringWriter();
        marshaller.marshal(comprobante, writer);
        return writer.toString();
    }

    private Definir getDefinir() {
        Long id = 1L;
        return definirR.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no encontrado con ID: " + id));
    }

    private BigDecimal obtenerTarifaIva() {
        Definir definir = getDefinir();
        if (definir.getPorciva() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal tarifa = definir.getPorciva();
        if (tarifa.compareTo(BigDecimal.ZERO) > 0 && tarifa.compareTo(BigDecimal.ONE) <= 0) {
            tarifa = tarifa.multiply(CIEN);
        }
        return tarifa.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal obtenerTarifaParaCodigo(String codigoImpuesto, String codigoPorcentaje, BigDecimal tarifaIva) {
        if (!"2".equals(codigoImpuesto) || codigoPorcentaje == null || "0".equals(codigoPorcentaje)) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return tarifaIva;
    }

    private BigDecimal calcularValorImpuesto(String codigoImpuesto, String codigoPorcentaje, BigDecimal baseImponible,
            BigDecimal tarifaIva) {
        if (baseImponible == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal tarifa = obtenerTarifaParaCodigo(codigoImpuesto, codigoPorcentaje, tarifaIva);
        if (BigDecimal.ZERO.compareTo(tarifa) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return baseImponible.multiply(tarifa).divide(CIEN, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPrecioTotalSinImpuesto(BigDecimal cantidad, BigDecimal precioUnitario, BigDecimal descuento) {
        BigDecimal qty = cantidad == null ? BigDecimal.ONE : cantidad;
        BigDecimal precio = precioUnitario == null ? BigDecimal.ZERO : precioUnitario;
        BigDecimal desc = descuento == null ? BigDecimal.ZERO : descuento;
        return qty.multiply(precio).subtract(desc).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularTotalSinImpuestos(List<Detalle> detalles) {
        if (detalles == null) {
            return BigDecimal.ZERO;
        }
        return detalles.stream()
                .map(Detalle::getPrecioTotalSinImpuesto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularTotalDescuento(List<Detalle> detalles) {
        if (detalles == null) {
            return BigDecimal.ZERO;
        }
        return detalles.stream()
                .map(Detalle::getDescuento)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void processAndSendInvoice(String toEmail, String subject, String body, MultipartFile xmlFile)
            throws Exception {
        byte[] xmlData = xmlFile.getBytes();
        String attachmentName = "factura_" + System.currentTimeMillis() + ".pdf";
    }

    private record ResumenImpuesto(String codigoImpuesto, String codigoPorcentaje, BigDecimal baseImponible,
            BigDecimal valor) {
    }
}
