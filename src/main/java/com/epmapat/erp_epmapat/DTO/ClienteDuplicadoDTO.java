package com.epmapat.erp_epmapat.DTO;

public record ClienteDuplicadoDTO(
    Long idCliente,
    String cedula,
    String nombre,
    String direccion,
    String telefono,
    String email,
    Boolean activo
) {}
