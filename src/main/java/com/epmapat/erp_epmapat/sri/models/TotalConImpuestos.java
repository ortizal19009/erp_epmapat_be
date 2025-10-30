package com.epmapat.erp_epmapat.sri.models;

import lombok.Data;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class TotalConImpuestos {

    @XmlElement(name = "totalImpuesto", required = true)
    private List<TotalImpuesto> totalImpuestos;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalImpuesto {
        @XmlElement(name = "codigo", required = true)
        private String codigo; // "2" IVA, "3" ICE, etc.
        
        @XmlElement(name = "codigoPorcentaje", required = true)
        private String codigoPorcentaje; // "0" 0%, "2" 12%, etc.
        
        @XmlElement(name = "baseImponible", required = true)
        private BigDecimal baseImponible;
        
        @XmlElement(name = "valor", required = true)
        private BigDecimal valor;
    }
}