package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.epmapat.erp_epmapat.rrhh.modelo.ThEmployeeFile;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThEmployeeFileR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThEmployeeFileServicio {

    private final ThEmployeeFileR dao;
    private final PersonalR personalR;
    private final ThAuditServicio audit;

    @Transactional
    public ThEmployeeFile save(ThEmployeeFile f) {
        if (f == null || f.getIdpersonal_personal() == null || f.getIdpersonal_personal().getIdpersonal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idpersonal_personal es obligatorio");
        if (f.getTipo_doc() == null || f.getTipo_doc().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipo_doc es obligatorio");
        if (f.getNombre_archivo() == null || f.getNombre_archivo().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre_archivo es obligatorio");
        if (f.getRuta_archivo() == null || f.getRuta_archivo().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ruta_archivo es obligatoria");

        Long idp = f.getIdpersonal_personal().getIdpersonal();
        f.setIdpersonal_personal(personalR.findById(idp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal no existe: " + idp)));
        if (f.getVersion_doc() == null || f.getVersion_doc() <= 0) f.setVersion_doc(1);
        if (f.getEstado() == null || f.getEstado().isBlank()) f.setEstado("ACTIVO");
        if (f.getFeccrea() == null) f.setFeccrea(LocalDate.now());

        ThEmployeeFile saved = dao.save(f);
        audit.log("TH_EMPLOYEE_FILE", saved.getIdfile(), "CREATE", saved.getTipo_doc()+" - "+saved.getNombre_archivo(), saved.getUsucrea());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ThEmployeeFile> byPersonal(Long idpersonal) {
        return dao.findByPersonal(idpersonal);
    }
}

