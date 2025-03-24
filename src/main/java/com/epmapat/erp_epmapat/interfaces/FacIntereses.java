package com.epmapat.erp_epmapat.interfaces;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public interface FacIntereses {
	Long getIdFactura();
	Float getSuma();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate getFeccrea();
	Long getFormapago();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate getFechatransferencia();

}
