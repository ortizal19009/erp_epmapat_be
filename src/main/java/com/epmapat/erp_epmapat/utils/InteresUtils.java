package com.epmapat.erp_epmapat.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class InteresUtils {
    private InteresUtils() {}

    public static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    public static final int SCALE_MONEY = 6;        // ajusta a tu BD (p. ej., DECIMAL(18,6))
    public static final int SCALE_PERCENT = 10;

    public static BigDecimal bd(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return new BigDecimal(v.toString());
        return new BigDecimal(v.toString());
    }

    /** Genera YearMonth desde 'from' inclusive hasta 'to' inclusive. */
    public static List<YearMonth> range(YearMonth from, YearMonth to) {
        if (from.isAfter(to)) return Collections.emptyList();
        long months = ChronoUnit.MONTHS.between(from, to);
        return Stream.iterate(from, ym -> ym.plusMonths(1))
                .limit(months + 1)
                .collect(Collectors.toList());
    }


    /** Convierte porcentaje (p.ej. 1.2) a ratio (0.012000...) */
    public static BigDecimal pctToRatio(BigDecimal pct) {
        return pct == null ? BigDecimal.ZERO :
                pct.divide(BigDecimal.valueOf(100), SCALE_PERCENT, RoundingMode.HALF_UP);
    }

    /** Capitalización mensual: factor = ∏ (1 + r_i). */
    public static BigDecimal compoundFactor(List<BigDecimal> monthlyRatios) {
        BigDecimal factor = BigDecimal.ONE;
        for (BigDecimal r : monthlyRatios) {
            factor = factor.multiply(BigDecimal.ONE.add(r, MC), MC);
        }
        return factor;
    }

    public static BigDecimal compoundInterest(BigDecimal principal, List<BigDecimal> monthlyRatios) {
        if (principal == null) principal = BigDecimal.ZERO;
        if (monthlyRatios == null || monthlyRatios.isEmpty()) return BigDecimal.ZERO;
        BigDecimal factor = compoundFactor(monthlyRatios);
        return principal.multiply(factor.subtract(BigDecimal.ONE, MC), MC).setScale(SCALE_MONEY, RoundingMode.HALF_UP);
    }
}