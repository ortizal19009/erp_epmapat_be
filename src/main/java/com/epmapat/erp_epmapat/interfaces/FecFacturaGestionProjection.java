package com.epmapat.erp_epmapat.interfaces;

import java.time.LocalDate;

public interface FecFacturaGestionProjection {
   Long getIdfactura();
   String getClaveacceso();
   String getSecuencial();
   String getXmlautorizado();
   String getErrores();
   String getEstado();
   String getEstablecimiento();
   String getPuntoemision();
   String getDireccionestablecimiento();
   LocalDate getFechaemision();
   String getTipoidentificacioncomprador();
   String getGuiaremision();
   String getRazonsocialcomprador();
   String getIdentificacioncomprador();
   String getDireccioncomprador();
   String getTelefonocomprador();
   String getEmailcomprador();
   String getConcepto();
   String getReferencia();
   String getRecaudador();
   Long getUsuariocobro();
   Boolean getSwmail();
   Integer getMail_intentos();
   String getMail_error();
   String getEmail_estado();
}
