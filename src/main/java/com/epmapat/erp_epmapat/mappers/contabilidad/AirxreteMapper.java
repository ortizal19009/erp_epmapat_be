package com.epmapat.erp_epmapat.mappers.contabilidad;

import org.springframework.stereotype.Component;

import com.epmapat.erp_epmapat.DTO.contabilidad.AirxreteCreateDTO;
import com.epmapat.erp_epmapat.modelo.contabilidad.Airxrete;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla10;

@Component
public class AirxreteMapper {

   public Airxrete toEntity(AirxreteCreateDTO dto, Retenciones retencion, Tabla10 tabla10) {
      Airxrete a = new Airxrete();
      a.setBaseimpair0(dto.baseimpair0());
      a.setBaseimpair12(dto.baseimpair12());
      a.setBaseimpairno(dto.baseimpairno());
      a.setBaseimpair(dto.baseimpair());
      a.setValretair(dto.valretair());
      a.setIdrete(retencion);
      a.setIdtabla10(tabla10);
      return a;
   }
}
