package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.support.TransactionTemplate;
import com.epmapat.erp_epmapat.repositorio.contabilidad.SinafipConsulta;

/** Comparacion optativa de lectura. PostgreSQL impide escrituras durante esta transaccion. */
@EnabledIfEnvironmentVariable(named = "SINAFIP_READONLY_JDBC_URL", matches = ".+")
class SinafipReferenciaLocalTest {
   @Test void compararConArchivosDelSistemaAnterior() throws Exception {
      ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME))
            .setLevel(ch.qos.logback.classic.Level.WARN);
      var source = new DriverManagerDataSource(System.getenv("SINAFIP_READONLY_JDBC_URL"),
            System.getenv("SINAFIP_READONLY_USER"), System.getenv("SINAFIP_READONLY_PASSWORD"));
      var manager = new DataSourceTransactionManager(source);
      manager.setEnforceReadOnly(true);
      var tx = new TransactionTemplate(manager);
      tx.setReadOnly(true);
      var service = new SinafipServicio(new SinafipConsulta(new JdbcTemplate(source)));
      Path referencias = Path.of(System.getenv("SINAFIP_LEGACY_REFERENCE"));
      Path output = Path.of("target/sinafip-comparacion");
      Files.createDirectories(output);
      List<String> informe = new ArrayList<>();
      for (String[] caso : List.of(new String[]{"apertura","AI_prueba1.txt"},
            new String[]{"balance","BC_prueba1.txt"}, new String[]{"detalle-apertura","DAI_prueba1.txt"},
            new String[]{"presupuesto","PI_prueba1.txt"},new String[]{"cedula","CIG_prueba1.txt"},
            new String[]{"reciprocas","TR_prueba1.txt"})) {
         com.epmapat.erp_epmapat.DTO.contabilidad.SinafipResultado resultado;
         try { resultado = tx.execute(status -> service.generar(caso[0],2026,1)); }
         catch (IllegalArgumentException error) {
            // Este diagnostico conserva las validaciones; no rellena asignaciones en la base real.
            String linea=caso[0]+": datos pendientes: "+error.getMessage();
            informe.add(linea);
            System.out.println("SINAFIP_COMPARACION "+linea);
            continue;
         }
         List<String> filas = resultado.bloques().stream().flatMap(b -> b.filas().stream()).map(f -> String.join("|",f)).toList();
         String contenido = filas.isEmpty() ? "" : String.join("\r\n",filas)+"\r\n";
         Files.writeString(output.resolve(caso[1]),contenido,StandardCharsets.UTF_8);
         if (!Files.exists(referencias.resolve(caso[1]))) {
            String linea=caso[0]+": referencia no disponible, actual="+filas.size()+", columnas="
                  +resultado.bloques().stream().map(b->Integer.toString(b.columnas().size())).toList();
            informe.add(linea);
            System.out.println("SINAFIP_COMPARACION "+linea);
            continue;
         }
         byte[] esperado = Files.readAllBytes(referencias.resolve(caso[1]));
         List<String> anterior = new String(esperado,StandardCharsets.ISO_8859_1).lines().toList();
         String linea = caso[0]+": anterior="+anterior.size()+", actual="+filas.size()
               +", bytesIguales="+Arrays.equals(esperado,contenido.getBytes(StandardCharsets.UTF_8))
               +", filasIguales="+frecuencias(anterior).equals(frecuencias(filas));
         boolean totalesIguales = true;
         if (!caso[0].equals("reciprocas")) {
            int primeraColumna = caso[0].equals("detalle-apertura") ? 6 : 4;
            int columnas = caso[0].equals("balance") ? 12 : caso[0].equals("detalle-apertura") ? 8 : 6;
            for (int i = primeraColumna; i < columnas; i++) totalesIguales &= total(anterior,i).compareTo(total(filas,i)) == 0;
         }
         linea += ", totalesIguales=" + totalesIguales;
         informe.add(linea);
         System.out.println("SINAFIP_COMPARACION "+linea);
      }
      Files.write(output.resolve("resultado.txt"),informe,StandardCharsets.UTF_8);
   }

   private Map<String,Long> frecuencias(List<String> filas) {
      return filas.stream().collect(java.util.stream.Collectors.groupingBy(f -> f,java.util.stream.Collectors.counting()));
   }

   private java.math.BigDecimal total(List<String> filas, int columna) {
      return filas.stream().map(f -> new java.math.BigDecimal(f.split("\\|",-1)[columna].replace(',','.')))
            .reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add);
   }
}
