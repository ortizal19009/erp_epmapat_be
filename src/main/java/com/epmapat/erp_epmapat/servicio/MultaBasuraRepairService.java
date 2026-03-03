package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.RecalculoMultaBasuraItemDTO;
import com.epmapat.erp_epmapat.interfaces.FacturaCuentaView;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

@Service
public class MultaBasuraRepairService {

    private static final Long RUBRO_MULTA_BASURA_ID = 1011L;
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final LecturasR lecturasR;
    private final FacturasR facturasR;
    private final RubroxfacR rubroxfacR;
    private final DefinirR definirR;

    public MultaBasuraRepairService(LecturasR lecturasR,
            FacturasR facturasR,
            RubroxfacR rubroxfacR,
            DefinirR definirR) {
        this.lecturasR = lecturasR;
        this.facturasR = facturasR;
        this.rubroxfacR = rubroxfacR;
        this.definirR = definirR;
    }

    private BigDecimal scale2(BigDecimal x) {
        return (x == null ? ZERO : x).setScale(2, RM);
    }

    /**
     * Regla actual (igual a tu método multas_basura):
     * si tiene al menos 1 pendiente -> RBU * 0.01
     */
    private BigDecimal calcularMultaBasura(Long cuenta, BigDecimal rbu, List<Long> pendientes) {
        if (cuenta == null)
            return ZERO;
        if (rbu == null)
            return ZERO;
        if (pendientes == null || pendientes.isEmpty())
            return ZERO;
        return scale2(rbu.multiply(BigDecimal.valueOf(0.01)));
    }

    @Transactional
    public List<RecalculoMultaBasuraItemDTO> recalcularPorRuta(Long idemision, Long idrutaxemision) {

        Definir definir = definirR.findTopByOrderByIddefinirDesc();
        BigDecimal rbu = (definir == null) ? null : definir.getRbu();

        List<FacturaCuentaView> facturasRuta = lecturasR.findFacturasPendientesByEmisionAndRutaXEmision(idemision, idrutaxemision);

        List<RecalculoMultaBasuraItemDTO> out = new ArrayList<>();

        for (FacturaCuentaView row : facturasRuta) {
            Long idfactura = row.getIdfactura();
            Long cuenta = row.getCuenta();

            if (idfactura == null)
                continue;

            Facturas factura = facturasR.findById(idfactura).orElse(null);
            if (factura == null) {
                out.add(new RecalculoMultaBasuraItemDTO(cuenta, idfactura, "FACTURA_NO_EXISTE", ZERO, null));
                continue;
            }
            // 🔒 Seguridad adicional
            if (factura.getPagado() != 0) {
                out.add(new RecalculoMultaBasuraItemDTO(cuenta, idfactura,
                        "FACTURA_YA_PAGADA_NO_MODIFICADA", ZERO, factura.getTotaltarifa()));
                continue;
            }

            // 1) Ver si YA tiene el rubro 1011
            boolean yaTiene = rubroxfacR.existsRubroInFactura(idfactura, RUBRO_MULTA_BASURA_ID);
            if (yaTiene) {
                // Opcional: si quieres, también puedes "cuadrar" el total con suma rubros
                BigDecimal suma = scale2(rubroxfacR.sumRubrosFactura(idfactura));
                factura.setTotaltarifa(suma);
                factura.setValorbase(suma);
                facturasR.save(factura);

                out.add(new RecalculoMultaBasuraItemDTO(cuenta, idfactura, "YA_EXISTIA_RUBRO_1011", ZERO, suma));
                continue;
            }

            // 2) Determinar si "debía" tener multa basura (pendientes)
            // Aquí reutilizas tu query actual:
            List<Long> pendientes = facturasR.findSinCobroAbo(cuenta);

            BigDecimal multaBasura = calcularMultaBasura(cuenta, rbu, pendientes);

            if (multaBasura.compareTo(ZERO) <= 0) {
                // No corresponde generar el rubro
                BigDecimal suma = scale2(rubroxfacR.sumRubrosFactura(idfactura));
                // Aun así puedes cuadrar:
                factura.setTotaltarifa(suma);
                factura.setValorbase(suma);
                facturasR.save(factura);

                out.add(new RecalculoMultaBasuraItemDTO(cuenta, idfactura, "NO_APLICA_MULTA", ZERO, suma));
                continue;
            }

            // 3) Insertar rubro 1011 (limpio por si hay duplicados raros)
            rubroxfacR.deleteByFacturaAndRubro(idfactura, RUBRO_MULTA_BASURA_ID);

            Rubroxfac r = new Rubroxfac();
            Rubros rub = new Rubros();
            rub.setIdrubro(RUBRO_MULTA_BASURA_ID);
            r.setIdrubro_rubros(rub);
            r.setIdfactura_facturas(factura);
            r.setCantidad(1F);
            r.setValorunitario(scale2(multaBasura));

            rubroxfacR.save(r);

            // 4) Recalcular total desde rubros (seguro)
            BigDecimal totalNuevo = scale2(rubroxfacR.sumRubrosFactura(idfactura));
            factura.setTotaltarifa(totalNuevo);
            factura.setValorbase(totalNuevo);
            facturasR.save(factura);

            out.add(new RecalculoMultaBasuraItemDTO(cuenta, idfactura, "GENERADO_1011_Y_ACTUALIZADO_TOTAL", multaBasura,
                    totalNuevo));
        }

        return out;
    }
}