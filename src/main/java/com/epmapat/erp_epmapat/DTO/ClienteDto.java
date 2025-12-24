package com.epmapat.erp_epmapat.DTO;

public class ClienteDto {
    public Long idcliente; // null si creado offline (server lo asigna)
    public String localId; // UUID generado en móvil
    public String cedula;

    public Long idtpidentifica; // en vez de Tpidentifica object
    public Long idnacionalidad;
    public Long idpjuridica;

    public String nombre;
    public String direccion;
    public String telefono;
    public String fechanacimiento; // ISO: "2025-12-24" (más fácil)
    public Long discapacitado;
    public Long porcdiscapacidad;
    public Long porcexonera;
    public Long estado;
    public String email;

    public Boolean activo;
    public String rol; // si aplica para negocio, ok

    public Long updatedAt; // epoch millis para sync
}
