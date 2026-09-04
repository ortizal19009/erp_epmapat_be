package com.epmapat.erp_epmapat.servicio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class BackupService {

    @Scheduled(cron = "0 0 2 * * *") // 2:00 AM todos los días
    public void generarBackupProgramado() throws IOException, InterruptedException {
        generarBackup(); // Reutiliza tu método de backup
    }


    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    private final TelegramNotificationService telegramNotificationService;

    public BackupService(TelegramNotificationService telegramNotificationService) {
        this.telegramNotificationService = telegramNotificationService;
    }

    // Configuración inyectable
    @Value("${backup.db.username:postgres}")
    private String dbUsername;

    @Value("${backup.db.password}")
    private String dbPassword;

    @Value("${backup.db.host:localhost}")
    private String dbHost;

    @Value("${backup.db.port:5432}")
    private String dbPort;

    @Value("${backup.db.name}")
    private String dbName;

    @Value("${backup.folder.windows:C:\\backups\\postgres\\}")
    private String backupFolderWindows;

    @Value("${backup.folder.linux:/var/backups/postgres/}")
    private String backupFolderLinux;

    @Value("${pgdump.path.windows:}")
    private String pgDumpPathWindows;

    @Value("${pgdump.path.linux:/usr/bin/pg_dump}")
    private String pgDumpPathLinux;

    public void generarBackup() throws IOException, InterruptedException {
        LocalDateTime startedAt = LocalDateTime.now();
        try {
            String backupFile = generarBackupInterno();
            telegramNotificationService.notifyBackup(
                    true,
                    backupFile,
                    Duration.between(startedAt, LocalDateTime.now()).toSeconds(),
                    "Respaldo generado correctamente"
            );
        } catch (IOException | InterruptedException exception) {
            telegramNotificationService.notifyBackup(
                    false,
                    "No generado",
                    Duration.between(startedAt, LocalDateTime.now()).toSeconds(),
                    exception.getMessage()
            );
            throw exception;
        }
    }

    private String generarBackupInterno() throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        String pgDumpPath = resolvePgDumpCommand(isWindows);
        String backupFolder = isWindows ? backupFolderWindows : backupFolderLinux;

        // Crear carpeta si no existe
        Path backupDir = Paths.get(backupFolder);
        Files.createDirectories(backupDir);

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String archivoBackup = backupDir.resolve("backup_" + fecha + ".sql").toString();

        logger.info("Generando backup en: {}", archivoBackup);

        ProcessBuilder pb = new ProcessBuilder(
                pgDumpPath,
                "-U", dbUsername,
                "-h", dbHost,
                "-p", dbPort,
                "-d", dbName,
                "-f", archivoBackup
        );

        // Variables de entorno
        pb.environment().put("PGPASSWORD", dbPassword);

        // Redirigir salida y errores
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("pg_dump: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            logger.info("✅ Backup generado exitosamente en {}", archivoBackup);
            return archivoBackup;
        } else {
            logger.error("❌ Error al generar backup. Código: {}", exitCode);
            throw new IOException("Backup falló con código " + exitCode);
        }
    }

    private String resolvePgDumpCommand(boolean isWindows) {
        if (isWindows) {
            if (pgDumpPathWindows != null && !pgDumpPathWindows.isBlank()) {
                Path configuredPath = Paths.get(pgDumpPathWindows);
                if (Files.isRegularFile(configuredPath)) {
                    return configuredPath.toString();
                }

                logger.warn("No se encontro pg_dump en {}. Se usara pg_dump desde el PATH.", pgDumpPathWindows);
            }

            return "pg_dump";
        }

        Path configuredPath = Paths.get(pgDumpPathLinux);
        if (Files.exists(configuredPath)) {
            return configuredPath.toString();
        }

        logger.warn("No se encontrÃ³ pg_dump en {}. Se intentarÃ¡ usar 'pg_dump' desde el PATH.", pgDumpPathLinux);
        return "pg_dump";
    }
}
