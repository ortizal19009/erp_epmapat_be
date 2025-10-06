package com.epmapat.erp_epmapat.sri.models;

import lombok.Data;
import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoTributaria {
    @XmlElement(name = "ambiente")
    private Byte ambiente;
    @XmlElement(name = "tipoEmision")
    private Byte tipoEmision;
    
    @XmlElement(name = "razonSocial")
    private String razonSocial;
    
    @XmlElement(name = "nombreComercial")
    private String nombreComercial;
        @XmlElement(name = "ruc")
    private String ruc;
    @XmlElement(name = "claveAcceso")
    private String claveAcceso;
    @XmlElement(name = "codDoc")
    private String codDoc;
    
    @XmlElement(name = "estab")
    private String estab;
    
    @XmlElement(name = "ptoEmi")
    private String ptoEmi;
    
    @XmlElement(name = "secuencial")
    private String secuencial;
    
    @XmlElement(name = "dirMatriz")
    private String dirMatriz;
}