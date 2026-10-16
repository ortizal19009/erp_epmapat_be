package com.epmapat.erp_epmapat.jasperReports.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.*;

/** Replaces only the SQL; retains the layout stored in reportesjr. Caches no financial data. */
public final class RefacturacionesReportOptimizer {
    private byte[] signature;
    private JasperReport compiled;

    public synchronized JasperReport load(byte[] source) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(source);
        if (compiled != null && Arrays.equals(signature, hash)) return compiled;
        var original = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(source));
        var design = JRXmlLoader.load(new ByteArrayInputStream(
                JRXmlWriter.writeReport(original, "UTF-8").getBytes(StandardCharsets.UTF_8)));
        try (var in = getClass().getResourceAsStream("/reports/Refacturaciones.jrxml")) {
            var optimized = JRXmlLoader.load(in);
            for (String name : new String[]{"RefFacturas", "RefRubros"}) {
                var target = (JRDesignDataset) design.getDatasetMap().get(name);
                var sql = optimized.getDatasetMap().get(name);
                if (target == null || sql == null) throw new JRException("Falta dataset " + name + " en Refacturaciones");
                target.setQuery((JRDesignQuery) sql.getQuery().clone());
            }
            design.setQuery((JRDesignQuery) optimized.getQuery().clone());
        }
        var result = JasperCompileManager.compileReport(design);
        signature = hash;
        compiled = result;
        return result;
    }
}
