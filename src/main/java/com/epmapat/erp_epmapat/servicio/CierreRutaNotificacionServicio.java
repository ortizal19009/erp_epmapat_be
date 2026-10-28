package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.LinkedHashMap;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.mail.internet.InternetAddress;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.emails.dtos.SendEmailRequest;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.repository.EmailMessageR;
import com.epmapat.erp_epmapat.emails.service.EmailComposerService;
import com.epmapat.erp_epmapat.emails.service.EmailBlacklistService;
import com.epmapat.erp_epmapat.emails.service.EmailBlacklistViolationException;
import com.epmapat.erp_epmapat.interfaces.ResumenPendientesCuenta;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.RutasxemisionR;

@Service
public class CierreRutaNotificacionServicio {
    static final String CORREO_PRUEBAS = "alexis.ortiz81@outlook.com";
    private final RutasxemisionR rutas;
    private final LecturasR lecturas;
    private final FacturaServicio facturas;
    private final EmailMessageR mensajes;
    private final EmailComposerService composer;
    private final Environment environment;
    private final EmailBlacklistService blacklist;

    public CierreRutaNotificacionServicio(RutasxemisionR rutas, LecturasR lecturas,
            FacturaServicio facturas, EmailMessageR mensajes, EmailComposerService composer, Environment environment, EmailBlacklistService blacklist) {
        this.rutas = rutas;
        this.lecturas = lecturas;
        this.facturas = facturas;
        this.mensajes = mensajes;
        this.composer = composer;
        this.environment = environment;
        this.blacklist = blacklist;
    }

    public record Resultado(int encolados, int duplicados, int sinCorreoValido, int sinCalcular, int bloqueados, boolean produccion) {}

    @Transactional
    public Resultado notificar(Long idrutaxemision) {
        // Serializa los reintentos de la misma ruta y la comprobacion de duplicados.
        Rutasxemision ruta = rutas.lockParaNotificar(idrutaxemision)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la ruta de emision"));
        if (!Integer.valueOf(1).equals(ruta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Primero cierre y calcule la ruta");
        }
        boolean produccion = environment.acceptsProfiles(Profiles.of("prod", "production"));
        List<Lecturas> registros = lecturas.findByIdrutaxemision(idrutaxemision);
        List<Long> cuentas = registros.stream().filter(l -> l.getIdabonado_abonados() != null)
            .map(l -> l.getIdabonado_abonados().getIdabonado()).filter(Objects::nonNull).distinct().toList();
        List<Long> excluidas = registros.stream().map(Lecturas::getIdfactura).filter(Objects::nonNull).distinct().toList();
        Map<Long, ResumenPendientesCuenta> pendientes = facturas.resumenPendientesCierrePorCuentas(cuentas, excluidas)
            .stream().collect(Collectors.toMap(ResumenPendientesCuenta::getCuenta, Function.identity(), (a, b) -> a));
        int encolados = 0, duplicados = 0, sinCorreo = 0, sinCalcular = 0, bloqueados = 0;
        for (Lecturas lectura : registros) {
            if (!Integer.valueOf(1).equals(lectura.getEstado()) || lectura.getTotal1() == null
                    || lectura.getIdfactura() == null || lectura.getIdabonado_abonados() == null) {
                sinCalcular++;
                continue;
            }
            var abonado = lectura.getIdabonado_abonados();
            Clientes responsable = abonado.getIdresponsable();
            String destino = produccion ? correoValido(responsable == null ? null : responsable.getEmail()) : CORREO_PRUEBAS;
            if (destino == null) { sinCorreo++; continue; }
            try {
                blacklist.validateRecipients(List.of(destino));
            } catch (EmailBlacklistViolationException e) {
                bloqueados++;
                continue;
            }
            String clave = "CIERRE_RUTA:" + (produccion ? "PROD:" : "LOCAL:") + lectura.getIdfactura() + ":" + abonado.getIdabonado();
            if (mensajes.findFirstByCorrelationIdOrderByCreatedAtDesc(clave).isPresent()) { duplicados++; continue; }
            ResumenPendientesCuenta resumen = pendientes.get(abonado.getIdabonado());
            String emision = ruta.getIdemision_emisiones().getEmision();
            String nombre = responsable == null ? "Responsable de pago" : responsable.getNombre();
            BigDecimal actual = lectura.getTotal1();
            BigDecimal anterior = resumen == null ? BigDecimal.ZERO : cero(resumen.getTotalPendiente());
            String detalle = "Consumo de agua pendiente: USD " + dinero(resumen == null ? null : resumen.getValorConsumo())
                + "\nServicios pendientes: USD " + dinero(resumen == null ? null : resumen.getValorServicios())
                + "\nCuotas de convenios pendientes: USD " + dinero(resumen == null ? null : resumen.getValorConvenios())
                + "\nIntereses incluidos en los valores pendientes: USD " + dinero(resumen == null ? null : resumen.getTotalIntereses())
                + "\nFacturas anteriores pendientes: " + (resumen == null ? 0 : resumen.getTotalFacturasPendientes());
            SendEmailRequest solicitud = new SendEmailRequest();
            solicitud.to = List.of(destino);
            solicitud.subject = (produccion ? "" : "[PRUEBA] ") + "Resumen de valores - Cuenta " + abonado.getIdabonado() + " - Emision " + emision;
            solicitud.correlationId = clave;
            solicitud.text = "Estimado/a " + nombre + ",\nCuenta: " + abonado.getIdabonado() + "\nEmision: " + emision
                + "\nVALOR ACTUAL GENERADO: USD " + dinero(actual) + "\n" + detalle
                + "\nTotal anterior pendiente: USD " + dinero(anterior)
                + "\nTotal pendiente incluido el valor actual: USD " + dinero(anterior.add(actual))
                + "\nValores consultados al cierre; pueden variar por pagos posteriores.";
            solicitud.templateKey = "emails/cierre-ruta";
            Map<String, Object> variables = new LinkedHashMap<>();
            variables.put("esPrueba", !produccion);
            variables.put("logoUrl", environment.getProperty("notificaciones.cierre.logo-url",
                "https://epmapatulcan.gob.ec/emapat/wp-content/uploads/2026/03/LOGO-blanco-1.png"));
            variables.put("nombre", nombre == null || nombre.isBlank() ? "Responsable de pago" : nombre);
            variables.put("cuenta", abonado.getIdabonado());
            variables.put("emision", periodo(emision));
            variables.put("actual", dinero(actual));
            variables.put("anterior", dinero(anterior));
            variables.put("total", dinero(anterior.add(actual)));
            variables.put("consumo", dinero(resumen == null ? null : resumen.getValorConsumo()));
            variables.put("servicios", dinero(resumen == null ? null : resumen.getValorServicios()));
            variables.put("convenios", dinero(resumen == null ? null : resumen.getValorConvenios()));
            variables.put("intereses", dinero(resumen == null ? null : resumen.getTotalIntereses()));
            variables.put("facturasPendientes", resumen == null || resumen.getTotalFacturasPendientes() == null ? 0L : resumen.getTotalFacturasPendientes());
            variables.put("direccion", environment.getProperty("notificaciones.cierre.contacto.direccion", "Juan Ram\u00f3n Arellano y Bol\u00edvar, esquina. Tulc\u00e1n."));
            variables.put("telefono", environment.getProperty("notificaciones.cierre.contacto.telefono", "(06) 2980 021"));
            variables.put("telefonoUrl", environment.getProperty("notificaciones.cierre.contacto.telefono-url", "tel:+59362980021"));
            variables.put("correoContacto", environment.getProperty("notificaciones.cierre.contacto.email", "info@epmapatulcan.gob.ec"));
            variables.put("horario", environment.getProperty("notificaciones.cierre.contacto.horario", "07:30 a 16:30"));
            solicitud.variables = variables;
            solicitud.text += "\n\nAtencion a la ciudadania - EPMAPA-T\n" + variables.get("direccion")
                + "\nHorario de recaudacion: " + variables.get("horario")
                + "\nTelefono: " + variables.get("telefono") + "\nCorreo: " + variables.get("correoContacto")
                + "\nPuntos de recaudacion: https://epmapatulcan.gob.ec/puntos-de-recaudacion/";
            composer.enqueue(EmailType.NOTIFICACION, solicitud);
            encolados++;
        }
        return new Resultado(encolados, duplicados, sinCorreo, sinCalcular, bloqueados, produccion);
    }

    static String correoValido(String valor) {
        if (valor == null) return null;
        String correo = valor.trim();
        if (correo.length() > 254 || !correo.matches("[^\\s@,;<>]+@[^\\s@,;<>]+\\.[^\\s@,;<>]+")) return null;
        try {
            InternetAddress direccion = new InternetAddress(correo, true);
            direccion.validate();
            return direccion.getAddress().equals(correo) ? correo : null;
        } catch (Exception e) { return null; }
    }
    private static BigDecimal cero(BigDecimal valor) { return valor == null ? BigDecimal.ZERO : valor; }
    private static String dinero(BigDecimal valor) { return cero(valor).setScale(2, RoundingMode.HALF_UP).toPlainString(); }
    static String periodo(String emision) {
        try {
            if (emision == null || !emision.matches("[0-9]{4}")) return emision;
            YearMonth mes = YearMonth.of(2000 + Integer.parseInt(emision.substring(0, 2)), Integer.parseInt(emision.substring(2)));
            return mes.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.forLanguageTag("es-EC")));
        } catch (RuntimeException e) { return emision; }
    }
}
