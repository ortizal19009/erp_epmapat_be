package com.epmapat.erp_epmapat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "microroutes")
public class MicroserviceConfig {
    private String recaudacion;
}
