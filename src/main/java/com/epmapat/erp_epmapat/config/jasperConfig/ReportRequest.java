package com.epmapat.erp_epmapat.config.jasperConfig;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReportRequest {
    private String reportName;
    private Map<String, Object> parameters;
    private List<Map<String, Object>> data;
}
