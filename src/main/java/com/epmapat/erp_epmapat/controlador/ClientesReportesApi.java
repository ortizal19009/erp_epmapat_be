package com.epmapat.erp_epmapat.controlador;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.epmapat.erp_epmapat.repositorio.ClientesReporteConsulta;

@RestController
@RequestMapping("/clientes/reportes")
public class ClientesReportesApi {
    private final ClientesReporteConsulta consulta;
    public ClientesReportesApi(ClientesReporteConsulta consulta) { this.consulta = consulta; }
    @GetMapping("/{tipo}")
    public List<Map<String,Object>> obtener(@PathVariable String tipo) {
        return switch(tipo) {
            case "listado" -> consulta.listado();
            case "cuentas" -> consulta.cuentas();
            case "pendientes" -> consulta.pendientes();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Reporte de clientes no valido");
        };
    }
}
