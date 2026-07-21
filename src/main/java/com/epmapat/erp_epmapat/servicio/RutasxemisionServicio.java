package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.ControlRutaStats;
import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.RutasxemisionR;

@Service
public class RutasxemisionServicio {

    @Autowired
    private RutasxemisionR dao;
    @Autowired
    private LecturasR lecturasR;

    public List<Rutasxemision> findByIdemision(Long idemision) {
        List<Rutasxemision> rutas = dao.findByIdemision(idemision);
        Map<Long, ControlRutaStats> statsPorRuta = new HashMap<>();

        for (ControlRutaStats stats : lecturasR.getControlRutaStatsByEmision(idemision)) {
            statsPorRuta.put(stats.getIdrutaxemision(), stats);
        }

        for (Rutasxemision ruta : rutas) {
            ControlRutaStats stats = statsPorRuta.get(ruta.getIdrutaxemision());
            if (stats == null) {
                ruta.setM3(0L);
                ruta.setTotalLecturas(0L);
                ruta.setLecturasCargadas(0L);
                continue;
            }

            BigDecimal m3 = stats.getM3();
            ruta.setM3(m3 == null ? 0L : m3.longValue());
            ruta.setTotalLecturas(stats.getLecturas() == null ? 0L : stats.getLecturas());
            ruta.setLecturasCargadas(stats.getLecturasTomadas() == null ? 0L : stats.getLecturasTomadas());
        }

        return rutas;
    }

    public Optional<Rutasxemision> findById(Long idemision) {
        return dao.findById(idemision);
    }

    public <S extends Rutasxemision> S save(S entity) {
            return dao.save(entity);
        
    }

    public Long contarPorEstadoYEmision(Long idemision_emisiones) {
        return dao.contarPorEstadoYIdemision( idemision_emisiones );
    }
    public Rutasxemision findByEmisionRuta(Long idemision, Long idruta){
        return dao.findByEmisionRuta(idemision, idruta);
    }

}
