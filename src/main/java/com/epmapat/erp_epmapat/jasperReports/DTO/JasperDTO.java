package com.epmapat.erp_epmapat.jasperReports.DTO;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class JasperDTO {
    String reportName;
    Map<String, Object> parameters = new HashMap<>();
    String extencion;

}
