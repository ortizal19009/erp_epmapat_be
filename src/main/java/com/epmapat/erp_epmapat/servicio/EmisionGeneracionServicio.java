package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.epmapat.erp_epmapat.DTO.EmisionGeneracionResponseDTO;
import com.epmapat.erp_epmapat.DTO.EmisionGeneracionRutaDetalleDTO;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Modulos;
import com.epmapat.erp_epmapat.modelo.Novedad;
import com.epmapat.erp_epmapat.modelo.Rutas;
import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.RutasR;
import com.epmapat.erp_epmapat.repositorio.RutasxemisionR;

@Service
public class EmisionGeneracionServicio {

    private static final Logger log = LoggerFactory.getLogger(EmisionGeneracionServicio.class);
    private static final Long MODULO_LECTURAS = 4L;
    private static final Long NOVEDAD_POR_DEFECTO = 1L;
    private static final ZoneId APP_ZONE = ZoneId.systemDefault();
    private static final long RUTA_LENTA_MS = 5_000L;

    private final EmisionesR emisionesR;
    private final RutasR rutasR;
    private final RutasxemisionR rutasxemisionR;
    private final AbonadosR abonadosR;
    private final LecturasR lecturasR;
    private final FacturasR facturasR;
    private final RubroxfacR rubroxfacR;
    private final TransactionTemplate transactionTemplate;

    public EmisionGeneracionServicio(
            EmisionesR emisionesR,
            RutasR rutasR,
            RutasxemisionR rutasxemisionR,
            AbonadosR abonadosR,
            LecturasR lecturasR,
            FacturasR facturasR,
            RubroxfacR rubroxfacR,
            PlatformTransactionManager transactionManager) {
        this.emisionesR = emisionesR;
        this.rutasR = rutasR;
        this.rutasxemisionR = rutasxemisionR;
        this.abonadosR = abonadosR;
        this.lecturasR = lecturasR;
        this.facturasR = facturasR;
        this.rubroxfacR = rubroxfacR;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public EmisionGeneracionResponseDTO generarPendientes(Long idemision, Long idusuario) {
        return procesarApertura(idemision, idusuario, true);
    }

    public EmisionGeneracionResponseDTO validarApertura(Long idemision) {
        return procesarApertura(idemision, 0L, false);
    }

    public Map<String, Object> generarFacturasCabeceraUltimaEmisionAbierta(Long idusuario) {
        Emisiones emision = emisionesR.findFirstByEstadoOrderByEmisionDesc(0);
        if (emision == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No existe una emision abierta para reparar.");
        }

        LocalDate fechaEmision = parseFechaEmision(emision.getEmision());
        List<Rutasxemision> rutas = rutasxemisionR.findByIdemision(emision.getIdemision());
        int rutasRecorridas = 0;
        int lecturasRevisadas = 0;
        int facturasCreadas = 0;

        for (Rutasxemision ruta : rutas) {
            rutasRecorridas++;
            List<Lecturas> lecturas = lecturasR.findByIdrutaxemision(ruta.getIdrutaxemision());
            for (Lecturas lectura : lecturas) {
                lecturasRevisadas++;
                if (lectura.getIdfactura() != null) {
                    continue;
                }

                Abonados abonado = lectura.getIdabonado_abonados();
                if (abonado == null || abonado.getIdabonado() == null) {
                    continue;
                }

                Facturas factura = crearFactura(abonado, fechaEmision, idusuario);
                lectura.setIdfactura(factura.getIdfactura());
                lecturasR.save(lectura);
                facturasCreadas++;
            }
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("success", true);
        respuesta.put("idemision", emision.getIdemision());
        respuesta.put("emision", emision.getEmision());
        respuesta.put("rutasRecorridas", rutasRecorridas);
        respuesta.put("lecturasRevisadas", lecturasRevisadas);
        respuesta.put("facturasCreadas", facturasCreadas);
        return respuesta;
    }

    private EmisionGeneracionResponseDTO procesarApertura(Long idemision, Long idusuario, boolean generarFaltantes) {
        Emisiones emision = emisionesR.findById(idemision)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe la emision " + idemision));

        LocalDate fechaEmisionLocal = parseFechaEmision(emision.getEmision());
        Date fechaEmision = Date.from(fechaEmisionLocal.atStartOfDay(APP_ZONE).toInstant());
        Date fechaCreacion = new Date();

        List<Rutas> rutasActivas = rutasR.findAllActive();
        List<EmisionGeneracionRutaDetalleDTO> detalleRutas = new ArrayList<>();
        List<RutaTiempo> tiemposRuta = new ArrayList<>();

        long rutasExistentes = 0;
        long rutasCreadas = 0;
        long rutasCompletas = 0;
        long totalLecturasEsperadas = 0;
        long lecturasExistentes = 0;
        long lecturasCreadas = 0;
        String accion = generarFaltantes ? "Generacion" : "Validacion";

        for (Rutas ruta : rutasActivas) {
            long inicioRuta = System.currentTimeMillis();
            EmisionGeneracionRutaDetalleDTO detalleRuta = procesarRuta(
                    emision,
                    ruta,
                    idusuario,
                    fechaEmisionLocal,
                    fechaEmision,
                    fechaCreacion,
                    generarFaltantes);
            long duracionRuta = System.currentTimeMillis() - inicioRuta;

            if (detalleRuta == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "No se pudo generar el detalle de la ruta " + ruta.getCodigo());
            }

            tiemposRuta.add(new RutaTiempo(
                    detalleRuta.getCodigoRuta(),
                    detalleRuta.getNombreRuta(),
                    duracionRuta,
                    detalleRuta.getAbonadosEsperados(),
                    detalleRuta.getLecturasCreadas(),
                    detalleRuta.getLecturasPendientes()));

            if (duracionRuta >= RUTA_LENTA_MS) {
                log.warn(
                        "{} emision {} ruta {} - {} tardo {} ms. esperadas={}, creadas={}, pendientes={}",
                        accion,
                        emision.getEmision(),
                        detalleRuta.getCodigoRuta(),
                        detalleRuta.getNombreRuta(),
                        duracionRuta,
                        detalleRuta.getAbonadosEsperados(),
                        detalleRuta.getLecturasCreadas(),
                        detalleRuta.getLecturasPendientes());
            } else {
                log.info(
                        "{} emision {} ruta {} completada en {} ms. esperadas={}, creadas={}, pendientes={}",
                        accion,
                        emision.getEmision(),
                        detalleRuta.getCodigoRuta(),
                        duracionRuta,
                        detalleRuta.getAbonadosEsperados(),
                        detalleRuta.getLecturasCreadas(),
                        detalleRuta.getLecturasPendientes());
            }

            if (detalleRuta.isRutaCreada()) {
                rutasCreadas++;
            } else {
                rutasExistentes++;
            }

            totalLecturasEsperadas += detalleRuta.getAbonadosEsperados();
            lecturasExistentes += detalleRuta.getLecturasExistentes();
            lecturasCreadas += detalleRuta.getLecturasCreadas();

            if (detalleRuta.getLecturasPendientes() == 0) {
                rutasCompletas++;
            }

            detalleRutas.add(detalleRuta);
        }

        long rutasPendientes = detalleRutas.stream().filter(r -> r.getLecturasPendientes() > 0).count();
        long lecturasPendientes = Math.max(totalLecturasEsperadas - (lecturasExistentes + lecturasCreadas), 0);
        logResumenRutasLentas(accion, emision.getEmision(), tiemposRuta);

        return EmisionGeneracionResponseDTO.builder()
                .idemision(idemision)
                .emision(emision.getEmision())
                .totalRutasEsperadas(rutasActivas.size())
                .rutasExistentes(rutasExistentes)
                .rutasCreadas(rutasCreadas)
                .rutasCompletas(rutasCompletas)
                .rutasPendientes(rutasPendientes)
                .totalLecturasEsperadas(totalLecturasEsperadas)
                .lecturasExistentes(lecturasExistentes)
                .lecturasCreadas(lecturasCreadas)
                .lecturasPendientes(lecturasPendientes)
                .rutas(detalleRutas)
                .build();
    }

    private void logResumenRutasLentas(String accion, String emision, List<RutaTiempo> tiemposRuta) {
        if (tiemposRuta.isEmpty()) {
            return;
        }

        List<RutaTiempo> topLentas = tiemposRuta.stream()
                .sorted(Comparator.comparingLong(RutaTiempo::duracionMs).reversed())
                .limit(5)
                .toList();

        StringBuilder resumen = new StringBuilder();
        for (RutaTiempo ruta : topLentas) {
            if (resumen.length() > 0) {
                resumen.append(" | ");
            }
            resumen.append(ruta.codigoRuta())
                    .append(" ")
                    .append(ruta.nombreRuta())
                    .append(": ")
                    .append(ruta.duracionMs())
                    .append(" ms")
                    .append(", esperadas=")
                    .append(ruta.abonadosEsperados())
                    .append(", creadas=")
                    .append(ruta.lecturasCreadas())
                    .append(", pendientes=")
                    .append(ruta.lecturasPendientes());
        }

        log.info("{} emision {} top rutas lentas: {}", accion, emision, resumen);
    }

    private EmisionGeneracionRutaDetalleDTO procesarRuta(
            Emisiones emision,
            Rutas ruta,
            Long idusuario,
            LocalDate fechaEmisionLocal,
            Date fechaEmision,
            Date fechaCreacion,
            boolean generarFaltantes) {
        Rutasxemision rutaXEmision = rutasxemisionR.findOptionalByEmisionRuta(emision.getIdemision(), ruta.getIdruta())
                .orElse(null);

        boolean rutaCreada = false;
        if (rutaXEmision == null && generarFaltantes) {
            rutaXEmision = crearRutaXEmision(emision, ruta, idusuario, fechaCreacion);
            rutaCreada = true;
        }

        List<Abonados> abonados = abonadosR.findByIdruta(ruta.getIdruta());
        long esperadosRuta = abonados.size();
        long existentesRuta = rutaXEmision == null
                ? 0
                : lecturasR.countDistinctAbonadosByRutaXEmision(rutaXEmision.getIdrutaxemision());
        long creadasRuta = 0;

        if (rutaXEmision != null) {
            for (Abonados abonado : abonados) {
                Optional<Lecturas> lecturaExistente = lecturasR
                        .findFirstByIdemisionAndIdabonado(emision.getIdemision(), abonado.getIdabonado());

                if (lecturaExistente.isPresent()) {
                    corregirFacturaDeLecturaSiCorresponde(
                            lecturaExistente.get(),
                            abonado,
                            fechaEmisionLocal,
                            idusuario);
                    continue;
                }

                if (!generarFaltantes) {
                    continue;
                }

                crearLecturaPendiente(
                        emision,
                        rutaXEmision,
                        abonado,
                        fechaEmisionLocal,
                        fechaEmision,
                        idusuario);
                creadasRuta++;
            }
        }

        long lecturasRuta = existentesRuta + creadasRuta;
        long pendientesRuta = Math.max(esperadosRuta - lecturasRuta, 0);

        return EmisionGeneracionRutaDetalleDTO.builder()
                .idruta(ruta.getIdruta())
                .codigoRuta(ruta.getCodigo())
                .nombreRuta(ruta.getDescripcion())
                .idrutaxemision(rutaXEmision == null ? null : rutaXEmision.getIdrutaxemision())
                .rutaCreada(rutaCreada)
                .abonadosEsperados(esperadosRuta)
                .lecturasExistentes(existentesRuta)
                .lecturasCreadas(creadasRuta)
                .lecturasPendientes(pendientesRuta)
                .build();
    }

    private Rutasxemision crearRutaXEmision(
            Emisiones emision,
            Rutas ruta,
            Long idusuario,
            Date fechaCreacion) {
        return transactionTemplate.execute(status -> {
            Rutasxemision existente = rutasxemisionR.findOptionalByEmisionRuta(emision.getIdemision(), ruta.getIdruta())
                    .orElse(null);
            if (existente != null) {
                return existente;
            }

            Rutasxemision nueva = new Rutasxemision();
            nueva.setEstado(0);
            nueva.setM3(0L);
            nueva.setTotal(BigDecimal.ZERO);
            nueva.setIdemision_emisiones(emision);
            nueva.setIdruta_rutas(ruta);
            nueva.setUsucrea(idusuario);
            nueva.setFeccrea(fechaCreacion);
            return rutasxemisionR.save(nueva);
        });
    }

    private void crearLecturaPendiente(
            Emisiones emision,
            Rutasxemision rutaXEmision,
            Abonados abonado,
            LocalDate fechaEmisionLocal,
            Date fechaEmision,
            Long idusuario) {
        transactionTemplate.executeWithoutResult(status -> {
            boolean yaExiste = lecturasR.findFirstByIdemisionAndIdabonado(emision.getIdemision(), abonado.getIdabonado())
                    .isPresent();
            if (yaExiste) {
                return;
            }

            Facturas factura = obtenerOCrearFactura(abonado, fechaEmisionLocal, idusuario);
            crearLectura(emision, rutaXEmision, abonado, factura, fechaEmision);
        });
    }

    private void corregirFacturaDeLecturaSiCorresponde(
            Lecturas lectura,
            Abonados abonado,
            LocalDate fechaEmision,
            Long idusuario) {
        if (lectura == null || lectura.getIdfactura() == null) {
            return;
        }

        Long idfactura = lectura.getIdfactura();
        if (!facturaPerteneceAOtraEmision(lectura, fechaEmision)) {
            return;
        }

        Facturas nuevaFactura = crearFactura(abonado, fechaEmision, idusuario);
        lectura.setIdfactura(nuevaFactura.getIdfactura());
        lecturasR.save(lectura);

        log.warn(
                "Lectura {} de emision {} abonado {} estaba enlazada a factura {} de otra emision. Se reasigno a nueva factura {} sin rubros.",
                lectura.getIdlectura(),
                lectura.getIdemision(),
                abonado.getIdabonado(),
                idfactura,
                nuevaFactura.getIdfactura());
    }

    private Facturas obtenerOCrearFactura(Abonados abonado, LocalDate fechaEmision, Long idusuario) {
        List<Facturas> facturasExistentes = facturasR.findByIdabonadoAndModuloAndFecha(
                abonado.getIdabonado(),
                MODULO_LECTURAS,
                fechaEmision);

        for (Facturas facturaExistente : facturasExistentes) {
            if (facturaPendienteReutilizable(facturaExistente)) {
                if (facturasExistentes.size() > 1) {
                    log.warn(
                            "Se encontraron {} facturas para abonado {} modulo {} fecha {}. Se reutilizara la factura vacia {}",
                            facturasExistentes.size(),
                            abonado.getIdabonado(),
                            MODULO_LECTURAS,
                            fechaEmision,
                            facturaExistente.getIdfactura());
                }
                return facturaExistente;
            }
        }

        return crearFactura(abonado, fechaEmision, idusuario);
    }

    private boolean facturaPendienteReutilizable(Facturas factura) {
        if (factura == null || factura.getIdfactura() == null) {
            return false;
        }

        boolean tieneRubros = !rubroxfacR.findAllByFacturaId(factura.getIdfactura()).isEmpty();
        if (tieneRubros) {
            return false;
        }

        boolean tieneLecturaAsociada = !lecturasR.findByIdfactura(factura.getIdfactura()).isEmpty();
        return !tieneLecturaAsociada;
    }

    private boolean facturaPerteneceAOtraEmision(Lecturas lectura, LocalDate fechaEmision) {
        Long idfactura = lectura.getIdfactura();
        if (idfactura == null) {
            return false;
        }

        Facturas factura = facturasR.findById(idfactura).orElse(null);
        if (factura == null) {
            return false;
        }

        if (factura.getFeccrea() != null && !fechaEmision.equals(factura.getFeccrea())) {
            return true;
        }

        List<Lecturas> lecturasFactura = lecturasR.findByIdfactura(idfactura);
        for (Lecturas lecturaFactura : lecturasFactura) {
            if (lecturaFactura.getIdlectura() == null || lectura.getIdlectura() == null) {
                continue;
            }
            if (!lecturaFactura.getIdlectura().equals(lectura.getIdlectura())
                    && lecturaFactura.getIdemision() != null
                    && !lecturaFactura.getIdemision().equals(lectura.getIdemision())) {
                return true;
            }
        }

        return false;
    }

    private Facturas crearFactura(Abonados abonado, LocalDate fechaEmision, Long idusuario) {
        Facturas factura = new Facturas();

        Modulos modulo = new Modulos();
        modulo.setIdmodulo(MODULO_LECTURAS);
        factura.setIdmodulo(modulo);

        Clientes cliente = new Clientes();
        cliente.setIdcliente(abonado.getIdresponsable().getIdcliente());
        factura.setIdcliente(cliente);

        factura.setIdabonado(abonado.getIdabonado());
        factura.setPorcexoneracion(0L);
        factura.setTotaltarifa(BigDecimal.ZERO);
        factura.setPagado(0);
        factura.setConveniopago(0L);
        factura.setEstadoconvenio(0L);
        factura.setFormapago(1L);
        factura.setValorbase(BigDecimal.ZERO);
        factura.setUsucrea(idusuario);
        factura.setEstado(1L);
        factura.setFeccrea(fechaEmision);

        return facturasR.save(factura);
    }

    private void crearLectura(Emisiones emision, Rutasxemision rutaXEmision, Abonados abonado, Facturas factura,
            Date fechaEmision) {
        Lecturas lectura = new Lecturas();
        lectura.setEstado(0);
        lectura.setFechaemision(fechaEmision);
        lectura.setLecturaanterior(resolveUltimaLectura(abonado.getIdabonado()));
        lectura.setLecturaactual(0F);
        lectura.setLecturadigitada(0F);
        lectura.setMesesmulta(0);

        Novedad novedad = new Novedad();
        novedad.setIdnovedad(NOVEDAD_POR_DEFECTO);
        lectura.setIdnovedad_novedades(novedad);

        lectura.setIdemision(emision.getIdemision());
        lectura.setIdabonado_abonados(abonado);
        lectura.setIdresponsable(abonado.getIdresponsable().getIdcliente());
        lectura.setIdcategoria(abonado.getIdcategoria_categorias().getIdcategoria());
        lectura.setIdrutaxemision_rutasxemision(rutaXEmision);
        lectura.setIdfactura(factura.getIdfactura());
        lectura.setTotal1(BigDecimal.ZERO);
        lectura.setTotal31(BigDecimal.ZERO);
        lectura.setTotal32(BigDecimal.ZERO);
        lectura.setFotoPath("");

        lecturasR.save(lectura);
    }

    private Float resolveUltimaLectura(Long idabonado) {
        Long ultimaLectura = lecturasR.ultimaLectura(idabonado);
        return ultimaLectura == null ? 0F : ultimaLectura.floatValue();
    }

    private LocalDate parseFechaEmision(String emision) {
        if (emision == null || emision.length() != 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de emision invalido: " + emision);
        }

        int anio = Integer.parseInt(emision.substring(0, 2)) + 2000;
        int mes = Integer.parseInt(emision.substring(2, 4));
        return LocalDate.of(anio, mes, 1);
    }

    private record RutaTiempo(
            String codigoRuta,
            String nombreRuta,
            long duracionMs,
            long abonadosEsperados,
            long lecturasCreadas,
            long lecturasPendientes) {
    }
}
