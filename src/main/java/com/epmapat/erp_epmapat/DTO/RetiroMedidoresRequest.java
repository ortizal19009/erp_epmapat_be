package com.epmapat.erp_epmapat.DTO;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public record RetiroMedidoresRequest(
    @NotEmpty List<@Positive Long> cuentas,
    @Positive Long idusuario,
    @Size(max = 180) String observacion) {}
