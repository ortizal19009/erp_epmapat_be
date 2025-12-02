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
    private static final int SCALE_MONEY = 6; // ajusta a la escala de tu columna destino
    private static final int SCALE_PERCENT = 10; // precisión al convertir % -> razón

    // ====== Reglas de negocio ======
    public record ReglaBatch(
            int lagMeses, // cuántos meses excluir al final (ej.: 2)
            boolean incluirMesInicio, // incluir el mes de emisión? (false = arranca el mes siguiente)
            boolean omitirSiRangoUnMes, // si el rango (inclusivo) tiene 1 mes, omitir
            boolean omitirSiUnicoMesEsActual // si el único mes es el "mes actual" (requiere lag=0), omitir
    ) {
        public static ReglaBatch porDefecto() {
            // Emula la lógica previa que comentaste:
            // - Excluir 2 meses (similar a "-2")
            // - NO incluir el mes de inicio (arranca siguiente)
            // - Si queda 1 mes, omite
            return new ReglaBatch(0, false, false, true);
        }
    }

    // ====== API pública ======

    /** Ejecuta el recálculo masivo con reglas por defecto. */
    public Map<String, Object> recalcularIntereses(LocalDate fechaCorte) {
        return recalcularIntereses(fechaCorte, ReglaBatch.porDefecto());
    }

    /** Ejecuta el recálculo masivo con reglas parametrizables. */
    @Transactional
    public Map<String, Object> recalcularIntereses(LocalDate fechaCorte, ReglaBatch regla) {
        // 1) Facturas (proyección liviana)
        List<FacLite> facturas = facturasR.getSinCobrarLite();
        if (facturas == null || facturas.isEmpty()) {
            return Map.of("status", 200, "totalFacturas", 0, "message", "Sin facturas");
        }

        // 2) Rango global de meses
        final YearMonth corteYM = YearMonth.from(fechaCorte);
        final YearMonth endYMGlobal = corteYM.minusMonths(Math.max(0, regla.lagMeses()));

        // minYM: menor YearMonth de inicio EFECTIVO (según reglas de incluirMesInicio)
        YearMonth minYM = facturas.stream()
                .map(f -> inicioEfectivo(f, regla))
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(endYMGlobal);

        if (minYM.isAfter(endYMGlobal)) {
            return Map.of("status", 200, "totalFacturas", facturas.size(), "message", "Sin meses para capitalizar");
        }

        // 3) Meses globales y % mensual (una sola carga)
        List<YearMonth> meses = rangeInclusive(minYM, endYMGlobal);
        Map<YearMonth, BigDecimal> pctMap = cargarPorcentajes(meses);

        // 4) Prefijos de producto: cum[i] = ∏_{0..i} (1 + r_mes)
        BigDecimal[] cum = buildCumProducts(meses, pctMap);

        // Índice inverso (mes -> posición en 'meses')
        Map<YearMonth, Integer> idx = indexOfMonths(meses);
        final int endIdxGlobal = idx.get(endYMGlobal);

        // 5) Precalcular factor desde cada inicio distinto presente
        Map<YearMonth, BigDecimal> factorDesdeInicio = precomputeFactorsDesdeInicio(
                facturas, regla, corteYM, endYMGlobal, idx, cum);

        // 6) Calcular interés O(1) por factura y persistir en lotes
        persistirEnLotes(facturas, factorDesdeInicio, regla, corteYM);

        return Map.of(
                "status", 200,
                "totalFacturas", facturas.size(),
                "message", "OK",
                "desde", minYM.toString(),
                "hasta", endYMGlobal.toString());
    }

    // ====== Núcleo de cálculo / helpers ======

    /**
     * Inicio efectivo según reglas (si formapago==4 usa transfer; si no, feccrea; y
     * aplica incluirMesInicio).
     */
    private YearMonth inicioEfectivo(FacLite f, ReglaBatch regla) {
        LocalDate base = (f.getFormaPago() != null && f.getFormaPago() == 4)
                ? f.getFecTransfer()
                : f.getFecCrea();
        if (base == null)
            return null;
        YearMonth ym = YearMonth.from(base);
        return (regla.incluirMesInicio ? ym : ym.plusMonths(1));
    }

    /** Construye la lista inclusiva [from..to]. */
    private static List<YearMonth> rangeInclusive(YearMonth from, YearMonth to) {
        if (from.isAfter(to))
            return Collections.emptyList();
        List<YearMonth> out = new ArrayList<>();
        YearMonth cur = from;
        while (!cur.isAfter(to)) {
            out.add(cur);
            cur = cur.plusMonths(1);
        }
        return out;
    }

    /**
     * Carga % por YearMonth (en %) para un rango de meses y rellena faltantes con
     * 0%.
     */
    private Map<YearMonth, BigDecimal> cargarPorcentajes(List<YearMonth> meses) {
        Map<YearMonth, BigDecimal> map = new HashMap<>(meses.size() * 2);
        YearMonth desde = meses.get(0);
        YearMonth hasta = meses.get(meses.size() - 1);

        List<Intereses> rows = interesesR.findByRango(
                (long) desde.getYear(), (long) desde.getMonthValue(),
                (long) hasta.getYear(), (long) hasta.getMonthValue());

        for (Intereses it : rows) {
            YearMonth ym = YearMonth.of(it.getAnio().intValue(), it.getMes().intValue());
            map.put(ym, it.getPorcentaje() == null ? BigDecimal.ZERO : it.getPorcentaje());
        }
        // Completar faltantes con 0%
        for (YearMonth ym : meses)
            map.putIfAbsent(ym, BigDecimal.ZERO);
        return map;
    }

    /** Devuelve ratio mensual a partir de % mensual (1.25 -> 0.0125). */
    private static BigDecimal pctToRatio(BigDecimal pct) {
        if (pct == null)
            return BigDecimal.ZERO;
        return pct.divide(BigDecimal.valueOf(100), SCALE_PERCENT, RoundingMode.HALF_UP);
    }

    /** Prefijos acumulados: cum[i] = ∏_{0..i} (1 + r_mes[i]). */
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

    /** Índice inverso: YearMonth -> posición en la lista 'meses'. */
    private Map<YearMonth, Integer> indexOfMonths(List<YearMonth> meses) {
        Map<YearMonth, Integer> idx = new HashMap<>(meses.size() * 2);
        for (int i = 0; i < meses.size(); i++)
            idx.put(meses.get(i), i);
        return idx;
    }

    /**
     * Precalcula el factor compuesto desde cada inicio distinto (según reglas),
     * aplicando:
     * - Sin meses: factor = 1
     * - Si rango tiene 1 mes y 'omitirSiRangoUnMes' => factor = 1
     * - Si rango tiene 1 mes, 'lag=0' y 'omitirSiUnicoMesEsActual' y mes == corteYM
     * => factor = 1
     */
    private Map<YearMonth, BigDecimal> precomputeFactorsDesdeInicio(
            List<FacLite> facturas,
            ReglaBatch regla,
            YearMonth corteYM,
            YearMonth endYMGlobal,
            Map<YearMonth, Integer> idx,
            BigDecimal[] cum) {
        final int endIdx = idx.get(endYMGlobal);

        Set<YearMonth> inicios = facturas.stream()
                .map(f -> inicioEfectivo(f, regla))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<YearMonth, BigDecimal> out = new HashMap<>(inicios.size() * 2);
        for (YearMonth desdeYM : inicios) {
            Integer sIdx = idx.get(desdeYM);

            // Sin meses a capitalizar
            if (sIdx == null || sIdx > endIdx) {
                out.put(desdeYM, BigDecimal.ONE);
                continue;
            }

            long mesesIncluidos = ChronoUnit.MONTHS.between(desdeYM, endYMGlobal) + 1;

            // Reglas de omisión de rangos "pequeños"
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
            if (denom.compareTo(BigDecimal.ZERO) == 0)
                denom = BigDecimal.ONE;

            out.put(desdeYM, numer.divide(denom, MC));
        }
        return out;
    }

    /** Calcula intereses por factura en O(1) y persiste en lotes con saveAll. */
    private void persistirEnLotes(List<FacLite> facturas,
            Map<YearMonth, BigDecimal> factorDesdeInicio,
            ReglaBatch regla,
            YearMonth corteYM) {

        final int batchSize = 1000;
        LocalDateTime now = LocalDateTime.now();
        List<Tmpinteresxfac> buffer = new ArrayList<>(batchSize);

        // Pre-cargar existentes por idfactura (para upsert)
        Map<Long, Tmpinteresxfac> existentes = tmpRepo
                .findAllByIdfacturaIn(facturas.stream().map(FacLite::getId).toList())
                .stream()
                .collect(Collectors.toMap(Tmpinteresxfac::getIdfactura, it -> it));

        for (FacLite f : facturas) {
            YearMonth desdeYM = inicioEfectivo(f, regla);
            if (desdeYM == null)
                continue;

            BigDecimal principal = (f.getSuma() == null) ? BigDecimal.ZERO : f.getSuma();
            if (principal.signum() <= 0)
                continue;

            BigDecimal factor = factorDesdeInicio.getOrDefault(desdeYM, BigDecimal.ONE);
            BigDecimal interes = principal.multiply(factor.subtract(BigDecimal.ONE, MC), MC)
                    .setScale(SCALE_MONEY, RoundingMode.HALF_UP);

            Tmpinteresxfac e = existentes.getOrDefault(f.getId(), new Tmpinteresxfac());
            e.setIdfactura(f.getId());
            e.setInteresapagar(interes);
            e.setFeccorte(now);

            buffer.add(e);
            if (buffer.size() == batchSize) {
                tmpRepo.saveAll(buffer);
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            tmpRepo.saveAll(buffer);
        }
    }
}
