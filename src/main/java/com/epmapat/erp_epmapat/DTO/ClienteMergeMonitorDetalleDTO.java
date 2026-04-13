package com.epmapat.erp_epmapat.DTO;

import java.util.List;

import com.epmapat.erp_epmapat.modelo.ClienteMerge;
import com.epmapat.erp_epmapat.modelo.ClienteMergeAbonado;
import com.epmapat.erp_epmapat.modelo.ClienteMergeCliente;
import com.epmapat.erp_epmapat.modelo.ClienteMergeFactura;
import com.epmapat.erp_epmapat.modelo.ClienteMergeLectura;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClienteMergeMonitorDetalleDTO {
    private ClienteMerge merge;
    private List<ClienteMergeCliente> clientes;
    private List<ClienteMergeAbonado> abonados;
    private List<ClienteMergeFactura> facturas;
    private List<ClienteMergeLectura> lecturas;
}
