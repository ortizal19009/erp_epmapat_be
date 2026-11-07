package com.epmapat.erp_epmapat.servicio.administracion;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

/** Generador local, sin conexiones ni cambios en la base de datos. */
@EnabledIfSystemProperty(named = "generar.catalogo.reportes", matches = "true")
class GenerarCatalogoReportesTest {
   private String sql(String value) { return "'" + value.replace("'", "''") + "'"; }

   @Test void generar() throws Exception {
      Path root = Path.of("src/main/resources/reports");
      Path output = root.resolve("catalogo-importado");
      Files.createDirectories(output);
      Map<String, Path> sources = new TreeMap<>();
      try (var files = Files.list(root)) {
         for (Path file : files.filter(Files::isRegularFile).collect(Collectors.toList())) {
            String name = file.getFileName().toString();
            if (name.endsWith(".jrxml")) sources.put(name.substring(0, name.length() - 6).toLowerCase(Locale.ROOT), file);
         }
      }
      try (var files = Files.list(root)) {
         for (Path file : files.filter(Files::isRegularFile).collect(Collectors.toList())) {
            String name = file.getFileName().toString();
            if (name.endsWith(".jasper")) sources.putIfAbsent(name.substring(0, name.length() - 7).toLowerCase(Locale.ROOT), file);
         }
      }
      List<String> rows = new ArrayList<>();
      List<String> inventory = new ArrayList<>(List.of("reporte\tmetodo\tparametros\testado\tobservacion"));
      for (Path source : sources.values()) {
         String fileName = source.getFileName().toString();
         boolean xml = fileName.endsWith(".jrxml");
         String name = fileName.substring(0, fileName.length() - (xml ? 6 : 7));
         try {
            JasperReport report = xml ? JasperCompileManager.compileReport(source.toString())
                  : (JasperReport) JRLoader.loadObject(source.toFile());
            String compiled = "catalogo-importado/" + name + ".jasper";
            JRSaver.saveObject(report, root.resolve(compiled).toString());
            org.junit.jupiter.api.Assertions.assertInstanceOf(JasperReport.class,
                  JRLoader.loadObject(root.resolve(compiled).toFile()));
            if (xml) Files.copy(source, output.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            ObjectNode params = new ObjectMapper().createObjectNode();
            for (JRParameter parameter : report.getParameters()) {
               if (!parameter.isSystemDefined()) params.put(parameter.getName(), parameter.getValueClassName() + "|"
                     + Optional.ofNullable(parameter.getPropertiesMap().getProperty("anchoCampo")).orElse("12") + "|"
                     + Optional.ofNullable(parameter.getPropertiesMap().getProperty("orden")).orElse("999"));
            }
            boolean hasSql = report.getQuery() != null && report.getQuery().getText() != null
                  && !report.getQuery().getText().isBlank();
            int method = hasSql ? 1 : 3;
            rows.add("  (" + sql(name) + ", " + sql(name) + ", " + method + ", "
                  + (xml ? sql("catalogo-importado/" + fileName) : "NULL") + ", " + sql(compiled) + ", " + sql(params.toString()) + "::json)");
            inventory.add(name + "\t" + method + "\t" + params.size() + "\tLISTO\t"
                  + (hasSql ? "SQL directo; requiere conexion y parametros" : "Dataset/plantilla; requiere datos del consumidor")
                  + (xml ? "" : "; solo compilado, sin JRXML"));
         } catch (Exception e) {
            inventory.add(name + "\t\t\tERROR\t" + e.getMessage().replaceAll("[\\r\\n\\t]+", " "));
         }
      }
      String script = "-- Generado desde resources/reports. No contiene binarios.\n"
            + "-- Primero aplicar 2026-11-05_reportesjr_archivos_opciones.sql.\n"
            + "-- Crea una opcion independiente para revisar y asignar los reportes importados.\n"
            + "-- No asigna los reportes a las opciones contables existentes.\n"
            + "-- Copiar reports/catalogo-importado a REPORTESJR_DIRECTORY/catalogo-importado.\n"
            + "-- Los nombres existentes se conservan sin modificaciones.\n"
            + "BEGIN;\nDO $catalogo$\nDECLARE\n"
            + "  v_idrepoxopcion smallint;\n  v_codigo text;\n  v_usucrea smallint;\n"
            + "  v_tabla text;\n  v_columna text;\n  v_secuencia text;\n  v_maximo bigint;\n  v_ultimo bigint;\n"
            + "BEGIN\n"
            + "  -- Sincronizar IDs automaticos sin retroceder las secuencias.\n  LOCK TABLE repoxopcion, reportesjr IN SHARE ROW EXCLUSIVE MODE;\n  FOREACH v_tabla IN ARRAY ARRAY['repoxopcion', 'reportesjr'] LOOP\n    v_columna := CASE WHEN v_tabla = 'repoxopcion' THEN 'idrepoxopcion' ELSE 'idreporte' END;\n    v_secuencia := pg_get_serial_sequence(v_tabla, v_columna);\n    IF v_secuencia IS NULL THEN\n      RAISE EXCEPTION 'La columna %.% no tiene secuencia asociada', v_tabla, v_columna;\n    END IF;\n    EXECUTE format('SELECT COALESCE(MAX(%I), 0) FROM %I', v_columna, v_tabla) INTO v_maximo;\n    EXECUTE format('SELECT last_value FROM %s', v_secuencia) INTO v_ultimo;\n    PERFORM setval(v_secuencia::regclass, GREATEST(v_maximo, v_ultimo), true);\n  END LOOP;\n"
            + "  SELECT min(idrepoxopcion) INTO v_idrepoxopcion FROM repoxopcion\n"
            + "  WHERE opcion = 'Reportes JR' AND nombre = 'Catalogo Jasper importado';\n"
            + "  IF v_idrepoxopcion IS NULL THEN\n"
            + "    SELECT usucrea INTO v_usucrea FROM repoxopcion WHERE usucrea IS NOT NULL ORDER BY idrepoxopcion LIMIT 1;\n"
            + "    IF v_usucrea IS NULL THEN\n"
            + "      RAISE EXCEPTION 'No hay usuario creador de referencia en repoxopcion';\n    END IF;\n"
            + "    SELECT n::text INTO v_codigo FROM generate_series(99001, 99999) AS candidatos(n)\n"
            + "    WHERE NOT EXISTS (SELECT 1 FROM repoxopcion r WHERE r.codigo = n::text) ORDER BY n LIMIT 1;\n"
            + "    IF v_codigo IS NULL THEN\n"
            + "      RAISE EXCEPTION 'No hay codigo libre para el catalogo de importacion';\n    END IF;\n"
            + "    INSERT INTO repoxopcion (codigo, opcion, nombre, usucrea, feccrea)\n"
            + "    VALUES (v_codigo, 'Reportes JR', 'Catalogo Jasper importado', v_usucrea, CURRENT_TIMESTAMP)\n"
            + "    RETURNING idrepoxopcion INTO v_idrepoxopcion;\n  END IF;\n"
            + "  INSERT INTO reportesjr (nomrep, desrep, metodo, jrxml_path, jasper_path, parametros, idrepoxopcion, feccrea)\n"
            + "  SELECT c.nomrep, c.desrep, c.metodo, c.jrxml_path, c.jasper_path, c.parametros, v_idrepoxopcion, CURRENT_TIMESTAMP\n"
            + "  FROM (VALUES\n" + String.join(",\n", rows)
            + "\n  ) AS c(nomrep, desrep, metodo, jrxml_path, jasper_path, parametros)\n"
            + "  WHERE NOT EXISTS (SELECT 1 FROM reportesjr existente WHERE lower(existente.nomrep) = lower(c.nomrep))\n"
            + "  ON CONFLICT (nomrep) DO NOTHING;\nEND $catalogo$;\nCOMMIT;\n";
      Files.writeString(Path.of("src/main/resources/sql/2026-11-05_insert_catalogo_reportesjr.sql"), script, StandardCharsets.UTF_8);
      Files.write(Path.of("docs/reportesjr-inventario.tsv"), inventory, StandardCharsets.UTF_8);
      System.out.println("CATALOGO: " + rows.size() + " listos de " + sources.size() + "; ver docs/reportesjr-inventario.tsv");
      org.junit.jupiter.api.Assertions.assertEquals(sources.size(), rows.size(), "Hay reportes sin compilar; revisar inventario");
   }
}
