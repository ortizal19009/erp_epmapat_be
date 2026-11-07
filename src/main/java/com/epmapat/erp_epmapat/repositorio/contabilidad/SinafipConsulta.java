package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import com.epmapat.erp_epmapat.DTO.contabilidad.SinafipAsignacion;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SinafipConsulta {
   private final JdbcTemplate jdbc;

   public Integer ejercicio() {
      List<Integer> years = jdbc.query("SELECT EXTRACT(YEAR FROM fechap)::integer FROM definir ORDER BY iddefinir LIMIT 1",
            (rs, row) -> (Integer) rs.getObject(1));
      return years.isEmpty() ? null : years.get(0);
   }

   public List<Map<String, Object>> cuentas(LocalDate desde, LocalDate hasta) {
      // Un solo recorrido de movimientos, sin mutar saldos del plan de cuentas.
      return jdbc.queryForList("""
         WITH movimientos AS (
           SELECT t.codcue,
             SUM(CASE WHEN a.tipasi = 1 AND t.debcre = 1 THEN t.valor::numeric ELSE 0 END) AS apertura_debe,
             SUM(CASE WHEN a.tipasi = 1 AND t.debcre = 2 THEN t.valor::numeric ELSE 0 END) AS apertura_haber,
             SUM(CASE WHEN a.tipasi <> 1 AND t.debcre = 1 THEN t.valor::numeric ELSE 0 END) AS movimiento_debe,
             SUM(CASE WHEN a.tipasi <> 1 AND t.debcre = 2 THEN t.valor::numeric ELSE 0 END) AS movimiento_haber
           FROM transaci t JOIN asientos a ON a.idasiento = t.idasiento
           WHERE a.fecha >= ? AND a.fecha < ? GROUP BY t.codcue
         )
         SELECT c.codcue, COALESCE(SUM(m.apertura_debe),0) AS apertura_debe,
           COALESCE(SUM(m.apertura_haber),0) AS apertura_haber,
           COALESCE(SUM(m.movimiento_debe),0) AS movimiento_debe,
           COALESCE(SUM(m.movimiento_haber),0) AS movimiento_haber
         FROM cuentas c
         LEFT JOIN movimientos m ON m.codcue = c.codcue OR m.codcue LIKE c.codcue || '.%'
         WHERE c.sigef = 1
         GROUP BY c.codcue ORDER BY c.codcue
         """, Date.valueOf(desde), Date.valueOf(hasta.plusDays(1)));
   }

   public List<Map<String, Object>> detalleApertura(LocalDate desde, LocalDate hasta) {
      return jdbc.queryForList("""
         SELECT t.codcue, t.tiptran, t.debcre, bx.valor::numeric AS valor,
                b.rucben, b.ciben, b.nomben, b.swconsufin
         FROM asientos a JOIN transaci t ON t.idasiento = a.idasiento
         JOIN benextran bx ON bx.inttra = t.inttra
         JOIN beneficiarios b ON b.idbene = bx.idbene
         WHERE a.tipasi = 1 AND a.fecha >= ? AND a.fecha < ? AND t.tiptran IN (4,7)
           AND LEFT(t.codcue,9) NOT IN ('124.83.81','124.97.08','224.85.51','224.85.61')
         ORDER BY t.codcue, b.rucben, b.ciben, bx.idbenxtra
         """, Date.valueOf(desde), Date.valueOf(hasta.plusDays(1)));
   }

   public List<Map<String, Object>> presupuesto(LocalDate desde, LocalDate hasta) {
      // La ejecucion se agrega ANTES del join para no multiplicar el presupuesto inicial.
      return jdbc.queryForList("""
         SELECT p.intpre, p.tippar, p.codpar, p.codigo, p.funcion, p.codacti,
                COALESCE(p.inicia,0)::numeric AS inicia,
                COALESCE(e.modifi,0) AS modifi, COALESCE(e.prmiso,0) AS prmiso,
                COALESCE(e.devengado,0) AS devengado, COALESCE(e.cobpagado,0) AS cobpagado
         FROM presupue p LEFT JOIN (
            SELECT intpre, SUM(ROUND(COALESCE(modifi,0)::numeric,2)) AS modifi,
                   SUM(ROUND(COALESCE(prmiso,0)::numeric,2)) AS prmiso,
                   SUM(ROUND(COALESCE(devengado,0)::numeric,2)) AS devengado,
                   SUM(ROUND(COALESCE(cobpagado,0)::numeric,2)) AS cobpagado
            FROM ejecucio WHERE fecha_eje >= ? AND fecha_eje < ? GROUP BY intpre
         ) e ON e.intpre = p.intpre
         WHERE p.tippar IN (1,2) ORDER BY p.tippar,p.codpar,p.intpre
         """, Date.valueOf(desde), Date.valueOf(hasta.plusDays(1)));
   }

   public List<Map<String, Object>> funcionesEspeciales() {
      return jdbc.queryForList("SELECT codigo, funcion FROM estrfunc WHERE funcion LIKE 'D%' ORDER BY codigo");
   }

   public String rucInstitucion() {
      List<String> rucs = jdbc.query("SELECT ruc FROM definir ORDER BY iddefinir LIMIT 1", (rs, row) -> rs.getString(1));
      return rucs.isEmpty() ? "" : rucs.get(0);
   }

   public List<Map<String,Object>> cuentasSinafip() {
      return jdbc.queryForList("SELECT codcue,nomcue FROM cuentas WHERE sigef=1 ORDER BY codcue");
   }

   public void asignar(SinafipAsignacion datos) {
      String tabla = switch (datos.origen()) {
         case "benextran" -> "benextran";
         case "pagoscobros" -> "pagoscobros";
         default -> throw new IllegalArgumentException("Origen de asignacion no valido");
      };
      String id = tabla.equals("benextran") ? "idbenxtra" : "idpagcob";
      Integer cuentas = jdbc.queryForObject("SELECT count(*) FROM cuentas WHERE codcue=? AND sigef=1", Integer.class, datos.codcuereci());
      if (cuentas == null || cuentas == 0) throw new IllegalArgumentException("Seleccione una cuenta marcada para SINAFIP");
      int actualizados = jdbc.update("UPDATE "+tabla+" d SET codcuereci=?,codparreci=?,asierefe=? "
            +"WHERE d."+id+"=? AND EXISTS (SELECT 1 FROM transaci t WHERE t.inttra=d.inttra AND t.tiptran IN "
            +(tabla.equals("benextran") ? "(2,3,5,6))" : "(8,9,11,12))"),
            datos.codcuereci(),datos.codparreci(),datos.asierefe(),datos.idregistro());
      if (actualizados != 1) throw new IllegalArgumentException("No existe la distribucion reciproca seleccionada");
   }

   public List<Map<String, Object>> reciprocas(LocalDate desde, LocalDate hasta) {
      // Ocho DataWindows del legado: 2,3,5,6 toman benextran; 8,9,11,12 toman pagoscobros.
      return jdbc.queryForList("""
         WITH distribuciones AS (
           SELECT t.inttra,t.idasiento,t.codcue,t.debcre,t.tiptran,
                  b.idbene,b.rucben,b.ciben,b.nomben,b.swconsufin,
                  bx.valor::numeric,bx.codparreci,bx.codcuereci,bx.asierefe,bx.idbenxtra AS orden
           FROM transaci t JOIN benextran bx ON bx.inttra=t.inttra JOIN beneficiarios b ON b.idbene=bx.idbene
           WHERE t.tiptran IN (2,3,5,6)
           UNION ALL
           SELECT t.inttra,t.idasiento,t.codcue,t.debcre,t.tiptran,
                  b.idbene,b.rucben,b.ciben,b.nomben,b.swconsufin,
                  pc.valor::numeric,pc.codparreci,pc.codcuereci,pc.asierefe,pc.idpagcob AS orden
           FROM transaci t JOIN pagoscobros pc ON pc.inttra=t.inttra JOIN beneficiarios b ON b.idbene=pc.idbene
           WHERE t.tiptran IN (8,9,11,12)
         )
         SELECT d.*, a.asiento,a.fecha,CASE WHEN d.tiptran<=6 THEN 'benextran' ELSE 'pagoscobros' END AS origen,
                d.orden AS idregistro,
                CASE WHEN EXISTS (SELECT 1 FROM cuentas c WHERE c.codcue=LEFT(d.codcue,9) AND c.sigef=1)
                     THEN LEFT(d.codcue,9) ELSE LEFT(d.codcue,6) END AS cuenta_sinafip
         FROM distribuciones d JOIN asientos a ON a.idasiento=d.idasiento
         WHERE a.fecha >= ? AND a.fecha < ? AND (
           (d.tiptran IN (2,8) AND LEFT(d.codcue,6) IN ('112.05','112.07')) OR
           (d.tiptran IN (3,9) AND LEFT(d.codcue,5)<>'113.8') OR
           (d.tiptran IN (5,11) AND LEFT(d.codcue,6) IN ('212.05','212.07')) OR
           (d.tiptran=6 AND LEFT(d.codcue,6) NOT IN ('213.51','213.61','213.81','213.71')
                       AND LEFT(d.codcue,9) NOT IN ('213.81.51','213.81.61')) OR
           (d.tiptran=12 AND LEFT(d.codcue,6) NOT IN ('213.51','213.61','213.81','213.71')
                        AND LEFT(d.codcue,9) NOT IN ('213.85.51','213.85.61')))
         ORDER BY d.tiptran,d.codcue,a.asiento,d.rucben,d.orden
         """, Date.valueOf(desde), Date.valueOf(hasta.plusDays(1)));
   }
}
