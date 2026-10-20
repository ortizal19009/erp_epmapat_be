package com.epmapat.erp_epmapat.jasperReports.utils;

// ReportCache.java
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReportCache {
    @org.springframework.beans.factory.annotation.Value("${erp.nc-endosos.enabled:false}") private boolean endososEnabled;
    @org.springframework.beans.factory.annotation.Autowired private com.epmapat.erp_epmapat.nc.NcSchemaState ncSchema;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public JasperReport getCompiled(String reportName) throws JRException {
        return cache.compute(reportName, (rn, existing) -> {
            try (InputStream is = new ClassPathResource(resource(rn)).getInputStream()) {
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

    private String resource(String name) {return "NtaCreditoIndividual".equals(name) && (endososEnabled || (ncSchema!=null && ncSchema.migrated())) ? "nc-reports/NtaCreditoConEndosos.jrxml" : "reports/"+name+".jrxml";}
    private long resolveSignature(String reportName) throws Exception {
        return new ClassPathResource(resource(reportName)).lastModified();
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
