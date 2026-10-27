package com.epmapat.erp_epmapat.nc;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

@EnabledIfEnvironmentVariable(named="NC_ENDOSOS_TEST_POSTGRES",matches="true")
class NcEndosoIntegrationTest {
 static JdbcTemplate db;static TransactionTemplate tx;static NcEndosoService service;static NcAplicacionCreditoServicio aplicaciones;static org.springframework.transaction.PlatformTransactionManager manager;
 @BeforeAll static void schema() throws Exception {
  var ds=new DriverManagerDataSource("jdbc:postgresql://127.0.0.1:55441/refacturaciones_test","refacturaciones_test","");db=new JdbcTemplate(ds);manager=new DataSourceTransactionManager(ds);tx=new TransactionTemplate(manager);service=new NcEndosoService(db,new ObjectMapper(),true,new BigDecimal("1000000"),true);aplicaciones=proxy(new NcAplicacionCreditoServicio(db,new NcSchemaState(db)));
  db.execute("CREATE TABLE usuarios(idusuario smallint PRIMARY KEY,estado boolean NOT NULL,nomusu text); CREATE TABLE clientes(idcliente bigint PRIMARY KEY,nombre text,cedula text); CREATE TABLE abonados(idabonado bigint PRIMARY KEY,estado smallint,idresponsable bigint REFERENCES clientes); CREATE TABLE ntacredito(idntacredito bigserial PRIMARY KEY,estado int NOT NULL DEFAULT 1,observacion text NOT NULL,valor double precision NOT NULL,devengado double precision,fechaeliminacion date,usuarioeliminacion bigint,razoneliminacion text,nrofactura text,idcliente_clientes bigint NOT NULL,usucrea bigint NOT NULL,feccrea date NOT NULL,idabonado_abonados integer,iddocumento_documentos integer,refdocumento varchar(30)); CREATE TABLE valoresnc(idvaloresnc bigserial PRIMARY KEY,idntacredito_ntacredito bigint REFERENCES ntacredito,valor numeric,saldo numeric,fechaaplicado date,estado int); CREATE TABLE facturas(idfactura bigint PRIMARY KEY,nrofactura text,idabonado bigint,valornotacredito numeric(14,2)); CREATE TABLE facxnc(idfacxnc bigserial PRIMARY KEY,idfactura_facturas bigint REFERENCES facturas,idvaloresnc_valoresnc bigint REFERENCES valoresnc); CREATE TABLE documentos(intdoc integer PRIMARY KEY,nomdoc text);");
  db.execute(Files.readString(Path.of("src/main/resources/sql/2026-10-19_nc_endosos.sql")));
 }
 @BeforeEach void seed(){db.execute("TRUNCATE facturas,facxnc,documentos,nc_endoso_auditoria,nc_endoso_permiso,nc_endoso_detalle,nc_endoso,ntacredito,valoresnc,abonados,clientes,usuarios RESTART IDENTITY CASCADE; INSERT INTO usuarios VALUES(1,true,'Solicitante'),(2,true,'Autoriza'),(3,true,'Ejecuta'),(4,true,'Revierte'); INSERT INTO clientes VALUES(1,'ORIGEN','ID1'),(2,'DESTINO','ID2'); INSERT INTO abonados VALUES(1106,1,1),(1250,1,2),(1320,1,2),(1450,1,2),(1500,0,2); INSERT INTO ntacredito(observacion,valor,devengado,idcliente_clientes,usucrea,feccrea,idabonado_abonados) VALUES('ORIGINAL',500,0,1,1,current_date,1106)");}
 @SuppressWarnings("unchecked") static <T>T proxy(T target){var f=new org.springframework.aop.framework.ProxyFactory(target);f.addAdvice(new org.springframework.transaction.interceptor.TransactionInterceptor(manager,new org.springframework.transaction.annotation.AnnotationTransactionAttributeSource()));return (T)f.getProxy();}
 <T>T run(Supplier<T> work){return tx.execute(status->work.get());}
 NcEndosoService.Solicitud request(String... values){var ds=new ArrayList<NcEndosoService.Destino>();long[] cuentas={1250,1320,1450};for(int i=0;i<values.length;i++)ds.add(new NcEndosoService.Destino(cuentas[i],new BigDecimal(values[i])));return new NcEndosoService.Solicitud(1L,"OFICIO-2026","Respaldo administrativo","Distribucion autorizada de saldos",UUID.randomUUID(),null,ds);}
 long number(Map<String,Object> e,String key){return ((Number)e.get(key)).longValue();}
 NcEndosoService.Accion action(Map<String,Object> e){return new NcEndosoService.Accion(number(e,"version"),"Motivo administrativo de prueba",UUID.randomUUID());}
 Map<String,Object> approved(NcEndosoService.Solicitud r){var e=run(()->service.crear(r,1));long id=number(e,"id");var pending=run(()->service.transicion(id,"solicitar",action(e),1));return run(()->service.transicion(id,"autorizar",action(pending),2));}
 BigDecimal available(){return (BigDecimal)service.disponibilidad(1).get("disponible");}
 @Test void completeAndIdempotent(){var r=request("200","150","150");var e=approved(r);var a=action(e);var done=run(()->service.ejecutar(number(e,"id"),a,3));assertEquals(0,available().signum());assertEquals(3,db.queryForObject("SELECT count(*) FROM ntacredito WHERE tipo_origen='ENDOSO'",Integer.class));run(()->service.ejecutar(number(e,"id"),a,3));assertEquals(3,db.queryForObject("SELECT count(*) FROM ntacredito WHERE tipo_origen='ENDOSO'",Integer.class));assertEquals(0,db.queryForObject("SELECT devengado FROM ntacredito WHERE idntacredito=1",BigDecimal.class).signum());assertEquals(0,db.queryForObject("SELECT sum(disponible) FROM nc_saldos",BigDecimal.class).compareTo(new BigDecimal("500")));}
 @Test void partialAndSpent(){var e=approved(request("350"));run(()->service.ejecutar(number(e,"id"),action(e),3));assertEquals(0,available().compareTo(new BigDecimal("150")));var other=approved(request("200"));assertThrows(ResponseStatusException.class,()->run(()->service.ejecutar(number(other,"id"),action(other),3)));}
 @Test void validationAndSegregation(){var r=request("200");var e=run(()->service.crear(r,1));var pending=run(()->service.transicion(number(e,"id"),"solicitar",action(e),1));assertEquals(403,assertThrows(ResponseStatusException.class,()->run(()->service.transicion(number(e,"id"),"autorizar",action(pending),1))).getRawStatusCode());assertThrows(ResponseStatusException.class,()->run(()->service.editar(number(e,"id"),r,1)));for(String v:List.of("0","-1","1.001"))assertThrows(ResponseStatusException.class,()->run(()->service.crear(request(v),1)));var duplicate=new NcEndosoService.Solicitud(1L,"DOC","REF","Motivo de prueba suficiente",UUID.randomUUID(),null,List.of(new NcEndosoService.Destino(1250L,BigDecimal.ONE),new NcEndosoService.Destino(1250L,BigDecimal.TEN)));assertThrows(ResponseStatusException.class,()->run(()->service.crear(duplicate,1)));for(long c:List.of(1106L,1500L,999999L)){var bad=new NcEndosoService.Solicitud(1L,"DOC","REF","Motivo de prueba suficiente",UUID.randomUUID(),null,List.of(new NcEndosoService.Destino(c,BigDecimal.ONE)));assertThrows(ResponseStatusException.class,()->run(()->service.crear(bad,1)));}}
 @Test void rollbackWhenThirdCreditFails(){var e=approved(request("200","150","150"));db.execute("CREATE FUNCTION nc_test_failure() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.idabonado_abonados=1450 THEN RAISE EXCEPTION 'Fallo tercer destino'; END IF; RETURN NEW; END $$; CREATE TRIGGER nc_test_failure BEFORE INSERT ON ntacredito FOR EACH ROW EXECUTE FUNCTION nc_test_failure();");try{assertThrows(RuntimeException.class,()->run(()->service.ejecutar(number(e,"id"),action(e),3)));assertEquals(0,db.queryForObject("SELECT count(*) FROM ntacredito WHERE tipo_origen='ENDOSO'",Integer.class));assertEquals("AUTORIZADO",service.obtener(number(e,"id")).get("estado"));assertEquals(0,available().compareTo(new BigDecimal("500")));}finally{db.execute("DROP TRIGGER nc_test_failure ON ntacredito; DROP FUNCTION nc_test_failure()");}}
 @Test void reversalAndUsedRejection(){var e=approved(request("200","50"));var done=run(()->service.ejecutar(number(e,"id"),action(e),3));var reversed=run(()->service.revertir(number(e,"id"),action(done),4));assertEquals("REVERTIDO",reversed.get("estado"));assertEquals(0,available().compareTo(new BigDecimal("500")));var approved=approved(request("100"));var doneSecond=run(()->service.ejecutar(number(approved,"id"),action(approved),3));long derived=db.queryForObject("SELECT nota_derivada_id FROM nc_endoso_detalle WHERE endoso_id=?",Long.class,number(approved,"id"));db.update("UPDATE ntacredito SET devengado=1 WHERE idntacredito=?",derived);var consumed=doneSecond;assertThrows(ResponseStatusException.class,()->run(()->service.revertir(number(approved,"id"),action(consumed),4)));
  var consulta=service.consultar(null,1250L,null,null,0,1);
  var resumen=(Map<?,?>)consulta.get("resumen");
  assertEquals(2L,((Number)resumen.get("total")).longValue());
  assertEquals(0,new BigDecimal(resumen.get("monto_total").toString()).compareTo(new BigDecimal("350")));
  assertEquals(0,new BigDecimal(resumen.get("monto_ejecutado").toString()).compareTo(new BigDecimal("100")));
  assertEquals(1,((List<?>)consulta.get("content")).size());
  var pagina2=service.consultar(null,1250L,null,null,1,1);
  var anterior=(Map<?,?>)((List<?>)pagina2.get("content")).get(0);
  assertEquals("1250, 1320",anterior.get("cuentas_destino"));
  assertEquals("ORIGEN",anterior.get("titular_origen"));
  assertEquals("Solicitante",anterior.get("solicitante"));
  assertEquals(1L,((Number)((Map<?,?>)service.consultar("EJECUTADO",null,null,null,0,20).get("resumen")).get("total")).longValue());
  assertEquals(0L,((Number)((Map<?,?>)service.consultar(null,9999L,null,null,0,20).get("resumen")).get("total")).longValue());
  assertThrows(ResponseStatusException.class,()->service.consultar(null,null,null,null,0,101));
 }
 @Test void concurrentEndososCannotOverspend() throws Exception {var a=approved(request("350"));var b=approved(request("350"));var pool=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);try{List<Future<Boolean>> results=new ArrayList<>();for(var e:List.of(a,b))results.add(pool.submit(()->{start.await();try{run(()->service.ejecutar(number(e,"id"),action(e),3));return true;}catch(ResponseStatusException ex){return false;}}));start.countDown();int success=0;for(var f:results)if(f.get(15,TimeUnit.SECONDS))success++;assertEquals(1,success);assertEquals(0,available().compareTo(new BigDecimal("150")));assertThrows(RuntimeException.class,()->db.update("UPDATE ntacredito SET devengado=200 WHERE idntacredito=1"));}finally{pool.shutdownNow();}}
 @Test void jwtIdentityAndExplicitPermissions(){var r=new MockHttpServletRequest();assertEquals(401,assertThrows(ResponseStatusException.class,()->service.actor(r,"SOLICITAR")).getRawStatusCode());r.setAttribute("jwtUserId",1L);assertEquals(1,service.actor(r,"SOLICITAR"));r.setAttribute("jwtUserId",2L);assertEquals(403,assertThrows(ResponseStatusException.class,()->service.actor(r,"SOLICITAR")).getRawStatusCode());db.update("INSERT INTO nc_endoso_permiso VALUES(2,'SOLICITAR')");assertEquals(2,service.actor(r,"SOLICITAR"));var disabled=new NcEndosoService(db,new ObjectMapper(),false,BigDecimal.TEN,false);assertEquals(503,assertThrows(ResponseStatusException.class,()->disabled.actor(r,"SOLICITAR")).getRawStatusCode());}
 @Test void approvedDistributionCannotBeAlteredAndReportsCompile() throws Exception {var e=approved(request("200"));db.update("UPDATE nc_endoso SET referencia='Contenido modificado' WHERE id=?",number(e,"id"));assertThrows(ResponseStatusException.class,()->run(()->service.ejecutar(number(e,"id"),action(e),3)));net.sf.jasperreports.engine.JasperCompileManager.compileReport("src/main/resources/nc-reports/EndosoNotaCredito.jrxml");net.sf.jasperreports.engine.JasperCompileManager.compileReport("src/main/resources/reports/NtaCreditoIndividual.jrxml");net.sf.jasperreports.engine.JasperCompileManager.compileReport("src/main/resources/nc-reports/NtaCreditoConEndosos.jrxml");}
 @Test void endorsementAndCollectionUseSameLock() throws Exception {
  var e=approved(request("350"));db.update("INSERT INTO facturas(idfactura,idabonado) VALUES(101,1106)");var pool=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);
  try{var endoso=pool.submit(()->{start.await();try{run(()->service.ejecutar(number(e,"id"),action(e),3));return true;}catch(RuntimeException ex){return false;}});
   var cobro=pool.submit(()->{start.await();try{return run(()->{aplicaciones.aplicar(101L,1106L,new BigDecimal("200.00"));return true;});}catch(RuntimeException ex){return false;}});
   start.countDown();int success=(endoso.get(15,TimeUnit.SECONDS)?1:0)+(cobro.get(15,TimeUnit.SECONDS)?1:0);assertEquals(1,success);assertTrue(available().signum()>=0);
  }finally{pool.shutdownNow();}
 }
 @Test void receivedCreditReportsAndAttachments() throws Exception {
  var draft=run(()->service.crear(request("200","150","150"),1));long id=number(draft,"id");
  run(()->{service.respaldo(id,"../../respaldo.pdf","%PDF-1.4\nrespaldo de prueba".getBytes(java.nio.charset.StandardCharsets.US_ASCII),1);return null;});
  var fresh=service.obtener(id);var pending=run(()->service.transicion(id,"solicitar",action(fresh),1));var approved=run(()->service.transicion(id,"autorizar",action(pending),2));var done=run(()->service.ejecutar(id,action(approved),3));
  long derived=db.queryForObject("SELECT nota_derivada_id FROM nc_endoso_detalle WHERE endoso_id=? AND cuenta_destino=1250",Long.class,id);
  db.update("INSERT INTO facturas(idfactura,nrofactura,idabonado) VALUES(100,'FACT-100',1250)");
  run(()->aplicaciones.aplicar(100L,1250L,new BigDecimal("25.00")));
  assertEquals(0,available().signum());assertEquals(0,((BigDecimal)service.disponibilidad(derived).get("disponible")).compareTo(new BigDecimal("175")));
  assertThrows(ResponseStatusException.class,()->run(()->service.crear(new NcEndosoService.Solicitud(derived,"DOC","REF","Intentar endoso encadenado",UUID.randomUUID(),null,List.of(new NcEndosoService.Destino(1320L,BigDecimal.TEN))),1)));
  assertThrows(ResponseStatusException.class,()->run(()->{service.respaldo(id,"x.pdf","%PDF-foo".getBytes(),1);return null;}));

  long movement=db.queryForObject("SELECT idvaloresnc FROM valoresnc WHERE idntacredito_ntacredito=?",Long.class,derived);

  var individualDesign=net.sf.jasperreports.engine.xml.JRXmlLoader.load("src/main/resources/nc-reports/NtaCreditoConEndosos.jrxml");
  // Image is supplied by deployment classpath; suppress it in the isolated fixture.
  for(var el:individualDesign.getTitle().getElements())if(el instanceof net.sf.jasperreports.engine.design.JRDesignImage){var expr=new net.sf.jasperreports.engine.design.JRDesignExpression();expr.setText("(java.lang.String)null");((net.sf.jasperreports.engine.design.JRDesignImage)el).setExpression(expr);}
  try(var connection=db.getDataSource().getConnection()){
   var p=new HashMap<String,Object>();p.put("idntacredito",(int)derived);p.put("idusuario",1);
   var individual=net.sf.jasperreports.engine.JasperFillManager.fillReport(net.sf.jasperreports.engine.JasperCompileManager.compileReport(individualDesign),p,connection);
   var pdf=net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf(individual);Files.write(Path.of("target/nc-derivada-verificacion.pdf"),pdf);
   try(var document=org.apache.pdfbox.pdmodel.PDDocument.load(pdf)){String text=new org.apache.pdfbox.text.PDFTextStripper().getText(document).replaceAll("\\s+"," ");assertTrue(text.contains("175"));assertTrue(text.contains("ENDOSO"));assertTrue(text.contains("25"));assertTrue(text.contains("SALDO ANTERIOR"));assertTrue(text.contains("SALDO RESTANTE"));assertFalse(text.contains("EFECTIVO"));}
  }
  var report=net.sf.jasperreports.engine.JasperCompileManager.compileReport("src/main/resources/nc-reports/EndosoNotaCredito.jrxml");
  var params=new HashMap<String,Object>();params.put("id",id);
  try(var connection=db.getDataSource().getConnection()){
   var print=net.sf.jasperreports.engine.JasperFillManager.fillReport(report,params,connection);
   var xlsxOut=new java.io.ByteArrayOutputStream();var exporter=new net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter();exporter.setExporterInput(new net.sf.jasperreports.export.SimpleExporterInput(print));exporter.setExporterOutput(new net.sf.jasperreports.export.SimpleOutputStreamExporterOutput(xlsxOut));exporter.exportReport();assertTrue(xlsxOut.size()>1000);
   var pdf=net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf(print);Files.write(Path.of("target/nc-endoso-verificacion.pdf"),pdf);
   try(var document=org.apache.pdfbox.pdmodel.PDDocument.load(pdf)){String text=new org.apache.pdfbox.text.PDFTextStripper().getText(document);assertTrue(text.contains("1250"));assertTrue(text.contains("500.00")||text.contains("500,00"));assertTrue(text.contains("no comprobante tributario SRI"));}
  }
 }
 @Test void collectionUsesReceivedCreditAndCannotRepeat() {
  var e=approved(request("200"));run(()->service.ejecutar(number(e,"id"),action(e),3));
  db.update("INSERT INTO facturas(idfactura,idabonado) VALUES(100,1250)");
  var caja=new com.epmapat.erp_epmapat.servicio.recaudacion.RecaudacionCobroServicio(manager);
  org.springframework.test.util.ReflectionTestUtils.setField(caja,"aplicacionCredito",aplicaciones);
  var factura=new com.epmapat.erp_epmapat.modelo.Facturas();factura.setIdfactura(100L);factura.setIdabonado(1250L);
  run(()->{org.springframework.test.util.ReflectionTestUtils.invokeMethod(caja,"registrarAplicacionNotaCredito",factura,new BigDecimal("25.00"));return null;});
  assertEquals(0,db.queryForObject("SELECT disponible FROM nc_saldos WHERE idabonado_abonados=1250",BigDecimal.class).compareTo(new BigDecimal("175")));
  assertEquals(0,available().compareTo(new BigDecimal("300")));
  assertEquals(0,db.queryForObject("SELECT valornotacredito FROM facturas WHERE idfactura=100",BigDecimal.class).compareTo(new BigDecimal("25")));
  assertEquals(1,db.queryForObject("SELECT count(*) FROM facxnc WHERE idfactura_facturas=100",Integer.class));
  assertThrows(IllegalStateException.class,()->run(()->aplicaciones.aplicar(100L,1250L,new BigDecimal("25.00"))));
  assertEquals(1,db.queryForObject("SELECT count(*) FROM valoresnc",Integer.class));
  assertThrows(org.springframework.transaction.IllegalTransactionStateException.class,()->aplicaciones.aplicar(100L,1250L,BigDecimal.ONE));
 }
 @Test void collectionSplitsNotesAndRollsBackAllWrites() {
  db.update("INSERT INTO ntacredito(observacion,valor,devengado,idcliente_clientes,usucrea,feccrea,idabonado_abonados) VALUES('OTRA',100,0,1,1,current_date,1106)");
  db.update("INSERT INTO facturas(idfactura,idabonado) VALUES(100,1106),(101,1250)");
  assertThrows(IllegalArgumentException.class,()->run(()->aplicaciones.aplicar(101L,1106L,BigDecimal.ONE)));
  db.execute("CREATE FUNCTION nc_movimiento_falla() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.idntacredito_ntacredito=2 THEN RAISE EXCEPTION 'Error segunda nota'; END IF; RETURN NEW; END $$; CREATE TRIGGER nc_movimiento_falla BEFORE INSERT ON valoresnc FOR EACH ROW EXECUTE FUNCTION nc_movimiento_falla();");
  try{assertThrows(RuntimeException.class,()->run(()->aplicaciones.aplicar(100L,1106L,new BigDecimal("550.00"))));
   assertEquals(0,db.queryForObject("SELECT sum(devengado) FROM ntacredito",BigDecimal.class).signum());assertEquals(0,db.queryForObject("SELECT count(*) FROM valoresnc",Integer.class));assertEquals(0,db.queryForObject("SELECT count(*) FROM facxnc",Integer.class));
  }finally{db.execute("DROP TRIGGER nc_movimiento_falla ON valoresnc; DROP FUNCTION nc_movimiento_falla()");}
  run(()->aplicaciones.aplicar(100L,1106L,new BigDecimal("550.00")));
  assertEquals(2,db.queryForObject("SELECT count(*) FROM valoresnc",Integer.class));assertEquals(0,db.queryForObject("SELECT sum(disponible) FROM nc_saldos",BigDecimal.class).compareTo(new BigDecimal("50")));
 }
 @Test void migratedBalanceRemainsSafeWhenEndososDisabled() {
  var e=approved(request("350"));run(()->service.ejecutar(number(e,"id"),action(e),3));
  var notas=new com.epmapat.erp_epmapat.servicio.NtacreditoServicio();
  var repository=org.mockito.Mockito.mock(com.epmapat.erp_epmapat.repositorio.NtacreditoR.class);
  org.springframework.test.util.ReflectionTestUtils.setField(notas,"dao",repository);org.springframework.test.util.ReflectionTestUtils.setField(notas,"ncSchema",new NcSchemaState(db));org.springframework.test.util.ReflectionTestUtils.setField(notas,"endososEnabled",false);
  notas.findSaldosByCuenta(1106L);org.mockito.Mockito.verify(repository).findSaldosConEndosos(1106L);org.mockito.Mockito.verify(repository,org.mockito.Mockito.never()).findSaldosByCuenta(1106L);
  db.update("INSERT INTO facturas(idfactura,idabonado) VALUES(100,1106)");assertThrows(IllegalStateException.class,()->run(()->aplicaciones.aplicar(100L,1106L,new BigDecimal("200.00"))));
 }
 @Test void httpWorkflowUsesJwtRolesAndCommittedTransactions() throws Exception {
  var mapper=new ObjectMapper();var jwt=new com.epmapat.erp_epmapat.seguridad.JwtService(mapper,"clave-exclusiva-de-prueba-local",3600);
  var api=new NcEndosoApi(proxy(service),db.getDataSource());
  var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(api).defaultRequest(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/").accept("application/json")).setControllerAdvice(new NcEndosoErrors()).addFilters(new com.epmapat.erp_epmapat.seguridad.JwtAuthenticationFilter(jwt)).build();
  String url="/api/nc-endosos";
  mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(url)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
  db.update("INSERT INTO nc_endoso_permiso VALUES(1,'SOLICITAR'),(1,'CONSULTAR'),(1,'AUTORIZAR'),(2,'AUTORIZAR'),(3,'EJECUTAR')");
  String t1="Bearer "+jwt.createWebToken(1L,"solicita","OPERADOR"),t2="Bearer "+jwt.createWebToken(2L,"autoriza","OPERADOR"),t3="Bearer "+jwt.createWebToken(3L,"ejecuta","OPERADOR");
  String created=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url).header("Authorization",t1).contentType("application/json").content(mapper.writeValueAsString(request("200","150","150")))).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
  long id=mapper.readTree(created).get("id").asLong();assertEquals(1,mapper.readTree(created).get("solicitante_id").asLong());
  String body=mapper.writeValueAsString(new NcEndosoService.Accion(0L,"Solicitud administrativa de prueba",UUID.randomUUID()));
  mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url+"/"+id+"/solicitar").header("Authorization",t1).contentType("application/json").content(body)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
  body=mapper.writeValueAsString(new NcEndosoService.Accion(1L,"Autorizacion administrativa de prueba",UUID.randomUUID()));
  mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url+"/"+id+"/autorizar").header("Authorization",t1).contentType("application/json").content(body)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
  mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url+"/"+id+"/autorizar").header("Authorization",t2).contentType("application/json").content(body)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
  body=mapper.writeValueAsString(new NcEndosoService.Accion(2L,"Ejecucion administrativa de prueba",UUID.randomUUID()));
  for(int i=0;i<2;i++)mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url+"/"+id+"/ejecutar").header("Authorization",t3).contentType("application/json").content(body)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
  assertEquals(3,db.queryForObject("SELECT count(*) FROM ntacredito WHERE tipo_origen='ENDOSO'",Integer.class));assertEquals(0,available().signum());
 }
}
