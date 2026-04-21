package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValorFactDTO {
    private Long idfactura;
    private Float subtotal;
    private BigDecimal total;
    private BigDecimal interes;
    private BigDecimal iva;
    private Integer numfacturas;
    private Long cuenta;
    private Long idcliente;
    private String nombre;
    private String cedula;
    private String direccionubicacion;
    private String modulo;
    private Long estado;
    private Integer pagado;
    private String nrofactura;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fectransferencia;
    private Long formapago;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate feccrea;
}
