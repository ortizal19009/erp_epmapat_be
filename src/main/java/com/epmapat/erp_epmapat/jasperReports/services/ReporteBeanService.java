package com.epmapat.erp_epmapat.jasperReports.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperBeanDTO;
import com.epmapat.erp_epmapat.jasperReports.utils.ParamConverter;
import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;
import com.epmapat.erp_epmapat.servicio.administracion.ReportejrService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import java.io.ByteArrayInputStream;

@RequiredArgsConstructor
@Service
public class ReporteBeanService {

   private final ReportejrService repojrService; // Servicio que busca Reportesjr por nombre
   private final ParamConverter paramConverter;

   public JasperPrint fillBeanReport(JasperBeanDTO dto) throws Exception {

      // Obtiene el objeto Jasper (.jasper) desde la BD
      Reportesjr reporte = repojrService.findByNomrep(dto.getReportName());
      Map<String, Object> params = paramConverter.convert(dto.getParameters());
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dto.getBeanCollection());
      JasperReport jasperReport = (JasperReport) JRLoader.loadObject(
            new ByteArrayInputStream(reporte.getJasper()));
      return JasperFillManager.fillReport(jasperReport, params, dataSource);
   }

}
