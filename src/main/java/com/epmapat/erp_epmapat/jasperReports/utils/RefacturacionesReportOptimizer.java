package com.epmapat.erp_epmapat.jasperReports.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.*;

/** Updates SQL and statistical summary, retaining the stored detail layout. Caches no financial data. */
public final class RefacturacionesReportOptimizer {
    private byte[] signature;
    private JasperReport compiled;

    public synchronized JasperReport load(byte[] source) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(source);
        if (compiled != null && Arrays.equals(signature, hash)) return compiled;
        var original = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(source));
        String xml = repairText(JRXmlWriter.writeReport(original, "UTF-8"));
        // An absent join/amount is not a zero-valued refacturation.
        xml = xml.replace("== null ? \"0.00\"", "== null ? \"N/D\"");
        var design = JRXmlLoader.load(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        try (var in = getClass().getResourceAsStream("/reports/Refacturaciones.jrxml")) {
            var optimized = JRXmlLoader.load(in);
            for (String name : new String[]{"RefFacturas", "RefRubros"}) {
                var target = (JRDesignDataset) design.getDatasetMap().get(name);
                var sql = optimized.getDatasetMap().get(name);
                if (target == null || sql == null) throw new JRException("Falta dataset " + name + " en Refacturaciones");
                target.setQuery((JRDesignQuery) sql.getQuery().clone());
                for (var variable : sql.getVariables()) {
                    if (variable.isSystemDefined()) continue;
                    target.removeVariable(variable.getName());
                    target.addVariable((JRDesignVariable) variable.clone());
                }
            }
            design.setQuery((JRDesignQuery) optimized.getQuery().clone());
            for (var variable : optimized.getVariables()) {
                if (!variable.getName().startsWith("res")) continue;
                design.removeVariable(variable.getName());
                design.addVariable((JRDesignVariable) variable.clone());
            }
            for (var band : design.getDetailSection().getBands()) {
                for (var element : band.getElements()) {
                    if (!(element instanceof JRComponentElement)) continue;
                    var component = ((JRComponentElement) element).getComponent();
                    if (!(component instanceof net.sf.jasperreports.components.table.StandardTable)) continue;
                    var table = (net.sf.jasperreports.components.table.StandardTable) component;
                    var run = (JRDesignDatasetRun) table.getDatasetRun();
                    for (var sourceBand : optimized.getDetailSection().getBands()) {
                        for (var sourceElement : sourceBand.getElements()) {
                            if (!(sourceElement instanceof JRComponentElement)) continue;
                            var sourceComponent = ((JRComponentElement) sourceElement).getComponent();
                            if (!(sourceComponent instanceof net.sf.jasperreports.components.table.StandardTable)) continue;
                            var sourceRun = ((net.sf.jasperreports.components.table.StandardTable) sourceComponent).getDatasetRun();
                            if (run.getDatasetName().equals(sourceRun.getDatasetName())) {
                                table.setDatasetRun((JRDatasetRun) sourceRun.clone());
                            }
                        }
                    }
                }
            }
            design.setSummary((JRDesignBand) optimized.getSummary().clone());
        }
        var result = JasperCompileManager.compileReport(design);
        signature = hash;
        compiled = result;
        return result;
    }

    private String repairText(String text) {
        for (char c : "áéíóúÁÉÍÓÚñÑüÜ©¿¡".toCharArray()) {
            String good = String.valueOf(c);
            String broken = new String(good.getBytes(StandardCharsets.UTF_8), java.nio.charset.Charset.forName("windows-1252"));
            text = text.replace(broken, good);
        }
        return text.replace("RE FACTURADO", "REFACTURADO");
    }
}
