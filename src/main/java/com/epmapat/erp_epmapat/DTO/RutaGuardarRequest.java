package com.epmapat.erp_epmapat.DTO;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;
public record RutaGuardarRequest(@Size(max=8) String codigo,
    @NotBlank @Size(max=255) String descripcion, @NotNull @Min(0) Long orden,
    @NotNull Boolean estado, @Positive Long usuario, @Size(max=240) String motivo,
    @Valid @Size(max=1000) List<@NotNull @Valid CuentaTraslado> cuentas) {
    public record CuentaTraslado(@NotNull @Positive Long cuenta, @Positive Long rutaOrigen) {}
}
