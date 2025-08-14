package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.EmisionOfCuentaDTO;
import com.epmapat.erp_epmapat.interfaces.ConsumoxCat_int;
import com.epmapat.erp_epmapat.interfaces.CountRubrosByEmision;
import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.interfaces.FecEmision;
import com.epmapat.erp_epmapat.interfaces.RepEmisionEmi;
import com.epmapat.erp_epmapat.interfaces.RepFacEliminadasByEmision;
import com.epmapat.erp_epmapat.interfaces.RubroxfacIReport;
import com.epmapat.erp_epmapat.modelo.Categorias;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Pliego24;
import com.epmapat.erp_epmapat.repositorio.CategoriaR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.Pliego24R;

@Service
public class LecturaServicio {

	@Autowired
	private LecturasR dao;
	@Autowired
	private Pliego24R dao_pliego;
	@Autowired
	private CategoriaR dao_categoria;

	// Lectura por Planilla
	public Lecturas findOnefactura(Long idfactura) {
		return dao.findOnefactura(idfactura);
	}

	public List<Lecturas> findByIdrutaxemision(Long idrutaxemision) {
		return dao.findByIdrutaxemision(idrutaxemision);
	}

	public List<Lecturas> findByIdabonado(Long idabonado, Long limit) {
		return dao.findByIdabonado(idabonado, limit);
	}

	public List<Lecturas> findByMonth() {
		return dao.findByMonth();
	}

	public List<Lecturas> findByIdRutasxEmision(Long idrutaxemision) {
		return dao.findByIdRutasxEmision(idrutaxemision);
	}

	public List<Lecturas> findLecturasByIdAbonados(Long idabonado) {
		return dao.findLecturasByIdAbonados(idabonado);
	}

	public List<Lecturas> findByRutas(Long idrutas) {
		return dao.findByRutas(idrutas);
	}

	public List<Lecturas> findByIdAbonado(Long idabonado) {
		return dao.findByIdAbonado(idabonado);
	}

	public List<Lecturas> findByNCliente(String nombre) {
		return dao.findByNCliente(nombre);
	}

	// Lectura por Planilla
	public List<Lecturas> findByIdfactura(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	// Lecuras de una Emision
	public List<Lecturas> findByIdemision(Long idemision) {
		return dao.findByIdemision(idemision);
	}

	public List<Lecturas> findByIdemisionIdAbonado(Long idemision, Long idabonado) {
		return dao.findByIdemisionIdAbonado(idemision, idabonado);
	}

	public Lecturas getById(Long id) {
		return null;
	}

	public Optional<Lecturas> findById(Long id) {
		return dao.findById(id);
	}

	public <S extends Lecturas> S saveLectura(S entity) {
		return dao.save(entity);
	}

	// Ultima lectura de un Abonado
	public Long ultimaLectura(Long idabonado) {
		return dao.ultimaLectura(idabonado);
	}

	public Long ultimaLecturaByIdemision(Long idabonado, Long idemision) {
		return dao.ultimaLecturaByIdemision(idabonado, idemision);
	}

	public BigDecimal totalEmisionXFactura(Long idemision) {
		return dao.totalEmisionXFactura(idemision);
	}

	public List<Object[]> RubrosEmitidos(Long idemision) {
		return dao.RubrosEmitidos(idemision);
	}

	public List<Object[]> R_EmisionFinal(Long idemision) {
		return dao.R_EmisionFinal(idemision);
	}

	public List<Object[]> R_EmisionActual(Long idemision) {
		return dao.R_EmisionActual(idemision);
	}

	/* OBTENER LISTADOD DE FACTURAS DE CONSUMO DE AGUA POR RUTAS, DEUDORES */
	public List<Lecturas> findDeudoresByRuta(Long idrutas) {
		return dao.findDeudoresByRuta(idrutas);
	}

	/* buscar la fecha de una emision por el id de una factura */
	public Date findDateByIdfactura(Long idfactura) {
		return dao.findDateByIdfactura(idfactura);
	}

	public List<FecEmision> getEmisionByIdfactura(Long idfactura) {
		return dao.getEmisionByIdfactura(idfactura);
	}

	public List<Lecturas> findByIdEmisiones(Long idemision) {
		return dao.findByIdEmisiones(idemision);
	}

	public List<RepFacEliminadasByEmision> findByIdEmisionesR(Long idemision) {
		return dao.findByIdEmisionesR(idemision);
	}

	public CompletableFuture<List<RubroxfacIReport>> getAllRubrosEmisionInicial(Long idemision) {
		return dao.getAllRubrosEmisionInicial(idemision);
	}

	public CompletableFuture<List<RubroxfacIReport>> getCuentaM3AllEmiInicial(Long idemision) {
		return dao.getCuentaM3AllEmiInicial(idemision);
	}

	public CompletableFuture<List<RubroxfacIReport>> getAllNewLecturas(Long idemision) {
		return dao.getAllNewLecturas(idemision);
	}

	public CompletableFuture<List<RubroxfacIReport>> getAllDeleteLecturas(Long idemision) {
		return dao.getAllDeleteLecturas(idemision);
	}

	public CompletableFuture<List<RubroxfacIReport>> getAllActual(Long idemision) {
		return dao.getAllActual(idemision);
	}

	public List<FacIntereses> getForIntereses(Long idfactura) {
		return dao.getForIntereses(idfactura);
	}

	public List<RepEmisionEmi> getReporteValEmitidosxEmision(Long idemision) {
		return dao.getReporteValEmitidosxEmision(idemision);
	}

	public List<ConsumoxCat_int> getConsumoxCategoria(Long idemision) {
		return dao.getConsumoxCategoria(idemision);
	}

	public List<CountRubrosByEmision> getCuentaRubrosByEmision(long idemision) {
		return dao.getCuentaRubrosByEmision(idemision);
	}

	/* CALCULO DEL PLIEGO TARIFARIO */
	/*
	 * PARAMETROS GENERALES:
	 * CUENTA, CATEGORIA, SWADULTOMAYOR, SWMUNICIPIO, L.ANTERIOR, L.ACTUAL, ESTADO,
	 * IDFACTURA, m3
	 */
	private static final BigDecimal[] porcResidencial = {
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

	public BigDecimal calcularValores(Long cuenta, Long idfactura, int m3, int categoria, boolean swMunicipio,
			boolean swAdultoMayor) {

		EmisionOfCuentaDTO valoresEmision = new EmisionOfCuentaDTO();
		valoresEmision.setCuenta(cuenta);
		valoresEmision.setIdfactura(idfactura);
		valoresEmision.setM3(m3);
		valoresEmision.setCategoria(categoria);
		valoresEmision.setSwMunicipio(swMunicipio);
		valoresEmision.setSwAdultoMayor(swAdultoMayor);
		Pliego24 pliego = dao_pliego._findBloque(categoria, m3);
		valoresEmision.setPliego24(pliego);
		Categorias _categoria = dao_categoria.getCategoriaById(categoria);
		valoresEmision.setCategorias(_categoria);
		BigDecimal aguapotable = aguaPotable(valoresEmision);
		BigDecimal alcantarillado = alcantarillado(valoresEmision);
		BigDecimal saneamiento = saneamiento(valoresEmision);
		BigDecimal conservacionFuentes = conservacionFuentes(valoresEmision);

		System.out.println("======================================");
		System.out.println("AGUA POTABLE: " + aguapotable.setScale(2, RoundingMode.HALF_UP));
		System.out.println("ALCANTARILLADO: " + alcantarillado.setScale(2, RoundingMode.HALF_UP));
		System.out.println("SANEAMIENTO: " + saneamiento.setScale(2, RoundingMode.HALF_UP));
		System.out.println("CONSERVACION DE FUENTES: " + conservacionFuentes.setScale(2, RoundingMode.HALF_UP));
		System.out.println("======================================");

		BigDecimal total = aguapotable
				.add(alcantarillado)
				.add(saneamiento)
				.add(conservacionFuentes);

		System.out.println("TOTAL: " + total);
		return total;
	}

	/* AGUA POTABLE */
	public BigDecimal aguaPotable(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal aguapotable = BigDecimal.ZERO;
		BigDecimal apFijo = BigDecimal.ZERO;
		BigDecimal apVariable = BigDecimal.ZERO;
		BigDecimal porcentaje = BigDecimal.ZERO;

		int index = Math.min(valoresEmision.getM3(), porcResidencial.length - 1);
		switch (valoresEmision.getCategoria()) {
			case 1: // RESIDENCIAL
			case 9:
				porcentaje = porcResidencial[index];
				apFijo = valoresEmision.getCategorias().getFijoagua()
						.subtract(BigDecimal.valueOf(0.10))
						.multiply(porcentaje);
				apVariable = BigDecimal.valueOf(valoresEmision.getM3()).multiply(valoresEmision.getPliego24().getAgua())
						.multiply(valoresEmision.getPliego24().getPorc());
				aguapotable = apFijo.add(apVariable.setScale(2, RoundingMode.HALF_UP)).setScale(2,
						RoundingMode.HALF_UP);
				if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor()
						&& valoresEmision.getM3() <= 70) {
					aguapotable = aguapotable.divide(BigDecimal.valueOf(2).setScale(2, RoundingMode.HALF_UP));
				}
				break;

			case 2: // COMERCIAL
			case 3: // INDUSTRIAL
				porcentaje = valoresEmision.getPliego24().getPorc();
				apFijo = valoresEmision.getCategorias().getFijoagua()
						.subtract(BigDecimal.valueOf(0.10))
						.multiply(porcentaje);
				apVariable = BigDecimal.valueOf(valoresEmision.getM3()).multiply(valoresEmision.getPliego24().getAgua())
						.multiply(valoresEmision.getPliego24().getPorc());
				aguapotable = apFijo.add(apVariable);
				break;

			case 4: // OFICIAL
				porcentaje = valoresEmision.getPliego24().getPorc();
				apFijo = valoresEmision.getCategorias().getFijoagua()
						.subtract(BigDecimal.valueOf(0.10))
						.multiply(porcentaje);
				apVariable = BigDecimal.valueOf(valoresEmision.getM3()).multiply(valoresEmision.getPliego24().getAgua())
						.multiply(porcentaje);
				aguapotable = apFijo.add(apVariable);
				if (valoresEmision.isSwMunicipio()) {
					aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
				}
				break;
		}

		return aguapotable;
	}

	/* ALCANTARILLADO */
	public BigDecimal alcantarillado(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal fijo, variable, porcentaje = BigDecimal.ONE;
		BigDecimal hidro = hidrosuccionador(valoresEmision);
		switch (valoresEmision.getCategoria()) {
			case 1: // RESIDENCIAL
			case 9: // ESPECIAL
				porcentaje = valoresEmision.getPliego24().getPorc();
				fijo = valoresEmision.getCategorias().getFijosanea().subtract(BigDecimal.valueOf(0.50))
						.multiply(porcentaje.setScale(2, RoundingMode.HALF_UP));
				variable = BigDecimal.valueOf(valoresEmision.getM3())
						.multiply(valoresEmision.getPliego24().getSaneamiento()
								.divide(BigDecimal.valueOf(2)))
						.multiply(porcentaje);
				valor = fijo.add(variable.setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

				if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor()
						&& valoresEmision.getM3() <= 70) {
					valor = valor.divide(BigDecimal.valueOf(2));
				}
				break;

			case 2: // COMERCIAL
			case 3: // INDUSTRIAL
				porcentaje = valoresEmision.getPliego24().getPorc();
				fijo = valoresEmision.getCategorias().getFijosanea().subtract(BigDecimal.valueOf(0.50))
						.multiply(porcentaje);
				variable = BigDecimal.valueOf(valoresEmision.getM3())
						.multiply(valoresEmision.getPliego24().getSaneamiento()
								.divide(BigDecimal.valueOf(2)))
						.multiply(porcentaje);
				valor = fijo.add(variable);
				break;
			case 4: // OFICIAL
				porcentaje = valoresEmision.getPliego24().getPorc();
				fijo = valoresEmision.getCategorias().getFijosanea().subtract(BigDecimal.valueOf(0.50))
						.multiply(porcentaje);
				variable = BigDecimal.valueOf(valoresEmision.getM3())
						.multiply(valoresEmision.getPliego24().getSaneamiento()
								.divide(BigDecimal.valueOf(2)))
						.multiply(porcentaje);
				valor = fijo.add(variable);

				if (valoresEmision.isSwMunicipio()) {
					valor = valor.divide(BigDecimal.valueOf(2));
				}
				break;
		}

		System.out.println("HIDRO SUCCIONADOR: " + hidro);
		valor = valor.add(hidro);

		return valor;
	}

	/* SANEAMIENTO */
	public BigDecimal saneamiento(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal variable, porcentaje;
		int index = Math.min(valoresEmision.getM3(), porcResidencial.length - 1);

		switch (valoresEmision.getCategoria()) {
			case 1: // RESIDENCIAL
			case 9: // ESPECIAL
				porcentaje = porcResidencial[index];
				// fijo = BigDecimal.valueOf(3.32).multiply(porcentaje); // 3.82 - 0.50
				variable = BigDecimal.valueOf(valoresEmision.getM3())
						.multiply(valoresEmision.getPliego24().getSaneamiento().divide(BigDecimal.valueOf(2)))
						.multiply(porcentaje);
				// 0.12 / 2
				valor = variable;

				if (valoresEmision.getCategoria() == 9) {
					if (valoresEmision.isSwAdultoMayor()
							&& valoresEmision.getM3() <= 70) {
						valor = valor.divide(BigDecimal.valueOf(2));

					} else {
						valor = variable;
					}
				}
				break;

			case 2: // COMERCIAL
				porcentaje = BigDecimal.valueOf(0.85);
				// fijo = BigDecimal.valueOf(5.25).multiply(porcentaje); // 5.75 - 0.50
				variable = BigDecimal.valueOf(valoresEmision.getM3())
						.multiply(valoresEmision.getPliego24().getSaneamiento())
						.multiply(porcentaje); // 0.3 / 2
				valor = variable;
				break;

			case 3: // INDUSTRIAL
				porcentaje = BigDecimal.valueOf(0.90);
				// fijo = BigDecimal.valueOf(11.50).multiply(porcentaje); // 12.00 - 0.50
				variable = BigDecimal.valueOf(valoresEmision.getM3())
						.multiply(valoresEmision.getPliego24().getSaneamiento().divide(BigDecimal.valueOf(2)))
						.multiply(porcentaje); // 0.5 / 2
				valor = variable;
				break;

			case 4: // OFICIAL
				porcentaje = BigDecimal.valueOf(0.75);
				// fijo = BigDecimal.valueOf(14.50); // ya sin multiplicar por porcentaje
				variable = BigDecimal.valueOf(valoresEmision.getM3())
						.multiply(valoresEmision.getPliego24().getSaneamiento().divide(BigDecimal.valueOf(2)))
						.multiply(porcentaje); // 0.70 / 2
				valor = variable;
				break;
		}

		return valor;
	}

	/* CONSERVACIÓN DE FUENTES */
	public BigDecimal conservacionFuentes(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.valueOf(0.10);
		switch (valoresEmision.getCategoria()) {
			case 4: // OFICIAL
				if (valoresEmision.isSwMunicipio()) {
					valor = valor.divide(BigDecimal.valueOf(2));
				}
				break;
			case 9: // ESPECIAL
				if (valoresEmision.isSwAdultoMayor() && valoresEmision.getM3() <= 70) {
					valor = valor.divide(BigDecimal.valueOf(2));
				}
				break;
			// RESIDENCIAL, COMERCIAL, INDUSTRIAL ya pagan completo (sin descuento)
			case 1: // RESIDENCIAL
			case 2: // COMERCIAL
			case 3: // INDUSTRIAL
				// valor ya está en 0.10
				break;
		}
		return valor;
	}

	/* HIDROSUCCIONADOR */
	public BigDecimal hidrosuccionador(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.valueOf(0.50).multiply(valoresEmision.getPliego24().getPorc());
		switch (valoresEmision.getCategoria()) {
			case 4: // OFICIAL
				if (valoresEmision.isSwMunicipio()) {
					valor = valor.divide(BigDecimal.valueOf(2));
				}
				break;
			case 9: // ESPECIAL
				if (valoresEmision.isSwAdultoMayor() && valoresEmision.getM3() <= 70) {
					valor = valor.divide(BigDecimal.valueOf(2));
				}
				break;
			// RESIDENCIAL, COMERCIAL, INDUSTRIAL no tienen descuento
			case 1: // RESIDENCIAL
			case 2: // COMERCIAL
			case 3: // INDUSTRIAL
				// valor ya está en 0.50
				break;
		}
		return valor;
	}

	/* MULTAS */
}
