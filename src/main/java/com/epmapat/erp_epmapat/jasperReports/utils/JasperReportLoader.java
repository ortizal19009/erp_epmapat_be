package com.epmapat.erp_epmapat.jasperReports.utils;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JasperReportLoader {
  private final ConcurrentHashMap<String, JasperReport> cache = new ConcurrentHashMap<>();

  /** Carga reportName.jasper desde classpath: reports/<reportName>.jasper */
  public JasperReport load(String reportName) {
    return cache.computeIfAbsent(reportName, rn -> {
      try {
        String path = "reports/" + rn + ".jasper";
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
          return (JasperReport) JRLoader.loadObject(is);
        }
      } catch (Exception e) {
        throw new RuntimeException("No se pudo cargar " + reportName + ".jasper desde resources/reports/", e);
      }
    });
  }
}