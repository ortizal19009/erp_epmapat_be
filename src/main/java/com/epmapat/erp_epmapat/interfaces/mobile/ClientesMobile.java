package com.epmapat.erp_epmapat.interfaces.mobile;

import java.time.LocalDate;

import com.epmapat.erp_epmapat.interfaces.NacionalidadView;
import com.epmapat.erp_epmapat.interfaces.TpidentificaView;

public interface ClientesMobile {
    Long getIdcliente();
    String getCedula();
    String getNombre();
    String getDireccion();
    String getTelefono();
    String getEmail();
    LocalDate getFechanacimiento();

    // ⚠️ NOMBRES EXACTOS de la entidad
    TpidentificaView getIdtpidentifica_tpidentifica();
    NacionalidadView getIdnacionalidad_nacionalidad();

    String getUsername();
    Boolean getActivo();
    String getRol();
}


