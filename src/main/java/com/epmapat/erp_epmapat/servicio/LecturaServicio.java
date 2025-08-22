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

		if ((categoria == 1 || (categoria == 9 && swAdultoMayor == true)) && m3 > 70) {
			System.out.println("Cambio de categoria a de adulto mayor especial a recidencial");

			valoresEmision.setCategoria(2);
		}
		Pliego24 pliego = dao_pliego._findBloque(valoresEmision.getCategoria(), m3);
		valoresEmision.setPliego24(pliego);
		Categorias _categoria = dao_categoria.getCategoriaById(valoresEmision.getCategoria());
		valoresEmision.setCategorias(_categoria);

		BigDecimal aguapotable = aguaPotable(valoresEmision);
		BigDecimal alcantarillado = alcantarillado(valoresEmision);
		BigDecimal saneamiento = saneamiento(valoresEmision);
		BigDecimal conservacionFuentes = conservacionFuentes(valoresEmision);
		System.out.println("Porcentaje BLOQUE " + pliego.getPorc());
		System.out.println("Id BLOQUE " + pliego.getIdpliego());
		System.out.println("======================================");
		System.out.println("----------- M3 " + m3 + " -----------");
		System.out.println("----------- CATEGORIA: " + _categoria.getDescripcion() + " -----------");
		System.out.println("AGUA POTABLE: " + aguapotable.setScale(2, RoundingMode.HALF_UP));
		System.out.println("ALCANTARILLADO: " + alcantarillado.setScale(2, RoundingMode.HALF_UP));
		System.out.println("SANEAMIENTO: " + saneamiento.setScale(2, RoundingMode.HALF_UP));
		System.out.println("CONSERVACION DE FUENTES: " + conservacionFuentes.setScale(2, RoundingMode.HALF_UP));
		System.out.println("======================================");
		if (categoria == 9 && swAdultoMayor == true && m3 > 34 && m3 <= 70) {
			System.out.println("Calcular exedente adulto mayor mas de 34 m3");
			m3 = 34;
			excedente(valoresEmision);
		}
		BigDecimal total = aguapotable
				.add(alcantarillado)
				.add(saneamiento)
				.add(conservacionFuentes);

		System.out.println("TOTAL: " + total.setScale(2, RoundingMode.HALF_UP));
		return total.setScale(2, RoundingMode.HALF_UP);
	}

	/* AGUA POTABLE - versión optimizada */
	public BigDecimal aguaPotable(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal aguapotable;
		BigDecimal apFijo;
		BigDecimal apVariable;
		BigDecimal porcentaje;
		// Determinar porcentaje según categoría
		if (valoresEmision.getCategoria() == 1 || valoresEmision.getCategoria() == 9) {
			// Residencial
			int index = Math.min(valoresEmision.getM3(), porcResidencial.length - 1);
			porcentaje = porcResidencial[index];

		} else {
			// Comercial, Industrial, Oficial u otras
			porcentaje = valoresEmision.getPliego24().getPorc();
		}
		// Cálculo común de fijo
		apFijo = valoresEmision.getCategorias().getFijoagua()
				.subtract(BigDecimal.valueOf(0.10))
				.multiply(porcentaje);
		// Cálculo común de variable
		apVariable = BigDecimal.valueOf(valoresEmision.getM3())
				.multiply(valoresEmision.getPliego24().getAgua())
				.multiply(porcentaje);
		// Total
		aguapotable = apFijo.add(apVariable);
		// Regla especial para Oficial (categoria 4)
		if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
			aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
		}
		if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor() == false) {
			aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
		}
		if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor() == true
				&& valoresEmision.getM3() < 70) {
			aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
		}
		return aguapotable;
	}

	/* ALCANTARILLADO - versión optimizada */
	public BigDecimal alcantarillado(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal fijo, variable;
		BigDecimal porcentaje;

		// Hidrosuccionador siempre se suma al final
		BigDecimal hidro = hidrosuccionador(valoresEmision);
		porcentaje = valoresEmision.getPliego24().getPorc();

		// Cálculo común de fijo
		fijo = valoresEmision.getCategorias().getFijosanea()
				.subtract(BigDecimal.valueOf(0.50))
				.multiply(porcentaje);

		// Cálculo común de variable
		variable = BigDecimal.valueOf(valoresEmision.getM3())
				.multiply(valoresEmision.getPliego24().getSaneamiento().divide(BigDecimal.valueOf(2)))
				.multiply(porcentaje);

		// Total
		valor = fijo.add(variable);

		// Regla especial para Oficial (categoria 4)
		if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}

		if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}
		if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor() == false) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}
		if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor() == true
				&& valoresEmision.getM3() < 34) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}

		// Sumar hidro al final
		valor = valor.add(hidro);

		System.out.println("HIDRO SUCCIONADOR: " + hidro);

		return valor;
	}

	/* SANEAMIENTO */
	public BigDecimal saneamiento(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal porcentaje = BigDecimal.ONE;

		int index = Math.min(valoresEmision.getM3(), porcResidencial.length - 1);

		if (valoresEmision.getCategoria() == 1 || valoresEmision.getCategoria() == 9) {
			// RESIDENCIAL o ESPECIAL
			porcentaje = porcResidencial[index];
			valor = BigDecimal.valueOf(valoresEmision.getM3())
					.multiply(valoresEmision.getPliego24().getSaneamiento()
							.divide(BigDecimal.valueOf(2)))
					.multiply(porcentaje);

		} else if (valoresEmision.getCategoria() == 2 || valoresEmision.getCategoria() == 3) {
			// COMERCIAL o INDUSTRIAL
			porcentaje = valoresEmision.getPliego24().getPorc();
			valor = BigDecimal.valueOf(valoresEmision.getM3())
					.multiply(valoresEmision.getPliego24().getSaneamiento()
							.divide(BigDecimal.valueOf(2)))
					.multiply(porcentaje);

		} else if (valoresEmision.getCategoria() == 4) {
			// OFICIAL
			porcentaje = valoresEmision.getPliego24().getPorc();
			valor = BigDecimal.valueOf(valoresEmision.getM3())
					.multiply(valoresEmision.getPliego24().getSaneamiento()
							.divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP))
					.multiply(porcentaje);

			if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
				valor = valor.divide(BigDecimal.valueOf(2));
			}
		}
		if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor() == false) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}
		if (valoresEmision.getCategoria() == 9 && valoresEmision.isSwAdultoMayor() == true
				&& valoresEmision.getM3() < 70) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}

		return valor.setScale(2, RoundingMode.HALF_UP);
	}

	/* CONSERVACIÓN DE FUENTES */
	public BigDecimal conservacionFuentes(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.valueOf(0.10);
		return valor;
	}

	/* HIDROSUCCIONADOR */
	public BigDecimal hidrosuccionador(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal porcentaje = BigDecimal.ONE;

		int index = Math.min(valoresEmision.getM3(), porcResidencial.length - 1);

		if (valoresEmision.getCategoria() == 1 || valoresEmision.getCategoria() == 9) {
			// RESIDENCIAL o ESPECIAL
			porcentaje = valoresEmision.getPliego24().getPorc();
			valor = BigDecimal.valueOf(0.50).multiply(porcentaje);

		} else if (valoresEmision.getCategoria() == 2 || valoresEmision.getCategoria() == 3) {
			// COMERCIAL o INDUSTRIAL
			porcentaje = valoresEmision.getPliego24().getPorc();
			valor = BigDecimal.valueOf(0.50).multiply(porcentaje);

		} else if (valoresEmision.getCategoria() == 4) {
			// OFICIAL
			porcentaje = valoresEmision.getPliego24().getPorc();
			valor = BigDecimal.valueOf(0.50).multiply(porcentaje);
			if (valoresEmision.isSwMunicipio()) {
				valor = valor.divide(BigDecimal.valueOf(2));
			}
		}

		return valor;
	}

	/* EXCEDENTE */
	public BigDecimal excedente(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		valoresEmision.setCategoria(1);
		Pliego24 pliego = dao_pliego._findBloque(valoresEmision.getCategoria(), valoresEmision.getM3());
		valoresEmision.setPliego24(pliego);
		Categorias _categoria = dao_categoria.getCategoriaById(valoresEmision.getCategoria());
		valoresEmision.setCategorias(_categoria);

		BigDecimal aguapotable = aguaPotable(valoresEmision);
		BigDecimal alcantarillado = alcantarillado(valoresEmision);
		BigDecimal saneamiento = saneamiento(valoresEmision);
		BigDecimal conservacionFuentes = conservacionFuentes(valoresEmision);

		System.out.println("=============EXCEDENTE================");
		System.out.println("----------- M3 " + valoresEmision.getM3() + " -----------");
		System.out.println("----------- CATEGORIA: " + _categoria.getDescripcion() + " -----------");
		System.out.println("AGUA POTABLE: " + aguapotable.setScale(2, RoundingMode.HALF_UP));
		System.out.println("ALCANTARILLADO: " + alcantarillado.setScale(2, RoundingMode.HALF_UP));
		System.out.println("SANEAMIENTO: " + saneamiento.setScale(2, RoundingMode.HALF_UP));
		System.out.println("CONSERVACION DE FUENTES: " + conservacionFuentes.setScale(2, RoundingMode.HALF_UP));
		System.out.println("=============EXCEDENTE================");

		return valor;
	}
	/* MULTAS */

}
