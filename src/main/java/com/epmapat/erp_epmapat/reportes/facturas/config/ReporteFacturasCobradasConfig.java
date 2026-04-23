package com.epmapat.erp_epmapat.reportes.facturas.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.epmapat.erp_epmapat.commons.JasperReportManager;
import com.epmapat.erp_epmapat.reportes.facturas.interfaces.i_ReporteFacturasCobradas_G;
import com.epmapat.erp_epmapat.reportes.facturas.servicios.s_ReporteFacturasCobradas_G;

@Configuration
public class ReporteFacturasCobradasConfig {

    @Bean
    public i_ReporteFacturasCobradas_G i_ReporteFacturasCobradas_G(
            JasperReportManager reportManager,
            DataSource dataSource) {
        return new s_ReporteFacturasCobradas_G(reportManager, dataSource);
    }
}
