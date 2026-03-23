package com.epmapat.erp_epmapat.jasper_Reports.utils;

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
    private final ConcurrentHashMap<String, JasperReport> cache = new ConcurrentHashMap<>();

    public JasperReport getCompiled(String reportName) throws JRException {
        return cache.computeIfAbsent(reportName, rn -> {
            try (InputStream is = new ClassPathResource("reports/" + rn + ".jrxml").getInputStream()) {
                JasperDesign design = JRXmlLoader.load(is);
                return JasperCompileManager.compileReport(design);
            } catch (Exception e) {
                throw new RuntimeException("Error compilando jrxml: " + rn, e);
            }
        });
    }
}
