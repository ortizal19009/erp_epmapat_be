package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.repositorio.InteresesR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InteresRepositoryAdapter {
    private final InteresesR interesesR;

    /**
     * Trae porcentajes en % por YearMonth, llenando faltantes con 0 si lo pides.
     */
    public Map<YearMonth, BigDecimal> getPorcentajesPorRango(YearMonth desde, YearMonth hasta) {
        if (desde.isAfter(hasta))
            return Collections.emptyMap();

        long yStart = desde.getYear();
        long mStart = desde.getMonthValue();
        long yEnd = hasta.getYear();
        long mEnd = hasta.getMonthValue();

        List<Intereses> rows = interesesR.findByRango(yStart, mStart, yEnd, mEnd);

        Map<YearMonth, BigDecimal> map = new HashMap<>();
        for (Intereses it : rows) {
            YearMonth ym = YearMonth.of(it.getAnio().intValue(), it.getMes().intValue());
            map.put(ym, it.getPorcentaje() == null ? BigDecimal.ZERO : it.getPorcentaje());
        }
        return map;
    }
}