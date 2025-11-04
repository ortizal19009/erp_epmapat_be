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

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDTO;
import com.epmapat.erp_epmapat.jasperReports.services.BuildReports;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@RestController
@RequestMapping("/jasperReports")

public class BuildReportsApi {
    @Autowired
    private BuildReports buildReports;
    @Autowired
    private DataSource dataSource;

    @GetMapping("/reportes")
    public ResponseEntity<String> reportesGetNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("El método GET no está permitido en este endpoint. Usa POST.");
    }

    @PostMapping("/reportes")
    public ResponseEntity<Resource> generarPdfFactura(@RequestBody JasperDTO jasperDTO) {
        try {
            JasperDTO dto = new JasperDTO();
            dto.setReportName(jasperDTO.getReportName());

            Map<String, Object> params = new HashMap<>();

            for (Map.Entry<String, Object> entry : jasperDTO.getParameters().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value == null)
                    continue;

                if ("desde".equalsIgnoreCase(key) || "hasta".equalsIgnoreCase(key) || "tope".equalsIgnoreCase(key)) {
                    params.put(key, parseDateToSQLType(value.toString()));
                } else if ("hdesde".equalsIgnoreCase(key) || "hhasta".equalsIgnoreCase(key)) {
                    params.put(key, parseToSqlTime(value.toString()));
                } else {
                    params.put(key, normalizeParameterValue(key, value));
                }

            }

            dto.setParameters(params);

            ByteArrayOutputStream outputStream;
            try (Connection conn = dataSource.getConnection()) {
                outputStream = buildReports.buildReport(dto, conn);
            }

            ByteArrayInputStream pdfStream = new ByteArrayInputStream(outputStream.toByteArray());
            InputStreamResource resource = new InputStreamResource(pdfStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + jasperDTO.getReportName() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(outputStream.size())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Object parseDateToSQLType(String value) throws ParseException {
        String[] formats = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                Date parsed = sdf.parse(value.trim());
                if (format.contains("HH")) {
                    return new java.sql.Timestamp(parsed.getTime());
                } else {
                    return new java.sql.Date(parsed.getTime());
                }
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

    private Object normalizeParameterValue(String key, Object value) {
        if (value instanceof Integer) {
            return value;
        } else if (value instanceof Long) {
            Long longVal = (Long) value;
            if (longVal >= Integer.MIN_VALUE && longVal <= Integer.MAX_VALUE) {
                return longVal.intValue();
            } else {
                throw new IllegalArgumentException("El valor Long excede el rango de Integer");
            }
        } else if (value instanceof java.util.Date) {
            return value; // Devuelve la fecha tal cual
        } else if (value instanceof String) {
            try {
                Integer intVal = Integer.valueOf((String) value);
                return intVal;
            } catch (NumberFormatException e) {
                return value; // o lanza excepción si sabes que debe ser Integer
            }
        }
        return value;
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

                JasperDTO dto = new JasperDTO();
                dto.setReportName(reportName);

                Map<String, Object> params = new HashMap<>();
                // Si tu reporte espera Integer:
                params.put("idfactura", it.getIdfactura().intValue());
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

  try (Connection conn = dataSource.getConnection();
       ByteArrayOutputStream out = new ByteArrayOutputStream()) {

    List<JasperPrint> prints = new ArrayList<>(req.getItems().size());

    for (MergeItem it : req.getItems()) {
      if (it.getIdfactura() == null) continue;

      String reportName =
          (it.getIdmodulo()!=null || it.getIdAbonado()!=null)
          ? pickReportName(it.getIdAbonado(), it.getIdmodulo())
          : pickReportNameFromDb(conn, it.getIdfactura());

      Map<String,Object> params = new HashMap<>();
      params.put("idfactura", it.getIdfactura().intValue());

      JasperPrint jp = buildReports.fillFromCompiled(reportName, params, conn);
      prints.add(jp);
    }

    if (prints.isEmpty()) return ResponseEntity.badRequest().build();

    // Exportar TODOS los JasperPrint a UN solo PDF
    JRPdfExporter exporter = new JRPdfExporter();
    exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

    SimplePdfExporterConfiguration cfg = new SimplePdfExporterConfiguration();
    cfg.setCompressed(true);                 // PDF comprimido
    // cfg.setMetadataTitle("Comprobantes EP"); // opcional
    exporter.setConfiguration(cfg);

    exporter.exportReport();
    byte[] pdf = out.toByteArray();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=comprobantes_merged.pdf")
        .contentLength(pdf.length)
        .body(pdf);

  } catch (Exception e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
}
