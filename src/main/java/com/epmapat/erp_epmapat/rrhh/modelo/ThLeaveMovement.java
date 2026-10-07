package com.epmapat.erp_epmapat.rrhh.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Append-only ledger; corrections are new movements, never edits of prior entries. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "th_leave_movements", uniqueConstraints = {
        @UniqueConstraint(name = "uq_th_leave_movements_request_type", columnNames = {"idrequest", "tipo"})
})
public class ThLeaveMovement {
    public enum Tipo { APERTURA, CONSUMO, REINTEGRO, AJUSTE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idmovement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idbalance", nullable = false, updatable = false)
    private ThLeaveBalance balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrequest", updatable = false)
    private ThLeaveRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idmovement_origen", updatable = false, unique = true)
    private ThLeaveMovement origen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private Tipo tipo;
    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal dias;
    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal disponibles_antes;
    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal disponibles_despues;
    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal usados_antes;
    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal usados_despues;
    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal asignados;
    @Column(nullable = false, updatable = false)
    private Long usuario;
    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;
    @Column(updatable = false, columnDefinition = "text")
    private String motivo;
    @Column(updatable = false, length = 36)
    private String clave;
}
