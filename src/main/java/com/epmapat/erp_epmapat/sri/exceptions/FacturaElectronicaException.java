package com.epmapat.erp_epmapat.sri.exceptions;

public class FacturaElectronicaException extends RuntimeException {

    public FacturaElectronicaException() {
        super();
    }

    public FacturaElectronicaException(String message) {
        super(message);
    }

    public FacturaElectronicaException(String message, Throwable cause) {
        super(message, cause);
    }

    public FacturaElectronicaException(Throwable cause) {
        super(cause);
    }
}
