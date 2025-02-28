package com.epmapat.erp_epmapat.controlador;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.servicio.NtacreditoServicio;

@RestController
@RequestMapping("/ntacredito")
@CrossOrigin("*")
public class NtacreditoApi {
    private NtacreditoServicio ntacreditoServicio;
}
