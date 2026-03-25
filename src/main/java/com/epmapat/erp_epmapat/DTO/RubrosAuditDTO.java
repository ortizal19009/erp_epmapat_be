package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class RubrosAuditDTO {
    private Long idrubro;
    private String descripcion;
    private Boolean estado;
    private Boolean calculable;
    private BigDecimal valor;
    private Boolean swiva;
    private Integer tipo;
    private Long esiva;
    private Long esdebito;
    private Long facturable;
    private Long idmodulo_modulos;
    private Long usucrea;
    private Date feccrea;
    private Long usumodi;
    private Date fecmodi;
    private Boolean ajenos;

    public RubrosAuditDTO() {}

    public RubrosAuditDTO(Long idrubro, String descripcion, Boolean estado, Boolean calculable, BigDecimal valor,
            Boolean swiva, Integer tipo, Long esiva, Long esdebito, Long facturable, Long idmodulo_modulos,
            Long usucrea, Date feccrea, Long usumodi, Date fecmodi, Boolean ajenos) {
        this.idrubro = idrubro;
        this.descripcion = descripcion;
        this.estado = estado;
        this.calculable = calculable;
        this.valor = valor;
        this.swiva = swiva;
        this.tipo = tipo;
        this.esiva = esiva;
        this.esdebito = esdebito;
        this.facturable = facturable;
        this.idmodulo_modulos = idmodulo_modulos;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
        this.ajenos = ajenos;
    }

    // getters y setters
    public Long getIdrubro() { return idrubro; }
    public void setIdrubro(Long idrubro) { this.idrubro = idrubro; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public Boolean getCalculable() { return calculable; }
    public void setCalculable(Boolean calculable) { this.calculable = calculable; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public Boolean getSwiva() { return swiva; }
    public void setSwiva(Boolean swiva) { this.swiva = swiva; }
    public Integer getTipo() { return tipo; }
    public void setTipo(Integer tipo) { this.tipo = tipo; }
    public Long getEsiva() { return esiva; }
    public void setEsiva(Long esiva) { this.esiva = esiva; }
    public Long getEsdebito() { return esdebito; }
    public void setEsdebito(Long esdebito) { this.esdebito = esdebito; }
    public Long getFacturable() { return facturable; }
    public void setFacturable(Long facturable) { this.facturable = facturable; }
    public Long getIdmodulo_modulos() { return idmodulo_modulos; }
    public void setIdmodulo_modulos(Long idmodulo_modulos) { this.idmodulo_modulos = idmodulo_modulos; }
    public Long getUsucrea() { return usucrea; }
    public void setUsucrea(Long usucrea) { this.usucrea = usucrea; }
    public Date getFeccrea() { return feccrea; }
    public void setFeccrea(Date feccrea) { this.feccrea = feccrea; }
    public Long getUsumodi() { return usumodi; }
    public void setUsumodi(Long usumodi) { this.usumodi = usumodi; }
    public Date getFecmodi() { return fecmodi; }
    public void setFecmodi(Date fecmodi) { this.fecmodi = fecmodi; }
    public Boolean getAjenos() { return ajenos; }
    public void setAjenos(Boolean ajenos) { this.ajenos = ajenos; }
}
