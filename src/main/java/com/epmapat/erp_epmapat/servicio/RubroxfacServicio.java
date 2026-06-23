package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

	public List<Rubroxfac> getByIdfactura(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	public List<Rubroxfac> getDetalleByIdfactura(Long idfactura) {
		return dao.findDetalleByIdfactura(idfactura);
	}

	public List<Rubroxfac> getByIdfactura1(Long idfactura) {
		return dao.findByIdfactura1(idfactura);
	}

	public List<Object[]> findRubros(Long idFactura) {
		return dao.findRubros(idFactura);
	}

	public List<Rubroxfac> getByIdrubro(Long idrubro) {
		return dao.findByIdrubro(idrubro);
	}

	public boolean getMulta(Long idfactura) {
		return dao.findMulta(idfactura);
	}

	public List<Object[]> getRubroTotalsByFechaCobro(LocalDate fechaCobro) {
		return dao.findRubroTotalByRubroxfacAndFechacobro(fechaCobro);
	}

	public List<Object[]> totalRubrosAnteriorRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
		return dao.totalRubrosAnteriorRangos(d_fecha, h_fecha, hasta);
	}

	public List<Object[]> totalRubrosActualRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
		return dao.totalRubrosActualRangos(d_fecha, h_fecha, hasta);
	}

	public List<Object[]> totalRubrosAnteriorByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta,
			Long idrecaudador) {
		return dao.totalRubrosAnteriorByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
	}

	public List<Object[]> totalRubrosActualByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta,
			Long idrecaudador) {
		return dao.totalRubrosActualByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
	}

	public List<Object[]> totalRubrosAnterior(LocalDate fecha, LocalDate hasta) {
		return dao.totalRubrosAnterior(fecha, hasta);
	}

	public List<Object[]> totalRubrosActual(LocalDate fecha, LocalDate hasta) {
		return dao.totalRubrosActual(fecha, hasta);
	}

	public <S extends Rubroxfac> S save(S entity) {
		if (entity.getIdfactura_facturas() == null || entity.getIdfactura_facturas().getIdfactura() == null) {
			throw new IllegalArgumentException("Debe enviar la factura asociada al rubro.");
		}
		if (entity.getIdrubro_rubros() == null || entity.getIdrubro_rubros().getIdrubro() == null) {
			throw new IllegalArgumentException("Debe enviar el rubro asociado.");
		}

		if (entity.getValorunitario() == null) {
			entity.setValorunitario(BigDecimal.ZERO);
		}
		return dao.save(entity);
	}

	public <S extends Rubroxfac> S saveSync(S entity) {
		if (entity.getValorunitario() == null) {
			entity.setValorunitario(BigDecimal.ZERO);
		}
		return dao.save(entity);
	}

	public BigDecimal getTotalInteres(Long idfactura) {
		return dao.getTotalInteres(idfactura);
	}

	public List<Rubroxfac> getByFacturaAndRubro(Long idfactura, Long idrubro) {
		return dao.findByFacturaAndRubro(idfactura, idrubro);
	}

	public Optional<Rubroxfac> findById(Long id) {
		return dao.findById(id);
	}

	public List<Object[]> getIva(BigDecimal iva, Long idfactura) {
		return dao.getIva(iva, idfactura);
	}

	public List<Object[]> getIvaByFacturas(BigDecimal iva, List<Long> ids) {
		return dao.getIvaByFacturas(iva, ids);
	}

	public List<Rubroxfac> getRubrosByFactura(Long idfactura) {
		return dao.getRubrosByFactura(idfactura);
	}

	public List<RubroxfacIReport> getRubrosByAbonado(Long idabonado) {
		return dao.getRubrosByAbonado(idabonado);
	}

	public List<Rubroxfac> getMultaByIdFactura(Long idfactura) {
		return dao.getMultaByIdFactura(idfactura);
	}

	public List<CarteraVencidaRubros_int> getCarteraVencidaxRubros(LocalDate fechacobro) {
		return dao.getCarteraVencidaxRubros(fechacobro);
	}

	public List<RubroxfacI> getRubrosForRemisiones(Long idcliente, LocalDate topefecha) {
		return dao.getRubrosForRemisiones(idcliente, topefecha);
	}
}
