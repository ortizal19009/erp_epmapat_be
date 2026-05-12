package com.epmapat.erp_epmapat.jasperReports.utils;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JasperReportLoader {
  private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

  /** Carga reportName.jasper desde classpath: reports/<reportName>.jasper */
  public JasperReport load(String reportName) {
    return cache.compute(reportName, (rn, existing) -> {
      try {
        long signature = resolveSignature(rn);
        if (existing != null && existing.signature == signature) {
          return existing;
        }

        ClassPathResource jasperResource = new ClassPathResource("reports/" + rn + ".jasper");
        if (jasperResource.exists()) {
          try (InputStream is = jasperResource.getInputStream()) {
            return new CacheEntry(signature, (JasperReport) JRLoader.loadObject(is));
          }
        }

        ClassPathResource jrxmlResource = new ClassPathResource("reports/" + rn + ".jrxml");
        try (InputStream is = jrxmlResource.getInputStream()) {
          return new CacheEntry(signature, JasperCompileManager.compileReport(is));
        }
      } catch (Exception e) {
        throw new RuntimeException("No se pudo cargar " + reportName + " desde resources/reports/ (.jasper o .jrxml)", e);
      }
    }).report;
  }

  private long resolveSignature(String reportName) throws Exception {
    return new ClassPathResource("reports/" + reportName + ".jrxml").getFile().lastModified();
  }

  private static final class CacheEntry {
    private final long signature;
    private final JasperReport report;

    private CacheEntry(long signature, JasperReport report) {
      this.signature = signature;
      this.report = report;
    }
  }
}
