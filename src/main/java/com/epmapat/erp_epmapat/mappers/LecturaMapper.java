package com.epmapat.erp_epmapat.mappers;

import com.epmapat.erp_epmapat.DTO.LecturaDto;
import com.epmapat.erp_epmapat.modelo.Lecturas;

public class LecturaMapper {

    public static LecturaDto toDto(Lecturas lectura) {
        if (lectura == null) return null;

        LecturaDto dto = new LecturaDto();
        dto.setIdlectura(lectura.getIdlectura());
        dto.setEstado(lectura.getEstado());
        dto.setFechaemision(lectura.getFechaemision());
        dto.setLecturaanterior(lectura.getLecturaanterior());
        dto.setLecturaactual(lectura.getLecturaactual());
        dto.setLecturadigitada(lectura.getLecturadigitada());
        dto.setMesesmulta(lectura.getMesesmulta());
        dto.setObservaciones(lectura.getObservaciones());
        dto.setIdemision(lectura.getIdemision());
        dto.setIdresponsable(lectura.getIdresponsable());
        dto.setIdcategoria(lectura.getIdcategoria());
        dto.setIdfactura(lectura.getIdfactura());
        dto.setTotal1(lectura.getTotal1());
        dto.setTotal31(lectura.getTotal31());
        dto.setTotal32(lectura.getTotal32());

        if (lectura.getIdrutaxemision_rutasxemision() != null)
            dto.setIdrutaxemision(lectura.getIdrutaxemision_rutasxemision().getIdrutaxemision());

        if (lectura.getIdnovedad_novedades() != null)
            dto.setIdnovedad(lectura.getIdnovedad_novedades().getIdnovedad());

        if (lectura.getIdabonado_abonados() != null)
            dto.setIdabonado(lectura.getIdabonado_abonados().getIdabonado());

        return dto;
    }

    public static Lecturas toEntity(LecturaDto dto) {
        if (dto == null) return null;

        Lecturas lectura = new Lecturas();
        lectura.setIdlectura(dto.getIdlectura());
        lectura.setEstado(dto.getEstado());
        lectura.setFechaemision(dto.getFechaemision());
        lectura.setLecturaanterior(dto.getLecturaanterior());
        lectura.setLecturaactual(dto.getLecturaactual());
        lectura.setLecturadigitada(dto.getLecturadigitada());
        lectura.setMesesmulta(dto.getMesesmulta());
        lectura.setObservaciones(dto.getObservaciones());
        lectura.setIdemision(dto.getIdemision());
        lectura.setIdresponsable(dto.getIdresponsable());
        lectura.setIdcategoria(dto.getIdcategoria());
        lectura.setIdfactura(dto.getIdfactura());
        lectura.setTotal1(dto.getTotal1());
        lectura.setTotal31(dto.getTotal31());
        lectura.setTotal32(dto.getTotal32());
        // Las relaciones ManyToOne deben setearse desde el Service
        return lectura;
    }
}