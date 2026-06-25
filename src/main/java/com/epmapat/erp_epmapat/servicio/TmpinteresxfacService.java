package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        for (FacSinCobrar item : facturas) {
            boolean existe = tmpinteresxfacR.findByIdfactura(item.getIdfactura()).isPresent();
            upsertInteresFactura(item.getIdfactura());

            if (existe) {
                totalActualizadas++;
            } else {
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

    @Transactional
    public BigDecimal upsertInteresFactura(Long idfactura) {
        BigDecimal interes = toBigDecimal(interesServicio.facturaid(idfactura));
        LocalDateTime fechaCorte = LocalDateTime.now();

        Tmpinteresxfac tmpFac = tmpinteresxfacR.findByIdfactura(idfactura)
                .orElseGet(Tmpinteresxfac::new);

        tmpFac.setIdfactura(idfactura);
        tmpFac.setInteresapagar(interes);
        tmpFac.setFeccorte(fechaCorte);
        tmpinteresxfacR.save(tmpFac);

        return interes;
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

    @Transactional
    public BigDecimal findByIdFactura(Long idfactura){
        return tmpinteresxfacR.findByIdfactura(idfactura)
                .map(Tmpinteresxfac::getInteresapagar)
                .orElseGet(() -> upsertInteresFactura(idfactura));
    }

    @Transactional
    public Map<Long, BigDecimal> findByIdFacturas(Collection<Long> idfacturas) {
        if (idfacturas == null || idfacturas.isEmpty()) {
            return Map.of();
        }

        Map<Long, BigDecimal> intereses = tmpinteresxfacR.findAllByIdfacturaIn(idfacturas).stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getIdfactura() != null)
                .collect(Collectors.toMap(
                        Tmpinteresxfac::getIdfactura,
                        item -> item.getInteresapagar() != null ? item.getInteresapagar() : BigDecimal.ZERO,
                        BigDecimal::add));

        idfacturas.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(idfactura -> intereses.computeIfAbsent(idfactura, this::upsertInteresFactura));

        return intereses;
    }

    @Transactional
    public Map<Long, BigDecimal> refreshByIdFacturas(Collection<Long> idfacturas) {
        if (idfacturas == null || idfacturas.isEmpty()) {
            return Map.of();
        }

        Map<Long, BigDecimal> intereses = new HashMap<>();
        idfacturas.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(idfactura -> intereses.put(idfactura, upsertInteresFactura(idfactura)));

        return intereses;
    }


}
