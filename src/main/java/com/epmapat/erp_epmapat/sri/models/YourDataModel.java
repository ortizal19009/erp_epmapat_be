package com.epmapat.erp_epmapat.sri.models;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class YourDataModel {
    private String field1;
    private int field2;
    private List<String> items;
    
    // Getters y Setters
    // Constructor
}