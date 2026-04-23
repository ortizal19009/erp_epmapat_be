package com.epmapat.erp_epmapat.jasperReports.utils;

// ReportCache.java
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.File;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReportCache {
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public JasperReport getCompiled(String reportName) throws JRException {
        return cache.compute(reportName, (rn, existing) -> {
            try (InputStream is = new ClassPathResource("reports/" + rn + ".jrxml").getInputStream()) {
                long signature = resolveSignature(rn);
                if (existing != null && existing.signature == signature) {
                    return existing;
                }
                JasperDesign design = JRXmlLoader.load(is);
                return new CacheEntry(signature, JasperCompileManager.compileReport(design));
            } catch (Exception e) {
                throw new RuntimeException("Error compilando jrxml: " + rn, e);
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
