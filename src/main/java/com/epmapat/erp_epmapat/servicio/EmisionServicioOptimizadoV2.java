package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.DTO.EmisionOfCuentaDTO;
import com.epmapat.erp_epmapat.interfaces.EmisionesInterface;
import com.epmapat.erp_epmapat.modelo.Categorias;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Pliego24;
import com.epmapat.erp_epmapat.modelo.Recargosxcuenta;
import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.CategoriaR;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.Pliego24R;
import com.epmapat.erp_epmapat.repositorio.RecargosxcuentaR;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

@Service
public class EmisionServicioOptimizadoV2 {

    private final FacturasR dao_facturas;
    private final Pliego24R dao_pliego;
    private final CategoriaR dao_categoria;
    private final RubroxfacR dao_rubroxfac;
    private final DefinirR dao_definir;
    private final EmisionesR dao;

    // ✅ Recargos (solo al guardar / calcularValores)
    private final RecargosxcuentaR dao_recargos;

    public EmisionServicioOptimizadoV2(
            FacturasR dao_facturas,
            Pliego24R dao_pliego,
            CategoriaR dao_categoria,
            RubroxfacR dao_rubroxfac,
            DefinirR dao_definir,
            EmisionesR dao,
            RecargosxcuentaR dao_recargos) {

        this.dao_facturas = dao_facturas;
        this.dao_pliego = dao_pliego;
        this.dao_categoria = dao_categoria;
        this.dao_rubroxfac = dao_rubroxfac;
        this.dao_definir = dao_definir;
        this.dao = dao;
        this.dao_recargos = dao_recargos;
    }

    // ----------------- Constantes y utilidades numéricas -----------------

    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final BigDecimal HALF = new BigDecimal("0.5");
    private static final BigDecimal TEN_CENTS = new BigDecimal("0.10");
    private static final BigDecimal FIFTY_CENTS = new BigDecimal("0.50");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    // ==========================
    // RECARGOS FIJOS (SIN BD)
    // ==========================
    private static final Long RUBRO_NOTIFICACION_ID = 2156L;
    private static final BigDecimal RUBRO_NOTIFICACION_VALOR = new BigDecimal("0.50");

    private static final Long RUBRO_INSPECCION_ID = 2155L;
    private static final BigDecimal RUBRO_INSPECCION_VALOR = new BigDecimal("5.00");

    private static BigDecimal scale2(BigDecimal x) {
        return (x == null ? ZERO : x).setScale(2, RM);
    }

    // Tabla de porcentajes residencial (índice por m3, saturado al final)
    private static final BigDecimal[] PORC_RESIDENCIAL = {
            BigDecimal.valueOf(0.777), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78),
            BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.778), BigDecimal.valueOf(0.778),
            BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.68),
            BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.68),
            BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.68),
            BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.676), BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.678),
            BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7)
    };

    // =====================================================================
    // ✅ MÉTODO REAL: CALCULA + GUARDA + APLICA RECARGOS (requiere idemision)
    // =====================================================================

    @Transactional
    public BigDecimal calcularValores(
            Long idemision,
            Long cuenta, // idabonado
            Long idfactura,
            int m3,
            int categoria,
            boolean swMunicipio,
            boolean swAdultoMayor,
            boolean swAguapotable,
            boolean swbasura,
            boolean swRefacturacion) {

        // ---------------------------
        // 1) Cargar factura y contexto
        // ---------------------------
        // // Asegurar no negativos
        Facturas factura = dao_facturas.findById(idfactura).orElseThrow();

        EmisionOfCuentaDTO ctx = buildContext(
                cuenta, idfactura, m3, categoria, swMunicipio, swAdultoMayor, swAguapotable, swbasura, factura);

        // ---------------------------
        // 2) Calcular rubros base
        // ---------------------------
        BigDecimal multa = multas(cuenta);
        /*
         * BigDecimal multa_basura = multas_basura(cuenta);
         */
        BigDecimal rb = ZERO;
        BigDecimal ap = baseAguaPotable(ctx);
        BigDecimal al = baseAlcantarillado(ctx);
        BigDecimal sa = baseSaneamiento(ctx);
        BigDecimal cf = calcConservacionFuentes(ctx.getCategoria());
        BigDecimal cfepmapat = calcConservacionFuentesEpmapat(ctx.getCategoria());
        if (ctx.isSwbasura()) {
            rb = rb.add(new BigDecimal("0.52").setScale(2, RM));
        } else {
            rb = calcRecoleccionBasura(ctx);
        }
        BigDecimal rbepmapat = recaudacionBasura();

        // Excedente (si aplica)
        /*
         * BigDecimal ex = ZERO;
         * if (ctx.getCategoria() == 9 && swAdultoMayor && m3 > 34 && m3 <= 70) {
         * ex = calcExcedente(ctx);
         * }
         */

        // Total base (SIN recargos todavía)
        BigDecimal total = ap.add(al)
                .add(sa)
                .add(cf)
                .add(rb)
                .add(rbepmapat)
                .add(cfepmapat);

        /*
         * if (ex.compareTo(ZERO) > 0)
         * total = total.add(ex);
         */
        if (multa.compareTo(ZERO) > 0)
            total = total.add(multa);
        /*
         * if (multa_basura.compareTo(ZERO) > 0)
         * total = total.add(multa_basura);
         */

        // ---------------------------
        // 3) Armar lista de rubros a guardar
        // ---------------------------
        List<Rubroxfac> rubros = new ArrayList<>(30);

        rubros.add(buildRubro(factura, 1001L, ap));
        rubros.add(buildRubro(factura, 1002L, al));
        rubros.add(buildRubro(factura, 1003L, sa));
        rubros.add(buildRubro(factura, 1006L, cf));
        rubros.add(buildRubro(factura, 1007L, cfepmapat));
        rubros.add(buildRubro(factura, 1008L, rb));
        rubros.add(buildRubro(factura, 1009L, rbepmapat));

        /*
         * if (ex.compareTo(ZERO) > 0) {
         * rubros.add(buildRubro(factura, 1005L, ex));
         * }
         */
        if (multa.compareTo(ZERO) > 0) {
            rubros.add(buildRubro(factura, 6L, multa));
        }
        /*
         * if (multa_basura.compareTo(ZERO) > 0) {
         * rubros.add(buildRubro(factura, 1011L, multa_basura));
         * }
         */
        // ---------------------------
        // 4) Recargos por emisión + abonado (SOLO guardar)
        // ---------------------------
        List<Recargosxcuenta> recargos = dao_recargos.findByEmisionAndAbonado(idemision, cuenta);
        if (recargos != null && !recargos.isEmpty()) {
            for (Recargosxcuenta rc : recargos) {
                if (rc == null)
                    continue;

                Long idrubro = null;

                // Si el registro guarda relación a Rubros
                if (rc.getIdrubro_rubros() != null && rc.getIdrubro_rubros().getIdrubro() != null) {
                    idrubro = rc.getIdrubro_rubros().getIdrubro();
                }

                // Si NO tienes idrubro en recargos, podrías mapear por tipo
                if (idrubro == null) {
                    if (rc.getTipo() == 1)
                        idrubro = RUBRO_NOTIFICACION_ID; // 2156
                    if (rc.getTipo() == 2)
                        idrubro = RUBRO_INSPECCION_ID; // 2155
                }

                if (idrubro == null)
                    continue;

                // ✅ valores fijos (sin BD)
                BigDecimal valor = null;
                if (Objects.equals(idrubro, RUBRO_NOTIFICACION_ID))
                    valor = RUBRO_NOTIFICACION_VALOR; // 0.50
                if (Objects.equals(idrubro, RUBRO_INSPECCION_ID))
                    valor = RUBRO_INSPECCION_VALOR; // 5.00

                // Si cae aquí, es porque tu recargo trae otro rubro que no estás cubriendo
                if (valor == null) {
                    continue;
                }

                rubros.add(buildRubro(factura, idrubro, valor));
                total = total.add(valor);

            }
        }

        // ---------------------------
        // 5) Guardar rubros y factura
        // ---------------------------
        upsertRubros(rubros);

        factura.setTotaltarifa(scale2(total));
        factura.setValorbase(scale2(total));
        factura.setFeccrea(LocalDate.now().withDayOfMonth(1));
        dao_facturas.save(factura);

        return scale2(total);
    }

    // =====================================================================
    // ✅ SIMULADOR: SOLO CALCULA (NO idemision, NO guarda, NO recargos)
    // =====================================================================

    @Transactional(readOnly = true)
    public Object simularValores(
            int m3,
            int categoria,
            boolean swMunicipio,
            boolean swAdultoMayor,
            boolean swAguapotable) {

        Map<String, Object> respuesta = new HashMap<>();

        EmisionOfCuentaDTO ctx = buildContext_simulador(m3, categoria, swMunicipio, swAdultoMayor, swAguapotable);

        BigDecimal ap = baseAguaPotable(ctx);
        BigDecimal al = baseAlcantarillado(ctx);
        BigDecimal sa = baseSaneamiento(ctx);
        BigDecimal cf = calcConservacionFuentes(ctx.getCategoria());
        BigDecimal cfepmapat = calcConservacionFuentesEpmapat(ctx.getCategoria());
        BigDecimal rb = calcRecoleccionBasura(ctx);
        BigDecimal rbepmapat = recaudacionBasura();

        /*
         * BigDecimal ex = ZERO;
         * if (ctx.getCategoria() == 9 && swAdultoMayor && m3 > 34 && m3 <= 70) {
         * ex = calcExcedente(ctx);
         * }
         */

        BigDecimal total = ap.add(al).add(sa).add(cf).add(rb).add(rbepmapat).add(cfepmapat);

        respuesta.put("Agua Potable", ap);
        respuesta.put("Alcantarillado", al);
        respuesta.put("Saneamiento", sa);
        respuesta.put("Conservacion Fuentes", cf);
        respuesta.put("Conservacion Fuentes Epmapat", cfepmapat);
        respuesta.put("Recoleccion Basura", rb);
        respuesta.put("Recaudacion Basura", rbepmapat);
        /*
         * if (ex.compareTo(ZERO) > 0)
         * respuesta.put("Excedente", ex);
         */
        respuesta.put("Total", total.setScale(2, RM));

        return respuesta;
    }

    // ----------------- Batch externo (si lo necesitas) -----------------

    @Transactional
    public List<EmisionesInterface> getSwAguapotable(Long idemision) {
        List<EmisionesInterface> emiI = dao.getSwAguapotable(idemision);
        emiI.forEach(e -> {
            calcularValores(
                    idemision,
                    e.getCuenta(),
                    e.getIdfactura(),
                    e.getM3(),
                    e.getCategoria(),
                    e.getSwMunicipio(),
                    e.getSwAdultoMayor(),
                    e.getSwAguapotable(),
                    e.getSwbasura(),
                    e.getSwRefacturacion());
        });
        return emiI;
    }
    // ----------------- Persistencia optimizada de rubros -----------------

    @Transactional
    private void upsertRubros(List<Rubroxfac> nuevos) {
        if (nuevos == null || nuevos.isEmpty())
            return;

        final Long idfac = nuevos.get(0).getIdfactura_facturas().getIdfactura();

        // De-duplicar entrada (último gana)
        Map<Long, Rubroxfac> dedup = new LinkedHashMap<>();
        for (Rubroxfac r : nuevos) {
            Long idrubro = r.getIdrubro_rubros().getIdrubro();
            dedup.put(idrubro, r);
        }
        Set<Long> rubrosAReemplazar = dedup.keySet();

        dao_rubroxfac.deleteByFacturaAndRubroIn(idfac, rubrosAReemplazar);
        dao_rubroxfac.saveAll(dedup.values());
    }

    private Rubroxfac buildRubro(Facturas factura, Long idrubro, BigDecimal valor) {
        Rubroxfac r = new Rubroxfac();
        Rubros rub = new Rubros();
        rub.setIdrubro(idrubro);
        r.setIdrubro_rubros(rub);
        r.setIdfactura_facturas(factura);
        r.setCantidad(1F);
        r.setValorunitario(scale2(valor));
        return r;
    }

    // ----------------- Asegurar pliego existente -----------------

    private void ensurePliego(EmisionOfCuentaDTO v) {
        if (v == null)
            return;

        Integer cat = v.getCategoria();
        boolean esResidencial = Objects.equals(cat, 1) || Objects.equals(cat, 9);

        if (!esResidencial && v.getPliego24() == null) {
            Pliego24 p = dao_pliego._findBloque(cat, v.getM3());
            if (p == null) {
                p = new Pliego24();
                p.setPorc(BigDecimal.ONE);
                p.setAgua(BigDecimal.ZERO);
                p.setSaneamiento(BigDecimal.ZERO);
            }
            v.setPliego24(p);
        }

        if (v.getPliego24() == null) {
            Pliego24 p = new Pliego24();
            p.setPorc(BigDecimal.ONE);
            p.setAgua(BigDecimal.ZERO);
            p.setSaneamiento(BigDecimal.ZERO);
            v.setPliego24(p);
        }
    }

    // ----------------- Construcción del contexto -----------------

    private EmisionOfCuentaDTO buildContext(
            Long cuenta,
            Long idfactura,
            int m3,
            int categoria,
            boolean swMunicipio,
            boolean swAdultoMayor,
            boolean swAguapotable,
            boolean swbasura,
            Facturas factura) {

        EmisionOfCuentaDTO v = new EmisionOfCuentaDTO();
        v.setCuenta(cuenta);
        v.setIdfactura(idfactura);
        v.setFactura(factura);
        v.setM3(m3);
        v.setSwMunicipio(swMunicipio);
        v.setSwAdultoMayor(swAdultoMayor);
        v.setSwAguapotable(swAguapotable);
        v.setCategoria(categoria);
        v.setSwbasura(swbasura);

        int catEfectiva = categoria;
        if ((categoria == 1 || (categoria == 9 && swAdultoMayor)) && m3 > 70) {
            catEfectiva = 2;
        }
        v.setCategoria(catEfectiva);

        Pliego24 pliego = dao_pliego._findBloque(catEfectiva, m3);
        v.setPliego24(pliego);

        Categorias cat = dao_categoria.getCategoriaById(catEfectiva);
        v.setCategorias(cat);

        return v;
    }

    private EmisionOfCuentaDTO buildContext_simulador(
            int m3,
            int categoria,
            boolean swMunicipio,
            boolean swAdultoMayor,
            boolean swAguapotable) {

        EmisionOfCuentaDTO v = new EmisionOfCuentaDTO();
        v.setM3(m3);
        v.setSwMunicipio(swMunicipio);
        v.setSwAdultoMayor(swAdultoMayor);
        v.setSwAguapotable(swAguapotable);
        v.setCategoria(categoria);

        int catEfectiva = categoria;
        if ((categoria == 1 || (categoria == 9 && swAdultoMayor)) && m3 > 70) {
            catEfectiva = 2;
        }
        v.setCategoria(catEfectiva);

        Pliego24 pliego = dao_pliego._findBloque(catEfectiva, m3);
        v.setPliego24(pliego);

        Categorias cat = dao_categoria.getCategoriaById(catEfectiva);
        v.setCategorias(cat);

        return v;
    }

    private static final BigDecimal DEFAULT_PORC = BigDecimal.ZERO;

    // ----------------- Cálculos puros -----------------

    private BigDecimal getPorcentaje(EmisionOfCuentaDTO v) {
        if (v == null)
            return DEFAULT_PORC;

        Integer cat = v.getCategoria();
        Integer m3 = v.getM3();
        int consumo = (m3 == null || m3 < 0) ? 0 : m3;

        if (Objects.equals(cat, 1) || Objects.equals(cat, 9)) {
            int idx = Math.min(consumo, PORC_RESIDENCIAL.length - 1);
            BigDecimal porc = PORC_RESIDENCIAL[idx];
            return porc != null ? porc : DEFAULT_PORC;
        }

        Pliego24 pliego = v.getPliego24();
        if (pliego == null || pliego.getPorc() == null)
            return DEFAULT_PORC;

        return pliego.getPorc();
    }

    private BigDecimal baseAguaPotable(EmisionOfCuentaDTO v) {
        ensurePliego(v);

        BigDecimal porcResidOPliego = getPorcentaje(v);
        BigDecimal apFijo = v.getCategorias().getFijoagua()
                .subtract(TEN_CENTS)
                .multiply(porcResidOPliego);

        BigDecimal porcPliego = v.getPliego24().getPorc();
        BigDecimal apVar = BigDecimal.valueOf(v.getM3())
                .multiply(v.getPliego24().getAgua())
                .multiply(porcPliego);

        BigDecimal total = apFijo.add(apVar);
        /*
         * if (v.getCategoria() == 4 && v.isSwMunicipio())
         * total = total.multiply(HALF);
         */
        if (v.getCategoria() == 9)
            total = total.multiply(HALF);

        // ✅ rango correcto
        if (v.getCategoria() == 9 && v.isSwAdultoMayor() && (v.getM3() > 34 && v.getM3() < 70)) {
            total = total.add(baseAguaPotableExcedente(v));
        }

        return total.setScale(2, RM);
    }

    private BigDecimal baseAguaPotableExcedente(EmisionOfCuentaDTO v) {
        ensurePliego(v);

        int n = v.getM3();
        if (n <= 0)
            return ZERO;

        int categoriaOriginal = v.getCategoria();
        Pliego24 pliegoOriginal = v.getPliego24();
        Categorias categoriaObjOriginal = v.getCategorias();

        try {
            v.setCategoria(1); // ✅ calcular como categoría 1
            v.setPliego24(dao_pliego._findBloque(1, n));
            v.setCategorias(dao_categoria.getCategoriaById(1));
            ensurePliego(v);

            BigDecimal porcPliego = v.getPliego24().getPorc();

            // apVar(n) - apVar(n-1) (lineal) => 1 * tarifa * porc
            BigDecimal excedente = BigDecimal.ONE
                    .multiply(v.getPliego24().getAgua())
                    .multiply(porcPliego);

            return excedente.setScale(2, RM);
        } finally {
            v.setCategoria(categoriaOriginal); // ✅ restaurar
            v.setPliego24(pliegoOriginal);
            v.setCategorias(categoriaObjOriginal);
        }
    }

    private BigDecimal baseAlcantarillado(EmisionOfCuentaDTO v) {
        ensurePliego(v);

        if (v.isSwAguapotable())
            return ZERO;

        BigDecimal porc = v.getPliego24().getPorc();

        BigDecimal fijo = v.getCategorias().getFijosanea()
                .subtract(FIFTY_CENTS)
                .multiply(porc);

        BigDecimal variable = BigDecimal.valueOf(v.getM3())
                .multiply(v.getPliego24().getSaneamiento().multiply(HALF))
                .multiply(porc);

        BigDecimal total = fijo.add(variable);
        /*
         * if (v.getCategoria() == 4 && v.isSwMunicipio())
         * total = total.multiply(HALF);
         */
        if (v.getCategoria() == 9)
            total = total.multiply(HALF);
        // ✅ rango correcto
        if (v.getCategoria() == 9 && v.isSwAdultoMayor() && (v.getM3() > 34 && v.getM3() < 70)) {
            total = total.add(baseAlcantarilladoExcedente(v));
        }

        return total.add(hidrosuccionador(v, porc)).setScale(2, RM);
    }

    private BigDecimal baseAlcantarilladoExcedente(EmisionOfCuentaDTO v) {
        ensurePliego(v);

        if (v.isSwAguapotable())
            return ZERO;

        int n = v.getM3();
        if (n <= 0)
            return ZERO;

        int categoriaOriginal = v.getCategoria();
        Pliego24 pliegoOriginal = v.getPliego24();
        Categorias categoriaObjOriginal = v.getCategorias();

        try {
            v.setCategoria(1); // ✅ calcular como categoría 1
            v.setPliego24(dao_pliego._findBloque(1, n));
            v.setCategorias(dao_categoria.getCategoriaById(1));
            ensurePliego(v);

            BigDecimal porc = v.getPliego24().getPorc();

            BigDecimal excedente = BigDecimal.ONE
                    .multiply(v.getPliego24().getSaneamiento().multiply(HALF))
                    .multiply(porc);

            return excedente.setScale(2, RM);
        } finally {
            v.setCategoria(categoriaOriginal); // ✅ restaurar
            v.setPliego24(pliegoOriginal);
            v.setCategorias(categoriaObjOriginal);
        }
    }

    private BigDecimal baseSaneamiento(EmisionOfCuentaDTO v) {
        ensurePliego(v);

        if (v.isSwAguapotable())
            return ZERO;

        BigDecimal porc = v.getPliego24().getPorc();
        BigDecimal total = BigDecimal.valueOf(v.getM3())
                .multiply(v.getPliego24().getSaneamiento().multiply(HALF))
                .multiply(porc);
        /*
         * if (v.getCategoria() == 4 && v.isSwMunicipio())
         * total = total.multiply(HALF);
         */
        if (v.getCategoria() == 9)
            total = total.multiply(HALF);

        return total.setScale(2, RM);
    }

    private BigDecimal calcConservacionFuentes(int categoria) {
        if (categoria == 9)
            return new BigDecimal("0.15").setScale(2, RM);
        return new BigDecimal("0.30").setScale(2, RM);
    }

    private BigDecimal calcConservacionFuentesEpmapat(int categoria) {
        if (categoria == 1)
            return new BigDecimal("0.20").setScale(2, RM);
        if (categoria == 2)
            return new BigDecimal("0.35").setScale(2, RM);
        if (categoria == 3)
            return new BigDecimal("0.50").setScale(2, RM);
        if (categoria == 4)
            return new BigDecimal("1.00").setScale(2, RM);
        if (categoria == 9)
            return new BigDecimal("0.10").setScale(2, RM);
        return ZERO;
    }

    private BigDecimal hidrosuccionador(EmisionOfCuentaDTO v, BigDecimal porc) {
        return FIFTY_CENTS.multiply(porc);
    }

    private BigDecimal recaudacionBasura() {
        return new BigDecimal("0.50").setScale(2, RM);
    }

    // ==========================
    // EXCEDENTE (se mantiene tu lógica)
    // ========================
    // ----------------- CALCULO TARIFA BASURA -----------------

    private BigDecimal calcRecoleccionBasura(EmisionOfCuentaDTO v) {

        BigDecimal TP = new BigDecimal("6.40");
        BigDecimal m = new BigDecimal("0.0034");

        BigDecimal sumco;
        int cat = v.getCategoria();

        if (cat == 1)
            sumco = new BigDecimal("0.3750");

        // sumco = new BigDecimal("0.4565");
        else if (cat == 2)
            sumco = new BigDecimal("0.7807");
        else if (cat == 3)
            sumco = new BigDecimal("4.1063");
        else if (cat == 4)
            sumco = new BigDecimal("10.7031");
        else if (cat == 9)
            sumco = new BigDecimal("0.2281");
        else
            sumco = ZERO;
        /*
         * BigDecimal parteFija = CO.multiply(Lr).add(CF.multiply(iF));
         * BigDecimal parteVariable = Qs.multiply(m3).add(sumco);
         */

        BigDecimal total = TP.multiply(m.multiply(BigDecimal.valueOf(v.getM3())).add(sumco));

        return total.setScale(2, RM);
    }

    // ----------------- Multas -----------------

    private static final BigDecimal PORCENTAJE_MULTA = new BigDecimal("0.005");

    public BigDecimal multas(Long cuenta) {
        // Se recalculan en cada invocación → siempre es "hoy"
        LocalDate fechaDesde = LocalDate.now();
        LocalDate fechaHasta = LocalDate.now();

        List<Long> idfacturas = dao_facturas._calcularPendientesDeAbonados(
                cuenta,
                fechaDesde,
                fechaHasta);

        if (idfacturas == null || idfacturas.isEmpty() || idfacturas.size() <= 1) {
            return ZERO;
        }

        Definir definir = dao_definir.findTopByOrderByIddefinirDesc();

        if (definir == null || definir.getRbu() == null) {
            return ZERO;
        }

        return definir.getRbu().multiply(PORCENTAJE_MULTA);
    }

    /*
     * private BigDecimal multas_basura(Long cuenta) {
     * List<Long> idfacturas = dao_facturas.findSinCobroAbo(cuenta);
     * if (idfacturas == null)
     * return ZERO;
     * 
     * long nroPendientes = idfacturas.size();
     * if (nroPendientes < 1)
     * return ZERO;
     * 
     * Definir definir = dao_definir.findTopByOrderByIddefinirDesc();
     * if (Objects.isNull(definir) || Objects.isNull(definir.getRbu()))
     * return ZERO;
     * 
     * return definir.getRbu().multiply(BigDecimal.valueOf(0.01));
     * }
     */
}
