package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.DTO.CondonacionCreateRequest;
import com.epmapat.erp_epmapat.DTO.CondonacionDecisionRequest;
import com.epmapat.erp_epmapat.DTO.CondonacionResponse;
import com.epmapat.erp_epmapat.modelo.CondMultasIntereses;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.repositorio.CondMultasInteresesR;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.servicio.administracion.VentanaServicio;
import com.epmapat.erp_epmapat.servicio.administracion.UsuarioServicio;

@Service
public class CondMultasInteresesServicio {
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_APROBADO = "APROBADO";
    private static final String ESTADO_RECHAZADO = "RECHAZADO";
    private static final Long RUBRO_MULTA_AGUA = 6L;
    private static final Long RUBRO_MULTA_BASURA = 1011L;

    @Autowired
    private CondMultasInteresesR dao;
    @Autowired
    private FacturasR facturasR;
    @Autowired
    private TmpinteresxfacService tmpinteresxfacService;
    @Autowired
    private RubroxfacServicio rubroxfacServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private VentanaServicio ventanaServicio;

    @Transactional
    public List<CondonacionResponse> crearSolicitudes(CondonacionCreateRequest request, Long idusuario) {
        validarUsuario(idusuario);
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos una factura para registrar la solicitud.");
        }
        if (request.getRazonExoneracion() == null || request.getRazonExoneracion().isBlank()) {
            throw new IllegalArgumentException("La razon de exoneracion es obligatoria.");
        }

        List<CondonacionResponse> respuesta = new ArrayList<>();
        for (CondonacionCreateRequest.Item item : request.getItems()) {
            if (item == null || item.getIdfactura() == null) {
                throw new IllegalArgumentException("Cada item debe incluir idfactura.");
            }

            Facturas factura = facturasR.findDetalleById(item.getIdfactura())
                    .orElseThrow(() -> new IllegalArgumentException("No existe la factura " + item.getIdfactura()));
            validarFacturaPendiente(factura);

            BigDecimal interesSolicitado = validarNoNegativo(item.getTotalinteres());
            BigDecimal multaSolicitada = validarNoNegativo(item.getTotalmultas());
            if (interesSolicitado.signum() == 0 && multaSolicitada.signum() == 0) {
                throw new IllegalArgumentException("La solicitud debe incluir interes, multa o ambos.");
            }

            BigDecimal interesDisponible = normalizar(tmpinteresxfacService.findByIdFactura(factura.getIdfactura()));
            BigDecimal multaDisponible = obtenerMultaFactura(factura);

            if (interesSolicitado.compareTo(interesDisponible) > 0) {
                throw new IllegalArgumentException("El interes solicitado supera el valor disponible de la factura.");
            }
            if (multaSolicitada.compareTo(multaDisponible) > 0) {
                throw new IllegalArgumentException("La multa solicitada supera el valor disponible de la factura.");
            }
            if (dao.existsSolicitudActiva(
                    factura.getIdfactura(),
                    interesSolicitado.signum() > 0,
                    multaSolicitada.signum() > 0)) {
                throw new IllegalArgumentException("Ya existe una solicitud pendiente o aprobada para la misma factura y concepto.");
            }

            CondMultasIntereses entity = new CondMultasIntereses();
            entity.setIdfactura_facturas(factura);
            entity.setTotalinteres(interesSolicitado);
            entity.setTotalmultas(multaSolicitada);
            entity.setRazoncondonacion(request.getRazonExoneracion().trim());
            entity.setUsucrea(idusuario);
            entity.setFeccrea(LocalDateTime.now());
            entity.setEstado(ESTADO_PENDIENTE);
            respuesta.add(toResponse(dao.save(entity)));
        }
        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<CondonacionResponse> listar(String estado, Long idfactura, Long idcliente, Long cuenta,
            String nrofactura, Long usucrea, LocalDate feccreaDesde, LocalDate feccreaHasta) {
        LocalDateTime fechaDesde = feccreaDesde == null ? null : feccreaDesde.atStartOfDay();
        LocalDateTime fechaHasta = feccreaHasta == null ? null : feccreaHasta.atTime(23, 59, 59);
        String estadoFiltro = normalizarEstadoFiltro(estado);
        String nroFacturaFiltro = limpiarTextoFiltro(nrofactura);

        return dao.findAllDetalleOrderByIdDesc()
                .stream()
                .filter(item -> coincideEstado(item, estadoFiltro))
                .filter(item -> coincideIdfactura(item, idfactura))
                .filter(item -> coincideIdcliente(item, idcliente))
                .filter(item -> coincideCuenta(item, cuenta))
                .filter(item -> coincideNroFactura(item, nroFacturaFiltro))
                .filter(item -> coincideUsucrea(item, usucrea))
                .filter(item -> coincideFechaDesde(item, fechaDesde))
                .filter(item -> coincideFechaHasta(item, fechaHasta))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CondonacionResponse obtener(Long id) {
        return toResponse(dao.findDetalleById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la solicitud " + id)));
    }

    @Transactional
    public CondonacionResponse aprobar(Long id, Long idusuario, CondonacionDecisionRequest request) {
        Usuarios aprobador = validarUsuario(idusuario);
        validarPermisoAprobacion(idusuario);
        CondMultasIntereses entity = dao.findForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la solicitud " + id));
        validarPendiente(entity);
        if (Objects.equals(entity.getUsucrea(), idusuario)) {
            throw new IllegalArgumentException("El usuario que registro la solicitud no puede aprobarla.");
        }

        Facturas factura = facturasR.findDetalleById(entity.getIdfactura_facturas().getIdfactura())
                .orElseThrow(() -> new IllegalArgumentException("No existe la factura asociada."));
        validarFacturaPendiente(factura);

        if (normalizar(entity.getTotalinteres()).signum() > 0) {
            factura.setSwinteres(Boolean.TRUE);
        }
        if (normalizar(entity.getTotalmultas()).signum() > 0) {
            factura.setSwmulta(Boolean.TRUE);
        }
        facturasR.save(factura);

        entity.setEstado(ESTADO_APROBADO);
        entity.setUsuarioAprueba(aprobador);
        entity.setFecaprobacion(LocalDateTime.now());
        entity.setObservacion_aprobacion(limpiarObservacion(request != null ? request.getObservacion() : null));
        return toResponse(dao.save(entity));
    }

    @Transactional
    public CondonacionResponse rechazar(Long id, Long idusuario, CondonacionDecisionRequest request) {
        Usuarios aprobador = validarUsuario(idusuario);
        validarPermisoAprobacion(idusuario);
        CondMultasIntereses entity = dao.findForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la solicitud " + id));
        validarPendiente(entity);
        if (Objects.equals(entity.getUsucrea(), idusuario)) {
            throw new IllegalArgumentException("El usuario que registro la solicitud no puede rechazarla.");
        }

        String observacion = limpiarObservacion(request != null ? request.getObservacion() : null);
        if (observacion == null) {
            throw new IllegalArgumentException("La observacion de rechazo es obligatoria.");
        }

        entity.setEstado(ESTADO_RECHAZADO);
        entity.setUsuarioAprueba(aprobador);
        entity.setFecaprobacion(LocalDateTime.now());
        entity.setObservacion_aprobacion(observacion);
        return toResponse(dao.save(entity));
    }

    private Usuarios validarUsuario(Long idusuario) {
        if (idusuario == null) {
            throw new IllegalArgumentException("No se pudo determinar el usuario autenticado.");
        }
        return usuarioServicio.findById(idusuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario autenticado."));
    }

    private void validarPendiente(CondMultasIntereses entity) {
        if (entity.getEstado() == null || !ESTADO_PENDIENTE.equalsIgnoreCase(entity.getEstado())) {
            throw new IllegalArgumentException("La solicitud ya fue procesada y no se encuentra pendiente.");
        }
    }

    private void validarPermisoAprobacion(Long idusuario) {
        if (!ventanaServicio.canApproveCondonaciones(idusuario)) {
            throw new IllegalArgumentException(
                    "El usuario no tiene permisos para aprobar o rechazar solicitudes de exoneracion.");
        }
    }

    private void validarFacturaPendiente(Facturas factura) {
        if (factura.getFechaeliminacion() != null || factura.getFechaanulacion() != null) {
            throw new IllegalArgumentException("La factura no esta disponible para exoneracion.");
        }
        if (factura.getFechacobro() != null || (factura.getPagado() != null && factura.getPagado() != 0)) {
            throw new IllegalArgumentException("No se puede procesar una factura pagada.");
        }
    }

    private BigDecimal obtenerMultaFactura(Facturas factura) {
        if (Boolean.TRUE.equals(factura.getSwmulta())) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return rubroxfacServicio.getByIdfactura(factura.getIdfactura()).stream()
                .filter(Objects::nonNull)
                .filter(this::esRubroMulta)
                .map(this::calcularTotalRubro)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private boolean esRubroMulta(Rubroxfac rubro) {
        Long idrubro = rubro.getIdrubro_rubros() != null ? rubro.getIdrubro_rubros().getIdrubro() : null;
        return Objects.equals(idrubro, RUBRO_MULTA_AGUA) || Objects.equals(idrubro, RUBRO_MULTA_BASURA);
    }

    private BigDecimal calcularTotalRubro(Rubroxfac rubro) {
        BigDecimal valor = rubro.getValorunitario() != null ? rubro.getValorunitario() : BigDecimal.ZERO;
        BigDecimal cantidad = rubro.getCantidad() != null ? BigDecimal.valueOf(rubro.getCantidad()) : BigDecimal.ONE;
        return valor.multiply(cantidad);
    }

    private BigDecimal validarNoNegativo(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("Los valores de interes y multa no pueden ser negativos.");
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizar(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private String limpiarObservacion(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private String normalizarEstadoFiltro(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        return estado.trim().toUpperCase();
    }

    private String limpiarTextoFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private boolean coincideEstado(CondMultasIntereses item, String estado) {
        if (estado == null) {
            return true;
        }
        return item.getEstado() != null && estado.equalsIgnoreCase(item.getEstado());
    }

    private boolean coincideIdfactura(CondMultasIntereses item, Long idfactura) {
        if (idfactura == null) {
            return true;
        }
        return item.getIdfactura_facturas() != null
                && Objects.equals(item.getIdfactura_facturas().getIdfactura(), idfactura);
    }

    private boolean coincideIdcliente(CondMultasIntereses item, Long idcliente) {
        if (idcliente == null) {
            return true;
        }
        return item.getIdfactura_facturas() != null
                && item.getIdfactura_facturas().getIdcliente() != null
                && Objects.equals(item.getIdfactura_facturas().getIdcliente().getIdcliente(), idcliente);
    }

    private boolean coincideCuenta(CondMultasIntereses item, Long cuenta) {
        if (cuenta == null) {
            return true;
        }
        return item.getIdfactura_facturas() != null
                && Objects.equals(item.getIdfactura_facturas().getIdabonado(), cuenta);
    }

    private boolean coincideNroFactura(CondMultasIntereses item, String nrofactura) {
        if (nrofactura == null) {
            return true;
        }
        if (item.getIdfactura_facturas() == null || item.getIdfactura_facturas().getNrofactura() == null) {
            return false;
        }
        return item.getIdfactura_facturas().getNrofactura()
                .toUpperCase(Locale.ROOT)
                .contains(nrofactura.toUpperCase(Locale.ROOT));
    }

    private boolean coincideUsucrea(CondMultasIntereses item, Long usucrea) {
        if (usucrea == null) {
            return true;
        }
        return Objects.equals(item.getUsucrea(), usucrea);
    }

    private boolean coincideFechaDesde(CondMultasIntereses item, LocalDateTime fechaDesde) {
        if (fechaDesde == null) {
            return true;
        }
        return item.getFeccrea() != null && !item.getFeccrea().isBefore(fechaDesde);
    }

    private boolean coincideFechaHasta(CondMultasIntereses item, LocalDateTime fechaHasta) {
        if (fechaHasta == null) {
            return true;
        }
        return item.getFeccrea() != null && !item.getFeccrea().isAfter(fechaHasta);
    }

    private CondonacionResponse toResponse(CondMultasIntereses entity) {
        CondonacionResponse dto = new CondonacionResponse();
        dto.setIdcondmultainteres(entity.getIdcondmultainteres());
        dto.setTotalinteres(entity.getTotalinteres());
        dto.setTotalmultas(entity.getTotalmultas());
        dto.setRazoncondonacion(entity.getRazoncondonacion());
        dto.setEstado(entity.getEstado());
        dto.setUsucrea(entity.getUsucrea());
        dto.setFeccrea(entity.getFeccrea());
        dto.setFecaprobacion(entity.getFecaprobacion());
        dto.setObservacionAprobacion(entity.getObservacion_aprobacion());
        if (entity.getUsuarioAprueba() != null) {
            dto.setIdusaprueba(entity.getUsuarioAprueba().getIdusuario());
            dto.setUsuarioAprueba(entity.getUsuarioAprueba().getNomusu());
        }
        if (entity.getUsucrea() != null) {
            usuarioServicio.findById(entity.getUsucrea()).ifPresent(u -> dto.setUsuarioCreador(u.getNomusu()));
        }
        if (entity.getIdfactura_facturas() != null) {
            Facturas factura = entity.getIdfactura_facturas();
            dto.setIdfactura(factura.getIdfactura());
            dto.setNrofactura(factura.getNrofactura());
            dto.setCuenta(factura.getIdabonado());
            dto.setFechaFactura(factura.getFeccrea());
            if (factura.getIdcliente() != null) {
                dto.setIdcliente(factura.getIdcliente().getIdcliente());
                dto.setAbonado(factura.getIdcliente().getNombre());
            }
        }
        return dto;
    }
}
