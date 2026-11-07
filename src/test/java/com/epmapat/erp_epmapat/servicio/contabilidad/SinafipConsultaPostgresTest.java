package com.epmapat.erp_epmapat.servicio.contabilidad;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.epmapat.erp_epmapat.repositorio.contabilidad.SinafipConsulta;

/** Usar exclusivamente una base temporal vacia. */
@EnabledIfEnvironmentVariable(named = "SINAFIP_TEST_JDBC_URL", matches = ".+")
class SinafipConsultaPostgresTest {
   @Test void consultaSinMultiplicarValoresYRespetaEjercicioCorteYTipoDeAsiento() {
      var source = new DriverManagerDataSource(System.getenv("SINAFIP_TEST_JDBC_URL"),"catalogo_test","");
      JdbcTemplate jdbc = new JdbcTemplate(source);
      jdbc.execute("CREATE TABLE definir(iddefinir int, fechap date,ruc text)");
      jdbc.execute("INSERT INTO definir VALUES(1,'2026-01-01','1234567890001')");
      jdbc.execute("CREATE TABLE cuentas(codcue text,sigef int)");
      jdbc.execute("INSERT INTO cuentas VALUES('111.01',1),('111.01.01',0),('211.01',1)");
      jdbc.execute("CREATE TABLE asientos(idasiento int,tipasi int,fecha timestamp)");
      jdbc.execute("INSERT INTO asientos VALUES(1,1,'2026-01-01'),(2,2,'2026-01-20'),(3,2,'2026-02-01'),(4,2,'2025-12-31'),(5,2,'2027-01-01')");
      jdbc.execute("CREATE TABLE transaci(inttra int,idasiento int,codcue text,debcre int,valor numeric,tiptran int)");
      jdbc.execute("INSERT INTO transaci VALUES(1,1,'111.01.01',1,100.01,4),(2,1,'111.01.01',2,25.01,7),(3,2,'111.01.01',1,20,2),(4,2,'111.01.01',2,7,3),(5,3,'111.01.01',1,10,2),(6,4,'111.01.01',1,999,2),(7,5,'111.01.01',1,999,2)");
      jdbc.execute("CREATE TABLE presupue(intpre int,tippar int,codpar text,codigo text,funcion text,codacti text,inicia numeric)");
      jdbc.execute("INSERT INTO presupue VALUES(1,2,'01.51.01.05','51.01.05','','',1000),(2,1,'13.01.01','13.01.01','','',2000)");
      jdbc.execute("CREATE TABLE ejecucio(intpre int,fecha_eje date,modifi numeric,prmiso numeric,devengado numeric,cobpagado numeric)");
      jdbc.execute("INSERT INTO ejecucio VALUES(1,'2026-01-10',100,20,10,5),(1,'2026-01-20',50,40,30,25),(1,'2026-02-01',999,999,999,999)");
      jdbc.execute("CREATE TABLE estrfunc(codigo text,funcion text)");
      var consulta = new SinafipConsulta(jdbc);
      var resultado = new SinafipServicio(consulta).generar("balance",2026,1);
      assertEquals("01|111|01|00|75,00|0,00|20,00|7,00|95,00|7,00|88,00|0,00",
            String.join("|",resultado.bloques().get(0).filas().get(0)));
      var presupuesto = consulta.presupuesto(LocalDate.of(2026,1,1),LocalDate.of(2026,1,31));
      var gasto = presupuesto.stream().filter(p -> p.get("tippar").equals(2)).findFirst().orElseThrow();
      assertEquals(0,new java.math.BigDecimal("1000").compareTo((java.math.BigDecimal)gasto.get("inicia")));
      assertEquals(0,new java.math.BigDecimal("150").compareTo((java.math.BigDecimal)gasto.get("modifi")));
      assertEquals(2,presupuesto.size(),"Tambien debe incluir partidas sin ejecucion");
      assertEquals(7,jdbc.queryForObject("SELECT count(*) FROM transaci",Integer.class));

      jdbc.execute("INSERT INTO asientos VALUES(6,2,'2026-01-31 23:45:00')");
      jdbc.execute("INSERT INTO transaci VALUES(8,6,'111.01.01',1,5,2)");
      var cierre = new SinafipServicio(consulta).generar("balance",2026,1);
      assertEquals("25,00",cierre.bloques().get(0).filas().get(0).get(6),"El ultimo dia incluye sus horas");
      jdbc.execute("ALTER TABLE asientos ADD asiento int");
      jdbc.execute("UPDATE asientos SET asiento=idasiento*10");
      jdbc.execute("CREATE TABLE beneficiarios(idbene int,rucben text,ciben text,nomben text,swconsufin int)");
      jdbc.execute("INSERT INTO beneficiarios VALUES(1,'1234567890001','','Proveedor',0)");
      jdbc.execute("CREATE TABLE benextran(idbenxtra int,inttra int,idbene int,valor numeric,codparreci text,codcuereci text,asierefe int)");
      jdbc.execute("CREATE TABLE pagoscobros(idpagcob int,inttra int,idbene int,valor numeric,codparreci text,codcuereci text,asierefe int)");
      int[] tipos={2,3,5,6,8,9,11,12};
      String[] codigos={"112.05.01","113.01.01","212.05.01","213.99.01","112.07.01","113.01.01","212.07.01","213.99.01"};
      for(int i=0;i<tipos.length;i++) {
         int id=100+tipos[i];
         jdbc.update("INSERT INTO transaci VALUES(?,2,?,1,100,?)",id,codigos[i],tipos[i]);
         String tabla=tipos[i]<=6 ? "benextran" : "pagoscobros";
         jdbc.update("INSERT INTO "+tabla+" VALUES(?,?,1,10,NULL,'213.01',7)",id,id);
      }
      var reciprocas = new SinafipServicio(consulta).generar("reciprocas",2026,1);
      assertEquals(8,reciprocas.bloques().get(0).filas().size());
      assertTrue(reciprocas.bloques().get(0).filas().stream().allMatch(f->f.size()==21));
      assertEquals(8,consulta.reciprocas(LocalDate.of(2026,1,2),LocalDate.of(2026,1,31)).size());
      jdbc.execute("INSERT INTO cuentas VALUES('213.01',1)");
      new SinafipServicio(consulta).asignar(new com.epmapat.erp_epmapat.DTO.contabilidad.SinafipAsignacion("benextran",102,"213.01","51.01.05",99L));
      assertEquals("51.01.05",jdbc.queryForObject("SELECT codparreci FROM benextran WHERE idbenxtra=102",String.class));
      assertEquals(0,new java.math.BigDecimal("10").compareTo(jdbc.queryForObject("SELECT valor FROM benextran WHERE idbenxtra=102",java.math.BigDecimal.class)));
      assertThrows(IllegalArgumentException.class,()->new SinafipServicio(consulta).asignar(
            new com.epmapat.erp_epmapat.DTO.contabilidad.SinafipAsignacion("otra_tabla",102,"213.01",null,0L)));
   }
}
