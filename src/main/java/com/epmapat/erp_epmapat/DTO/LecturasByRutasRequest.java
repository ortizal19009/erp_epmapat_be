package com.epmapat.erp_epmapat.DTO;

import java.util.List;
import javax.validation.constraints.NotEmpty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturasByRutasRequest {
    @NotEmpty(message = "La lista de IDs no puede estar vacía")
    private List<Long> ids;
}