package com.epmapat.erp_epmapat.modelo.administracion;
import java.time.Instant;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Table(name="request_audit") @Getter @Setter
public class RequestAudit {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Instant fecha;
 @Column(length=16, nullable=false) private String metodo;
 @Column(length=2048, nullable=false) private String ruta;
 private int estado;
 private long duracionMs;
 @Column(length=64) private String ip;
 private Long usuarioId;
}
