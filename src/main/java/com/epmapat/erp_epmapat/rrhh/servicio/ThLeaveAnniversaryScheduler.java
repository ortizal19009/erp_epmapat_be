package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component @RequiredArgsConstructor @Slf4j
@ConditionalOnProperty(name="rrhh.vacaciones.automaticas.enabled", havingValue="true", matchIfMissing=true)
public class ThLeaveAnniversaryScheduler implements ApplicationRunner {
    private final JdbcTemplate jdbc;
    private final ThLeaveAnniversaryServicio worker;
    private final AtomicBoolean running = new AtomicBoolean();

    @Override public void run(ApplicationArguments args) {
        jdbc.execute("CREATE TABLE IF NOT EXISTS th_leave_annual_control (id integer PRIMARY KEY CHECK(id=1), activacion date NOT NULL)");
        jdbc.update("INSERT INTO th_leave_annual_control(id,activacion) VALUES(1,?) ON CONFLICT(id) DO NOTHING",
                java.sql.Date.valueOf(LocalDate.now(ZoneId.of("America/Guayaquil"))));
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron="${rrhh.vacaciones.automaticas.cron:0 10 0 * * *}", zone="America/Guayaquil")
    public void acreditarPendientes() {
        if (!running.compareAndSet(false, true)) return;
        try {
            LocalDate activacion = jdbc.queryForObject("SELECT activacion FROM th_leave_annual_control WHERE id=1", java.sql.Date.class).toLocalDate();
            LocalDate hoy = LocalDate.now(ZoneId.of("America/Guayaquil"));
            long cursor = 0;
            while (true) {
                var ids = jdbc.queryForList("SELECT idpersonal FROM personal WHERE estado IS TRUE AND fecinicio IS NOT NULL "
                        + "AND fecinicio < ? AND idpersonal > ? ORDER BY idpersonal LIMIT 200", Long.class, java.sql.Date.valueOf(hoy), cursor);
                if (ids.isEmpty()) break;
                for (Long id : ids) {
                    try { worker.acreditar(id, activacion, hoy); }
                    catch (Exception error) { log.error("No se pudo acreditar aniversario personal={}; requiere revision", id, error); }
                    cursor = id;
                }
            }
        } catch (Exception error) { log.error("Error acreditando vacaciones por aniversario", error); }
        finally { running.set(false); }
    }
}
