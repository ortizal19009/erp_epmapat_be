package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.epmapat.erp_epmapat.rrhh.modelo.Cargos;
import com.epmapat.erp_epmapat.rrhh.modelo.Contemergencia;
import com.epmapat.erp_epmapat.rrhh.modelo.Personal;
import com.epmapat.erp_epmapat.rrhh.modelo.Tpcontratos;
import com.epmapat.erp_epmapat.rrhh.repositorio.CargosR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ContemergenciasR;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;
import com.epmapat.erp_epmapat.rrhh.repositorio.TpcontratosR;

import lombok.RequiredArgsConstructor;

@Service("rrhhLegacyPersonalServicio")
@RequiredArgsConstructor
public class PersonalServicio {
    private final PersonalR dao;
    private final ContemergenciasR contemergenciasR;
    private final CargosR cargosR;
    private final TpcontratosR tpcontratosR;

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

        resolverReferencias(p);
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

        resolverReferencias(actual);
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

    private void resolverReferencias(Personal p) {
        p.setIdcontemergencia_contemergencias(resolveContemergencia(p.getIdcontemergencia_contemergencias()));
        p.setIdcargo_cargos(resolveCargo(p.getIdcargo_cargos()));
        p.setIdtpcontrato_tpcontratos(resolveContrato(p.getIdtpcontrato_tpcontratos()));
    }

    private Contemergencia resolveContemergencia(Contemergencia referencia) {
        if (referencia == null) {
            return null;
        }
        if (referencia.getIdcontemergencia() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "idcontemergencia_contemergencias.idcontemergencia es obligatorio");
        }
        return contemergenciasR.findById(referencia.getIdcontemergencia())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Contacto de emergencia no existe: " + referencia.getIdcontemergencia()));
    }

    private Cargos resolveCargo(Cargos referencia) {
        if (referencia == null) {
            return null;
        }
        if (referencia.getIdcargo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idcargo_cargos.idcargo es obligatorio");
        }
        return cargosR.findById(referencia.getIdcargo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cargo no existe: " + referencia.getIdcargo()));
    }

    private Tpcontratos resolveContrato(Tpcontratos referencia) {
        if (referencia == null) {
            return null;
        }
        if (referencia.getIdtpcontratos() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "idtpcontrato_tpcontratos.idtpcontratos es obligatorio");
        }
        return tpcontratosR.findById(referencia.getIdtpcontratos())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tipo de contrato no existe: " + referencia.getIdtpcontratos()));
    }
}
