package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.epmapat.erp_epmapat.rrhh.modelo.ThAction;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThActionR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThActionServicio {

    private final ThActionR dao;
    private final PersonalR personalR;
    private final ThAuditServicio auditServicio;

    private static final Set<String> TIPOS_VALIDOS = Set.of(
            "INGRESO", "MOVIMIENTO", "ENCARGO", "SUBROGACION", "DESVINCULACION", "REINCORPORACION");

    @Transactional
    public ThAction save(ThAction entity) {
        validar(entity);
        entity.setIdpersonal_personal(personalR.findById(entity.getIdpersonal_personal().getIdpersonal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Personal no existe: " + entity.getIdpersonal_personal().getIdpersonal())));
        if (entity.getFeccrea() == null) {
            entity.setFeccrea(LocalDate.now());
        }
        if (entity.getEstado() == null) {
            entity.setEstado(true);
        }
        ThAction saved = dao.save(entity);
        auditServicio.log("TH_ACTION", saved.getIdaction(), "CREATE", saved.getTipoaccion()+" - "+(saved.getMotivo()==null?"":saved.getMotivo()), saved.getUsucrea());
        return saved;
    }

    @Transactional(readOnly = true)
    public ThAction findById(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Acción no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<ThAction> findByPersonal(Long idpersonal) {
        return dao.findByPersonal(idpersonal);
    }

    private void validar(ThAction e) {
        if (e == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body vacío");
        }
        if (e.getIdpersonal_personal() == null || e.getIdpersonal_personal().getIdpersonal() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idpersonal_personal es obligatorio");
        }
        if (e.getTipoaccion() == null || e.getTipoaccion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipoaccion es obligatorio");
        }
        String tipo = e.getTipoaccion().trim().toUpperCase();
        if (!TIPOS_VALIDOS.contains(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "tipoaccion inválido. Válidos: " + TIPOS_VALIDOS);
        }
        e.setTipoaccion(tipo);

        if (e.getFecvigencia() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fecvigencia es obligatoria");
        }
    }
}


