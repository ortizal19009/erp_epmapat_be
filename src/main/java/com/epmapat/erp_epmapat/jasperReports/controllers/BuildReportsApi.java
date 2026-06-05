package com.epmapat.erp_epmapat.jasperReports.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.jasperReports.DTO.Jasper_DTO;
import com.epmapat.erp_epmapat.jasperReports.services.BuildReports;
import com.epmapat.erp_epmapat.jasperReports.services.ReporteExportService;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@RestController
@RequestMapping("/jasperReports")

public class BuildReportsApi {
    private static final String MERGED_FILENAME = "comprobantes_merged.pdf";

    @Autowired
    private BuildReports buildReports;
    @Autowired
    private ReporteExportService reporteExportService;
    @Autowired
    private DataSource dataSource;

    private final ConcurrentHashMap<String, MergeJobStatus> mergeJobs = new ConcurrentHashMap<>();

    @GetMapping("/reportes")
    public ResponseEntity<String> reportesGetNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("El método GET no está permitido en este endpoint. Usa POST.");
    }

    @PostMapping("/reportes")
    public ResponseEntity<Resource> generarPdfFactura(@RequestBody Jasper_DTO jasperDTO) {
        try {
            Jasper_DTO dto = new Jasper_DTO();
            dto.setReportName(jasperDTO.getReportName());
            dto.setExtencion(resolveExtension(jasperDTO.getExtencion()));

            Map<String, Object> params = new HashMap<>();

            Map<String, Object> requestParameters = jasperDTO.getParameters() != null
                    ? jasperDTO.getParameters()
                    : new HashMap<>();

            for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value == null)
                    continue;

                if ("desde".equalsIgnoreCase(key) || "hasta".equalsIgnoreCase(key) || "tope".equalsIgnoreCase(key)) {
                    params.put(key, parseDateToSQLType(value.toString()));
                } else if ("hdesde".equalsIgnoreCase(key) || "hhasta".equalsIgnoreCase(key)) {
                    params.put(key, parseToSqlTime(value.toString()));
                } else {
                    params.put(key, normalizeParameterValue(dto.getReportName(), key, value));
                }

            }

            dto.setParameters(params);

            ByteArrayOutputStream outputStream;
            try (Connection conn = dataSource.getConnection()) {
                var jasperPrint = buildReports.buildPrint(dto.getReportName(), dto.getParameters(), conn);
                outputStream = reporteExportService.export(dto.getExtencion(), jasperPrint);
            }

            byte[] fileBytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + jasperDTO.getReportName() + "." + dto.getExtencion())
                    .contentType(resolveMediaType(dto.getExtencion()))
                    .contentLength(fileBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String resolveExtension(String extencion) {
        if (extencion == null || extencion.trim().isEmpty()) {
            return "pdf";
        }

        String normalized = extencion.trim().toLowerCase();
        if (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }

        return switch (normalized) {
            case "pdf", "xml", "xlsx", "csv" -> normalized;
            default -> throw new IllegalArgumentException("Formato no soportado: " + extencion);
        };
    }

    private MediaType resolveMediaType(String extension) {
        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "xml" -> MediaType.APPLICATION_XML;
            case "xlsx" -> MediaType
                    .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "csv" -> MediaType.parseMediaType("text/csv");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private Object parseDateToSQLType(String value) throws ParseException {
        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                Date parsed = sdf.parse(value.trim());
                // ðŸ”¹ SIEMPRE devolver Timestamp, no Date
                return new java.sql.Timestamp(parsed.getTime());
            } catch (ParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Fecha inválida: " + value);
    }

    private java.sql.Time parseToSqlTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be null or empty");
        }

        String[] formats = {
                "HH:mm:ss",
                "HH:mm"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                Date parsed = sdf.parse(timeStr.trim());
                return new java.sql.Time(parsed.getTime());
            } catch (ParseException e) {
                // Try next format
            }
        }

        throw new IllegalArgumentException("Invalid time format: '" + timeStr +
                "'. Expected formats: HH:mm:ss or HH:mm");
    }

    private Object normalizeParameterValue(String reportName, String key, Object value) {
        if (value instanceof Long) {
            return value;
        } else if (value instanceof Integer) {
            return shouldPromoteToLong(reportName, key) ? Long.valueOf(((Integer) value).longValue()) : value;
        } else if (value instanceof java.util.Date) {
            return value; // Devuelve la fecha tal cual
        } else if (value instanceof String) {
            try {
                String trimmed = ((String) value).trim();
                if (shouldPromoteToLong(reportName, key)) {
                    return Long.valueOf(trimmed);
                }
                return value;
            } catch (NumberFormatException e) {
                return value; // o lanza excepción si sabes que debe ser numérico
            }
        }
        return value;
    }

    private boolean shouldPromoteToLong(String reportName, String key) {
        if (key == null) {
            return false;
        }
        String normalizedKey = key.trim().toLowerCase();
        String normalizedReport = reportName == null ? "" : reportName.trim();
        if (normalizedReport.startsWith("CompPago")) {
            return true;
        }
        return "idfactura".equals(normalizedKey)
                || "cuenta".equals(normalizedKey)
                || "idabonado".equals(normalizedKey)
                || "idntacredito".equals(normalizedKey)
                || "idntacredito_ntacredito".equals(normalizedKey)
                || normalizedKey.endsWith("_id");
    }

    @PostMapping("/comprobante")
    public ResponseEntity<String> imprimirPDF(@RequestParam("pdf") MultipartFile pdfFile) {
        try {
            // Guardar archivo temporal
            File tempFile = File.createTempFile("comprobante_", ".pdf");
            pdfFile.transferTo(tempFile);

            // Llamar función de impresión
            imprimirArchivoPDF(tempFile);

            return ResponseEntity.ok("Impresión enviada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al imprimir: " + e.getMessage());
        }
    }

    private void imprimirArchivoPDF(File pdf) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Windows: usar comando nativo para imprimir
            String comando = "cmd /c start /min acrord32 /p /h \"" + pdf.getAbsolutePath() + "\"";
            Runtime.getRuntime().exec(comando);
        } else {
            // Linux o macOS: usar lpr
            String[] comando = { "lp", "-d", "nombre_impresora", pdf.getAbsolutePath() };
            Process process = new ProcessBuilder(comando).start();
        }
    }

    /* MergeComprobantes */
    @PostMapping(value = "/_comprobantes/merge", produces = "application/pdf")
    public ResponseEntity<byte[]> _mergeComprobantes(@RequestBody MergeReq req) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Opcional: ordenar por id
        // req.getItems().sort(Comparator.comparing(MergeItem::getIdfactura));

        var merger = new org.apache.pdfbox.multipdf.PDFMergerUtility();
        java.util.List<java.io.InputStream> fuentes = new java.util.ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                var out = new java.io.ByteArrayOutputStream()) {

            for (MergeItem it : req.getItems()) {
                if (it.getIdfactura() == null)
                    continue;

                String reportName;
                if (it.getIdmodulo() != null || it.getIdAbonado() != null) {
                    reportName = pickReportName(it.getIdAbonado(), it.getIdmodulo());
                } else {
                    // Usa la consulta si no te mandan esos datos desde el front
                    reportName = pickReportNameFromDb(conn, it.getIdfactura());
                }

                Jasper_DTO dto = new Jasper_DTO();
                dto.setReportName(reportName);

                Map<String, Object> params = new HashMap<>();
                // Si tu reporte espera Integer:
                params.put("idfactura", it.getIdfactura());
                dto.setParameters(params);

                ByteArrayOutputStream os = buildReports.buildReport(dto, conn);
                ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                fuentes.add(is);
                merger.addSource(is);
            }

            if (fuentes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            merger.setDestinationStream(out);
            merger.mergeDocuments(org.apache.pdfbox.io.MemoryUsageSetting.setupTempFileOnly());

            byte[] unido = out.toByteArray();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobantes_merged.pdf")
                    .contentLength(unido.length)
                    .body(unido);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            for (var is : fuentes)
                try {
                    is.close();
                } catch (Exception ignore) {
                }
        }
    }

    public static class MergeReq {
        private java.util.List<MergeItem> items;

        public java.util.List<MergeItem> getItems() {
            return items;
        }

        public void setItems(java.util.List<MergeItem> items) {
            this.items = items;
        }
    }

    /**
     * Item con la info MÍNIMA para escoger el reporte sin ir a BD.
     * Si no quieres enviar estos campos desde el front, puedes omitirlos
     * y consultar en BD (ver comentario más abajo).
     */
    public static class MergeItem {
        private Long idfactura;
        private Integer idmodulo; // opcional
        private Integer idAbonado; // opcional

        public Long getIdfactura() {
            return idfactura;
        }

        public void setIdfactura(Long idfactura) {
            this.idfactura = idfactura;
        }

        public Integer getIdmodulo() {
            return idmodulo;
        }

        public void setIdmodulo(Integer idmodulo) {
            this.idmodulo = idmodulo;
        }

        public Integer getIdAbonado() {
            return idAbonado;
        }

        public void setIdAbonado(Integer idAbonado) {
            this.idAbonado = idAbonado;
        }
    }

    private String pickReportName(Integer idAbonado, Integer idmodulo) {
        if (idAbonado != null && idAbonado > 0
                && (Integer.valueOf(3).equals(idmodulo) || Integer.valueOf(4).equals(idmodulo))) {
            return "CompPagoConsumoAgua";
        } else if (Integer.valueOf(27).equals(idmodulo)) {
            return "CompPagoConvenios";
        } else {
            return "CompPagoServicios";
        }
    }

    private String pickReportNameFromDb(Connection conn, Long idfactura) throws SQLException {
        String sql = "SELECT a.idabonado AS idAbonado, f.idmodulo AS idmodulo " +
                "FROM facturas f JOIN abonados a ON f.idabonado = a.idabonado " +
                "WHERE f.idfactura = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idfactura);
            try (ResultSet rs = ps.executeQuery()) {
                Integer idAbonado = null, idmodulo = null;
                if (rs.next()) {
                    idAbonado = rs.getInt("idAbonado");
                    if (rs.wasNull())
                        idAbonado = null;
                    idmodulo = rs.getInt("idmodulo");
                    if (rs.wasNull())
                        idmodulo = null;
                }
                return pickReportName(idAbonado, idmodulo);
            }
        }
    }

    private byte[] generarMergePdf(MergeReq req) throws Exception {
        try (Connection conn = dataSource.getConnection();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<MergeItem> itemsValidos = req.getItems().stream()
                    .filter(it -> it != null && it.getIdfactura() != null)
                    .collect(Collectors.toList());

            if (itemsValidos.isEmpty()) {
                throw new IllegalArgumentException("No existen items válidos para generar el merge");
            }

            Map<Long, ReportInfo> reportInfoByFactura = cargarReportInfoFaltante(conn, itemsValidos);
            List<JasperPrint> prints = new ArrayList<>(itemsValidos.size());

            for (MergeItem it : itemsValidos) {
                ReportInfo info = resolveReportInfo(it, reportInfoByFactura.get(it.getIdfactura()));
                String reportName = pickReportName(info.idAbonado(), info.idmodulo());

                Map<String, Object> params = new HashMap<>();
                params.put("idfactura", it.getIdfactura());

                JasperPrint jp = buildReports.fillFromCompiled(reportName, params, conn);
                prints.add(jp);
            }

            if (prints.isEmpty()) {
                throw new IllegalArgumentException("No se pudo generar ningún comprobante");
            }

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

            SimplePdfExporterConfiguration cfg = new SimplePdfExporterConfiguration();
            cfg.setCompressed(true);
            exporter.setConfiguration(cfg);
            exporter.exportReport();

            return out.toByteArray();
        }
    }

    private Map<Long, ReportInfo> cargarReportInfoFaltante(Connection conn, List<MergeItem> items) throws SQLException {
        List<Long> ids = items.stream()
                .filter(it -> it.getIdfactura() != null && it.getIdmodulo() == null && it.getIdAbonado() == null)
                .map(MergeItem::getIdfactura)
                .distinct()
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return new HashMap<>();
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = "SELECT f.idfactura, a.idabonado AS idAbonado, f.idmodulo AS idmodulo "
                + "FROM facturas f LEFT JOIN abonados a ON f.idabonado = a.idabonado "
                + "WHERE f.idfactura IN (" + placeholders + ")";

        Map<Long, ReportInfo> result = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setLong(i + 1, ids.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer idAbonado = rs.getInt("idAbonado");
                    if (rs.wasNull()) {
                        idAbonado = null;
                    }

                    Integer idmodulo = rs.getInt("idmodulo");
                    if (rs.wasNull()) {
                        idmodulo = null;
                    }

                    result.put(rs.getLong("idfactura"), new ReportInfo(idAbonado, idmodulo));
                }
            }
        }

        return result;
    }

    private ReportInfo resolveReportInfo(MergeItem item, ReportInfo dbInfo) {
        Integer idAbonado = item.getIdAbonado() != null ? item.getIdAbonado()
                : dbInfo != null ? dbInfo.idAbonado() : null;
        Integer idmodulo = item.getIdmodulo() != null ? item.getIdmodulo()
                : dbInfo != null ? dbInfo.idmodulo() : null;
        return new ReportInfo(idAbonado, idmodulo);
    }

    @PostMapping(value = "/__comprobantes/merge", produces = "application/pdf")
    public ResponseEntity<byte[]> __mergeComprobantes(@RequestBody MergeReq req) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try (Connection conn = dataSource.getConnection();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<JasperPrint> prints = new ArrayList<>(req.getItems().size());

            for (MergeItem it : req.getItems()) {
                if (it.getIdfactura() == null)
                    continue;

                String reportName = (it.getIdmodulo() != null || it.getIdAbonado() != null)
                        ? pickReportName(it.getIdAbonado(), it.getIdmodulo())
                        : pickReportNameFromDb(conn, it.getIdfactura());

                Map<String, Object> params = new HashMap<>();
                params.put("idfactura", it.getIdfactura().intValue());

                // (Opcional) Virtualizer si esperas muchas páginas:
                // JRSwapFile swap = new JRSwapFile(System.getProperty("java.io.tmpdir"), 1024,
                // 1024);
                // JRSwapFileVirtualizer virt = new JRSwapFileVirtualizer(100, swap, true);
                // params.put(JRParameter.REPORT_VIRTUALIZER, virt);

                JasperPrint print = buildReports.buildPrint(reportName, params, conn);
                prints.add(print);
            }

            if (prints.isEmpty())
                return ResponseEntity.badRequest().build();

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            SimplePdfExporterConfiguration cfg = new SimplePdfExporterConfiguration();
            cfg.setCompressed(true);
            exporter.setConfiguration(cfg);
            exporter.exportReport();

            byte[] unido = out.toByteArray();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=comprobantes_merged.pdf")
                    .contentLength(unido.length)
                    .body(unido);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/comprobantes/merge", produces = "application/pdf")
    public ResponseEntity<byte[]> mergeComprobantes(@RequestBody MergeReq req) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] pdf = generarMergePdf(req);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + MERGED_FILENAME)
                    .contentLength(pdf.length)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/comprobantes/merge/async")
    public ResponseEntity<MergeJobResponse> mergeComprobantesAsync(@RequestBody MergeReq req) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String jobId = UUID.randomUUID().toString();
        MergeJobStatus job = MergeJobStatus.pending(jobId, req.getItems().size());
        mergeJobs.put(jobId, job);

        CompletableFuture.runAsync(() -> {
            job.setStatus("PROCESSING");
            job.setMessage("Generando merge de comprobantes");
            try {
                byte[] pdf = generarMergePdf(req);
                job.setPdf(pdf);
                job.setStatus("DONE");
                job.setMessage("Merge generado correctamente");
            } catch (Exception e) {
                job.setStatus("ERROR");
                job.setMessage(e.getMessage());
            }
        });

        return ResponseEntity.accepted().body(MergeJobResponse.from(job));
    }

    @GetMapping("/comprobantes/merge/async/{jobId}")
    public ResponseEntity<MergeJobResponse> getMergeComprobantesJob(@PathVariable String jobId) {
        MergeJobStatus job = mergeJobs.get(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(MergeJobResponse.from(job));
    }

    @GetMapping(value = "/comprobantes/merge/async/{jobId}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> downloadMergeComprobantesJob(@PathVariable String jobId) {
        MergeJobStatus job = mergeJobs.get(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        if (!"DONE".equals(job.getStatus()) || job.getPdf() == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        byte[] pdf = job.getPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + MERGED_FILENAME)
                .contentLength(pdf.length)
                .body(pdf);
    }

    private record ReportInfo(Integer idAbonado, Integer idmodulo) {
    }

    public static class MergeJobResponse {
        private String jobId;
        private String status;
        private Integer totalItems;
        private String message;
        private Boolean ready;

        public static MergeJobResponse from(MergeJobStatus job) {
            MergeJobResponse response = new MergeJobResponse();
            response.setJobId(job.getJobId());
            response.setStatus(job.getStatus());
            response.setTotalItems(job.getTotalItems());
            response.setMessage(job.getMessage());
            response.setReady("DONE".equals(job.getStatus()));
            return response;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(Integer totalItems) {
            this.totalItems = totalItems;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getReady() {
            return ready;
        }

        public void setReady(Boolean ready) {
            this.ready = ready;
        }
    }

    public static class MergeJobStatus {
        private String jobId;
        private String status;
        private Integer totalItems;
        private String message;
        private byte[] pdf;

        public static MergeJobStatus pending(String jobId, Integer totalItems) {
            MergeJobStatus status = new MergeJobStatus();
            status.setJobId(jobId);
            status.setStatus("PENDING");
            status.setTotalItems(totalItems);
            status.setMessage("Trabajo en cola");
            return status;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(Integer totalItems) {
            this.totalItems = totalItems;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public byte[] getPdf() {
            return pdf;
        }

        public void setPdf(byte[] pdf) {
            this.pdf = pdf;
        }
    }
}


