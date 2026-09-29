package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.PreemisionReporteDTO;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Rutas;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreemisionServicio {
    private final EmisionesR emisiones;
    private final LecturasR lecturas;
    private final EmisionServicioOptimizadoV2 calculador;

    @Transactional(readOnly = true)
    public PreemisionReporteDTO reporte(Long idemision) {
        Emisiones emision = emisiones.findById(idemision)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emisión no encontrada"));
        if (!Objects.equals(emision.getEstado(), 0)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La preemisión solo está disponible para emisiones abiertas");
        }
        PreemisionReporteDTO reporte = new PreemisionReporteDTO();
        reporte.setIdemision(idemision);
        reporte.setEmision(emision.getEmision());
        Map<Long, PreemisionReporteDTO.Ruta> rutas = new LinkedHashMap<>();
        for (Lecturas lectura : lecturas.findByIdemision(idemision)) {
            Abonados abonado = lectura.getIdabonado_abonados();
            if (abonado == null || abonado.getIdcategoria_categorias() == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "La lectura " + lectura.getIdlectura() + " no tiene abonado o categoría para calcular");
            }
            Rutas ruta = lectura.getIdrutaxemision_rutasxemision() == null ? null
                    : lectura.getIdrutaxemision_rutasxemision().getIdruta_rutas();
            Long idruta = ruta == null ? null : ruta.getIdruta();
            PreemisionReporteDTO.Ruta resumen = rutas.computeIfAbsent(idruta, id -> {
                PreemisionReporteDTO.Ruta item = new PreemisionReporteDTO.Ruta();
                item.setIdruta(id);
                item.setCodigo(ruta == null ? "" : ruta.getCodigo());
                item.setRuta(ruta == null ? "Sin ruta" : ruta.getDescripcion());
                return item;
            });
            float anterior = lectura.getLecturaanterior() == null ? 0 : lectura.getLecturaanterior();
            float actual = lectura.getLecturaactual() == null ? 0 : lectura.getLecturaactual();
            // El cierre envía consumos no negativos a un parámetro entero del calculador V2.
            int m3 = (int) Math.max(actual - anterior, 0);
            BigDecimal valor = calculador.previsualizarValores(idemision, abonado.getIdabonado(),
                    lectura.getIdfactura(), m3, abonado.getIdcategoria_categorias().getIdcategoria().intValue(),
                    Boolean.TRUE.equals(abonado.getMunicipio()), Boolean.TRUE.equals(abonado.getAdultomayor()),
                    Boolean.TRUE.equals(abonado.getSwalcantarillado()), Boolean.TRUE.equals(abonado.getSwbasura()));
            PreemisionReporteDTO.Cuenta cuenta = new PreemisionReporteDTO.Cuenta();
            cuenta.setCuenta(abonado.getIdabonado());
            cuenta.setAbonado(abonado.getIdcliente_clientes() == null ? "" : abonado.getIdcliente_clientes().getNombre());
            cuenta.setCategoria(abonado.getIdcategoria_categorias().getDescripcion());
            cuenta.setAnterior(anterior);
            cuenta.setActual(actual);
            cuenta.setM3(m3);
            cuenta.setValor(valor);
            cuenta.setObservacion(actual < anterior ? "Consumo negativo: calculado con 0 m³, igual que el cierre"
                    : lectura.getLecturaactual() == null ? "Sin lectura actual: cálculo provisional" : "");
            resumen.getDetalle().add(cuenta);
            resumen.setCuentas(resumen.getCuentas() + 1);
            resumen.setM3(resumen.getM3() + m3);
            resumen.setValor(resumen.getValor().add(valor));
            reporte.setCuentas(reporte.getCuentas() + 1);
            reporte.setM3(reporte.getM3() + m3);
            reporte.setValor(reporte.getValor().add(valor));
            if (actual < anterior) reporte.setConsumosNegativos(reporte.getConsumosNegativos() + 1);
        }
        reporte.getRutas().addAll(rutas.values());
        reporte.getRutas().sort(java.util.Comparator.comparing(r -> r.getCodigo() == null ? "" : r.getCodigo()));
        return reporte;
    }
}
