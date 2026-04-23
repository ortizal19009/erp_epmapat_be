package com.epmapat.erp_epmapat.servicio;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmisionMantenimientoServicio {

    private final EmisionServicio emisionServicio;
    private final RutasxemisionServicio rutasxemisionServicio;
    private final LecturaServicio lecturaServicio;

    public EmisionMantenimientoServicio(EmisionServicio emisionServicio,
                                        RutasxemisionServicio rutasxemisionServicio,
                                        LecturaServicio lecturaServicio) {
        this.emisionServicio = emisionServicio;
        this.rutasxemisionServicio = rutasxemisionServicio;
        this.lecturaServicio = lecturaServicio;
    }

    @Transactional
    public Map<String, Object> reabrirEmision(Long idemision, Long usumodi) {
        Emisiones emision = emisionServicio.findById(idemision)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe la Emision Id: " + idemision));

        List<Rutasxemision> rutas = rutasxemisionServicio.findByIdemision(idemision);
        int rutasActualizadas = 0;

        emision.setEstado(0);
        emision.setM3(0L);
        emision.setUsuariocierre(null);
        emision.setFechacierre(null);
        emision.setUsumodi(usumodi);
        emision.setFecmodi(java.util.Date.from(ZonedDateTime.now(ZoneId.systemDefault()).toInstant()));
        emisionServicio.save(emision);

        for (Rutasxemision ruta : rutas) {
            boolean cambio = ruta.getEstado() == null || ruta.getEstado() != 0
                    || ruta.getUsuariocierre() != null
                    || ruta.getFechacierre() != null
                    || ruta.getM3() == null || ruta.getM3() != 0L
                    || ruta.getTotal() != null && ruta.getTotal().compareTo(BigDecimal.ZERO) != 0;

            ruta.setEstado(0);
            ruta.setUsuariocierre(null);
            ruta.setFechacierre(null);
            ruta.setM3(0L);
            ruta.setTotal(BigDecimal.ZERO);
            rutasxemisionServicio.save(ruta);
            if (cambio) {
                rutasActualizadas++;
            }
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idemision", idemision);
        respuesta.put("estado", 0);
        respuesta.put("rutasActualizadas", rutasActualizadas);
        respuesta.put("totalRutas", rutas.size());
        return respuesta;
    }

    @Transactional
    public Map<String, Object> eliminarEmision(Long idemision, Long usumodi) {
        Map<String, Object> respuesta = reabrirEmision(idemision, usumodi);
        lecturaServicio.deleteRubrosByIdEmisin(idemision);
        respuesta.put("rubroxfacActualizado", true);
        respuesta.put("accion", "ELIMINADA");
        return respuesta;
    }
}
