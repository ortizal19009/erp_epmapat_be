package com.epmapat.erp_epmapat.servicio;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RuntimeEnvironmentService {

    private final Environment environment;

    public RuntimeEnvironmentService(Environment environment) {
        this.environment = environment;
    }

    public List<String> getActiveProfiles() {
        return Arrays.asList(environment.getActiveProfiles());
    }

    public String getOsName() {
        return System.getProperty("os.name", "desconocido");
    }

    public boolean isWindows() {
        return getOsName().toLowerCase().contains("win");
    }

    public boolean isLinux() {
        return getOsName().toLowerCase().contains("linux");
    }

    public boolean isProduction() {
        return getActiveProfiles().stream().anyMatch("prod"::equalsIgnoreCase);
    }

    public String getEnvironmentLabel() {
        if (isProduction() && isLinux()) {
            return "PROD_UBUNTU";
        }
        if (isProduction()) {
            return "PRODUCCION";
        }
        if (isWindows()) {
            return "LOCAL_WINDOWS";
        }
        if (isLinux()) {
            return "LINUX";
        }
        return "DESCONOCIDO";
    }

    public String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return "desconocido";
        }
    }
}
