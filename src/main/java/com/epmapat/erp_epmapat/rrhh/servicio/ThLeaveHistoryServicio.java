package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.*;
import com.epmapat.erp_epmapat.rrhh.modelo.ThAuditLog;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveMovement.Tipo;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveHistoryServicio {
    private final ThLeaveRequestR requests;
    private final ThAuditLogR audit;
    private final ThLeaveMovementR movements;
    private static final Map<String, String> ACTION_LABELS = Map.of("CREATE", "creación", "APPROVE", "aprobación",
            "REJECT", "rechazo", "CANCEL", "cancelación", "REVERSE", "reversión");

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ThLeaveHistoryResponse consultar(Long idrequest) {
        if (idrequest == null || idrequest <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud inválida");
        var request = requests.findById(idrequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        List<ThAuditLog> records = audit.findByEntidadAndIdregistroOrderByFechaDesc("TH_LEAVE_REQUEST", idrequest);
        var eventos = records.stream().sorted(Comparator.comparing(ThAuditLog::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(ThAuditLog::getIdaudit))
                .map(a -> new ThLeaveHistoryResponse.Evento(a.getIdaudit(), a.getAccion(), a.getDetalle(), a.getUsuario(), a.getFecha()))
                .toList();
        var financieros = movements.findByRequestIdrequestOrderByIdmovementAsc(idrequest);
        List<String> warnings = new ArrayList<>();
        Set<String> actions = new HashSet<>();
        records.forEach(a -> actions.add(normalize(a.getAccion())));
        List<String> expected = new ArrayList<>(List.of("CREATE"));
        switch (normalize(request.getEstado())) {
            case "APROBADA": expected.add("APPROVE"); break;
            case "RECHAZADA": expected.add("REJECT"); break;
            case "CANCELADA": expected.add("CANCEL"); break;
            case "REVERTIDA": expected.addAll(List.of("APPROVE", "REVERSE")); break;
            case "SOLICITADA": break;
            default: warnings.add("Estado histórico no reconocido; requiere revisión");
        }
        for (String action : expected)
            if (!actions.contains(action)) warnings.add("No existe un evento registrado de " + ACTION_LABELS.get(action) + "; no se reconstruye automáticamente");
        if (records.stream().anyMatch(a -> a.getFecha() == null || a.getUsuario() == null))
            warnings.add("Hay eventos sin fecha o usuario; revisar su evidencia histórica");
        String state = normalize(request.getEstado());
        if ("VACACION".equals(normalize(request.getTipolicencia())) && Set.of("APROBADA", "REVERTIDA").contains(state)) {
            if (financieros.stream().noneMatch(m -> m.getTipo() == Tipo.CONSUMO))
                warnings.add("No existe consumo registrado para esta aprobación; requiere conciliación histórica");
            if ("REVERTIDA".equals(state) && financieros.stream().noneMatch(m -> m.getTipo() == Tipo.REINTEGRO))
                warnings.add("No existe reintegro registrado para esta reversión; requiere revisión");
        }
        return new ThLeaveHistoryResponse(ThLeaveInboxResponse.Solicitud.from(request), LocalDateTime.now(), eventos,
                financieros.stream().map(ThLeaveMovementResponse::from).toList(), List.copyOf(warnings));
    }

    private String normalize(String value) { return value == null ? "" : value.trim().toUpperCase(Locale.ROOT); }
}
