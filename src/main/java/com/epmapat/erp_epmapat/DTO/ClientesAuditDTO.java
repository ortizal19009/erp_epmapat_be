package com.epmapat.erp_epmapat.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientesAuditDTO {
    private Long idcliente;
    private String cedula;
    private Long idtpidentifica;
    private String nombre;
    private String direccion;
    private String telefono;
    private LocalDate fechanacimiento;
    private Long discapacitado;
    private Long porcdiscapacidad;
    private Long porcexonera;
    private Long estado;
    private String email;
    private Long usucrea;
    private Long idnacionalidad;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
    private Long idpjuridica;
    private String username;
    private Boolean activo;
    private String password;
    private String rol;
}