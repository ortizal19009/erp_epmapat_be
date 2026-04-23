package com.epmapat.erp_epmapat.commons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * @author <a href="mailto:4softwaredevelopers@gmail.com">Jordy Rodríguezr</a>
 * @project demo-spring-boot-jasper
 * @class JasperComponent
 * @description 
 * @HU_CU_REQ 
 * @date 17 sep. 2021
 */
@Component
public class JasperReportManager {

	private static final String REPORT_FOLDER = "reports";

	private static final String JASPER = ".jasper";
	private final ConcurrentHashMap<String, CacheEntry> compiledReports = new ConcurrentHashMap<>();

	/**
	 * @param fileName
	 * @param tipoReporte
	 * @param params
	 * @param ds
	 * @return
	 * @throws JRException 
	 * @throws IOException 
	 */
	public ByteArrayOutputStream export(String fileName, Map<String, Object> params,
			Connection con) throws JRException, IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JasperReport report = getCompiledReport(fileName);
		JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, con);
		/* if (tipoReporte.equalsIgnoreCase(tipoReporte.toString())) {
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setDetectCellType(true);
			configuration.setCollapseRowSpan(true);
			exporter.setConfiguration(configuration);
			exporter.exportReport();
		} else {
		} */
		JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

		return stream;
	}

	public JasperReport getCompiledReport(String fileName) throws JRException, IOException {
		try {
			ClassPathResource jrxmlResource = new ClassPathResource(REPORT_FOLDER + File.separator + fileName + ".jrxml");
			long signature = resolveSignature(jrxmlResource);
			CacheEntry cached = compiledReports.get(fileName);
			if (cached != null && cached.signature == signature) {
				return cached.report;
			}

			JasperReport report = compileReport(fileName);
			compiledReports.put(fileName, new CacheEntry(signature, report));
			return report;
		} catch (ReportCompilationRuntimeException e) {
			Throwable cause = e.getCause();
			if (cause instanceof JRException jrException) {
				throw jrException;
			}
			if (cause instanceof IOException ioException) {
				throw ioException;
			}
			throw e;
		}
	}

	private JasperReport compileReport(String fileName) {
		try {
			ClassPathResource jasperResource = new ClassPathResource(REPORT_FOLDER + File.separator + fileName + JASPER);
			if (jasperResource.exists()) {
				try (InputStream inputStream = jasperResource.getInputStream()) {
					return (JasperReport) JRLoader.loadObject(inputStream);
				}
			}

			ClassPathResource jrxmlResource = new ClassPathResource(REPORT_FOLDER + File.separator + fileName + ".jrxml");
			try (InputStream inputStream = jrxmlResource.getInputStream()) {
				return JasperCompileManager.compileReport(inputStream);
			}
		} catch (IOException | JRException e) {
			throw new ReportCompilationRuntimeException(e);
		}
	}

	private long resolveSignature(ClassPathResource resource) throws IOException {
		File file = resource.getFile();
		return file.lastModified();
	}

	private static final class CacheEntry {
		private final long signature;
		private final JasperReport report;

		private CacheEntry(long signature, JasperReport report) {
			this.signature = signature;
			this.report = report;
		}
	}

	private static final class ReportCompilationRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private ReportCompilationRuntimeException(Throwable cause) {
			super(cause);
		}
	}

}
