package com.epmapat.erp_epmapat.servicio;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.DTO.RecargoXCtaReq;
import com.epmapat.erp_epmapat.DTO.ValidarRecargosRequest;
import com.epmapat.erp_epmapat.DTO.ValidarRecargosResponse;
import com.epmapat.erp_epmapat.excepciones.BusinessConflictException;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Recargosxcuenta;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import com.epmapat.erp_epmapat.repositorio.RecargosxcuentaR;
import com.epmapat.erp_epmapat.repositorio.RubrosR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecargosxcuentaService {

    private final RecargosxcuentaR recargosR;
    private final EmisionesR emisionesR;
    private final AbonadosR abonadosR;
    private final RubrosR rubrosR;

    // =========================
    // Helpers (null checks)
    // =========================
    private static Long requireId(Long id, String msg) {
        if (id == null)
            throw new IllegalArgumentException(msg);
        return id;
    }

    private static Integer requireTipo(Integer tipo, String msg) {
        if (tipo == null)
            throw new IllegalArgumentException(msg);
        if (tipo != 1 && tipo != 2)
            throw new IllegalArgumentException(msg + " (solo 1 o 2)");
        return tipo;
    }

    // =========================
    // CRUD básicos
    // =========================
    public Recargosxcuenta save(Recargosxcuenta recargosxcuenta) {
        return recargosR.save(recargosxcuenta);
    }

    public void deleteById(Long id) {
        recargosR.deleteById(id);
    }

    public List<Recargosxcuenta> findAllByEmision(Long emision) {
        return recargosR.findByIdEmision(emision);
    }

    // =========================
    // Validar (sin guardar)
    // =========================
    @Transactional(readOnly = true)
    public ValidarRecargosResponse validar(ValidarRecargosRequest req) {

        ValidarRecargosResponse resp = new ValidarRecargosResponse();

        if (req == null) {
            resp.addBloqueado(0L, 0, "Request vacío.");
            return resp;
        }

        if (req.getIdemision() == null) {
            resp.addBloqueado(0L, 0, "Debe enviar idemision.");
            return resp;
        }

        Emisiones emision = emisionesR.findById(req.getIdemision())
                .orElseThrow(() -> new BusinessConflictException("Emisión no existe."));

        // ✅ Regla #1: emisión abierta
        if (emision.getEstado() == null || emision.getEstado() != 0) {
            resp.addBloqueado(0L, 0, "La emisión está cerrada (estado != 0).");
            return resp;
        }

        LocalDate fechaLocal = (req.getFecha() != null) ? req.getFecha() : LocalDate.now();
        Timestamp ts = Timestamp.valueOf(fechaLocal.atStartOfDay());

        if (req.getItems() == null || req.getItems().isEmpty()) {
            resp.addBloqueado(0L, 0, "No hay items para validar.");
            return resp;
        }

        for (ValidarRecargosRequest.Item it : req.getItems()) {
            Long idabonado = it.getIdabonado();
            Integer tipoObj = it.getTipo();

            if (idabonado == null) {
                resp.addBloqueado(0L, tipoObj, "Item sin idabonado.");
                continue;
            }
            if (tipoObj == null || (tipoObj != 1 && tipoObj != 2)) {
                resp.addBloqueado(idabonado, tipoObj, "Tipo inválido (solo 1 o 2).");
                continue;
            }

            int tipo = tipoObj;

            if (tipo == 1) {
                // ✅ Regla #2: tipo 1 no repetible por emisión
                if (recargosR.existsEnEmisionYTipo(idabonado, req.getIdemision(), 1)) {
                    resp.addBloqueado(idabonado, 1, "Ya existe NOTIFICACIÓN (tipo 1) para esta emisión.");
                    continue;
                }

                // ✅ Regla extra: tipo 1 mensual
                if (recargosR.existsTipo1EnMes(idabonado, ts)) {
                    resp.addBloqueado(idabonado, 1, "Ya existe NOTIFICACIÓN (tipo 1) en este mes.");
                }

            } else {
                // ✅ Regla #3: tipo 2 anual
                if (recargosR.existsTipo2EnAnio(idabonado, ts)) {
                    resp.addBloqueado(idabonado, 2, "Ya existe INSPECCIÓN (tipo 2) en este año.");
                }
            }
        }

        return resp;
    }

    // =========================
    // Guardar batch con validaciones
    // =========================
    @Transactional
    public List<Recargosxcuenta> guardarBatchConValidaciones(List<RecargoXCtaReq> reqs) {

        if (reqs == null || reqs.isEmpty()) {
            throw new IllegalArgumentException("No hay registros para guardar.");
        }

        // ✅ idemision requerido (y debe ser el mismo en todos)
        Long idemision = requireId(reqs.get(0).getIdemision(), "idemision es obligatorio (primer item).");

        for (int i = 0; i < reqs.size(); i++) {
            RecargoXCtaReq r = reqs.get(i);
            Long em = requireId(r.getIdemision(), "idemision es obligatorio (item " + i + ").");
            if (!idemision.equals(em)) {
                throw new IllegalArgumentException("Todos los registros deben pertenecer a la misma emisión.");
            }
        }

        // ✅ validar emisión existe y está abierta
        Emisiones emision = emisionesR.findById(idemision)
                .orElseThrow(() -> new IllegalArgumentException("Emisión no existe: " + idemision));

        if (emision.getEstado() == null || emision.getEstado() != 0) {
            throw new IllegalStateException("La emisión está cerrada. No se puede cargar valores.");
        }

        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        // vamos acumulando errores para devolver un solo mensaje (opcional pero útil)
        List<String> errores = new ArrayList<>();
        List<Recargosxcuenta> toSave = new ArrayList<>();

        for (int i = 0; i < reqs.size(); i++) {
            RecargoXCtaReq req = reqs.get(i);

            Long idabonado = requireId(req.getIdabonado(), "Falta idabonado (item " + i + ").");
            Integer tipo = requireTipo(req.getTipo(), "Tipo inválido (item " + i + ").");

            Long idrubro = requireId(req.getIdrubro(), "Falta idrubro (item " + i + ").");

            // usucrea/usuresp requeridos para auditoría
            requireId(req.getUsucrea(), "Falta usucrea (item " + i + ").");
            requireId(req.getUsuresp(), "Falta usuresp (item " + i + ").");

            Timestamp fecha = (req.getFecha() != null) ? req.getFecha() : ahora;

            // =========================
            // Reglas de negocio
            // =========================
            if (tipo == 1) {
                // Regla #2: no repetir por emisión
                if (recargosR.existsEnEmisionYTipo(idabonado, idemision, 1)) {
                    errores.add("Cuenta " + idabonado + ": ya existe NOTIFICACIÓN (tipo 1) en emisión " + idemision);
                    continue;
                }
                // Regla mensual
                if (recargosR.existsTipo1EnMes(idabonado, fecha)) {
                    errores.add("Cuenta " + idabonado + ": ya existe NOTIFICACIÓN (tipo 1) en el mes");
                    continue;
                }
            }

            if (tipo == 2) {
                // Regla #3: anual
                if (recargosR.existsTipo2EnAnio(idabonado, fecha)) {
                    errores.add("Cuenta " + idabonado + ": ya existe INSPECCIÓN (tipo 2) en el año");
                    continue;
                }
            }

            // =========================
            // Construir entidad (sin findById -> referencias)
            // =========================
            Recargosxcuenta entity = new Recargosxcuenta();
            entity.setIdabonado_abonados(abonadosR.getReferenceById(idabonado));
            entity.setIdemision_emisiones(emision); // ya está cargada
            entity.setIdrubro_rubros(rubrosR.getReferenceById(idrubro));

            entity.setTipo(tipo);
            entity.setObservacion(req.getObservacion());

            entity.setUsucrea(req.getUsucrea());
            entity.setFeccrea(ahora);

            entity.setUsuresp(req.getUsuresp());
            entity.setFecha(fecha);

            entity.setUsumodi(null);
            entity.setFecmodi(null);

            toSave.add(entity);
        }

        if (!errores.isEmpty()) {
            // puedes usar tu BusinessConflictException si prefieres status 409
            throw new BusinessConflictException(String.join(" | ", errores));
        }

        return recargosR.saveAll(toSave);
    }
}
