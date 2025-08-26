package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.FacSinCobrar;
import com.epmapat.erp_epmapat.modelo.Tmpinteresxfac;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.TmpinteresxfacR;

@Service
public class TmpinteresxfacService {
    @Autowired
    private TmpinteresxfacR tmpinteresxfacR;
    @Autowired
    private FacturasR facturasR;
    @Autowired
    private InteresServicio interesServicio;

    public Tmpinteresxfac save(Tmpinteresxfac tmpinteresxfac) {
        return tmpinteresxfacR.save(tmpinteresxfac);
    }

    public Map<String, Object> updateTmpInteresxfac() {
        int totalActualizadas = 0;
        int totalGuardadas = 0;

        List<FacSinCobrar> facturas = facturasR.getIdsFromFacturasSincobrar();
        LocalDateTime date = LocalDateTime.now();

        for (FacSinCobrar item : facturas) {
            Tmpinteresxfac tmpFac = tmpinteresxfacR.findByIdfactura(item.getIdfactura());
            BigDecimal interes = toBigDecimal(interesServicio.facturaid(item.getIdfactura()));

            if (tmpFac != null) {
                tmpFac.setInteresapagar(interes);
                tmpFac.setFeccorte(date);
                tmpinteresxfacR.save(tmpFac);
                totalActualizadas++;
            } else {
                Tmpinteresxfac nuevo = new Tmpinteresxfac();
                nuevo.setIdfactura(item.getIdfactura());
                nuevo.setInteresapagar(interes);
                nuevo.setFeccorte(date);
                tmpinteresxfacR.save(nuevo);
                totalGuardadas++;
            }
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", 200);
        respuesta.put("totalFacturas", facturas.size());
        respuesta.put("totalActualizadas", totalActualizadas);
        respuesta.put("totalGuardadas", totalGuardadas);
        respuesta.put("message", "Proceso finalizado correctamente");

        return respuesta;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else {
            return new BigDecimal(value.toString());
        }
    }

}
