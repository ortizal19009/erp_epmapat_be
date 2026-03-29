package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.epmapat.erp_epmapat.rrhh.modelo.Personal;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;

import lombok.RequiredArgsConstructor;

@Service("rrhhLegacyPersonalServicio")
@RequiredArgsConstructor
public class PersonalServicio {
    private final PersonalR dao;

    @Transactional(readOnly = true)
    public List<Personal> findAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Personal> search(String q, Boolean estado, Integer page, Integer size) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 200) ? 20 : size;
        Pageable pageable = PageRequest.of(p, s);
        return dao.search(q == null ? "" : q.trim(), estado, pageable);
    }

    @Transactional(readOnly = true)
    public Personal findById(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal no encontrado: " + id));
    }

    @Transactional
    public Personal save(Personal p) {
        validarBasico(p);
        if (Boolean.TRUE.equals(dao.existsByIdentificacion(p.getIdentificacion()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La identificación ya existe: " + p.getIdentificacion());
        }
        if (p.getFeccrea() == null) {
            p.setFeccrea(LocalDate.now());
        }
        if (p.getEstado() == null) {
            p.setEstado(true);
        }
        return dao.save(p);
    }

    @Transactional
    public Personal update(Long id, Personal p) {
        Personal actual = findById(id);

        validarBasico(p);
        if (Boolean.TRUE.equals(dao.existsByIdentificacionAndIdpersonalNot(p.getIdentificacion(), id))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La identificación ya existe: " + p.getIdentificacion());
        }

        actual.setNombres(p.getNombres());
        actual.setApellidos(p.getApellidos());
        actual.setIdentificacion(p.getIdentificacion());
        actual.setEmail(p.getEmail());
        actual.setCelular(p.getCelular());
        actual.setDireccion(p.getDireccion());
        actual.setIdcontemergencia_contemergencias(p.getIdcontemergencia_contemergencias());
        actual.setIdcargo_cargos(p.getIdcargo_cargos());
        actual.setIdtpcontrato_tpcontratos(p.getIdtpcontrato_tpcontratos());
        actual.setUsumodi(p.getUsumodi());
        actual.setFecmodi(LocalDate.now());
        actual.setEstado(p.getEstado() != null ? p.getEstado() : actual.getEstado());
        actual.setCodigo(p.getCodigo());
        actual.setFecnacimiento(p.getFecnacimiento());
        actual.setSufijo(p.getSufijo());
        actual.setTituloprofesional(p.getTituloprofesional());
        actual.setFecinicio(p.getFecinicio());
        actual.setFecfin(p.getFecfin());
        actual.setNomfirma(p.getNomfirma());

        return dao.save(actual);
    }

    @Transactional
    public void inactivar(Long id, Long usumodi) {
        Personal actual = findById(id);
        actual.setEstado(false);
        actual.setUsumodi(usumodi);
        actual.setFecmodi(LocalDate.now());
        dao.save(actual);
    }

    private void validarBasico(Personal p) {
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body vacío");
        }
        if (p.getIdentificacion() == null || p.getIdentificacion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "identificacion es obligatoria");
        }
        if (p.getFecinicio() != null && p.getFecfin() != null && p.getFecinicio().isAfter(p.getFecfin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fecinicio no puede ser mayor a fecfin");
        }
    }
}
