package com.epmapat.erp_epmapat.jasperReports.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.epmapat.erp_epmapat.jasperReports.DTO.ReportParameterDTO;

@Component
public class ParamConverter {

   public Map<String, Object> convert(List<ReportParameterDTO> list) {
      Map<String, Object> map = new HashMap<>();
      if (list == null)
         return map;
      for (ReportParameterDTO p : list) {
         map.put(p.getName(), p.getValue());
      }
      return map;
   }
}
