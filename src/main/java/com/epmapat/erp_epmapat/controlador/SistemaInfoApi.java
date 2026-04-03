package com.epmapat.erp_epmapat.controlador;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.servicio.RuntimeEnvironmentService;

@RestController
@RequestMapping("/sistema")
public class SistemaInfoApi {

    private final RuntimeEnvironmentService runtimeEnvironmentService;

    @Value("${server.port:8080}")
    private String serverPort;

    public SistemaInfoApi(RuntimeEnvironmentService runtimeEnvironmentService) {
        this.runtimeEnvironmentService = runtimeEnvironmentService;
    }

    @GetMapping("/entorno")
    public Map<String, Object> getEntorno() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("entorno", runtimeEnvironmentService.getEnvironmentLabel());
        out.put("produccion", runtimeEnvironmentService.isProduction());
        out.put("sistemaOperativo", runtimeEnvironmentService.getOsName());
        out.put("perfilesActivos", runtimeEnvironmentService.getActiveProfiles());
        out.put("host", runtimeEnvironmentService.getHostName());
        out.put("puerto", serverPort);
        return out;
    }
}
