package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.swing.text.Position.Bias;

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
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Pliego24;
import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.CategoriaR;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.Pliego24R;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

@Service
public class LecturaServicio {

	@Autowired
	private LecturasR dao;
	@Autowired
	private Pliego24R dao_pliego;
	@Autowired
	private CategoriaR dao_categoria;
	@Autowired
	private FacturasR dao_facturas;
	@Autowired
	private DefinirR dao_definir;
	@Autowired
	private RubroxfacR dao_rubroxfac;

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
			boolean swAdultoMayor, boolean swAguapotable) {
		BigDecimal multa = multas(cuenta);
		BigDecimal total = BigDecimal.ZERO;
		Facturas factura = dao_facturas.findById(idfactura).orElseThrow();
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();
		if (factura != null) {
			EmisionOfCuentaDTO valoresEmision = new EmisionOfCuentaDTO();
			valoresEmision.setCuenta(cuenta);
			valoresEmision.setIdfactura(idfactura);
			valoresEmision.setM3(m3);
			valoresEmision.setCategoria(categoria);
			valoresEmision.setSwMunicipio(swMunicipio);
			valoresEmision.setSwAdultoMayor(swAdultoMayor);
			valoresEmision.setFactura(factura);
			valoresEmision.setSwAguapotable(swAguapotable);

			BigDecimal aguapotable = BigDecimal.ZERO;
			BigDecimal alcantarillado = BigDecimal.ZERO;
			BigDecimal saneamiento = BigDecimal.ZERO;
			BigDecimal conservacionFuentes = BigDecimal.ZERO;

			if ((categoria == 1 || (categoria == 9 && swAdultoMayor == true)) && m3 > 70) {
				valoresEmision.setCategoria(2);
			}
			Pliego24 pliego = dao_pliego._findBloque(valoresEmision.getCategoria(), m3);
			valoresEmision.setPliego24(pliego);
			Categorias _categoria = dao_categoria.getCategoriaById(valoresEmision.getCategoria());
			valoresEmision.setCategorias(_categoria);
			BigDecimal excedente = BigDecimal.ZERO;
			if (valoresEmision.isSwAguapotable() == false) {
				aguapotable = aguaPotable(valoresEmision).setScale(2, RoundingMode.HALF_UP);
				saneamiento = saneamiento(valoresEmision).setScale(2, RoundingMode.HALF_UP);
			}
			alcantarillado = alcantarillado(valoresEmision).setScale(2, RoundingMode.HALF_UP);
			conservacionFuentes = conservacionFuentes(valoresEmision).setScale(2, RoundingMode.HALF_UP);
			if (categoria == 9 && swAdultoMayor == true && m3 > 34 && m3 <= 70) {
				excedente = excedente(valoresEmision);
				rubro.setIdrubro(1005L);
				rubroxfac.setIdrubro_rubros(rubro);
				rubroxfac.setCantidad(1F);
				rubroxfac.setIdfactura_facturas(factura);
				rubroxfac.setValorunitario(excedente.setScale(2, RoundingMode.HALF_UP));
				saveRxf(rubroxfac);
			}
			if (multa.compareTo(BigDecimal.ZERO) > 0) {
				System.out.println("MULTA " + multa);
				rubro.setIdrubro(5L);
				rubroxfac.setIdrubro_rubros(rubro);
				rubroxfac.setCantidad(1F);
				rubroxfac.setIdfactura_facturas(factura);
				rubroxfac.setValorunitario(multa.setScale(2, RoundingMode.HALF_UP));
				saveRxf(rubroxfac);
			}
			total = aguapotable
					.add(alcantarillado)
					.add(saneamiento)
					.add(conservacionFuentes).add(excedente).add(multa);
			factura.setTotaltarifa(total);
			factura.setFeccrea(LocalDate.now().withDayOfMonth(1));
			factura.setValorbase(total);
			dao_facturas.save(factura);
		}

		return total.setScale(2, RoundingMode.HALF_UP);
	}

	/* AGUA POTABLE - versión optimizada */
	public BigDecimal aguaPotable(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal aguapotable = BigDecimal.ZERO;
		BigDecimal apFijo;
		BigDecimal apVariable;
		BigDecimal porcentaje;
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();
		if (!valoresEmision.isSwAguapotable()) {
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
			porcentaje = valoresEmision.getPliego24().getPorc();

			apVariable = BigDecimal.valueOf(valoresEmision.getM3())
					.multiply(valoresEmision.getPliego24().getAgua())
					.multiply(porcentaje);
			// Total
			aguapotable = apFijo.add(apVariable);
			// Regla especial para Oficial (categoria 4)
			if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
				aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
			}

			if (valoresEmision.getCategoria() == 9) {
				aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
			}

		}

		rubro.setIdrubro(1001L);
		rubroxfac.setIdrubro_rubros(rubro);
		rubroxfac.setIdfactura_facturas(valoresEmision.getFactura());
		rubroxfac.setCantidad(1F);
		rubroxfac.setValorunitario(aguapotable.setScale(2, RoundingMode.HALF_UP));
		saveRxf(rubroxfac);
		return aguapotable;
	}

	/* ALCANTARILLADO - versión optimizada */
	public BigDecimal alcantarillado(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal fijo, variable;
		BigDecimal porcentaje;
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();
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

		if (valoresEmision.getCategoria() == 9) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}

		// Sumar hidro al final
		valor = valor.add(hidro);

		System.out.println("HIDRO SUCCIONADOR: " + hidro);
		rubro.setIdrubro(1002L);
		rubroxfac.setIdrubro_rubros(rubro);
		rubroxfac.setIdfactura_facturas(valoresEmision.getFactura());
		rubroxfac.setCantidad(1F);
		rubroxfac.setValorunitario(valor.setScale(2, RoundingMode.HALF_UP));
		saveRxf(rubroxfac);
		return valor;
	}

	/* SANEAMIENTO */
	public BigDecimal saneamiento(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal porcentaje = BigDecimal.ZERO;
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();
		if (!valoresEmision.isSwAguapotable()) {

			porcentaje = valoresEmision.getPliego24().getPorc();

			// Cálculo común de variable
			valor = BigDecimal.valueOf(valoresEmision.getM3())
					.multiply(valoresEmision.getPliego24().getSaneamiento().divide(BigDecimal.valueOf(2)))
					.multiply(porcentaje);
			if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
				valor = valor.divide(BigDecimal.valueOf(2));
			}
			if (valoresEmision.getCategoria() == 9) {
				valor = valor.divide(BigDecimal.valueOf(2));
			}
		}
		rubro.setIdrubro(1003L);
		rubroxfac.setIdrubro_rubros(rubro);
		rubroxfac.setIdfactura_facturas(valoresEmision.getFactura());
		rubroxfac.setCantidad(1F);
		rubroxfac.setValorunitario(valor.setScale(2, RoundingMode.HALF_UP));
		saveRxf(rubroxfac);

		return valor.setScale(2, RoundingMode.HALF_UP);
	}

	/* AGUA POTABLE - versión optimizada */
	public BigDecimal exaguaPotable(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal aguapotable = BigDecimal.ZERO;
		BigDecimal apFijo;
		BigDecimal apVariable;
		BigDecimal porcentaje;
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();
		if (!valoresEmision.isSwAguapotable()) {
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
			porcentaje = valoresEmision.getPliego24().getPorc();

			apVariable = BigDecimal.valueOf(valoresEmision.getM3())
					.multiply(valoresEmision.getPliego24().getAgua())
					.multiply(porcentaje);
			// Total
			aguapotable = apFijo.add(apVariable);
			// Regla especial para Oficial (categoria 4)
			if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
				aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
			}

			if (valoresEmision.getCategoria() == 9) {
				aguapotable = aguapotable.divide(BigDecimal.valueOf(2));
			}

		}
		return aguapotable;
	}

	/* ALCANTARILLADO - versión optimizada */
	public BigDecimal exalcantarillado(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal fijo, variable;
		BigDecimal porcentaje;
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();
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

		if (valoresEmision.getCategoria() == 9) {
			valor = valor.divide(BigDecimal.valueOf(2));
		}

		// Sumar hidro al final
		valor = valor.add(hidro);
		return valor;
	}

	/* SANEAMIENTO */
	public BigDecimal exsaneamiento(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal porcentaje = BigDecimal.ZERO;
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();
		if (!valoresEmision.isSwAguapotable()) {

			porcentaje = valoresEmision.getPliego24().getPorc();

			// Cálculo común de variable
			valor = BigDecimal.valueOf(valoresEmision.getM3())
					.multiply(valoresEmision.getPliego24().getSaneamiento().divide(BigDecimal.valueOf(2)))
					.multiply(porcentaje);
			if (valoresEmision.getCategoria() == 4 && valoresEmision.isSwMunicipio()) {
				valor = valor.divide(BigDecimal.valueOf(2));
			}
			if (valoresEmision.getCategoria() == 9) {
				valor = valor.divide(BigDecimal.valueOf(2));
			}
		}

		return valor.setScale(2, RoundingMode.HALF_UP);
	}

	/* CONSERVACIÓN DE FUENTES */
	public BigDecimal conservacionFuentes(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.valueOf(0.10);
		Rubroxfac rubroxfac = new Rubroxfac();
		Rubros rubro = new Rubros();

		rubro.setIdrubro(1004L);
		rubroxfac.setIdrubro_rubros(rubro);
		rubroxfac.setIdfactura_facturas(valoresEmision.getFactura());
		rubroxfac.setCantidad(1F);
		rubroxfac.setValorunitario(valor.setScale(2, RoundingMode.HALF_UP));
		saveRxf(rubroxfac);

		return valor;
	}

	/* HIDROSUCCIONADOR */
	public BigDecimal hidrosuccionador(EmisionOfCuentaDTO valoresEmision) {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal porcentaje = BigDecimal.ZERO;
		porcentaje = valoresEmision.getPliego24().getPorc();
		valor = BigDecimal.valueOf(0.50).multiply(porcentaje);
		return valor;
	}

	/* EXCEDENTE */
	public BigDecimal excedente(EmisionOfCuentaDTO valoresExcedente1) {
		valoresExcedente1.setCategoria(1);
		BigDecimal valor = BigDecimal.ZERO;
		valoresExcedente1.setCategoria(1);
		Pliego24 pliego = dao_pliego._findBloque(valoresExcedente1.getCategoria(), valoresExcedente1.getM3());
		valoresExcedente1.setPliego24(pliego);
		Categorias _categoria = dao_categoria.getCategoriaById(valoresExcedente1.getCategoria());
		valoresExcedente1.setCategorias(_categoria);
		BigDecimal apex1 = exaguaPotable(valoresExcedente1);
		BigDecimal aex1 = exalcantarillado(valoresExcedente1);
		BigDecimal sex1 = exsaneamiento(valoresExcedente1);
		BigDecimal cex1 = conservacionFuentes(valoresExcedente1);
		BigDecimal suma1 = apex1.add(aex1).add(sex1).add(cex1);
		EmisionOfCuentaDTO valoresExcedente2 = new EmisionOfCuentaDTO();
		valoresExcedente2 = valoresExcedente1;
		valoresExcedente2.setM3(valoresExcedente1.getM3() - 1);
		BigDecimal apex2 = exaguaPotable(valoresExcedente2);
		BigDecimal aex2 = exalcantarillado(valoresExcedente2);
		BigDecimal sex2 = exsaneamiento(valoresExcedente2);
		BigDecimal cex2 = conservacionFuentes(valoresExcedente2);
		BigDecimal suma2 = apex2.add(aex2).add(sex2).add(cex2);
		BigDecimal excedente = suma1.subtract(suma2);
		return excedente;
	}

	/* MULTAS */
	public BigDecimal multas(Long cuentas) {
		List<Long> idfacturas = dao_facturas.findSinCobroAbo(cuentas);
		long nroPendientes = idfacturas.size();
		BigDecimal multa = BigDecimal.ZERO;
		if (nroPendientes == 3) {
			multa = BigDecimal.valueOf(2);
		}
		/*
		 * if (nroPendientes > 2) {
		 * Definir definir = dao_definir.findTopByOrderByIddefinirDesc(); // 👈 último
		 * registro
		 * if (definir != null) {
		 * BigDecimal rbu = definir.getRbu();
		 * multa = multa.add(rbu.multiply(BigDecimal.valueOf(0.01)));
		 * }
		 * }
		 */

		return multa;
	}

	private Rubroxfac saveRxf(Rubroxfac rxf) {
		Rubroxfac swrxf = dao_rubroxfac.findOneFxR(
				rxf.getIdfactura_facturas().getIdfactura(),
				rxf.getIdrubro_rubros().getIdrubro());

		if (swrxf != null) {
			// ✅ actualizar valores en el registro existente
			swrxf.setValorunitario(rxf.getValorunitario());
			swrxf.setCantidad(rxf.getCantidad());
			// si hay más campos que deben actualizarse, también agrégalos aquí
			return dao_rubroxfac.save(swrxf);
		} else {
			// ✅ guardar nuevo registro
			return dao_rubroxfac.save(rxf);
		}
	}

}
