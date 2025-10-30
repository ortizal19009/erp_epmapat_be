package com.epmapat.erp_epmapat.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.repositorio.InteresesR;
import com.epmapat.erp_epmapat.utils.InteresUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;

@Service
public class InteresServicio {

	@Autowired
	private InteresesR dao;
	@Autowired
	private FacturaServicio s_factura;
	@Autowired
	private LecturaServicio s_lectura;
    @Autowired
    private InteresRepositoryAdapter repo;

	public List<Intereses> findAll() {
		return dao.findAll();
	}

	public List<Intereses> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	public List<Intereses> findByAnioMes(Number anio, Number mes) {
		return dao.findByAnioMes(anio, mes);
	}

	public List<Intereses> findUltimo() {
		return dao.findUltimo();
	}

	public <S extends Intereses> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Intereses> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(Intereses entity) {
		dao.delete(entity);
	}

	public Object facturaid(Long idfactura) {
		List<FacIntereses> factura = s_lectura.getForIntereses(idfactura);

		if (factura.isEmpty()) {
			factura = s_factura.getForIntereses(idfactura);
		}
		// Variable para almacenar el interés total de todas las facturas
		final double[] totalInteres = { 0.0 };
		// Uso de Java Streams para mapear la lista
		factura.stream().forEach(_factura -> {
			// Convertir la fecha de creación a LocalDate
			LocalDate fecInicio;
			if (_factura.getFormapago() == 4) {
				fecInicio = _factura.getFechatransferencia();

			} else {
				fecInicio = _factura.getFeccrea();

			}
			// LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
			LocalDate fecFinal = LocalDate.now();
			int anioI = fecInicio.getYear();
			int anioF = fecFinal.getYear();
			int mesF = fecFinal.getMonthValue();
			List<Float> todosPorcentajes = new ArrayList<>();
			if (anioI < anioF) {
				int mesI = fecInicio.getMonthValue();
				if (mesI == 12 && mesF == 1 && anioI + 1 == anioF) {
				} else {
					while (anioI <= anioF) {
						if (anioI < anioF) {
							List<Float> porcentaje = dao.porcentajes(anioI, mesI, 12);
							todosPorcentajes.addAll(porcentaje); // Añadir los porcentajes a la lista total
						} else if (anioI == anioF) {
							List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
							if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
								porcentaje.add(0.00f);
							} else {
								porcentaje = dao.porcentajes(anioF, 1, fecFinal.getMonthValue() - 2);
								todosPorcentajes.addAll(porcentaje);
							}
						}
						mesI = 1;
						anioI++;
					}
				}

			} else if (anioF < anioI) {
			} else {
				List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
				if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
					porcentaje.add(0.00f);
				} else {

					porcentaje = dao.porcentajes(fecFinal.getYear(), fecInicio.getMonthValue(),
							fecFinal.getMonthValue() - 2);
					todosPorcentajes.addAll(porcentaje);
				}
			}
			todosPorcentajes.forEach(interes -> {
				double interesCalculado = (interes * (_factura.getSuma() + totalInteres[0])) / 100;
				totalInteres[0] += interesCalculado; // Sumar al interés total
			});
		});
		return totalInteres[0];
	}


    public InteresRepositoryAdapter getRepoAdapter() { return this.repo; }


    public Object interesToFactura(ValorFactDTO factura) {

		// Variable para almacenar el interés total de todas las facturas
		final double[] totalInteres = { 0.0 };
		// Uso de Java Streams para mapear la lista
		//factura.stream().forEach(_factura -> {});
			// Convertir la fecha de creación a LocalDate
			LocalDate fecInicio;
			if (factura.getFormapago() == 4) {
				fecInicio = factura.getFectransferencia();

			} else {
				fecInicio = (factura.getFeccrea());

			}
			// LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
			LocalDate fecFinal = LocalDate.now();
			int anioI = fecInicio.getYear();
			int anioF = fecFinal.getYear();
			int mesF = fecFinal.getMonthValue();
			List<Float> todosPorcentajes = new ArrayList<>();
			if (anioI < anioF) {
				int mesI = fecInicio.getMonthValue();
				if (mesI == 12 && mesF == 1 && anioI + 1 == anioF) {
				} else {
					while (anioI <= anioF) {
						if (anioI < anioF) {
							List<Float> porcentaje = dao.porcentajes(anioI, mesI, 12);
							todosPorcentajes.addAll(porcentaje); // Añadir los porcentajes a la lista total
						} else if (anioI == anioF) {
							List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
							if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
								porcentaje.add(0.00f);
							} else {
								porcentaje = dao.porcentajes(anioF, 1, fecFinal.getMonthValue() - 2);
								todosPorcentajes.addAll(porcentaje);
							}
						}
						mesI = 1;
						anioI++;
					}
				}

			} else if (anioF < anioI) {
			} else {
				List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
				if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
					porcentaje.add(0.00f);
				} else {

					porcentaje = dao.porcentajes(fecFinal.getYear(), fecInicio.getMonthValue(),
							fecFinal.getMonthValue() - 2);
					todosPorcentajes.addAll(porcentaje);
				}
			}
			todosPorcentajes.forEach(interes -> {
				double interesCalculado = (interes * (factura.getSubtotal() + totalInteres[0])) / 100;
				totalInteres[0] += interesCalculado; // Sumar al interés total
			});
		
		return totalInteres[0];
	}

    // ===== Reglas parametrizables =====
    record ReglaInteres(
            int lagMeses,                 // cuántos meses excluir al final (ej: 2)
            boolean incluirMesInicio,     // incluir el mes de emisión? (false = empieza mes siguiente)
            boolean omitirSiRangoUnMes,   // si el rango tiene un solo mes, omitir
            boolean omitirSiUnicoMesEsActual // si el único mes es el mes de corte (mes actual), omitir
    ) {
        static ReglaInteres porDefecto() {
            // Emula tu lógica previa: excluye 2 meses, inicia mes siguiente,
            // si queda 1 mes => omitir (equivalente a tus guardas).
            return new ReglaInteres(0, false, false, true);
        }
    }
    /**
     * Igual que tu lógica previa:
     *
     * var reglas = new ReglaInteres(2, false, true, false);
     * calcularInteresFactura(id, LocalDate.now(), reglas);
     *
     *
     * No calcular si el único mes es el actual (con lag=0):
     *
     * var reglas = new ReglaInteres(0, false, false, true);
     *
     *
     * Incluir el mes de inicio y solo excluir 1 mes al final:
     *
     * var reglas = new ReglaInteres(1, true, false, false);
     * */


    public BigDecimal calcularInteresFactura(Long idfactura, LocalDate hoy) {
        return calcularInteresFactura(idfactura, hoy, ReglaInteres.porDefecto());
    }

    // ===== Versión configurable =====
    public BigDecimal calcularInteresFactura(Long idfactura, LocalDate hoy, ReglaInteres regla) {
        List<FacIntereses> factura = s_lectura.getForIntereses(idfactura);
        if (factura == null || factura.isEmpty()) factura = s_factura.getForIntereses(idfactura);
        if (factura == null || factura.isEmpty()) return BigDecimal.ZERO.setScale(InteresUtils.SCALE_MONEY);

        BigDecimal total = BigDecimal.ZERO;
        final YearMonth corteYM = YearMonth.from(hoy);

        for (FacIntereses f : factura) {
            // 1) Fecha de inicio según forma de pago
            LocalDate fecInicio = (f.getFormapago() != null && f.getFormapago() == 4)
                    ? f.getFechatransferencia()
                    : f.getFeccrea();
            if (fecInicio == null) continue;

            BigDecimal principal = InteresUtils.bd(f.getSuma());
            if (principal.signum() <= 0) continue;

            YearMonth startYM = YearMonth.from(fecInicio);

            // 2) Rango efectivo [desdeYM .. endYM]
            YearMonth endYM   = corteYM.minusMonths(Math.max(0, regla.lagMeses()));
            YearMonth desdeYM = regla.incluirMesInicio ? startYM : startYM.plusMonths(1);

            // Sin meses a capitalizar
            if (desdeYM.isAfter(endYM)) continue;

            // Tamaño del rango (inclusivo)
            long mesesIncluidos = java.time.temporal.ChronoUnit.MONTHS.between(desdeYM, endYM) + 1;

            // Reglas de omisión por tamaño
            if (mesesIncluidos == 1) {
                if (regla.omitirSiRangoUnMes) continue;
                if (regla.omitirSiUnicoMesEsActual && endYM.equals(corteYM)) continue;
            }

            // 3) Traer % por rango (en %)
            Map<YearMonth, BigDecimal> pctMap = repo.getPorcentajesPorRango(desdeYM, endYM);

            // 4) Componer factor = ∏ (1 + r_mensual)
            BigDecimal factor = BigDecimal.ONE;
            YearMonth cur = desdeYM;
            while (!cur.isAfter(endYM)) {
                BigDecimal pct = pctMap.getOrDefault(cur, BigDecimal.ZERO); // % mensual (ej. 1.25)
                BigDecimal ratio = pct.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
                factor = factor.multiply(BigDecimal.ONE.add(ratio, InteresUtils.MC), InteresUtils.MC);
                cur = cur.plusMonths(1);
            }

            // 5) Interés = principal * (factor - 1)
            BigDecimal interes = principal.multiply(factor.subtract(BigDecimal.ONE, InteresUtils.MC), InteresUtils.MC)
                    .setScale(InteresUtils.SCALE_MONEY, RoundingMode.HALF_UP);

            total = total.add(interes, InteresUtils.MC);
        }

        return total.setScale(InteresUtils.SCALE_MONEY, RoundingMode.HALF_UP);
    }



}