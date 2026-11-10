package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.AguaTramiteAplicacionRequest;
import com.epmapat.erp_epmapat.modelo.AguaTramite;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;

@Service
public class AguaTramiteAplicacionServicio {
    private final AguaTramiteServicio tramites;
    private final AbonadosR abonados;
    private final AuditoriaGenericaService auditoria;
    private final EntityManager em;

    public AguaTramiteAplicacionServicio(AguaTramiteServicio tramites, AbonadosR abonados,
            AuditoriaGenericaService auditoria, EntityManager em) {
        this.tramites = tramites;
        this.abonados = abonados;
        this.auditoria = auditoria;
        this.em = em;
    }

    @Transactional
    public AguaTramite aplicar(AguaTramiteAplicacionRequest request) {
        AguaTramite tramite = request.getTramite();
        Abonados datos = request.getAbonado();
        if (tramite == null || datos == null || tramite.getIdabonado() == null
                || !Objects.equals(tramite.getIdabonado(), datos.getIdabonado())
                || tramite.getIdaguatramite() != null || tramite.getIdtipotramite_tipotramite() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tramite nuevo y cuenta coincidente son obligatorios");
        }
        Long tipo = tramite.getIdtipotramite_tipotramite().getIdtipotramite();
        if (!Long.valueOf(2).equals(tipo) && !Long.valueOf(9).equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo admite retiro de medidor y cambio de categoria");
        }
        Abonados cuenta = abonados.findById(tramite.getIdabonado()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la cuenta"));
        em.refresh(cuenta, LockModeType.PESSIMISTIC_WRITE);
        if (tipo == 2L && !Long.valueOf(1).equals(cuenta.getEstado()) && !Long.valueOf(2).equals(cuenta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede retirar un medidor activo o suspendido");
        }
        if (tipo == 9L && (datos.getIdcategoria_categorias() == null || datos.getIdcategoria_categorias().getIdcategoria() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seleccione la nueva categoria");
        }
        em.createNativeQuery("SELECT set_config('epmapat.historial_modulo', 'TRAMITES_AGUA', true) AS historial_modulo, "
                + "set_config('epmapat.historial_usuario', :usuario, true) AS historial_usuario, "
                + "set_config('epmapat.historial_observacion', :observacion, true) AS historial_observacion")
            .setParameter("usuario", tramite.getUsucrea() == null ? "" : tramite.getUsucrea().toString())
            .setParameter("observacion", tramite.getObservacion() == null ? "" : tramite.getObservacion()).getSingleResult();
        var anterior = new LinkedHashMap<String, Object>();
        anterior.put("idabonado", cuenta.getIdabonado());
        anterior.put("estado", cuenta.getEstado());
        anterior.put("idcategoria", cuenta.getIdcategoria_categorias() == null ? null : cuenta.getIdcategoria_categorias().getIdcategoria());
        anterior.put("adultomayor", cuenta.getAdultomayor());
        anterior.put("municipio", cuenta.getMunicipio());
        auditoria.saveAuditAtomic("abonados", cuenta.getIdabonado(), anterior, tramite.getUsucrea(),
            tramite.getObservacion(), tipo == 2L ? "RETIRO_MEDIDOR" : "CAMBIO_CATEGORIA");
        if (tipo == 2L) {
            cuenta.setEstado(3L);
        } else {
            cuenta.setIdcategoria_categorias(datos.getIdcategoria_categorias());
            cuenta.setAdultomayor(datos.getAdultomayor());
            cuenta.setMunicipio(datos.getMunicipio());
        }
        cuenta.setUsumodi(tramite.getUsucrea());
        cuenta.setFecmodi(LocalDate.now(ZoneId.of("America/Guayaquil")));
        abonados.save(cuenta);
        tramite.setIdcliente_clientes(cuenta.getIdcliente_clientes());
        tramite.setEstado(3);
        return tramites.save(tramite);
    }
}
