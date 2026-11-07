package com.epmapat.erp_epmapat.controlador;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/abonados")
@CrossOrigin(origins = "*")
public class AbonadoHistorialApi {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    public AbonadoHistorialApi(JdbcTemplate jdbc, ObjectMapper mapper) { this.jdbc = jdbc; this.mapper = mapper; }

    @GetMapping("/{idabonado}/historial")
    @Transactional(readOnly = true, isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public Map<String, Object> consultar(@PathVariable Long idabonado,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size < 1 || size > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginacion invalida");
        if (!Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM abonados WHERE idabonado=?)", Boolean.class, idabonado)))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abonado");
        Long total = jdbc.queryForObject("SELECT count(*) FROM abonado_historial WHERE idabonado=?", Long.class, idabonado);
        var content = jdbc.queryForList("SELECT idhistorial, fecha, usuario, persona, modulo, observacion, cambios::text FROM abonado_historial WHERE idabonado=? ORDER BY fecha DESC, idhistorial DESC LIMIT ? OFFSET ?", idabonado, size, (long)page * size);
        for (var item : content) {
            try { item.put("cambios", mapper.readTree((String)item.get("cambios"))); }
            catch (Exception ex) { throw new IllegalStateException("Historial invalido", ex); }
        }
        return Map.of("content", content, "number", page, "size", size, "totalElements", total, "totalPages", (total + size - 1) / size, "first", page == 0, "last", ((long)page + 1) * size >= total);
    }
}
