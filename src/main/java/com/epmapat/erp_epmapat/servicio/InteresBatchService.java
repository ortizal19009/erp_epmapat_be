package com.epmapat.erp_epmapat.servicio;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.FacLite;
import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.modelo.Tmpinteresxfac;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.InteresesR;
import com.epmapat.erp_epmapat.repositorio.TmpinteresxfacR;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class InteresBatchService {

    private final FacturasR facturasR;
    private final TmpinteresxfacR tmpRepo;
    private final InteresesR interesesR;

    public InteresBatchService(FacturasR facturasR, TmpinteresxfacR tmpRepo, InteresesR interesesR) {
        this.facturasR = facturasR;
        this.tmpRepo = tmpRepo;
        this.interesesR = interesesR;
    }

    // ====== Config numérica ======
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    private static final int SCALE_MONEY = 6;
    private static final int SCALE_PERCENT = 10;

    // ====== Config batch ======
    // Para el IN(...) de JPA: mantener esto muy por debajo de 32767
    private static final int IN_CHUNK = 10_000;

    // SaveAll por lotes (esto es distinto al IN_CHUNK)
    private static final int SAVE_BATCH = 1_000;

    // ====== Reglas de negocio ======
    public record ReglaBatch(
            int lagMeses,
            boolean incluirMesInicio,
            boolean omitirSiRangoUnMes,
            boolean omitirSiUnicoMesEsActual
    ) {
        public static ReglaBatch porDefecto() {
            return new ReglaBatch(0, false, false, true);
        }
    }

    // ====== API pública ======

    public Map<String, Object> recalcularIntereses(LocalDate fechaCorte) {
        return recalcularIntereses(fechaCorte, ReglaBatch.porDefecto());
    }

    @Transactional
    public Map<String, Object> recalcularIntereses(LocalDate fechaCorte, ReglaBatch regla) {

        List<FacLite> facturas = facturasR.getSinCobrarLite();
        if (facturas == null || facturas.isEmpty()) {
            return Map.of("status", 200, "totalFacturas", 0, "message", "Sin facturas");
        }

        final YearMonth corteYM = YearMonth.from(fechaCorte);
        final YearMonth endYMGlobal = corteYM.minusMonths(Math.max(0, regla.lagMeses()));

        YearMonth minYM = facturas.stream()
                .map(f -> inicioEfectivo(f, regla))
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(endYMGlobal);

        if (minYM.isAfter(endYMGlobal)) {
            return Map.of("status", 200, "totalFacturas", facturas.size(), "message", "Sin meses para capitalizar");
        }

        List<YearMonth> meses = rangeInclusive(minYM, endYMGlobal);
        if (meses.isEmpty()) {
            return Map.of("status", 200, "totalFacturas", facturas.size(), "message", "Sin meses para capitalizar");
        }

        Map<YearMonth, BigDecimal> pctMap = cargarPorcentajes(meses);
        BigDecimal[] cum = buildCumProducts(meses, pctMap);
        Map<YearMonth, Integer> idx = indexOfMonths(meses);

        Map<YearMonth, BigDecimal> factorDesdeInicio = precomputeFactorsDesdeInicio(
                facturas, regla, corteYM, endYMGlobal, idx, cum
        );

        persistirEnLotes(facturas, factorDesdeInicio, regla);

        return Map.of(
                "status", 200,
                "totalFacturas", facturas.size(),
                "message", "OK",
                "desde", minYM.toString(),
                "hasta", endYMGlobal.toString()
        );
    }

    // ====== Núcleo de cálculo / helpers ======

    private YearMonth inicioEfectivo(FacLite f, ReglaBatch regla) {
        LocalDate base = (f.getFormaPago() != null && f.getFormaPago() == 4)
                ? f.getFecTransfer()
                : f.getFecCrea();
        if (base == null) return null;

        YearMonth ym = YearMonth.from(base);
        return (regla.incluirMesInicio ? ym : ym.plusMonths(1));
    }

    private static List<YearMonth> rangeInclusive(YearMonth from, YearMonth to) {
        if (from == null || to == null || from.isAfter(to)) return Collections.emptyList();
        List<YearMonth> out = new ArrayList<>();
        YearMonth cur = from;
        while (!cur.isAfter(to)) {
            out.add(cur);
            cur = cur.plusMonths(1);
        }
        return out;
    }

    private Map<YearMonth, BigDecimal> cargarPorcentajes(List<YearMonth> meses) {
        Map<YearMonth, BigDecimal> map = new HashMap<>(meses.size() * 2);

        YearMonth desde = meses.get(0);
        YearMonth hasta = meses.get(meses.size() - 1);

        List<Intereses> rows = interesesR.findByRango(
                (long) desde.getYear(), (long) desde.getMonthValue(),
                (long) hasta.getYear(), (long) hasta.getMonthValue()
        );

        for (Intereses it : rows) {
            YearMonth ym = YearMonth.of(it.getAnio().intValue(), it.getMes().intValue());
            map.put(ym, it.getPorcentaje() == null ? BigDecimal.ZERO : it.getPorcentaje());
        }

        for (YearMonth ym : meses) {
            map.putIfAbsent(ym, BigDecimal.ZERO);
        }
        return map;
    }

    private static BigDecimal pctToRatio(BigDecimal pct) {
        if (pct == null) return BigDecimal.ZERO;
        return pct.divide(BigDecimal.valueOf(100), SCALE_PERCENT, RoundingMode.HALF_UP);
    }

    private BigDecimal[] buildCumProducts(List<YearMonth> meses, Map<YearMonth, BigDecimal> pctMap) {
        BigDecimal[] cum = new BigDecimal[meses.size()];
        BigDecimal r0 = pctToRatio(pctMap.get(meses.get(0)));
        cum[0] = BigDecimal.ONE.add(r0, MC);

        for (int i = 1; i < meses.size(); i++) {
            BigDecimal r = pctToRatio(pctMap.get(meses.get(i)));
            cum[i] = cum[i - 1].multiply(BigDecimal.ONE.add(r, MC), MC);
        }
        return cum;
    }

    private Map<YearMonth, Integer> indexOfMonths(List<YearMonth> meses) {
        Map<YearMonth, Integer> idx = new HashMap<>(meses.size() * 2);
        for (int i = 0; i < meses.size(); i++) {
            idx.put(meses.get(i), i);
        }
        return idx;
    }

    private Map<YearMonth, BigDecimal> precomputeFactorsDesdeInicio(
            List<FacLite> facturas,
            ReglaBatch regla,
            YearMonth corteYM,
            YearMonth endYMGlobal,
            Map<YearMonth, Integer> idx,
            BigDecimal[] cum
    ) {
        final Integer endIdxObj = idx.get(endYMGlobal);
        if (endIdxObj == null) return Collections.emptyMap();
        final int endIdx = endIdxObj;

        Set<YearMonth> inicios = facturas.stream()
                .map(f -> inicioEfectivo(f, regla))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<YearMonth, BigDecimal> out = new HashMap<>(inicios.size() * 2);

        for (YearMonth desdeYM : inicios) {
            Integer sIdx = idx.get(desdeYM);

            if (sIdx == null || sIdx > endIdx) {
                out.put(desdeYM, BigDecimal.ONE);
                continue;
            }

            long mesesIncluidos = ChronoUnit.MONTHS.between(desdeYM, endYMGlobal) + 1;

            if (mesesIncluidos == 1) {
                if (regla.omitirSiRangoUnMes) {
                    out.put(desdeYM, BigDecimal.ONE);
                    continue;
                }
                if (regla.omitirSiUnicoMesEsActual && endYMGlobal.equals(corteYM)) {
                    out.put(desdeYM, BigDecimal.ONE);
                    continue;
                }
            }

            BigDecimal numer = cum[endIdx];
            BigDecimal denom = (sIdx == 0) ? BigDecimal.ONE : cum[sIdx - 1];
            if (denom.compareTo(BigDecimal.ZERO) == 0) denom = BigDecimal.ONE;

            out.put(desdeYM, numer.divide(denom, MC));
        }
        return out;
    }

    // ====== FIX REAL: precargar existentes en chunks y guardar por lotes ======

    private void persistirEnLotes(
            List<FacLite> facturas,
            Map<YearMonth, BigDecimal> factorDesdeInicio,
            ReglaBatch regla
    ) {
        LocalDateTime now = LocalDateTime.now();

        // 1) Extraer IDs una sola vez (sin duplicados, sin null)
        List<Long> ids = facturas.stream()
                .map(FacLite::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 2) Precargar existentes SIN reventar el driver (chunk IN)
        Map<Long, Tmpinteresxfac> existentes = precargarExistentesEnChunks(ids);

        // 3) Calcular y guardar por lotes
        List<Tmpinteresxfac> buffer = new ArrayList<>(SAVE_BATCH);

        for (FacLite f : facturas) {
            Long idFactura = f.getId();
            if (idFactura == null) continue;

            YearMonth desdeYM = inicioEfectivo(f, regla);
            if (desdeYM == null) continue;

            BigDecimal principal = (f.getSuma() == null) ? BigDecimal.ZERO : f.getSuma();
            if (principal.signum() <= 0) continue;

            BigDecimal factor = factorDesdeInicio.getOrDefault(desdeYM, BigDecimal.ONE);
            BigDecimal interes = principal.multiply(factor.subtract(BigDecimal.ONE, MC), MC)
                    .setScale(SCALE_MONEY, RoundingMode.HALF_UP);

            Tmpinteresxfac e = existentes.get(idFactura);
            if (e == null) e = new Tmpinteresxfac();

            e.setIdfactura(idFactura);
            e.setInteresapagar(interes);
            e.setFeccorte(now);

            buffer.add(e);

            if (buffer.size() >= SAVE_BATCH) {
                tmpRepo.saveAll(buffer);
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            tmpRepo.saveAll(buffer);
        }
    }

    private Map<Long, Tmpinteresxfac> precargarExistentesEnChunks(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashMap<>();

        Map<Long, Tmpinteresxfac> out = new HashMap<>(ids.size() * 2);

        for (int i = 0; i < ids.size(); i += IN_CHUNK) {
            List<Long> sub = ids.subList(i, Math.min(i + IN_CHUNK, ids.size()));

            List<Tmpinteresxfac> rows = tmpRepo.findAllByIdfacturaIn(sub);
            for (Tmpinteresxfac it : rows) {
                if (it.getIdfactura() != null) {
                    out.put(it.getIdfactura(), it);
                }
            }
        }
        return out;
    }
}
