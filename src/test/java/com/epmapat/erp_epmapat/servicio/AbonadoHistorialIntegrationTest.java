package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.support.TransactionTemplate;
import com.epmapat.erp_epmapat.controlador.AbonadoHistorialApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;

@EnabledIfEnvironmentVariable(named = "HISTORIAL_TEST_URL", matches = ".+")
class AbonadoHistorialIntegrationTest {
    static JdbcTemplate db;
    static TransactionTemplate tx;
    static DriverManagerDataSource admin;
    static String schema;
    static DriverManagerDataSource ds;
    static void migration() {
        var script = new ResourceDatabasePopulator(new ClassPathResource("sql/2026-11-06_abonado_historial.sql"));
        script.setSeparator(";;");
        script.execute(ds);
    }
    @BeforeAll static void setup() {
        admin = new DriverManagerDataSource(System.getenv("HISTORIAL_TEST_URL"), "historial_test", "");
        schema = "historial_test_" + UUID.randomUUID().toString().replace("-", "");
        new JdbcTemplate(admin).execute("CREATE SCHEMA " + schema);
        ds = new DriverManagerDataSource(System.getenv("HISTORIAL_TEST_URL"), "historial_test", "");
        var props = new Properties(); props.setProperty("currentSchema", schema); ds.setConnectionProperties(props);
        db = new JdbcTemplate(ds); tx = new TransactionTemplate(new DataSourceTransactionManager(ds));
        db.execute("CREATE TABLE clientes(idcliente bigint PRIMARY KEY,nombre text); CREATE TABLE usuarios(idusuario bigint PRIMARY KEY,nomusu text); CREATE TABLE categorias(idcategoria bigint PRIMARY KEY,descripcion text); CREATE TABLE rutas(idruta bigint PRIMARY KEY,codigo text,descripcion text); CREATE TABLE abonados(idabonado bigint PRIMARY KEY,nromedidor text,idcategoria_categorias bigint,idresponsable bigint,idcliente_clientes bigint,idruta_rutas bigint,secuencia bigint,estado bigint,geolocalizacion text,usumodi bigint,fecmodi date,usucrea bigint,feccrea date)");
        migration(); migration(); // reinstalar no duplica el trigger ni elimina los datos
        db.execute("INSERT INTO clientes VALUES(1,'Propietario anterior'),(2,'Propietario nuevo'); INSERT INTO usuarios VALUES(7,'Operador de prueba'); INSERT INTO categorias VALUES(1,'Residencial'),(2,'Comercial'); INSERT INTO rutas VALUES(1,'001','Norte'),(2,'002','Sur')");
    }
    @BeforeEach void reset() {
        db.execute("TRUNCATE abonados, abonado_historial; INSERT INTO abonados(idabonado,nromedidor,idcategoria_categorias,idresponsable,idcliente_clientes,idruta_rutas,secuencia,estado,usumodi,fecmodi) VALUES(100,'M-1',1,1,1,1,1,1,7,CURRENT_DATE)");
    }
    @AfterAll static void cleanup() {
        if (admin != null && schema != null) new JdbcTemplate(admin).execute("DROP SCHEMA " + schema + " CASCADE");
    }
    void context() {
        db.queryForObject("SELECT set_config('epmapat.historial_modulo','TRAMITES_AGUA',true)", String.class);
        db.queryForObject("SELECT set_config('epmapat.historial_usuario','7',true)", String.class);
        db.queryForObject("SELECT set_config('epmapat.historial_observacion','Solicitud 42',true)", String.class);
    }
    @Test void capturesEveryChangedFieldAndHistoricalNames() throws Exception {
        tx.execute(s -> { context(); db.update("UPDATE abonados SET nromedidor='M-2',idcategoria_categorias=2,idresponsable=2,idcliente_clientes=2,idruta_rutas=2 WHERE idabonado=100"); return null; });
        db.update("UPDATE clientes SET nombre='Nombre posterior' WHERE idcliente=1");
        var row = db.queryForMap("SELECT *,cambios::text AS detalle FROM abonado_historial");
        assertEquals("TRAMITES_AGUA", row.get("modulo")); assertEquals("Operador de prueba",row.get("persona")); assertEquals("Solicitud 42",row.get("observacion"));
        var changes = new ObjectMapper().readTree((String)row.get("detalle")); assertEquals(5,changes.size());
        assertTrue(changes.toString().contains("Propietario anterior")); assertTrue(changes.toString().contains("Comercial")); assertTrue(changes.toString().contains("002 - Sur"));
        migration(); assertEquals(1,db.queryForObject("SELECT count(*) FROM abonado_historial",Integer.class));
    }
    @Test void ignoresNoOpAndMetadataOnlyUpdates() {
        db.update("UPDATE abonados SET nromedidor=nromedidor,usumodi=8,fecmodi=CURRENT_DATE WHERE idabonado=100");
        assertEquals(0,db.queryForObject("SELECT count(*) FROM abonado_historial",Integer.class));
    }
    @Test void rollbackRemovesBothChangeAndHistoryAndContextDoesNotLeak() {
        tx.execute(s -> { context(); db.update("UPDATE abonados SET nromedidor='M-2' WHERE idabonado=100"); s.setRollbackOnly(); return null; });
        assertEquals("M-1",db.queryForObject("SELECT nromedidor FROM abonados",String.class));
        assertEquals(0,db.queryForObject("SELECT count(*) FROM abonado_historial",Integer.class));
        db.update("UPDATE abonados SET geolocalizacion='0,1' WHERE idabonado=100");
        var row=db.queryForMap("SELECT modulo,usuario FROM abonado_historial"); assertEquals("OTROS",row.get("modulo")); assertNull(row.get("usuario"));
    }
    @Test void handlesNullsAndPaginatesOnlyTheRequestedAccount() {
        db.update("UPDATE abonados SET nromedidor=NULL WHERE idabonado=100");
        db.update("UPDATE abonados SET nromedidor='M-3' WHERE idabonado=100");
        var api = new AbonadoHistorialApi(db,new ObjectMapper());
        var page=api.consultar(100L,0,1); assertEquals(2L,page.get("totalElements")); assertEquals(2L,page.get("totalPages"));
        assertEquals(1,((java.util.List<?>)page.get("content")).size());
        assertThrows(ResponseStatusException.class,()->api.consultar(999L,0,10));
        assertThrows(ResponseStatusException.class,()->api.consultar(100L,-1,10));
        assertThrows(ResponseStatusException.class,()->api.consultar(100L,0,101));
    }
}
