package com.epmapat.erp_epmapat.modelo;

import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import java.util.List;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usrxrutas")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Usrxrutas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idusrxruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario_usuarios")
    private Usuarios idusuario_usuarios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision_emisiones")
    private Emisiones idemision_emisiones;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<Rutas> rutas;
}
