package com.epmapat.erp_epmapat.interfaces.mobile;

import java.time.LocalDate;

public interface AbonadosMobile {
    Long getIdabonado();

    String getNromedidor();

    Long getEstado();

    LocalDate getFechainstalacion();

    String getDireccionubicacion();

    String getObservacion();

    Long getIdresponsable();

    Long getIdcategoria_categorias();

    Long getIdruta_rutas();

    Long getIdcliente_clientes();

    Long getIdestadom_estadom();

    Boolean getMunicipio();

    Boolean getAdultomayor();

    Boolean getSwalcantarillado();

    String getGeolocalizacion();

}
