package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.CategoriaR;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.Pliego24R;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

@Service
public class EmisionServicioOptimizado {

    private final FacturasR dao_facturas;
    private final Pliego24R dao_pliego;
    private final CategoriaR dao_categoria;
    private final RubroxfacR dao_rubroxfac;
    private final DefinirR dao_definir;
    private final EmisionesR dao;

    public EmisionServicioOptimizado(FacturasR dao_facturas,
            Pliego24R dao_pliego,
            CategoriaR dao_categoria,
            RubroxfacR dao_rubroxfac,
            DefinirR dao_definir,
            EmisionesR dao) {
        this.dao_facturas = dao_facturas;
        this.dao_pliego = dao_pliego;
        this.dao_categoria = dao_categoria;
        this.dao_rubroxfac = dao_rubroxfac;
        this.dao_definir = dao_definir;
        this.dao = dao;
    }

    // ----------------- Constantes y utilidades numéricas -----------------

    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final BigDecimal HALF = new BigDecimal("0.5");
    private static final BigDecimal TEN_CENTS = new BigDecimal("0.10");
    private static final BigDecimal FIFTY_CENTS = new BigDecimal("0.50");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static BigDecimal scale2(BigDecimal x) {
        return x.setScale(2, RM);
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

    // ----------------- Servicio principal -----------------

    @Transactional
    public BigDecimal calcularValores(Long cuenta,
            Long idfactura,
            int m3,
            int categoria,
            boolean swMunicipio,
            boolean swAdultoMayor,
            boolean swAguapotable) {

        Facturas factura = dao_facturas.findById(idfactura).orElseThrow();
        // Construimos el contexto (categoría efectiva, pliego y categoría ya
        // consultados)
        EmisionOfCuentaDTO ctx = buildContext(cuenta, idfactura, m3, categoria, swMunicipio, swAdultoMayor,
                swAguapotable, factura);

        // --- Cálculos puros ---
        BigDecimal multa = multas(cuenta);

        BigDecimal ap = baseAguaPotable(ctx);
        BigDecimal al = baseAlcantarillado(ctx);
        BigDecimal sa = baseSaneamiento(ctx);
        BigDecimal cf = calcConservacionFuentes();

        BigDecimal ex = ZERO;
        if (ctx.getCategoria() == 9 && swAdultoMayor && m3 > 34 && m3 <= 70) {
            ex = calcExcedente(ctx);
        }

        BigDecimal total = ap.add(al).add(sa).add(cf).add(ex).add(multa);

        // --- Persistimos en lote (una sola vez) ---
        List<Rubroxfac> rubros = new ArrayList<>(6);
        rubros.add(buildRubro(factura, 1001L, ap));
        rubros.add(buildRubro(factura, 1002L, al));
        rubros.add(buildRubro(factura, 1003L, sa));
        rubros.add(buildRubro(factura, 1004L, cf));
        if (ex.compareTo(ZERO) > 0)
            rubros.add(buildRubro(factura, 1005L, ex));
        if (multa.compareTo(ZERO) > 0)
            rubros.add(buildRubro(factura, 6L, multa));

        upsertRubros(rubros);

        factura.setTotaltarifa(scale2(total));
        factura.setValorbase(scale2(total));
        factura.setFeccrea(LocalDate.now().withDayOfMonth(1));
        dao_facturas.save(factura);

        return scale2(total);
    }

    // ----------------- Batch externo (si lo necesitas) -----------------

    @Transactional
    public List<EmisionesInterface> getSwAguapotable(Long idemision) {
        List<EmisionesInterface> emiI = dao.getSwAguapotable(idemision);
        // Procesa uno por uno (sin paralelismo sobre la misma sesión JPA)
        emiI.forEach(e -> {
            calcularValores(
                    e.getCuenta(),
                    e.getIdfactura(),
                    e.getM3(),
                    e.getCategoria(),
                    e.getSwMunicipio(),
                    e.getSwAdultoMayor(),
                    e.getSwAguapotable());
        });
        return emiI;
    }

    // ----------------- Persistencia optimizada de rubros -----------------

    @Transactional
    private void upsertRubros(List<Rubroxfac> nuevos) {
        if (nuevos == null || nuevos.isEmpty())
            return;

        final Long idfac = nuevos.get(0).getIdfactura_facturas().getIdfactura();

        // De-duplicar la entrada: si vienen repetidos en memoria, nos quedamos con el
        // último
        Map<Long, Rubroxfac> dedup = new LinkedHashMap<>();
        for (Rubroxfac r : nuevos) {
            Long idrubro = r.getIdrubro_rubros().getIdrubro();
            dedup.put(idrubro, r); // el último gana
        }
        Set<Long> rubrosAReemplazar = dedup.keySet();

        // 1) Borrar TODOS los existentes de esa factura para esos rubros (incluye
        // duplicados)
        dao_rubroxfac.deleteByFacturaAndRubroIn(idfac, rubrosAReemplazar);

        // 2) Insertar únicamente los nuevos (de-duplicados)
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

        // Si NO es residencial y no tiene pliego, intentamos cargarlo desde BD
        if (!esResidencial && v.getPliego24() == null) {
            Pliego24 p = dao_pliego._findBloque(cat, v.getM3());
            if (p == null) {
                // Evita NPE más adelante con un dummy
                p = new Pliego24();
                p.setPorc(BigDecimal.ONE); // 100%
                p.setAgua(BigDecimal.ZERO);
                p.setSaneamiento(BigDecimal.ZERO);
            }
            v.setPliego24(p);
        }

        // Si es residencial y el pliego no existe, igual inicializamos por seguridad
        if (v.getPliego24() == null) {
            Pliego24 p = new Pliego24();
            p.setPorc(BigDecimal.ONE);
            p.setAgua(BigDecimal.ZERO);
            p.setSaneamiento(BigDecimal.ZERO);
            v.setPliego24(p);
        }
    }

    // ----------------- Construcción del contexto -----------------

    private EmisionOfCuentaDTO buildContext(Long cuenta, Long idfactura, int m3, int categoria,
            boolean swMunicipio, boolean swAdultoMayor,
            boolean swAguapotable, Facturas factura) {
        EmisionOfCuentaDTO v = new EmisionOfCuentaDTO();
        v.setCuenta(cuenta);
        v.setIdfactura(idfactura);
        v.setFactura(factura);
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

    private static final BigDecimal DEFAULT_PORC = BigDecimal.ZERO; // decide tu valor por defecto

    // ----------------- Cálculos puros (sin efectos colaterales) -----------------
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
        if (pliego == null || pliego.getPorc() == null) {
            return DEFAULT_PORC;
        }

        return pliego.getPorc();
    } // 👈 AQUÍ SE CIERRA CORRECTAMENTE EL MÉTODO getPorcentaje

    // 🔹 Método baseAguaPotable separado
    private BigDecimal baseAguaPotable(EmisionOfCuentaDTO v) {
        ensurePliego(v); // 👈 evita NullPointerException

        BigDecimal porcResidOPliego = getPorcentaje(v);
        BigDecimal apFijo = v.getCategorias().getFijoagua()
                .subtract(TEN_CENTS)
                .multiply(porcResidOPliego);

        BigDecimal porcPliego = v.getPliego24().getPorc();
        BigDecimal apVar = BigDecimal.valueOf(v.getM3())
                .multiply(v.getPliego24().getAgua())
                .multiply(porcPliego);

        BigDecimal total = apFijo.add(apVar);

        if (v.getCategoria() == 4 && v.isSwMunicipio())
            total = total.multiply(HALF);
        if (v.getCategoria() == 9)
            total = total.multiply(HALF);

        return total;
    }

    // 🔹 Método baseAlcantarillado separado
    private BigDecimal baseAlcantarillado(EmisionOfCuentaDTO v) {
        ensurePliego(v); // 👈 agregado

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

        if (v.getCategoria() == 4 && v.isSwMunicipio())
            total = total.multiply(HALF);
        if (v.getCategoria() == 9)
            total = total.multiply(HALF);

        // + hidro al final
        return total.add(hidrosuccionador(v, porc));
    }

    private BigDecimal baseSaneamiento(EmisionOfCuentaDTO v) {
        ensurePliego(v); // 👈 agregado

        if (v.isSwAguapotable())
            return ZERO;

        BigDecimal porc = v.getPliego24().getPorc();
        BigDecimal total = BigDecimal.valueOf(v.getM3())
                .multiply(v.getPliego24().getSaneamiento().multiply(HALF))
                .multiply(porc);

        if (v.getCategoria() == 4 && v.isSwMunicipio())
            total = total.multiply(HALF);
        if (v.getCategoria() == 9)
            total = total.multiply(HALF);

        return total;
    }

    private BigDecimal calcConservacionFuentes() {
        return TEN_CENTS;
    }

    private BigDecimal hidrosuccionador(EmisionOfCuentaDTO v, BigDecimal porc) {
        return FIFTY_CENTS.multiply(porc);
    }

    // --- Excedente: cálculo diferencial sin persistir nada ---
    private BigDecimal calcExcedente(EmisionOfCuentaDTO base) {
        // v1 = con m3 actual forzando categoría 1
        EmisionOfCuentaDTO v1 = copyForExcedente(base, base.getM3());
        // v2 = con m3-1 forzando categoría 1
        EmisionOfCuentaDTO v2 = copyForExcedente(base, base.getM3() - 1);

        BigDecimal s1 = baseAguaPotable(v1).add(baseAlcantarillado(v1)).add(baseSaneamiento(v1))
                .add(calcConservacionFuentes());
        BigDecimal s2 = baseAguaPotable(v2).add(baseAlcantarillado(v2)).add(baseSaneamiento(v2))
                .add(calcConservacionFuentes());

        return s1.subtract(s2);
    }

    // Copia “pura” para excedente (categoría 1, sw's iguales, pliego/categoría
    // recalculados)
    private EmisionOfCuentaDTO copyForExcedente(EmisionOfCuentaDTO from, int m3Nuevo) {
        EmisionOfCuentaDTO v = new EmisionOfCuentaDTO();
        v.setCuenta(from.getCuenta());
        v.setIdfactura(from.getIdfactura());
        v.setFactura(from.getFactura());
        v.setM3(Math.max(0, m3Nuevo));
        v.setSwMunicipio(from.isSwMunicipio());
        v.setSwAdultoMayor(from.isSwAdultoMayor());
        v.setSwAguapotable(from.isSwAguapotable());
        v.setCategoria(1);
        v.setCategoria(1);

        Pliego24 pl = dao_pliego._findBloque(1, v.getM3());
        v.setPliego24(pl);

        Categorias cat = dao_categoria.getCategoriaById(1);
        v.setCategorias(cat);

        return v;
    }

    // ----------------- Multas -----------------

    private BigDecimal multas(Long cuenta) {
        List<Long> idfacturas = dao_facturas.findSinCobroAbo(cuenta);
        if (idfacturas == null)
            return ZERO;

        long nroPendientes = idfacturas.size();
        if (nroPendientes <= 2)
            return ZERO;

        Definir definir = dao_definir.findTopByOrderByIddefinirDesc();
        if (Objects.isNull(definir) || Objects.isNull(definir.getRbu()))
            return ZERO;

        return definir.getRbu().multiply(BigDecimal.valueOf(0.005));
    }
}
