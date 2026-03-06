package com.epmapat.erp_epmapat.servicio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.AuditoriaMobileRequest;

@Service
public class AuditoriaTxtService {

    @Value("${audit.output.dir:}")
    private String auditOutputDir;

    @Value("${audit.output.windows:C:/ERP/auditoria}")
    private String auditWindows;

    @Value("${audit.output.linux:/var/log/epmapat/auditoria}")
    private String auditLinux;

    public String save(AuditoriaMobileRequest req) throws IOException {
        String baseDir = resolveBaseDir();
        Path dir = Paths.get(baseDir);
        Files.createDirectories(dir);

        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Path file = dir.resolve("audit_" + day + ".txt");

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String content = new StringBuilder()
                .append("================ AUDITORIA MOBILE ================\n")
                .append("Accion: ").append(n(req.accion)).append("\n")
                .append("Entidad: ").append(n(req.entidad)).append("\n")
                .append("Id: ").append(n(req.entidadId)).append("\n")
                .append("Usuario: ").append(n(req.usuario)).append("\n")
                .append("FechaHoraServidor: ").append(ts).append("\n")
                .append("FechaHoraCliente: ").append(n(req.fechaHora)).append("\n")
                .append("Dia: ").append(n(req.dia)).append("\n")
                .append("Ubicacion: ").append(n(req.ubicacion)).append("\n")
                .append("Datos:\n")
                .append(n(req.payload)).append("\n")
                .append("===================================================\n\n")
                .toString();

        Files.write(file, content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        return file.toAbsolutePath().toString();
    }

    private String resolveBaseDir() {
        if (auditOutputDir != null && !auditOutputDir.isBlank()) return auditOutputDir;

        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) return auditWindows;
        return auditLinux;
    }

    private String n(String s) {
        return (s == null || s.isBlank()) ? "N/D" : s;
    }
}
