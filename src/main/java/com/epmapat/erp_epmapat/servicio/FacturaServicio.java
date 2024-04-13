package com.epmapat.erp_epmapat.servicio;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.epmapat.erp_epmapat.interfaces.FacturasI;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.repositorio.FacturasR;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@Service
public class FacturaServicio {

	@Autowired
	private FacturasR dao;

	public Facturas validarUltimafactura(String codrecaudador) {
		return dao.validarUltimafactura(codrecaudador);
	}

	public List<Facturas> findByUsucobro(Long idusuario, Date dfecha, Date hfecha) {
		return dao.findByUsucobro(idusuario, dfecha, hfecha);
	}

	public List<FacturasI> findByFechacobro(Date fechacobro) {
		return dao.findByFechacobro(fechacobro);
	}

	public List<Facturas> findAll() {
		return dao.findAll();
	}

	public List<Facturas> findDesde(Long desde, Long hasta) {
		return dao.findDesde(desde, hasta);
	}

	@SuppressWarnings("null")
	public Optional<Facturas> findById(Long idfactura) {
		return dao.findById(idfactura);
	}

	// Planillas por Cliente
	public List<Facturas> findByIdcliente(Long idcliente) {
		return dao.findByIdcliente(idcliente);
	}

	// Planillas por Abonado
	public List<Facturas> findByIdabonado(Long idabonado) {
		return dao.findByIdabonado(idabonado);
	}

	// Una Planilla (como lista)
	public List<Facturas> buscarPlanilla(Long idfactura) {
		return dao.findByIdfactura(idfactura);
	}

	// Planillas por Abonado y Fecha
	public List<Facturas> buscarPorAbonadoYFechaCreacionRange(Long idabonado, LocalDate fechaDesde,
			LocalDate fechaHasta) {
		return dao.findByAbonadoAndFechaCreacionRange(idabonado, fechaDesde, fechaHasta);
	}

	// Planillas Sin Cobrar de un Cliente
	public List<Facturas> findSinCobro(Long idcliente) {
		return dao.findSinCobro(idcliente);
	}

	// Planillas Sin Cobrar de un Abonado (para Multas)
	public List<Long> findSinCobroAbo(Long idabonado) {
		return dao.findSinCobroAbo(idabonado);
	}

	// Cuenta las Planillas Pendientes de un Abonado
	public long getCantidadFacturasByAbonadoAndPendientes(Long idabonado) {
		return dao.countFacturasByAbonadoAndPendientes(idabonado);
	}

	// Planillas Sin Cobrar de un Abonado (Para convenios)
	public List<Facturas> findSinCobrarAbo(Long idmodulo, Long idabonado) {
		return dao.findSinCobrarAbo(idmodulo, idabonado);
	}

	// Recaudación diaria - Facturas cobrasdas <Facturas>
	// public List<Facturas> findByFechacobro(LocalDate fecha) {
	// return dao.findByFechacobro(fecha);
	// }

	// Recaudación diaria - Facturas cobradas (Sumando los rubros)
	public List<Object[]> findByFechacobroTotRangos(LocalDate d_fecha, LocalDate h_fecha) {
		return dao.findByFechacobroTotRangos(d_fecha, h_fecha);
	}

	public List<Object[]> findByFechacobroTotByRecaudador(LocalDate d_fecha, LocalDate h_fecha, Long idrecaudador) {
		return dao.findByFechacobroTotByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	// Total diario por Forma de cobro
	public List<Object[]> totalFechaFormacobroRangos(LocalDate d_fecha, LocalDate h_fecha) {
		return dao.totalFechaFormacobroRangos(d_fecha, h_fecha);
	}

	// Total diario por Forma de cobro
	public List<Object[]> totalFechaFormacobroByRecaudador(LocalDate d_fecha, LocalDate h_fecha, Long idrecaudador) {
		return dao.totalFechaFormacobroByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	public List<Object[]> findByFechacobroTot(LocalDate fecha) {
		return dao.findByFechacobroTot(fecha);
	}

	// Total diario por Forma de cobro
	public List<Object[]> totalFechaFormacobro(LocalDate fecha) {
		return dao.totalFechaFormacobro(fecha);
	}

	@SuppressWarnings("null")
	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public List<Facturas> findByIdFactura(Long idabonado) {
		return dao.findByIdFactura(idabonado);
	}

	public List<Facturas> findByNrofactura(String nrofactura) {
		return dao.findByNrofactura(nrofactura);
	}

	@SuppressWarnings("null")
	public <S extends Facturas> S save(S entity) {
		return dao.save(entity);
	}

	public FacturasR getDao() {
		return dao;
	}

	public void setDao(FacturasR dao) {
		this.dao = dao;
	}

	/*
	 * ===========================
	 * REPORTES FACTURAS COBRADAS
	 * ===========================
	 */
	public String exportFacturasCobradas(String format, Date v_dfecha, Date v_hfecha, Date c_fecha)
			throws FileNotFoundException, JRException {
		// List<Object> factura = dao.findAll();
		String path = "C://reportes//";
		File file = ResourceUtils.getFile("classpath:facturasCobradas.jrxml");
		JasperReport jasper = JasperCompileManager.compileReport(file.getAbsolutePath());
		// JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(ob);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("format", format);
		parameters.put("v_dfecha", v_dfecha);
		parameters.put("v_hfecha", v_hfecha);
		parameters.put("c_fecha", c_fecha);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, parameters);
		if (format.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "//factruas_cobradas.html");
		}
		if (format.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, path + "//facturas_cobradas.pdf");
		}
		if (format.equalsIgnoreCase("xmls")) {
			JRPdfExporter exporter = new JRPdfExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(
					new SimpleOutputStreamExporterOutput("employeeReport.pdf"));

			SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
			reportConfig.setSizePageToContent(true);
			reportConfig.setForceLineBreakPolicy(false);

			SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
			exportConfig.setMetadataAuthor("baeldung");
			exportConfig.setEncrypted(true);
			exportConfig.setAllowedPermissionsHint("PRINTING");

			exporter.setConfiguration(reportConfig);
			exporter.setConfiguration(exportConfig);

			exporter.exportReport();
		}
		return "path: " + path;
	}

	/*
	 * ===========================
	 * REPORTES FACTURAS RUBROS
	 * ===========================
	 */

}
