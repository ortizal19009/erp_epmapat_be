package com.epmapat.erp_epmapat.servicio.recaudacion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCajaDTO;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCajaOperacionResponse;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCobroRequest;
import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCobroResponse;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Facxnc;
import com.epmapat.erp_epmapat.interfaces.FacSinCobrar;
import com.epmapat.erp_epmapat.interfaces.NtaCreditoSaldos;
import com.epmapat.erp_epmapat.excepciones.CredencialesInvalidasException;
import com.epmapat.erp_epmapat.modelo.Cajas;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Facxrecauda;
import com.epmapat.erp_epmapat.modelo.Ntacredito;
import com.epmapat.erp_epmapat.modelo.Recaudacion;
import com.epmapat.erp_epmapat.modelo.Recaudaxcaja;
import com.epmapat.erp_epmapat.modelo.Valoresnc;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.servicio.AbonadoServicio;
import com.epmapat.erp_epmapat.servicio.CajaServicio;
import com.epmapat.erp_epmapat.servicio.FacturaServicio;
import com.epmapat.erp_epmapat.servicio.Fec_facturaService;
import com.epmapat.erp_epmapat.servicio.FacxncService;
import com.epmapat.erp_epmapat.servicio.FacxrecaudaServicio;
import com.epmapat.erp_epmapat.servicio.NtacreditoServicio;
import com.epmapat.erp_epmapat.servicio.RecaudacionServicio;
import com.epmapat.erp_epmapat.servicio.RecaudaxcajaServicio;
import com.epmapat.erp_epmapat.servicio.RubroxfacServicio;
import com.epmapat.erp_epmapat.servicio.ValoresncServicio;
import com.epmapat.erp_epmapat.servicio.administracion.DefinirServicio;
import com.epmapat.erp_epmapat.servicio.administracion.UsuarioServicio;

@Service
public class RecaudacionCobroServicio {

    @Autowired
    private FacturaServicio facturaServicio;
    @Autowired
    private AbonadoServicio abonadoServicio;
    @Autowired
    private FacxrecaudaServicio facxrecaudaServicio;
    @Autowired
    private RecaudacionServicio recaudacionServicio;
    @Autowired
    private RecaudaxcajaServicio recaudaxcajaServicio;
    @Autowired
    private CajaServicio cajaServicio;
    @Autowired
    private RubroxfacServicio rubroxfacServicio;
    @Autowired
    private DefinirServicio definirServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private Fec_facturaService fecFacturaService;
    @Autowired
    private NtacreditoServicio ntacreditoServicio;
    @Autowired
    private ValoresncServicio valoresncServicio;
    @Autowired
    private FacxncService facxncService;

    @Transactional
    public List<ValorFactDTO> getSincobroByCuenta(Long cuenta) {
        Map<Long, ValorFactDTO> pendientesPorId = new java.util.LinkedHashMap<>();

        agregarPendientesDeCliente(pendientesPorId, resolverIdClienteTitularPorCuenta(cuenta));
        agregarPendientesDeCliente(pendientesPorId, resolverIdClienteResponsablePorCuenta(cuenta));
        agregarPendientesDeCuenta(pendientesPorId, cuenta);

        List<ValorFactDTO> pendientes = new ArrayList<>(pendientesPorId.values());
        pendientes.sort(Comparator
                .comparing(ValorFactDTO::getCuenta, Comparator.nullsLast(Long::compareTo))
                .thenComparing(ValorFactDTO::getFeccrea, Comparator.nullsLast(java.time.LocalDate::compareTo))
                .thenComparing(ValorFactDTO::getIdfactura, Comparator.nullsLast(Long::compareTo)));
        cargarIvasMasivos(pendientes);
        return pendientes;
    }

    @Transactional
    public List<ValorFactDTO> getSincobroByCliente(Long idcliente) {
        List<ValorFactDTO> respuesta = new ArrayList<>();

        List<FacSinCobrar> pendientes = facturaServicio.findFacSincobro(idcliente);
        for (FacSinCobrar item : pendientes) {
            if (item == null || item.getIdfactura() == null) {
                continue;
            }

            ValorFactDTO dto = new ValorFactDTO();
            dto.setIdfactura(item.getIdfactura());
            dto.setIdcliente(item.getIdCliente() != null ? item.getIdCliente() : idcliente);
            dto.setCuenta(item.getIdAbonado());
            dto.setNombre(item.getNombre());
            dto.setCedula(item.getCedula());
            dto.setDireccionubicacion(item.getDireccionubicacion());
            dto.setFeccrea(toLocalDate(item.getFeccrea()));
            dto.setFormapago(item.getFormaPago());
            dto.setEstado(item.getEstado());
            dto.setPagado(item.getPagado() != null ? item.getPagado().intValue() : null);
            dto.setModulo(item.getModulo());
            dto.setSubtotal(item.getTotal() != null ? item.getTotal().floatValue() : 0f);
            dto.setTotal(item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO);
            dto.setInteres(item.getInteres() != null ? item.getInteres() : BigDecimal.ZERO);
            dto.setIva(BigDecimal.ZERO);
            respuesta.add(dto);
        }

        cargarIvasMasivos(respuesta);
        respuesta.sort(Comparator
                .comparing(ValorFactDTO::getCuenta, Comparator.nullsLast(Long::compareTo))
                .thenComparing(ValorFactDTO::getFeccrea, Comparator.nullsLast(java.time.LocalDate::compareTo))
                .thenComparing(ValorFactDTO::getIdfactura, Comparator.nullsLast(Long::compareTo)));
        return respuesta;
    }

    private Long resolverIdClienteTitularPorCuenta(Long cuenta) {
        if (cuenta == null) {
            return null;
        }

        List<Abonados> abonados = abonadoServicio.getAbonadoByid(cuenta);
        if (abonados == null || abonados.isEmpty()) {
            return null;
        }

        Abonados abonado = abonados.get(0);
        if (abonado == null || abonado.getIdcliente_clientes() == null) {
            return null;
        }

        return abonado.getIdcliente_clientes().getIdcliente();
    }

    private Long resolverIdClienteResponsablePorCuenta(Long cuenta) {
        if (cuenta == null) {
            return null;
        }

        Optional<Abonados> abonadoOpt = abonadoServicio.findById(cuenta);
        if (abonadoOpt.isEmpty()) {
            return null;
        }

        Abonados abonado = abonadoOpt.get();
        if (abonado.getIdresponsable() == null) {
            return null;
        }

        return abonado.getIdresponsable().getIdcliente();
    }

    private void agregarPendientesDeCliente(Map<Long, ValorFactDTO> pendientesPorId, Long idcliente) {
        if (idcliente == null) {
            return;
        }

        List<FacSinCobrar> pendientes = facturaServicio.findFacSincobro(idcliente);
        for (FacSinCobrar item : pendientes) {
            if (item == null || item.getIdfactura() == null) {
                continue;
            }

            ValorFactDTO dto = new ValorFactDTO();
            dto.setIdfactura(item.getIdfactura());
            dto.setIdcliente(item.getIdCliente() != null ? item.getIdCliente() : idcliente);
            dto.setCuenta(item.getIdAbonado());
            dto.setNombre(item.getNombre());
            dto.setCedula(item.getCedula());
            dto.setDireccionubicacion(item.getDireccionubicacion());
            dto.setFeccrea(toLocalDate(item.getFeccrea()));
            dto.setFormapago(item.getFormaPago());
            dto.setEstado(item.getEstado());
            dto.setPagado(item.getPagado() != null ? item.getPagado().intValue() : null);
            dto.setModulo(item.getModulo());
            dto.setSubtotal(item.getTotal() != null ? item.getTotal().floatValue() : 0f);
            dto.setTotal(item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO);
            dto.setInteres(item.getInteres() != null ? item.getInteres() : BigDecimal.ZERO);
            dto.setIva(BigDecimal.ZERO);
            pendientesPorId.putIfAbsent(dto.getIdfactura(), dto);
        }
    }

    private void agregarPendientesDeCuenta(Map<Long, ValorFactDTO> pendientesPorId, Long cuenta) {
        if (cuenta == null) {
            return;
        }

        List<ValorFactDTO> pendientes = facturaServicio.findSincobroDatos(cuenta);
        for (ValorFactDTO item : pendientes) {
            if (item == null || item.getIdfactura() == null) {
                continue;
            }

            ValorFactDTO dto = new ValorFactDTO();
            dto.setIdfactura(item.getIdfactura());
            dto.setCuenta(item.getCuenta());
            dto.setNombre(item.getNombre());
            dto.setCedula(item.getCedula());
            dto.setDireccionubicacion(item.getDireccionubicacion());
            dto.setFeccrea(item.getFeccrea());
            dto.setFormapago(item.getFormapago());
            dto.setSubtotal(item.getSubtotal() != null ? item.getSubtotal() : 0f);
            dto.setTotal(item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO);
            dto.setInteres(item.getInteres() != null ? item.getInteres() : BigDecimal.ZERO);
            dto.setModulo(null);
            dto.setIva(BigDecimal.ZERO);
            pendientesPorId.putIfAbsent(dto.getIdfactura(), dto);
        }
    }

    @Transactional
    public RecaudacionCajaDTO getEstadoCaja(Long idusuario) {
        Cajas caja = cajaServicio.findCajaByIdUsuario(idusuario);
        if (caja == null) {
            return new RecaudacionCajaDTO(null, null, 0, null, null, null, null, null, null, null);
        }

        Recaudaxcaja recxcaja = recaudaxcajaServicio.findLastConexion(caja.getIdcaja());
        Long secuencial = obtenerSecuencialActual(caja, recxcaja);
        Long siguiente = secuencial != null ? secuencial + 1 : null;

        return new RecaudacionCajaDTO(
                caja.getIdcaja(),
                recxcaja != null ? recxcaja.getIdrecaudaxcaja() : null,
                recxcaja != null ? recxcaja.getEstado() : 0,
                caja.getIdusuario_usuarios() != null ? caja.getIdusuario_usuarios().getNomusu() : null,
                caja.getIdptoemision_ptoemision() != null ? caja.getIdptoemision_ptoemision().getEstablecimiento() : null,
                caja.getCodigo(),
                recxcaja != null ? recxcaja.getFacinicio() : null,
                recxcaja != null ? recxcaja.getFacfin() : null,
                secuencial,
                siguiente);
    }

    @Transactional
    public RecaudacionCajaOperacionResponse abrirCaja(String username, String password) {
        if (username == null || username.isBlank() || password == null) {
            throw new IllegalArgumentException("Debe indicar usuario y contraseña.");
        }

        var login = usuarioServicio.chargeLogin(username.trim());
        if (login == null || login.getNomusu() == null || login.getCodusu() == null) {
            throw new CredencialesInvalidasException("Credenciales incorrectas.");
        }

        if (!Objects.equals(login.getNomusu(), username.trim())
                || !Objects.equals(login.getCodusu(), encriptarClave(password))) {
            throw new CredencialesInvalidasException("Credenciales incorrectas.");
        }

        Usuarios usuario = usuarioServicio.findById(login.getIdusuario())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario autenticado."));

        Cajas caja = cajaServicio.findCajaByIdUsuario(usuario.getIdusuario());
        if (caja == null) {
            throw new IllegalArgumentException("Este usuario no tiene caja asignada.");
        }

        Recaudaxcaja recxcajaActual = recaudaxcajaServicio.findLastConexion(caja.getIdcaja());
        if (recxcajaActual != null && Integer.valueOf(1).equals(recxcajaActual.getEstado())) {
            RecaudacionCajaDTO cajaDto = getEstadoCaja(usuario.getIdusuario());
            return new RecaudacionCajaOperacionResponse("La caja ya se encuentra abierta.", cajaDto);
        }

        Long secuencial = obtenerSecuencialActual(caja, recxcajaActual);
        if (secuencial == null) {
            secuencial = 1L;
        }

        Recaudaxcaja nueva = new Recaudaxcaja();
        nueva.setEstado(1);
        nueva.setFacinicio(secuencial);
        nueva.setFacfin(secuencial);
        nueva.setFechainiciolabor(new Date());
        nueva.setHorainicio(LocalTime.now());
        nueva.setIdcaja_cajas(caja);
        nueva.setIdusuario_usuarios(usuario);
        recaudaxcajaServicio.save(nueva);

        caja.setEstado(1L);
        cajaServicio.save(caja);

        RecaudacionCajaDTO cajaDto = getEstadoCaja(usuario.getIdusuario());
        return new RecaudacionCajaOperacionResponse("Caja abierta correctamente.", cajaDto);
    }

    @Transactional
    public RecaudacionCajaOperacionResponse cerrarCaja(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el usuario.");
        }

        var login = usuarioServicio.chargeLogin(username.trim());
        if (login == null || login.getIdusuario() == null) {
            throw new IllegalArgumentException("No existe el usuario.");
        }

        Usuarios usuario = usuarioServicio.findById(login.getIdusuario())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario autenticado."));

        Cajas caja = cajaServicio.findCajaByIdUsuario(usuario.getIdusuario());
        if (caja == null) {
            throw new IllegalArgumentException("Este usuario no tiene caja asignada.");
        }

        Recaudaxcaja recxcaja = recaudaxcajaServicio.findLastConexion(caja.getIdcaja());
        if (recxcaja == null || !Integer.valueOf(1).equals(recxcaja.getEstado())) {
            RecaudacionCajaDTO cajaDto = getEstadoCaja(usuario.getIdusuario());
            return new RecaudacionCajaOperacionResponse("La caja ya se encuentra cerrada.", cajaDto);
        }

        recxcaja.setEstado(0);
        recxcaja.setFechafinlabor(new Date());
        recxcaja.setHorafin(LocalTime.now());
        recaudaxcajaServicio.save(recxcaja);

        caja.setEstado(0L);
        cajaServicio.save(caja);

        RecaudacionCajaDTO cajaDto = getEstadoCaja(usuario.getIdusuario());
        return new RecaudacionCajaOperacionResponse("Caja cerrada correctamente.", cajaDto);
    }

    @Transactional
    public RecaudacionCobroResponse cobrar(RecaudacionCobroRequest request) {
        if (request == null || request.getFacturas() == null || request.getFacturas().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una factura para cobrar.");
        }

        Long idusuario = request.getAutentification();
        List<Facturas> facturas = new ArrayList<>();
        for (Long idfactura : request.getFacturas()) {
            Facturas factura = facturaServicio.findById(idfactura)
                    .orElseThrow(() -> new IllegalArgumentException("No existe la factura " + idfactura));
            facturas.add(factura);
        }

        Facturas primera = facturas.get(0);
        Long idcliente = primera.getIdcliente() != null ? primera.getIdcliente().getIdcliente() : null;
        List<ValorFactDTO> pendientes;
        if (idcliente != null) {
            pendientes = getSincobroByCliente(idcliente).stream()
                    .filter(dto -> request.getFacturas().contains(dto.getIdfactura()))
                    .collect(Collectors.toList());
        } else {
            Long cuenta = primera.getIdabonado();
            if (cuenta == null) {
                throw new IllegalArgumentException("La factura seleccionada no tiene una cuenta asociada.");
            }

            pendientes = facturaServicio.findSincobroDatos(cuenta)
                    .stream()
                    .filter(dto -> request.getFacturas().contains(dto.getIdfactura()))
                    .peek(this::completarIva)
                    .collect(Collectors.toList());
        }

        Cajas caja = cajaServicio.findCajaByIdUsuario(idusuario);
        if (caja == null) {
            throw new IllegalArgumentException("No existe una caja asociada al usuario.");
        }

        Recaudaxcaja recxcaja = recaudaxcajaServicio.findLastConexion(caja.getIdcaja());
        if (recxcaja == null) {
            throw new IllegalArgumentException("La caja no tiene una conexión activa para generar secuenciales.");
        }

        Map<Long, ValorFactDTO> pendientesPorId = pendientes.stream()
                .filter(dto -> dto.getIdfactura() != null)
                .collect(Collectors.toMap(
                        ValorFactDTO::getIdfactura,
                        dto -> dto,
                        (a, b) -> a));

        List<ValorFactDTO> facturasParaCobro = new ArrayList<>();
        for (Facturas factura : facturas) {
            ValorFactDTO pendiente = pendientesPorId.get(factura.getIdfactura());
            if (pendiente == null) {
                pendiente = construirPendienteDesdeFactura(factura);
            }
            facturasParaCobro.add(pendiente);
        }

        BigDecimal totalCalculado = facturasParaCobro.stream()
                .map(dto -> {
                    BigDecimal subtotal = dto.getSubtotal() != null ? BigDecimal.valueOf(dto.getSubtotal()) : BigDecimal.ZERO;
                    BigDecimal interes = dto.getInteres() != null ? dto.getInteres() : BigDecimal.ZERO;
                    BigDecimal iva = dto.getIva() != null ? dto.getIva() : BigDecimal.ZERO;
                    return subtotal.add(interes).add(iva);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Recaudacion recaudacion = request.getRecaudacion() != null ? request.getRecaudacion() : new Recaudacion();
        recaudacion.setRecaudador(idusuario);
        recaudacion.setUsucrea(request.getAutentification());
        recaudacion.setValor(totalCalculado);
        recaudacion.setTotalpagar(totalCalculado);
        if (recaudacion.getRecibo() == null) {
            recaudacion.setRecibo(totalCalculado);
        }
        if (recaudacion.getCambio() == null && recaudacion.getRecibo() != null) {
            recaudacion.setCambio(recaudacion.getRecibo().subtract(totalCalculado));
        }
        if (recaudacion.getFormapago() == null) {
            recaudacion.setFormapago(1L);
        }
        if (recaudacion.getEstado() == null) {
            recaudacion.setEstado(1);
        }
        if (recaudacion.getFechacobro() == null) {
            recaudacion.setFechacobro(ZonedDateTime.now(ZoneId.systemDefault()));
        }
        if (recaudacion.getFeccrea() == null) {
            recaudacion.setFeccrea(ZonedDateTime.now(ZoneId.systemDefault()));
        }

        Recaudacion recaudacionGuardada = recaudacionServicio.save(recaudacion);

        Definir definir = definirServicio.ultima();
        BigDecimal tasaIva = obtenerTasaIva(definir);
        String numeroFacturaSiguiente = null;
        BigDecimal saldoNotaCreditoPendiente = recaudacion.getNcvalor() != null
                ? recaudacion.getNcvalor().max(BigDecimal.ZERO)
                : BigDecimal.ZERO;

        for (Facturas factura : facturas) {
            ValorFactDTO pendiente = pendientesPorId.get(factura.getIdfactura());
            if (pendiente == null) {
                pendiente = construirPendienteDesdeFactura(factura);
            }

            if (factura.getNrofactura() == null || factura.getNrofactura().isBlank()) {
                Long siguiente = siguienteSecuencial(recxcaja);
                factura.setNrofactura(formatearNumeroFactura(caja, siguiente));
                recxcaja.setFacfin(siguiente);
                recaudaxcajaServicio.save(recxcaja);
                numeroFacturaSiguiente = factura.getNrofactura();
            }

            BigDecimal interes = pendiente.getInteres() != null ? pendiente.getInteres() : BigDecimal.ZERO;
            BigDecimal iva = pendiente.getIva() != null ? pendiente.getIva() : calcularIva(factura.getIdfactura(), tasaIva);
            BigDecimal totalFactura = calcularTotalFacturaParaCobro(pendiente, interes, iva);
            BigDecimal valorNotaCreditoAplicado = calcularValorNotaCreditoAplicado(totalFactura, saldoNotaCreditoPendiente);

            factura.setFechacobro(LocalDate.now());
            factura.setHoracobro(LocalTime.now());
            factura.setUsuariocobro(idusuario);
            factura.setInterescobrado(interes);
            factura.setSwiva(iva);
            factura.setValornotacredito(valorNotaCreditoAplicado);
            factura.setPagado(1);
            factura.setFormapago(resolveFormaPago(recaudacion));
            factura.setEstado(Objects.equals(factura.getEstado(), 2L) ? 2L : 1L);
            facturaServicio.save(factura);
            registrarAplicacionNotaCredito(factura, valorNotaCreditoAplicado);
            fecFacturaService.asegurarFecFactura(factura.getIdfactura());
            saldoNotaCreditoPendiente = saldoNotaCreditoPendiente.subtract(valorNotaCreditoAplicado).max(BigDecimal.ZERO);

            Facxrecauda facxrecauda = new Facxrecauda();
            facxrecauda.setIdrecaudacion(recaudacionGuardada);
            facxrecauda.setIdfactura(factura);
            facxrecauda.setEstado(1);
            facxrecaudaServicio.save(facxrecauda);
        }

        RecaudacionCajaDTO cajaDto = getEstadoCaja(idusuario);
        return new RecaudacionCobroResponse(recaudacionGuardada, cajaDto, facturasParaCobro, totalCalculado, numeroFacturaSiguiente);
    }

    private void completarIva(ValorFactDTO dto) {
        if (dto == null || dto.getIdfactura() == null) {
            return;
        }
        Definir definir = definirServicio.ultima();
        BigDecimal tasaIva = obtenerTasaIva(definir);
        dto.setIva(calcularIva(dto.getIdfactura(), tasaIva));
        BigDecimal subtotal = dto.getSubtotal() != null ? BigDecimal.valueOf(dto.getSubtotal()) : BigDecimal.ZERO;
        BigDecimal interes = dto.getInteres() != null ? dto.getInteres() : BigDecimal.ZERO;
        dto.setTotal(subtotal);
    }

    private ValorFactDTO construirPendienteDesdeFactura(Facturas factura) {
        ValorFactDTO dto = new ValorFactDTO();
        dto.setIdfactura(factura.getIdfactura());
        dto.setIdcliente(factura.getIdcliente() != null ? factura.getIdcliente().getIdcliente() : null);
        dto.setCuenta(factura.getIdabonado());
        dto.setNombre(factura.getIdcliente() != null ? factura.getIdcliente().getNombre() : null);
        dto.setCedula(factura.getIdcliente() != null ? factura.getIdcliente().getCedula() : null);
        dto.setDireccionubicacion(null);
        dto.setFeccrea(factura.getFeccrea());
        dto.setFormapago(factura.getFormapago());
        dto.setEstado(factura.getEstado());
        dto.setPagado(factura.getPagado());
        dto.setModulo(factura.getIdmodulo() != null ? factura.getIdmodulo().getDescripcion() : null);
        BigDecimal subtotal = sumarSubtotalFactura(factura.getIdfactura());
        BigDecimal interes = rubroxfacServicio.getTotalInteres(factura.getIdfactura());
        dto.setSubtotal(subtotal != null ? subtotal.floatValue() : 0f);
        dto.setTotal(subtotal != null ? subtotal : BigDecimal.ZERO);
        dto.setInteres(interes != null ? interes : BigDecimal.ZERO);
        dto.setIva(calcularIva(factura.getIdfactura()));
        return dto;
    }

    private void cargarIvasMasivos(List<ValorFactDTO> facturas) {
        if (facturas == null || facturas.isEmpty()) {
            return;
        }

        Definir definir = definirServicio.ultima();
        BigDecimal tasaIva = obtenerTasaIva(definir);
        if (tasaIva == null || tasaIva.compareTo(BigDecimal.ZERO) <= 0) {
            facturas.forEach(dto -> dto.setIva(BigDecimal.ZERO));
            return;
        }

        List<Long> ids = facturas.stream()
                .map(ValorFactDTO::getIdfactura)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            facturas.forEach(dto -> dto.setIva(BigDecimal.ZERO));
            return;
        }

        List<Object[]> ivaRows = rubroxfacServicio.getIvaByFacturas(tasaIva, ids);
        java.util.Map<Long, BigDecimal> ivaPorFactura = new java.util.HashMap<>();
        for (Object[] row : ivaRows) {
            if (row == null || row.length < 2 || row[0] == null) {
                continue;
            }
            Long idfactura = Long.valueOf(String.valueOf(row[0]));
            BigDecimal iva = toBigDecimal(row[1]);
            ivaPorFactura.merge(idfactura, iva, BigDecimal::add);
        }

        facturas.forEach(dto -> dto.setIva(ivaPorFactura.getOrDefault(dto.getIdfactura(), BigDecimal.ZERO)));
    }

    private BigDecimal calcularIva(Long idfactura) {
        Definir definir = definirServicio.ultima();
        return calcularIva(idfactura, obtenerTasaIva(definir));
    }

    private BigDecimal calcularIva(Long idfactura, BigDecimal tasaIva) {
        if (idfactura == null || tasaIva == null || tasaIva.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        List<Object[]> ivaRows = rubroxfacServicio.getIva(tasaIva, idfactura);
        if (ivaRows == null || ivaRows.isEmpty() || ivaRows.get(0) == null || ivaRows.get(0).length < 2) {
            return BigDecimal.ZERO;
        }
        return toBigDecimal(ivaRows.get(0)[1]);
    }

    private BigDecimal sumarSubtotalFactura(Long idfactura) {
        if (idfactura == null) {
            return BigDecimal.ZERO;
        }
        return rubroxfacServicio.getByIdfactura1(idfactura).stream()
                .filter(Objects::nonNull)
                .map(r -> {
                    BigDecimal valor = r.getValorunitario() != null ? r.getValorunitario() : BigDecimal.ZERO;
                    BigDecimal cantidad = r.getCantidad() != null ? BigDecimal.valueOf(r.getCantidad()) : BigDecimal.ONE;
                    return valor.multiply(cantidad);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal obtenerTasaIva(Definir definir) {
        if (definir == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal tasa = definir.getPorciva() != null
                ? definir.getPorciva()
                : BigDecimal.valueOf(definir.getIva());

        if (tasa.compareTo(BigDecimal.ONE) > 0) {
            tasa = tasa.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        }

        return tasa;
    }

    private Long obtenerSecuencialActual(Cajas caja, Recaudaxcaja recxcaja) {
        if (recxcaja != null && recxcaja.getFacfin() != null) {
            return recxcaja.getFacfin();
        }
        if (caja != null && caja.getUltimafact() != null) {
            try {
                return Long.parseLong(caja.getUltimafact());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private Long siguienteSecuencial(Recaudaxcaja recxcaja) {
        if (recxcaja == null) {
            return 1L;
        }
        if (recxcaja.getFacfin() == null) {
            return recxcaja.getFacinicio() != null ? recxcaja.getFacinicio() : 1L;
        }
        return recxcaja.getFacfin() + 1;
    }

    private String formatearNumeroFactura(Cajas caja, Long secuencial) {
        String establecimiento = caja != null && caja.getIdptoemision_ptoemision() != null
                ? caja.getIdptoemision_ptoemision().getEstablecimiento()
                : "000";
        String codigo = caja != null && caja.getCodigo() != null ? caja.getCodigo() : "000";
        String sec = secuencial != null ? String.format("%09d", secuencial) : "000000000";
        return establecimiento + "-" + codigo + "-" + sec;
    }

    private Long resolveFormaPago(Recaudacion recaudacion) {
        if (recaudacion == null || recaudacion.getFormapago() == null) {
            return 1L;
        }
        if (recaudacion.getNcvalor() != null && recaudacion.getNcvalor().compareTo(BigDecimal.ZERO) > 0) {
            return 3L;
        }
        return recaudacion.getFormapago();
    }

    private BigDecimal calcularTotalFacturaParaCobro(ValorFactDTO pendiente, BigDecimal interes, BigDecimal iva) {
        BigDecimal subtotal = pendiente != null && pendiente.getTotal() != null ? pendiente.getTotal() : BigDecimal.ZERO;
        return subtotal
                .add(interes != null ? interes : BigDecimal.ZERO)
                .add(iva != null ? iva : BigDecimal.ZERO);
    }

    private BigDecimal calcularValorNotaCreditoAplicado(BigDecimal totalFactura, BigDecimal saldoNotaCreditoPendiente) {
        if (totalFactura == null || saldoNotaCreditoPendiente == null) {
            return BigDecimal.ZERO;
        }
        if (totalFactura.compareTo(BigDecimal.ZERO) <= 0 || saldoNotaCreditoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return totalFactura.min(saldoNotaCreditoPendiente).setScale(2, RoundingMode.HALF_UP);
    }

    private void registrarAplicacionNotaCredito(Facturas factura, BigDecimal valorAplicado) {
        if (factura == null || factura.getIdabonado() == null || valorAplicado == null
                || valorAplicado.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal pendientePorAplicar = valorAplicado;
        List<NtaCreditoSaldos> notasCreditoDisponibles = ntacreditoServicio.findSaldosByCuenta(factura.getIdabonado());

        for (NtaCreditoSaldos saldoNc : notasCreditoDisponibles) {
            if (saldoNc == null || saldoNc.getIdntacredito() == null || saldoNc.getSaldo() == null) {
                continue;
            }
            if (pendientePorAplicar.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal saldoDisponible = saldoNc.getSaldo();
            if (saldoDisponible.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal valorUso = saldoDisponible.min(pendientePorAplicar).setScale(2, RoundingMode.HALF_UP);
            if (valorUso.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            Ntacredito notaCredito = ntacreditoServicio.findById(saldoNc.getIdntacredito())
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró la nota de crédito " + saldoNc.getIdntacredito()));

            BigDecimal devengadoActual = notaCredito.getDevengado() != null ? notaCredito.getDevengado() : BigDecimal.ZERO;
            notaCredito.setDevengado(devengadoActual.add(valorUso).setScale(2, RoundingMode.HALF_UP));
            ntacreditoServicio.save(notaCredito);

            Valoresnc valoresnc = new Valoresnc();
            valoresnc.setEstado(1L);
            valoresnc.setValor(valorUso);
            valoresnc.setFechaaplicado(LocalDate.now());
            valoresnc.setSaldo(saldoDisponible.subtract(valorUso).setScale(2, RoundingMode.HALF_UP));
            valoresnc.setIdntacredito_ntacredito(notaCredito);
            Valoresnc valoresncGuardado = valoresncServicio.save(valoresnc);

            Facxnc facxnc = new Facxnc();
            facxnc.setIdfactura_facturas(factura);
            facxnc.setIdvaloresnc_valoresnc(valoresncGuardado);
            facxncService.save(facxnc);

            pendientePorAplicar = pendientePorAplicar.subtract(valorUso).max(BigDecimal.ZERO);
        }

        if (pendientePorAplicar.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("La nota de crédito no tiene saldo suficiente para cubrir el valor aplicado.");
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private java.time.LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    private String encriptarClave(String x) {
        StringBuilder y = new StringBuilder();
        for (int i = 0; i < x.length(); i++) {
            y.append((int) x.charAt(i));
        }

        StringBuilder rtn = new StringBuilder();
        for (int i = 0; i < y.length(); i += 2) {
            rtn.append(y.charAt(i));
        }

        rtn.append(x.trim().length());

        for (int i = y.length() - 1; i >= 0; i -= 2) {
            rtn.append(y.charAt(i));
        }

        return rtn.toString();
    }
}
