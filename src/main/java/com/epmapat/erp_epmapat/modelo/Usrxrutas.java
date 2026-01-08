package com.epmapat.erp_epmapat.modelo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usrxrutas")
public class Usrxrutas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idusrxruta;

    private Long idusuario_usuarios;

    private Long idemision_emisiones;

    @Column(columnDefinition = "jsonb")
    private JsonNode rutas;
}
