package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.epmapat.erp_epmapat.DTO.*;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.*;
import javax.persistence.spi.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.jupiter.api.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

/** Real PostgreSQL transactions and advisory locks in a disposable database, never a production datasource. */
@Testcontainers(disabledWithoutDocker=true)
class MobileSyncPostgresTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    static EntityManagerFactory factory;
    static EntityManager em;
    static JpaTransactionManager transactions;
    static JdbcTemplate jdbc;
    static MobileReadingReceiptR receipts;

    @BeforeAll static void setup() throws Exception {
        var ds=new DriverManagerDataSource(postgres.getJdbcUrl(),postgres.getUsername(),postgres.getPassword());
        var info=new MutablePersistenceUnitInfo() {
            @Override public ClassLoader getClassLoader() { return MobileSyncPostgresTest.class.getClassLoader(); }
            @Override public ClassLoader getNewTempClassLoader() { return getClassLoader(); }
            @Override public void addTransformer(ClassTransformer transformer) {}
        };
        info.setPersistenceUnitName("mobile-sync-test"); info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        info.setNonJtaDataSource(ds); info.setExcludeUnlistedClasses(true);
        for (Class<?> type: List.of(MobileReadingReceipt.class,TrackingSession.class,TrackingPoint.class,RouteDeviationEvent.class,GpsGapEvent.class))
            info.addManagedClassName(type.getName());
        factory=new HibernatePersistenceProvider().createContainerEntityManagerFactory(info,Map.of(
            "hibernate.hbm2ddl.auto","create-drop",
            "hibernate.dialect","org.hibernate.dialect.PostgreSQL10Dialect",
            "hibernate.physical_naming_strategy","org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy"));
        em=SharedEntityManagerCreator.createSharedEntityManager(factory);
        transactions=new JpaTransactionManager(factory);
        receipts=new JpaRepositoryFactory(em).getRepository(MobileReadingReceiptR.class);
        jdbc=new JdbcTemplate(ds);
        jdbc.execute("CREATE TABLE sync_business_test (id bigint PRIMARY KEY, write_count int NOT NULL DEFAULT 0)");
        jdbc.execute("CREATE TABLE sync_audit_test (reading_id bigint NOT NULL)");
        jdbc.execute("CREATE TABLE lecturas (idlectura bigint PRIMARY KEY)");
        jdbc.execute("INSERT INTO lecturas(idlectura) VALUES(123)");
        try (var connection=ds.getConnection()) {
            var script=new org.springframework.core.io.FileSystemResource("src/main/resources/sql/2026-10-13_mobile_sync_receipts.sql");
            org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript(connection,script);
            org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript(connection,script);
        }
    }
    @AfterAll static void close() { if(factory!=null) factory.close(); }
    @BeforeEach void reset() {
        jdbc.execute("TRUNCATE mobile_reading_receipts, tracking_sessions, tracking_points, route_deviation_events, gps_gap_events CASCADE");
        jdbc.execute("TRUNCATE sync_business_test, sync_audit_test");
        jdbc.execute("INSERT INTO sync_business_test(id) VALUES(123)");
    }
    @SuppressWarnings("unchecked") private static <T> T transactional(T target) {
        var proxy=new ProxyFactory(target); proxy.setProxyTargetClass(true);
        proxy.addAdvice(new TransactionInterceptor(transactions,new AnnotationTransactionAttributeSource()));
        return (T)proxy.getProxy();
    }
    private MobileReadingCommitService commitService(AtomicBoolean failAfterWrite) {
        var business=mock(LecturaServicio.class);
        when(business.actualizarLecturaConAuditoriaAtomica(anyLong(),any(),anyLong(),anyString(),anyString())).thenAnswer(call -> {
            em.createNativeQuery("UPDATE sync_business_test SET write_count=write_count+1 WHERE id=123").executeUpdate();
            em.createNativeQuery("INSERT INTO sync_audit_test(reading_id) VALUES(123)").executeUpdate();
            if(failAfterWrite.get()) throw new IllegalStateException("simulated failure after business and audit");
            return new Lecturas();
        });
        return transactional(new MobileReadingCommitService(em,receipts,business,new ObjectMapper(),mock(LecturaFotoStorageService.class)));
    }
    private LecturaUploadItemDto item() {
        var item=new LecturaUploadItemDto(); item.setIdlectura(123L); item.setOperationId(UUID.randomUUID().toString());
        item.setLecturaactual(10f); return item;
    }
    @Test void expansiveSqlCanRunTwiceWithoutDeletingExistingReading() {
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM lecturas WHERE idlectura=123",Integer.class));
        assertEquals(2,jdbc.queryForObject("SELECT count(*) FROM information_schema.columns WHERE table_name='lecturas' AND column_name IN ('reading_source','reading_capture_age_millis')",Integer.class));
    }
    @Test void concurrentRetriesAfterLostResponseWriteAndAuditExactlyOnce() throws Exception {
        var service=commitService(new AtomicBoolean(false)); var item=item();
        var executor=Executors.newFixedThreadPool(2);
        try {
            var futures=executor.invokeAll(List.<Callable<Void>>of(
                ()->{service.commit(item,new Lecturas(),7L,"test","MODIFICACION");return null;},
                ()->{service.commit(item,new Lecturas(),7L,"test","MODIFICACION");return null;}));
            for(var future:futures) future.get(20,TimeUnit.SECONDS);
            service.commit(item,new Lecturas(),7L,"test","MODIFICACION");
            assertEquals(1,jdbc.queryForObject("SELECT write_count FROM sync_business_test WHERE id=123",Integer.class));
            assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM sync_audit_test",Integer.class));
            assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM mobile_reading_receipts",Integer.class));
        } finally { executor.shutdownNow(); }
    }
    @Test void businessAuditAndReceiptRollbackTogetherAndRetryCanCommit() {
        var failure=new AtomicBoolean(true); var service=commitService(failure); var item=item();
        assertThrows(IllegalStateException.class,()->service.commit(item,new Lecturas(),7L,"test","MODIFICACION"));
        assertEquals(0,jdbc.queryForObject("SELECT write_count FROM sync_business_test WHERE id=123",Integer.class));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM sync_audit_test",Integer.class));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM mobile_reading_receipts",Integer.class));
        failure.set(false); service.commit(item,new Lecturas(),7L,"test","MODIFICACION");
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM mobile_reading_receipts",Integer.class));
    }
    @Test void concurrentSessionRegistrationRecoversSamePrimaryKey() throws Exception {
        var target=new TrackingServicio();
        ReflectionTestUtils.setField(target,"trackingSessionR",new JpaRepositoryFactory(em).getRepository(TrackingSessionR.class));
        ReflectionTestUtils.setField(target,"entityManager",em);
        var service=transactional(target); var dto=new TrackingSessionDto();
        dto.setId(UUID.randomUUID().toString()); dto.setReaderId(7L); dto.setWorkDate(LocalDate.of(2026,10,13)); dto.setConfirmIdentity(true);
        var executor=Executors.newFixedThreadPool(2);
        try {
            var futures=executor.invokeAll(List.<Callable<String>>of(()->service.startSession(dto).getId(),()->service.startSession(dto).getId()));
            for(var future:futures) assertEquals(dto.getId(),future.get(20,TimeUnit.SECONDS));
            assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM tracking_sessions",Integer.class));
        } finally {executor.shutdownNow();}
    }
}
