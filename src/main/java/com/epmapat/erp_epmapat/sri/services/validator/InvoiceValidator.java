package com.epmapat.erp_epmapat.sri.services.validator;

import java.util.Map;

import org.springframework.stereotype.Service;
@Service
public class InvoiceValidator {

    public static void validate(Map<String, String> facturaData) {
        if (facturaData.get("secuencial") == null || facturaData.get("secuencial").isEmpty()) {
            throw new IllegalArgumentException("El XML no contiene el campo requerido: secuencial");
        }
        if (facturaData.get("importeTotal") == null || facturaData.get("importeTotal").isEmpty()) {
            throw new IllegalArgumentException("El XML no contiene el campo requerido: importeTotal");
        }
        if (facturaData.get("rucEmisor") == null || facturaData.get("rucEmisor").isEmpty()) {
            throw new IllegalArgumentException("El XML no contiene el campo requerido: rucEmisor");
        }
    }
}

