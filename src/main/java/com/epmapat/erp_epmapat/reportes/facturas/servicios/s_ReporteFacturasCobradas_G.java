package com.epmapat.erp_epmapat.reportes.facturas.servicios;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.commons.JasperReportManager;
import com.epmapat.erp_epmapat.modelo.administracion.ReporteModelDTO;
import com.epmapat.erp_epmapat.reportes.facturas.interfaces.i_ReporteFacturasCobradas_G;

import net.sf.jasperreports.engine.JRException;

@Service
public class s_ReporteFacturasCobradas_G implements i_ReporteFacturasCobradas_G {

	@Autowired
	private JasperReportManager reportManager;

	@Autowired
	private DataSource dataSource;

	/**
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws JRException
	 * @see 
	 */
	@Override
	public ReporteModelDTO obtenerFacturasCobradas_G(Map<String, Object> params)
			throws JRException, IOException, SQLException {
		String fileName = "facturasCobradas";
		ReporteModelDTO dto = new ReporteModelDTO();
		String extension = params.get("tipo").toString().equalsIgnoreCase("EXCEL") ? ".xlsx"
				: ".pdf";
		dto.setFileName(fileName + extension);

		ByteArrayOutputStream stream = reportManager.export(fileName, params.get("tipo").toString(), params,
				dataSource.getConnection());

		byte[] bs = stream.toByteArray();
		dto.setStream(new ByteArrayInputStream(bs));
		dto.setLength(bs.length);

		return dto;
	}

}
