package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

/** Fixed, disposable database only; never uses application datasource properties. */
@EnabledIfEnvironmentVariable(named = "RRHH_TEST_POSTGRES", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThLeavePostgresTest {
    AnnotationConfigApplicationContext context;
    ThLeaveRequestServicio service;
    ThLeaveRequestR requests;
    ThLeaveBalanceR balances;
    ThLeaveMovementR ledger;
    ThAuditServicio audit;
    TransactionTemplate tx;
    Long employeeId;

    @BeforeAll void open() {
        context = new AnnotationConfigApplicationContext(Config.class);
        // Verify the real migration twice, including new columns, only in the fixed disposable DB.
        DataSource datasource = context.getBean(DataSource.class);
        JdbcTemplate jdbc = new JdbcTemplate(datasource);
        jdbc.execute("DROP TABLE th_leave_movements");
        jdbc.execute("ALTER TABLE th_leave_requests DROP COLUMN resuelto_por, DROP COLUMN fecha_resolucion, DROP COLUMN motivo_resolucion");
        ResourceDatabasePopulator migration = new ResourceDatabasePopulator(
                new ClassPathResource("sql/2026-10-06_rrhh_leave_movements.sql"));
        migration.execute(datasource); migration.execute(datasource);
        ResourceDatabasePopulator adjustments = new ResourceDatabasePopulator(new ClassPathResource("sql/2026-10-06_rrhh_leave_balance_adjustments.sql"));
        adjustments.execute(datasource); adjustments.execute(datasource);
        jdbc.execute("ALTER TABLE th_leave_balances DROP COLUMN version");
        jdbc.execute("ALTER TABLE th_audit_log ALTER COLUMN detalle TYPE VARCHAR(255)");
        Long legacyPerson = jdbc.queryForObject("INSERT INTO personal(nombres,estado) VALUES ('Migración',true) RETURNING idpersonal", Long.class);
        jdbc.update("INSERT INTO th_leave_balances(idpersonal_personal,anio,dias_asignados,dias_usados,dias_disponibles,estado) VALUES (?,1900,8,2,6,true)", legacyPerson);
        ResourceDatabasePopulator versions = new ResourceDatabasePopulator(new ClassPathResource("sql/2026-10-06_rrhh_leave_balance_version.sql"));
        versions.execute(datasource); versions.execute(datasource);
        assertEquals(0L, jdbc.queryForObject("SELECT version FROM th_leave_balances WHERE idpersonal_personal=?", Long.class, legacyPerson));
        assertEquals(0, jdbc.queryForObject("SELECT dias_disponibles FROM th_leave_balances WHERE idpersonal_personal=?", BigDecimal.class, legacyPerson).compareTo(new BigDecimal("6")));
        service = context.getBean(ThLeaveRequestServicio.class);
        requests = context.getBean(ThLeaveRequestR.class);
        balances = context.getBean(ThLeaveBalanceR.class);
        ledger = context.getBean(ThLeaveMovementR.class);
        audit = context.getBean(ThAuditServicio.class);
        tx = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
    }
    @AfterAll void close() { if (context != null) context.close(); }

    @BeforeEach void employee() {
        reset(audit);
        employeeId = tx.execute(status -> {
            Personal employee = new Personal(); employee.setNombres("Prueba"); employee.setEstado(true);
            employee = context.getBean(PersonalR.class).save(employee);
            ThLeaveBalance balance = new ThLeaveBalance(); balance.setIdpersonal_personal(employee);
            balance.setAnio(2026); balance.setEstado(true);
            balance.setDias_asignados(new BigDecimal("3")); balance.setDias_disponibles(new BigDecimal("3"));
            balance.setDias_usados(BigDecimal.ZERO); balances.save(balance);
            return employee.getIdpersonal();
        });
    }

    ThLeaveRequest request(int firstDay) {
        ThLeaveRequest request = new ThLeaveRequest();
        Personal employee = new Personal(); employee.setIdpersonal(employeeId);
        request.setIdpersonal_personal(employee); request.setUsucrea(8L); request.setTipolicencia("VACACION");
        request.setFechainicio(LocalDate.of(2026, 10, firstDay));
        request.setFechafin(LocalDate.of(2026, 10, firstDay + 1));
        return request;
    }

    boolean attempt(Runnable operation) {
        try { operation.run(); return true; }
        catch (ResponseStatusException ex) { assertEquals(409, ex.getStatus().value()); return false; }
    }

    void exactlyOne(Supplier<Boolean> first, Supplier<Boolean> second) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Callable<Boolean> one = () -> { ready.countDown(); assertTrue(start.await(10, TimeUnit.SECONDS)); return first.get(); };
            Callable<Boolean> two = () -> { ready.countDown(); assertTrue(start.await(10, TimeUnit.SECONDS)); return second.get(); };
            Future<Boolean> a = executor.submit(one); Future<Boolean> b = executor.submit(two);
            assertTrue(ready.await(10, TimeUnit.SECONDS)); start.countDown();
            int successes = (a.get(30, TimeUnit.SECONDS) ? 1 : 0) + (b.get(30, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, successes);
        } finally { start.countDown(); executor.shutdownNow(); }
    }

    Long balanceId() { return tx.execute(s -> balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow().getIdbalance()); }
    ThLeaveMovementServicio movements() { return context.getBean(ThLeaveMovementServicio.class); }
    String key() { return java.util.UUID.randomUUID().toString(); }

    ThLeaveInboxServicio inbox() { return context.getBean(ThLeaveInboxServicio.class); }

    ThLeaveHistoryServicio history() { return context.getBean(ThLeaveHistoryServicio.class); }

    ThLeaveBalanceStateServicio balanceState() { return context.getBean(ThLeaveBalanceStateServicio.class); }
    Long balanceVersion(Long id) { return tx.execute(s -> balances.findById(id).orElseThrow().getVersion()); }

    @Test void balanceInactivationBlocksNewDebitsButAllowsRefundAndAuditedReactivation() {
        persistAuditInCallerTransaction();
        Long approved = service.save(request(10)).getIdrequest(); service.aprobar(approved, 8L, "Original");
        Long pending = service.save(request(20)).getIdrequest(); Long b = balanceId();
        Long before = balanceVersion(b); String reason = "Revisión " + "x".repeat(1900);
        balanceState().cambiar(b, false, before, reason, 9L);
        tx.executeWithoutResult(s -> { ThLeaveBalance value = balances.findById(b).orElseThrow();
            assertFalse(value.getEstado()); assertEquals(before + 1, value.getVersion());
            assertEquals(0, value.getDias_disponibles().compareTo(BigDecimal.ONE));
            assertEquals(0, value.getDias_usados().compareTo(new BigDecimal("2"))); });
        assertThrows(ResponseStatusException.class, () -> service.aprobar(pending, 8L, "Nueva"));
        assertThrows(ResponseStatusException.class, () -> movements().ajustar(b, BigDecimal.ONE, "Ajuste", key(), 8L));
        service.revertir(approved, 8L, "Consumo devuelto");
        balanceState().cambiar(b, true, balanceVersion(b), "Revisado", 8L);
        var history = balanceState().historial(b);
        assertTrue(history.getEstado()); assertEquals(before + 3, history.getVersion());
        assertEquals(List.of("DEACTIVATE", "ACTIVATE"), history.getEventos().stream().map(e -> e.getAccion()).toList());
        assertTrue(history.getEventos().get(0).getDetalle().contains(reason));
        assertEquals(9L, history.getEventos().get(0).getUsuario());
        assertEquals(3, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void concurrentInactivationWithSameVersionChangesOnce() throws Exception {
        persistAuditInCallerTransaction(); Long b = balanceId(); Long version = balanceVersion(b);
        exactlyOne(() -> attempt(() -> balanceState().cambiar(b, false, version, "Uno", 8L)),
                () -> attempt(() -> balanceState().cambiar(b, false, version, "Dos", 9L)));
        assertFalse(balanceState().historial(b).getEstado());
        assertEquals(version + 1, balanceVersion(b)); assertEquals(1, balanceState().historial(b).getEventos().size());
    }

    @Test void approvalAgainstInactivationIsSerializedAndRejectsStaleVersion() throws Exception {
        persistAuditInCallerTransaction(); Long id = service.save(request(10)).getIdrequest();
        Long b = balanceId(); Long version = balanceVersion(b);
        exactlyOne(() -> attempt(() -> balanceState().cambiar(b, false, version, "Bloquear", 9L)),
                () -> attempt(() -> service.aprobar(id, 8L, "Aprobar")));
        tx.executeWithoutResult(s -> { ThLeaveBalance value = balances.findById(b).orElseThrow();
            String state = requests.findById(id).orElseThrow().getEstado();
            assertEquals(Boolean.TRUE.equals(value.getEstado()) ? "APROBADA" : "SOLICITADA", state);
            assertEquals(0, value.getDias_disponibles().compareTo(Boolean.TRUE.equals(value.getEstado()) ? BigDecimal.ONE : new BigDecimal("3")));
            assertEquals(version + 1, value.getVersion()); });
    }

    @Test void balanceStateAuditFailureRollsBackStateVersionAndEvent() {
        persistAuditInCallerTransaction(); Long b = balanceId(); Long version = balanceVersion(b);
        ThAuditServicio writer = new ThAuditServicio(context.getBean(ThAuditLogR.class));
        doAnswer(inv -> { writer.log(inv.getArgument(0), inv.getArgument(1), inv.getArgument(2), inv.getArgument(3), inv.getArgument(4));
            throw new IllegalStateException("Fallo auditoría"); }).when(audit).log(eq("TH_LEAVE_BALANCE"), eq(b), eq("DEACTIVATE"), anyString(), eq(8L));
        assertThrows(IllegalStateException.class, () -> balanceState().cambiar(b, false, version, "Revisar", 8L));
        assertTrue(balanceState().historial(b).getEstado()); assertEquals(version, balanceVersion(b));
        assertTrue(balanceState().historial(b).getEventos().isEmpty());
    }

    @Test void reactivationRejectsExternalLedgerDifferenceAndDuplicateAnnualBalance() {
        Long b = balanceId(); movements().abrirLibro(b, 8L); balanceState().cambiar(b, false, balanceVersion(b), "Revisar", 8L);
        tx.executeWithoutResult(s -> { ThLeaveBalance value = balances.findById(b).orElseThrow();
            value.setDias_asignados(new BigDecimal("4")); value.setDias_disponibles(new BigDecimal("4")); balances.save(value); });
        assertThrows(ResponseStatusException.class, () -> balanceState().cambiar(b, true, balanceVersion(b), "Activar", 8L));
        tx.executeWithoutResult(s -> { ThLeaveBalance value = balances.findById(b).orElseThrow();
            value.setDias_asignados(new BigDecimal("3")); value.setDias_disponibles(new BigDecimal("3")); balances.save(value);
            ThLeaveBalance duplicate = new ThLeaveBalance(); duplicate.setIdpersonal_personal(value.getIdpersonal_personal());
            duplicate.setAnio(2026); duplicate.setEstado(false); balances.save(duplicate); });
        assertThrows(ResponseStatusException.class, () -> balanceState().cambiar(b, true, balanceVersion(b), "Activar", 8L));
        assertFalse(balanceState().historial(b).getEstado());
    }

    @Test void unchangedStateIsNoOpAndFinancialAdjustmentInvalidatesOldVersion() {
        persistAuditInCallerTransaction(); Long b = balanceId(); Long version = balanceVersion(b);
        balanceState().cambiar(b, true, version, "Ya activo", 8L);
        assertEquals(version, balanceVersion(b)); assertTrue(balanceState().historial(b).getEventos().isEmpty());
        movements().ajustar(b, BigDecimal.ONE, "Aumento", key(), 8L);
        assertEquals(version + 1, balanceVersion(b));
        assertThrows(ResponseStatusException.class, () -> balanceState().cambiar(b, false, version, "Datos antiguos", 8L));
        assertTrue(balanceState().historial(b).getEstado()); assertTrue(balanceState().historial(b).getEventos().isEmpty());
    }

    void persistAuditInCallerTransaction() {
        // The production request service owns the transaction; the test mock delegates its writes to the real repository.
        ThAuditServicio writer = new ThAuditServicio(context.getBean(ThAuditLogR.class));
        doAnswer(inv -> { writer.log(inv.getArgument(0), inv.getArgument(1), inv.getArgument(2), inv.getArgument(3), inv.getArgument(4));
            return null; }).when(audit).log(anyString(), anyLong(), anyString(), nullable(String.class), anyLong());
    }

    @Test void historyContainsPersistedDecisionsAndOnlyLinkedFinancialMovements() {
        persistAuditInCallerTransaction();
        Long id = service.save(request(10)).getIdrequest(); service.aprobar(id, 8L, "Original"); service.revertir(id, 9L, "Corrección");
        Long another = service.save(request(20)).getIdrequest();
        tx.executeWithoutResult(s -> new ThAuditServicio(context.getBean(ThAuditLogR.class)).log("TH_ACTION", id, "CREATE", "Ajeno", 8L));
        var result = history().consultar(id);
        assertEquals(List.of("CREATE", "APPROVE", "REVERSE"), result.getEventos().stream().map(e -> e.getAccion()).toList());
        assertEquals(List.of(8L, 8L, 9L), result.getEventos().stream().map(e -> e.getUsuario()).toList());
        assertTrue(result.getEventos().stream().allMatch(e -> e.getFecha() != null));
        assertEquals("REVERTIDA", result.getSolicitud().getEstado()); assertEquals("Original", result.getSolicitud().getObservacion_aprobacion());
        assertEquals(2, result.getMovimientos().size()); assertEquals("CONSUMO", result.getMovimientos().get(0).getTipo());
        assertEquals("REINTEGRO", result.getMovimientos().get(1).getTipo());
        assertEquals(result.getMovimientos().get(0).getIdmovement(), result.getMovimientos().get(1).getIdmovement_origen());
        assertTrue(result.getMovimientos().stream().allMatch(m -> id.equals(m.getIdrequest())));
        assertTrue(result.getAdvertencias().isEmpty()); assertEquals(1, history().consultar(another).getEventos().size());
    }

    @Test void historyKeepsRejectionAndCancellationReasonsWithoutFinancialMovements() {
        persistAuditInCallerTransaction();
        Long rejected = service.save(request(10)).getIdrequest(); service.rechazar(rejected, 9L, "No procede");
        Long cancelled = service.save(request(20)).getIdrequest(); service.cancelar(cancelled, 8L, "No requerida");
        var rejection = history().consultar(rejected); var cancellation = history().consultar(cancelled);
        assertEquals(List.of("CREATE", "REJECT"), rejection.getEventos().stream().map(e -> e.getAccion()).toList());
        assertEquals("No procede", rejection.getEventos().get(1).getDetalle());
        assertEquals(List.of("CREATE", "CANCEL"), cancellation.getEventos().stream().map(e -> e.getAccion()).toList());
        assertEquals("No requerida", cancellation.getEventos().get(1).getDetalle());
        assertTrue(rejection.getMovimientos().isEmpty()); assertTrue(cancellation.getMovimientos().isEmpty());
        assertTrue(rejection.getAdvertencias().isEmpty()); assertTrue(cancellation.getAdvertencias().isEmpty());
    }

    @Test void historyDoesNotBackfillLegacyApprovalWithoutEventsOrMovements() {
        Long id = tx.execute(s -> { ThLeaveRequest r = request(10); r.setEstado("APROBADA"); r.setDias_solicitados(new BigDecimal("2"));
            return requests.save(r).getIdrequest(); });
        var result = history().consultar(id);
        assertTrue(result.getEventos().isEmpty()); assertTrue(result.getMovimientos().isEmpty());
        assertFalse(result.getAdvertencias().isEmpty());
        assertEquals(0, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
        assertEquals("APROBADA", tx.execute(s -> requests.findById(id).orElseThrow().getEstado()));
    }

    @Test void rolledBackApprovalLeavesNoAuditEventOrMovementInHistory() {
        persistAuditInCallerTransaction(); Long id = service.save(request(10)).getIdrequest();
        ThAuditServicio writer = new ThAuditServicio(context.getBean(ThAuditLogR.class));
        doAnswer(inv -> { writer.log(inv.getArgument(0), inv.getArgument(1), inv.getArgument(2), inv.getArgument(3), inv.getArgument(4));
            throw new IllegalStateException("Fallo tras escribir auditoría"); })
            .when(audit).log(anyString(), anyLong(), eq("APPROVE"), nullable(String.class), anyLong());
        assertThrows(IllegalStateException.class, () -> service.aprobar(id, 8L, "Revisado"));
        var result = history().consultar(id);
        assertEquals("SOLICITADA", result.getSolicitud().getEstado());
        assertEquals(List.of("CREATE"), result.getEventos().stream().map(e -> e.getAccion()).toList());
        assertTrue(result.getMovimientos().isEmpty()); assertTrue(result.getAdvertencias().isEmpty());
    }

    @Test void inboxPaginatesInStableOrderAndCombinesPersonStateAndType() {
        Long first = service.save(request(10)).getIdrequest();
        ThLeaveRequest permiso = request(20); permiso.setTipolicencia("PERMISO");
        Long second = service.save(permiso).getIdrequest(); service.aprobar(second, 8L, "Permiso");
        ThLeaveRequest licencia = request(25); licencia.setTipolicencia("LICENCIA");
        Long third = service.save(licencia).getIdrequest();
        var page0 = inbox().consultar("todas", "todos", employeeId, null, null, 0, 1);
        assertEquals(3, page0.getTotal_elementos()); assertEquals(3, page0.getTotal_paginas());
        assertEquals(first, page0.getContenido().get(0).getIdrequest());
        assertEquals(second, inbox().consultar("TODAS", null, employeeId, null, null, 1, 1).getContenido().get(0).getIdrequest());
        assertEquals(third, inbox().consultar("TODAS", null, employeeId, null, null, 2, 1).getContenido().get(0).getIdrequest());
        var approved = inbox().consultar(" aprobada ", " permiso ", employeeId, null, null, 0, 20);
        assertEquals(1, approved.getTotal_elementos()); assertEquals(second, approved.getContenido().get(0).getIdrequest());
        assertEquals(employeeId, approved.getContenido().get(0).getIdpersonal());
        assertTrue(inbox().consultar("TODAS", null, employeeId, null, null, 10, 1).getContenido().isEmpty());
    }

    @Test void annualBookExportReflectsRealConsumptionAdjustmentAndRefundWithoutWrites() {
        Long id = service.save(request(10)).getIdrequest(); service.aprobar(id, 8L, "Consumo");
        movements().ajustar(balanceId(), new BigDecimal("1.25"), "Ajuste", key(), 8L);
        service.revertir(id, 9L, "Reintegro");
        var rows = movements().byPersonal(employeeId, 2026);
        String csv = new String(com.epmapat.erp_epmapat.rrhh.dto.ThLeaveMovementCsv.render(employeeId, rows), java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(4, rows.size()); assertEquals(5, csv.lines().count());
        assertTrue(csv.contains("\"CONSUMO\";")); assertTrue(csv.contains("\"AJUSTE\";1.25;"));
        var debit = rows.stream().filter(r -> r.getTipo().equals("CONSUMO")).findFirst().orElseThrow();
        var refund = rows.stream().filter(r -> r.getTipo().equals("REINTEGRO")).findFirst().orElseThrow();
        assertEquals(debit.getIdmovement(), refund.getIdmovement_origen());
        assertTrue(csv.contains(";" + refund.getIdrequest() + ";" + debit.getIdmovement() + ";\"REINTEGRO\";"));
        assertTrue(movements().byPersonal(employeeId, 2027).isEmpty());
        assertEquals(rows, movements().byPersonal(employeeId, 2026));
    }

    @Test void calendarIncludesIntersectingApprovedRequestsAndExcludesReversedPendingAndOtherTypes() {
        Long vacation = service.save(request(10)).getIdrequest(); service.aprobar(vacation, 8L, "Confirmado");
        ThLeaveRequest permiso = request(20); permiso.setTipolicencia("PERMISO");
        Long permit = service.save(permiso).getIdrequest(); service.aprobar(permit, 8L, "Confirmado");
        service.save(request(25));
        LocalDate start = LocalDate.of(2026, 10, 11);
        var result = inbox().calendario(null, employeeId, start, LocalDate.of(2026, 10, 31), 0);
        assertEquals(List.of(vacation, permit), result.getContenido().stream().map(r -> r.getIdrequest()).toList());
        assertEquals(100, result.getTamano());
        assertEquals(permit, inbox().calendario("PERMISO", employeeId, start, LocalDate.of(2026, 10, 31), 0)
                .getContenido().get(0).getIdrequest());
        service.revertir(vacation, 8L, "Cambio");
        assertEquals(1, inbox().calendario(null, employeeId, start, LocalDate.of(2026, 10, 31), 0).getTotal_elementos());
    }

    @Test void calendarKeepsLargeLegacyResultSetsBoundedToOneHundredRows() {
        tx.execute(s -> {
            for (int i = 0; i < 101; i++) {
                ThLeaveRequest row = request(10); row.setEstado(" aprobada "); row.setTipolicencia(" permiso ");
                row.setFechainicio(LocalDate.of(2027, 10, 1)); row.setFechafin(LocalDate.of(2027, 10, 2));
                context.getBean(ThLeaveRequestR.class).save(row);
            }
            return null;
        });
        var first = inbox().calendario("PERMISO", employeeId, LocalDate.of(2027, 10, 1), LocalDate.of(2027, 10, 31), 0);
        var second = inbox().calendario("PERMISO", employeeId, LocalDate.of(2027, 10, 1), LocalDate.of(2027, 10, 31), 1);
        assertEquals(101, first.getTotal_elementos()); assertEquals(2, first.getTotal_paginas());
        assertEquals(100, first.getContenido().size()); assertEquals(1, second.getContenido().size());
        assertTrue(first.getContenido().get(99).getIdrequest() < second.getContenido().get(0).getIdrequest());
    }

    @Test void inboxDateFilterIncludesIntersectionAndSingleBoundaries() {
        Long first = service.save(request(10)).getIdrequest(); Long second = service.save(request(20)).getIdrequest();
        LocalDate overlap = LocalDate.of(2026, 10, 11);
        var page = inbox().consultar("SOLICITADA", "VACACION", employeeId, overlap, overlap, 0, 20);
        assertEquals(1, page.getTotal_elementos()); assertEquals(first, page.getContenido().get(0).getIdrequest());
        assertEquals(first, inbox().consultar("TODAS", null, employeeId, null, LocalDate.of(2026, 10, 10), 0, 20).getContenido().get(0).getIdrequest());
        assertEquals(second, inbox().consultar("TODAS", null, employeeId, LocalDate.of(2026, 10, 21), null, 0, 20).getContenido().get(0).getIdrequest());
    }

    @Test void inboxUpdatesAfterDecisionAndKeepsTerminalHistory() {
        Long id = service.save(request(10)).getIdrequest();
        assertEquals(1, inbox().consultar("SOLICITADA", null, employeeId, null, null, 0, 20).getTotal_elementos());
        service.cancelar(id, 8L, "No requerida");
        assertEquals(0, inbox().consultar("SOLICITADA", null, employeeId, null, null, 0, 20).getTotal_elementos());
        var cancelled = inbox().consultar("CANCELADA", null, employeeId, null, null, 0, 20);
        assertEquals(1, cancelled.getTotal_elementos()); assertEquals("No requerida", cancelled.getContenido().get(0).getMotivo_resolucion());
    }

    @Test void generalInboxIncludesMultipleEmployeesWithoutIndividualFetches() {
        Long other = tx.execute(s -> { Personal p = new Personal(); p.setNombres("Otro"); p.setEstado(true);
            return context.getBean(PersonalR.class).save(p).getIdpersonal(); });
        ThLeaveRequest one = request(10); one.setTipolicencia("LICENCIA");
        one.setFechainicio(LocalDate.of(2040, 1, 1)); one.setFechafin(LocalDate.of(2040, 1, 2)); service.save(one);
        ThLeaveRequest two = request(20); two.setTipolicencia("LICENCIA");
        Personal p = new Personal(); p.setIdpersonal(other); two.setIdpersonal_personal(p);
        two.setFechainicio(LocalDate.of(2040, 1, 4)); two.setFechafin(LocalDate.of(2040, 1, 5)); service.save(two);
        var page = inbox().consultar("SOLICITADA", "LICENCIA", null, LocalDate.of(2040, 1, 1), LocalDate.of(2040, 1, 10), 0, 20);
        assertEquals(2, page.getTotal_elementos());
        assertEquals(java.util.Set.of(employeeId, other), page.getContenido().stream().map(r -> r.getIdpersonal()).collect(java.util.stream.Collectors.toSet()));
    }

    @Test void reconciliationMatchesRealApprovalAdjustmentsAndReversalWithoutWriting() {
        Long id = service.save(request(10)).getIdrequest(); service.aprobar(id, 8L, "Original");
        movements().ajustar(balanceId(), new BigDecimal("1.25"), "Incremento", key(), 8L);
        movements().ajustar(balanceId(), new BigDecimal("-0.25"), "Disminución", key(), 8L);
        service.revertir(id, 9L, "Reintegro");
        var row = context.getBean(ThLeaveReconciliationServicio.class).verificar(employeeId, 2026).getEjercicios().get(0);
        assertEquals("COINCIDE", row.getEstado_libro()); assertEquals(5, row.getMovimientos());
        assertEquals(0, row.getApertura().compareTo(new BigDecimal("3")));
        assertEquals(0, row.getConsumos().compareTo(new BigDecimal("2")));
        assertEquals(0, row.getReintegros().compareTo(new BigDecimal("2")));
        assertEquals(0, row.getAjustes().compareTo(BigDecimal.ONE));
        assertEquals(0, row.getDisponibles_libro().compareTo(new BigDecimal("4")));
        assertTrue(row.getSolicitudes_sin_consumo().isEmpty()); assertTrue(row.getAlertas().isEmpty());
        assertEquals(5, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
        assertEquals("REVERTIDA", tx.execute(s -> requests.findById(id).orElseThrow().getEstado()));
    }

    @Test void reconciliationPreservesLegacyUsedDaysAndDetectsHistoricalApprovals() {
        Long requestId = tx.execute(s -> {
            ThLeaveBalance b = balances.findById(balanceId()).orElseThrow();
            b.setDias_usados(BigDecimal.ONE); b.setDias_disponibles(new BigDecimal("2")); balances.save(b);
            ThLeaveRequest r = request(10); r.setEstado("APROBADA"); r.setDias_solicitados(BigDecimal.ONE);
            return requests.save(r).getIdrequest();
        });
        var checker = context.getBean(ThLeaveReconciliationServicio.class);
        assertEquals("SIN_LIBRO", checker.verificar(employeeId, null).getEjercicios().get(0).getEstado_libro());
        movements().abrirLibro(balanceId(), 8L);
        var row = checker.verificar(employeeId, null).getEjercicios().get(0);
        assertEquals("COINCIDE", row.getEstado_libro());
        assertEquals(0, row.getUsados_libro().compareTo(BigDecimal.ONE));
        assertEquals(List.of(requestId), row.getSolicitudes_sin_consumo());
        assertEquals(1, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void reconciliationIdentifiesCoherentExternalBalanceEdit() {
        movements().abrirLibro(balanceId(), 8L);
        tx.executeWithoutResult(s -> { ThLeaveBalance b = balances.findById(balanceId()).orElseThrow();
            b.setDias_asignados(new BigDecimal("4")); b.setDias_disponibles(new BigDecimal("4")); balances.save(b); });
        var row = context.getBean(ThLeaveReconciliationServicio.class).verificar(employeeId, null).getEjercicios().get(0);
        assertEquals("DIFERENCIA", row.getEstado_libro());
        assertEquals(0, row.getDiferencia_disponibles().compareTo(BigDecimal.ONE));
        assertEquals(0, row.getDiferencia_asignados().compareTo(BigDecimal.ONE));
        assertEquals(0, row.getDiferencia_usados().signum());
    }

    @Test void adjustmentRetryDoesNotDuplicateAndPreservesUsedDays() {
        Long request = service.save(request(10)).getIdrequest(); service.aprobar(request, 8L, "OK");
        Long balance = balanceId(); String key = key();
        movements().ajustar(balance, new BigDecimal("1.25"), "Corrección", key, 8L);
        movements().ajustar(balance, new BigDecimal("1.25"), "Corrección", key, 8L);
        tx.executeWithoutResult(s -> {
            ThLeaveBalance b = balances.findById(balance).orElseThrow();
            assertEquals(0, b.getDias_asignados().compareTo(new BigDecimal("4.25")));
            assertEquals(0, b.getDias_disponibles().compareTo(new BigDecimal("2.25")));
            assertEquals(0, b.getDias_usados().compareTo(new BigDecimal("2")));
            assertEquals(3, ledger.findByPersonal(employeeId, null).size());
        });
        assertThrows(ResponseStatusException.class, () -> movements().ajustar(balance, BigDecimal.ONE, "Corrección", key, 8L));
    }

    @Test void concurrentAdjustmentsCannotOverdraw() throws Exception {
        Long balance = balanceId();
        exactlyOne(() -> attempt(() -> movements().ajustar(balance, new BigDecimal("-2"), "Uno", key(), 8L)),
                () -> attempt(() -> movements().ajustar(balance, new BigDecimal("-2"), "Dos", key(), 9L)));
        assertEquals(0, (int) tx.execute(s -> balances.findById(balance).orElseThrow().getDias_disponibles().compareTo(BigDecimal.ONE)));
    }

    @Test void simultaneousSameKeyReturnsSameMovement() throws Exception {
        Long balance = balanceId(); String key = key();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Callable<Void> operation = () -> { start.await(); movements().ajustar(balance, BigDecimal.ONE, "Uno", key, 8L); return null; };
            Future<Void> a = pool.submit(operation), b = pool.submit(operation); start.countDown();
            a.get(30, TimeUnit.SECONDS); b.get(30, TimeUnit.SECONDS);
            assertEquals(2, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
            assertEquals(0, (int) tx.execute(s -> balances.findById(balance).orElseThrow().getDias_disponibles().compareTo(new BigDecimal("4"))));
        } finally { start.countDown(); pool.shutdownNow(); }
    }

    @Test void invalidAdjustmentsLeaveNoOpeningOrBalanceChanges() {
        Long balance = balanceId();
        assertThrows(ResponseStatusException.class, () -> movements().ajustar(balance, new BigDecimal("-4"), "Motivo", key(), 8L));
        assertThrows(ResponseStatusException.class, () -> movements().ajustar(balance, new BigDecimal("0.001"), "Motivo", key(), 8L));
        assertThrows(ResponseStatusException.class, () -> movements().ajustar(balance, BigDecimal.ONE, "  ", key(), 8L));
        assertThrows(ResponseStatusException.class, () -> movements().ajustar(balance, BigDecimal.ZERO, "Motivo", key(), 8L));
        assertEquals(0, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void explicitOpeningIsIdempotentAndDetectsExternalChanges() {
        Long balance = balanceId(); movements().abrirLibro(balance, 8L); movements().abrirLibro(balance, 9L);
        assertEquals(1, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
        tx.executeWithoutResult(s -> { ThLeaveBalance b = balances.findById(balance).orElseThrow();
            b.setDias_asignados(new BigDecimal("4")); b.setDias_disponibles(new BigDecimal("4")); balances.save(b); });
        assertThrows(ResponseStatusException.class, () -> movements().ajustar(balance, BigDecimal.ONE, "Motivo", key(), 8L));
    }

    @Test void concurrentOverlappingCreationKeepsSingleRequest() throws Exception {
        exactlyOne(() -> attempt(() -> service.save(request(10))), () -> attempt(() -> service.save(request(10))));
        assertEquals(1, (int) tx.execute(s -> requests.findByPersonal(employeeId).size()));
    }

    @Test void concurrentApprovalOfSameRequestDeductsOnce() throws Exception {
        Long id = service.save(request(10)).getIdrequest();
        exactlyOne(() -> attempt(() -> service.aprobar(id, 8L, "Primera")),
                () -> attempt(() -> service.aprobar(id, 9L, "Segunda")));
        assertEquals(0, (int) tx.execute(s -> balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow()
                .getDias_disponibles().compareTo(BigDecimal.ONE)));
    }

    @Test void concurrentDifferentApprovalsCannotOverdrawSharedBalance() throws Exception {
        Long first = service.save(request(10)).getIdrequest();
        Long second = service.save(request(20)).getIdrequest();
        exactlyOne(() -> attempt(() -> service.aprobar(first, 8L, "Primera")),
                () -> attempt(() -> service.aprobar(second, 9L, "Segunda")));
        assertEquals(0, (int) tx.execute(s -> balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow()
                .getDias_disponibles().compareTo(BigDecimal.ONE)));
        assertEquals(1L, (long) tx.execute(s -> requests.findByPersonal(employeeId).stream()
                .filter(r -> "APROBADA".equals(r.getEstado())).count()));
    }

    @Test void failureAfterDeductionRollsBackBalanceAndApproval() {
        Long id = service.save(request(10)).getIdrequest();
        doThrow(new IllegalStateException("Audit failure")).when(audit).log(anyString(), anyLong(), eq("APPROVE"), anyString(), anyLong());
        assertThrows(IllegalStateException.class, () -> service.aprobar(id, 8L, "Revisado"));
        assertEquals("SOLICITADA", tx.execute(s -> requests.findById(id).orElseThrow().getEstado()));
        assertEquals(0, (int) tx.execute(s -> balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow()
                .getDias_disponibles().compareTo(new BigDecimal("3"))));
        assertEquals(0, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void concurrentReversalsReturnDaysOnceAndPreserveOriginalApproval() throws Exception {
        Long id = service.save(request(10)).getIdrequest();
        service.aprobar(id, 8L, "Aprobación original");
        exactlyOne(() -> attempt(() -> service.revertir(id, 9L, "Corrección uno")),
                () -> attempt(() -> service.revertir(id, 10L, "Corrección dos")));
        tx.executeWithoutResult(s -> {
            ThLeaveRequest reversed = requests.findById(id).orElseThrow();
            assertEquals("REVERTIDA", reversed.getEstado());
            assertEquals(8L, reversed.getAprobador_id());
            assertEquals("Aprobación original", reversed.getObservacion_aprobacion());
            assertNotNull(reversed.getResuelto_por());
            assertNotNull(reversed.getFecha_resolucion());
            assertEquals(0, balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow()
                    .getDias_disponibles().compareTo(new BigDecimal("3")));
            var movements = ledger.findByPersonal(employeeId, 2026);
            assertEquals(3, movements.size());
            assertEquals(ThLeaveMovement.Tipo.REINTEGRO, movements.get(0).getTipo());
            assertEquals(movements.get(1).getIdmovement(), movements.get(0).getOrigen().getIdmovement());
            assertEquals(0, movements.stream().map(ThLeaveMovement::getDias).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .compareTo(new BigDecimal("3")));
        });
    }

    @Test void cancellationReleasesDatesWithoutCreatingBalanceMovements() {
        Long id = service.save(request(10)).getIdrequest();
        service.cancelar(id, 8L, "Cambio de fechas");
        assertEquals("CANCELADA", tx.execute(s -> requests.findById(id).orElseThrow().getEstado()));
        assertDoesNotThrow(() -> service.save(request(10)));
        assertEquals(0, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void failureAfterRefundRollsBackLedgerAndState() {
        Long id = service.save(request(10)).getIdrequest();
        service.aprobar(id, 8L, "Original");
        doThrow(new IllegalStateException("Audit failure")).when(audit).log(anyString(), anyLong(), eq("REVERSE"), anyString(), anyLong());
        assertThrows(IllegalStateException.class, () -> service.revertir(id, 9L, "Corregir"));
        assertEquals("APROBADA", tx.execute(s -> requests.findById(id).orElseThrow().getEstado()));
        assertEquals(0, (int) tx.execute(s -> balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow()
                .getDias_disponibles().compareTo(BigDecimal.ONE)));
        assertEquals(2, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void legacyApprovalWithoutRecordedDebitCannotBeRefunded() {
        ThLeaveRequest legacy = request(10); legacy.setEstado("APROBADA"); legacy.setDias_solicitados(new BigDecimal("2"));
        Long id = tx.execute(s -> requests.save(legacy).getIdrequest());
        assertThrows(ResponseStatusException.class, () -> service.revertir(id, 8L, "Histórica"));
        assertEquals("APROBADA", tx.execute(s -> requests.findById(id).orElseThrow().getEstado()));
        assertEquals(0, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void approvalAndCancellationAreMutuallyExclusive() throws Exception {
        Long id = service.save(request(10)).getIdrequest();
        exactlyOne(() -> attempt(() -> service.aprobar(id, 8L, "Aprobar")),
                () -> attempt(() -> service.cancelar(id, 9L, "Cancelar")));
        tx.executeWithoutResult(s -> {
            String state = requests.findById(id).orElseThrow().getEstado();
            assertTrue("APROBADA".equals(state) || "CANCELADA".equals(state));
            assertEquals(0, balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow().getDias_disponibles()
                    .compareTo("APROBADA".equals(state) ? BigDecimal.ONE : new BigDecimal("3")));
        });
    }

    @Test void openingPreservesLegacyUsedDaysAndOnlyRecordedConsumptionIsRefunded() {
        tx.executeWithoutResult(s -> {
            ThLeaveBalance balance = balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow();
            balance.setDias_usados(BigDecimal.ONE); balance.setDias_disponibles(new BigDecimal("2"));
            balances.save(balance);
        });
        Long id = service.save(request(10)).getIdrequest();
        service.aprobar(id, 8L, "Actual"); service.revertir(id, 9L, "Corregir");
        tx.executeWithoutResult(s -> {
            ThLeaveBalance balance = balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow();
            assertEquals(0, balance.getDias_usados().compareTo(BigDecimal.ONE));
            assertEquals(0, balance.getDias_disponibles().compareTo(new BigDecimal("2")));
            assertEquals(0, ledger.findByPersonal(employeeId, 2026).stream().map(ThLeaveMovement::getDias)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(new BigDecimal("2")));
        });
    }

    @Test void editsOutsideLedgerRequireReconciliationBeforeRefund() {
        Long id = service.save(request(10)).getIdrequest(); service.aprobar(id, 8L, "Actual");
        tx.executeWithoutResult(s -> {
            ThLeaveBalance balance = balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow();
            balance.setDias_asignados(new BigDecimal("4")); balance.setDias_disponibles(new BigDecimal("2"));
            balances.save(balance);
        });
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.revertir(id, 9L, "Corregir"));
        assertTrue(error.getReason().contains("conciliación"));
        assertEquals("APROBADA", tx.execute(s -> requests.findById(id).orElseThrow().getEstado()));
        assertEquals(2, (int) tx.execute(s -> ledger.findByPersonal(employeeId, null).size()));
    }

    @Test void newAnnualBalanceCreatesOpeningAndReadResponseIncludesOnlyThatYear() {
        ThLeaveBalance balance = new ThLeaveBalance();
        Personal employee = new Personal(); employee.setIdpersonal(employeeId);
        balance.setIdpersonal_personal(employee); balance.setAnio(2027);
        balance.setDias_asignados(new BigDecimal("20")); balance.setDias_usados(BigDecimal.ONE); balance.setUsucrea(8L);
        context.getBean(ThLeaveBalanceServicio.class).save(balance);
        var rows = context.getBean(ThLeaveMovementServicio.class).byPersonal(employeeId, 2027);
        assertEquals(1, rows.size());
        assertEquals("APERTURA", rows.get(0).getTipo());
        assertEquals(2027, rows.get(0).getAnio());
        assertEquals(8L, rows.get(0).getUsuario());
        assertEquals(0, rows.get(0).getDias().compareTo(new BigDecimal("19")));
        assertNull(rows.get(0).getIdrequest());
    }

    @Test void reversalCanRestoreRecordedDaysToAnInactiveBalance() {
        Long id = service.save(request(10)).getIdrequest(); service.aprobar(id, 8L, "Original");
        tx.executeWithoutResult(s -> {
            ThLeaveBalance balance = balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow();
            balance.setEstado(false); balances.save(balance);
        });
        service.revertir(id, 9L, "Corregir consumo");
        assertEquals(0, (int) tx.execute(s -> balances.findByPersonalAndAnio(employeeId, 2026).orElseThrow()
                .getDias_disponibles().compareTo(new BigDecimal("3"))));
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:postgresql://127.0.0.1:55439/rrhh_test", "rrhh_test", "");
        }
        @Bean LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource); factory.setPackagesToScan("com.epmapat.erp_epmapat.rrhh.modelo");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop",
                    "hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect"));
            return factory;
        }
        @Bean PlatformTransactionManager transactionManager(EntityManagerFactory factory) { return new JpaTransactionManager(factory); }
        @Bean EntityManager entityManager(EntityManagerFactory factory) { return SharedEntityManagerCreator.createSharedEntityManager(factory); }
        @Bean PersonalR people(EntityManager em) { return new JpaRepositoryFactory(em).getRepository(PersonalR.class); }
        @Bean ThLeaveRequestR requests(EntityManager em) { return new JpaRepositoryFactory(em).getRepository(ThLeaveRequestR.class); }
        @Bean ThLeaveBalanceR balances(EntityManager em) { return new JpaRepositoryFactory(em).getRepository(ThLeaveBalanceR.class); }
        @Bean ThLeaveMovementR ledger(EntityManager em) { return new JpaRepositoryFactory(em).getRepository(ThLeaveMovementR.class); }
        @Bean ThLeaveMovementServicio movements(ThLeaveMovementR ledger, ThLeaveBalanceR balances) {
            return new ThLeaveMovementServicio(ledger, balances);
        }
        @Bean ThLeaveReconciliationServicio reconciliation(PersonalR people, ThLeaveBalanceR balances,
                ThLeaveMovementR ledger, ThLeaveRequestR requests) {
            return new ThLeaveReconciliationServicio(people, balances, ledger, requests);
        }
        @Bean ThLeaveInboxServicio inbox(ThLeaveRequestR requests) { return new ThLeaveInboxServicio(requests); }
        @Bean ThLeaveBalanceStateServicio balanceState(ThLeaveBalanceR balances, ThLeaveMovementR ledger,
                ThAuditServicio audit, ThAuditLogR logs) { return new ThLeaveBalanceStateServicio(balances, ledger, audit, logs); }
        @Bean ThAuditLogR auditLogs(EntityManager em) { return new JpaRepositoryFactory(em).getRepository(ThAuditLogR.class); }
        @Bean ThLeaveHistoryServicio history(ThLeaveRequestR requests, ThAuditLogR logs, ThLeaveMovementR ledger) {
            return new ThLeaveHistoryServicio(requests, logs, ledger);
        }
        @Bean ThLeaveBalanceServicio balanceService(ThLeaveBalanceR balances, PersonalR people, ThLeaveMovementServicio movements) {
            return new ThLeaveBalanceServicio(balances, people, movements);
        }
        @Bean ThAuditServicio audit() { return mock(ThAuditServicio.class); }
        @Bean ThLeaveRequestServicio service(ThLeaveRequestR requests, PersonalR people, ThLeaveBalanceR balances,
                ThAuditServicio audit, ThLeaveMovementServicio movements) {
            return new ThLeaveRequestServicio(requests, people, balances, audit, movements);
        }
    }
}
