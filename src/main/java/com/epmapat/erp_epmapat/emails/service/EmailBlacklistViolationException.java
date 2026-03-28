package com.epmapat.erp_epmapat.emails.service;

public class EmailBlacklistViolationException extends IllegalArgumentException {
    public EmailBlacklistViolationException(String message) {
        super(message);
    }
}
