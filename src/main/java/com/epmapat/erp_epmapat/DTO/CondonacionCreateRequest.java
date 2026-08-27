package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CondonacionCreateRequest {
    private List<Item> items;
    @JsonAlias("razoncondonacion")
    private String razonExoneracion;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getRazonExoneracion() {
        return razonExoneracion;
    }

    public void setRazonExoneracion(String razonExoneracion) {
        this.razonExoneracion = razonExoneracion;
    }

    public String getRazoncondonacion() {
        return razonExoneracion;
    }

    public void setRazoncondonacion(String razoncondonacion) {
        this.razonExoneracion = razoncondonacion;
    }

    public static class Item {
        private Long idfactura;
        private BigDecimal totalinteres;
        private BigDecimal totalmultas;

        public Long getIdfactura() {
            return idfactura;
        }

        public void setIdfactura(Long idfactura) {
            this.idfactura = idfactura;
        }

        public BigDecimal getTotalinteres() {
            return totalinteres;
        }

        public void setTotalinteres(BigDecimal totalinteres) {
            this.totalinteres = totalinteres;
        }

        public BigDecimal getTotalmultas() {
            return totalmultas;
        }

        public void setTotalmultas(BigDecimal totalmultas) {
            this.totalmultas = totalmultas;
        }
    }
}
