package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.CarteraVencidaRubros_int;
import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.interfaces.RubroxfacIReport;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;

@Service
public class RubroxfacServicio {
	@Autowired
	private RubroxfacR dao;

	// Campos Rubro y valor de una Planilla
	public List<Map<String, Object>> rubrosByIdfactura(Long idfactura) {
		return dao.rubrosByIdfactura(idfactura);
	}

	public Double findRubroxfac(Long idfactura) {
		return dao.findSuma(idfactura);
	}

	public Double getSumaRubros(Long idfactura) {
		return dao.sumaRubros(idfactura);
	}

	public List<RubroxfacI> getByFechaCobro(Date d, Date h) {
		return dao.getByFechaCobro(d, h);
	}

	public List<Rubroxfac> findByFecha(Date d, Date h) {
		return dao.findByFecha(d, h);
	}

	public List<Rubroxfac> findSinCobroRF(Long cuenta) {
		return dao.findSinCobroRF(cuenta);
	}

	// Rubros de una Planilla
	public List<Rubroxfac> getByIdfactura(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	// Rubros de una Planilla
	public List<Rubroxfac> getByIdfactura1(Long idfactura) {
		return dao.findByIdfactura1(idfactura);
	}

	// Campos Rubro.descripcion y rubroxfac.valorunitario de una Planilla
	public List<Object[]> findRubros(Long idFactura) {
		return dao.findRubros(idFactura);
	}

	// Movimientos de un Rubro
	public List<Rubroxfac> getByIdrubro(Long idrubro) {
		return dao.findByIdrubro(idrubro);
	}

	// Multa de una Factura
	public boolean getMulta(Long idfactura) {
		return dao.findMulta(idfactura);
	}

	// Recaudacion diaria - Total por Rubros (Todos)
	public List<Object[]> getRubroTotalsByFechaCobro(LocalDate fechaCobro) {
		return dao.findRubroTotalByRubroxfacAndFechacobro(fechaCobro);
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
	public List<Object[]> totalRubrosAnteriorRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
		List<Object[]> resultados = dao.totalRubrosAnteriorRangos(d_fecha, h_fecha, hasta);
		return resultados;
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
	public List<Object[]> totalRubrosActualRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
		List<Object[]> resultados = dao.totalRubrosActualRangos(d_fecha, h_fecha, hasta);
		return resultados;
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
	public List<Object[]> totalRubrosAnteriorByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta,
			Long idrecaudador) {
		List<Object[]> resultados = dao.totalRubrosAnteriorByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
		return resultados;
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
	public List<Object[]> totalRubrosActualByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta,
			Long idrecaudador) {
		List<Object[]> resultados = dao.totalRubrosActualByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
		return resultados;
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
	public List<Object[]> totalRubrosAnterior(LocalDate fecha, LocalDate hasta) {
		List<Object[]> resultados = dao.totalRubrosAnterior(fecha, hasta);
		return resultados;
	}

	// Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
	public List<Object[]> totalRubrosActual(LocalDate fecha, LocalDate hasta) {
		List<Object[]> resultados = dao.totalRubrosActual(fecha, hasta);
		return resultados;
	}

	// Grabar
	@SuppressWarnings("unchecked")
	public <S extends Rubroxfac> S save(S entity) {
		if (entity == null || entity.getIdfactura_facturas() == null || entity.getIdrubro_rubros() == null) {
			throw new IllegalArgumentException("La entidad o sus dependencias no pueden ser nulas.");
		}

		Long idFactura = entity.getIdfactura_facturas().getIdfactura();
		Long idRubro = entity.getIdrubro_rubros().getIdrubro();

		System.out.println("Consultando: " + idFactura + " - " + idRubro);

		// Buscar rubros existentes para la factura y rubro específicos
		List<Rubroxfac> rxfList = dao.getOneFxR(idFactura, idRubro);

		if (rxfList.isEmpty()) {
			if (entity.getValorunitario() == null) {
				entity.setValorunitario(BigDecimal.ZERO);
			}

			return dao.save(entity);
		} else {
			// Eliminar duplicados, manteniendo solo el primero
			rxfList.stream().skip(1).forEach(duplicado -> {
				try {
					dao.deleteById(duplicado.getIdrubroxfac());
				} catch (ObjectOptimisticLockingFailureException e) {
					System.out.println("El registro ya fue eliminado o no existe: " + duplicado.getIdrubroxfac());
				}
			});

			Rubroxfac existente = rxfList.get(0);
			System.out.println(existente.getIdrubro_rubros().getIdrubro());
			// Actualización lógica según el caso
			if (idRubro == 5) {
				if (existente.getValorunitario() != null
						&& !existente.getValorunitario().equals(entity.getValorunitario())) {
					existente.setValorunitario(existente.getValorunitario().add(entity.getValorunitario()));
					dao.save(existente);

				}
			}
			return (S) existente;

		}
	}

	public Optional<Rubroxfac> findById(Long id) {
		return dao.findById(id);
	}

	public List<Object[]> getIva(BigDecimal iva, Long idfactura) {
		return dao.getIva(iva, idfactura);
	}

	/* FACTURACIÓN ELECTRONICA */
	public List<Rubroxfac> getRubrosByFactura(Long idfactura) {
		return dao.getRubrosByFactura(idfactura);
	}

	public List<RubroxfacIReport> getRubrosByAbonado(Long idabonado) {
		return dao.getRubrosByAbonado(idabonado);
	}

	/* CONSULTAR MULTAS POR FACTURA */
	public List<Rubroxfac> getMultaByIdFactura(Long idfactura) {
		return dao.getMultaByIdFactura(idfactura);
	}

	/* REPORTE DE CARTERA VENCIDA POR RUBROS */
	public List<CarteraVencidaRubros_int> getCarteraVencidaxRubros(Date fechacobro) {
		return dao.getCarteraVencidaxRubros(fechacobro);
	}

}
