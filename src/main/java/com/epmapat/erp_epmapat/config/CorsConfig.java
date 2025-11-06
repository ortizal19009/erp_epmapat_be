// src/main/java/com/epmapat/erp_epmapat/config/CorsConfig.java
package com.epmapat.erp_epmapat.config;

import java.util.List;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter() {
    CorsConfiguration cfg = new CorsConfiguration();

    // Debe coincidir EXACTO con el Origin del navegador: http://192.168.0.88 (puerto 80)
    //cfg.setAllowedOrigins(List.of("*"));
    // Si no usas cookies/sesión: usa patrón y desactiva credenciales:
     cfg.addAllowedOriginPattern("*"); cfg.setAllowCredentials(false);

    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
    cfg.setExposedHeaders(List.of("Content-Disposition"));
    //cfg.setAllowCredentials(false);   // pon false si no hay cookies
    cfg.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);

    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(Integer.MIN_VALUE); // muy temprano en la cadena
    return bean;
  }
}
