package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.administracion.CorreosEnviados;

@Repository
public interface CorreosEnviadosR extends JpaRepository<CorreosEnviados, Long> {

    List<CorreosEnviados> findByModuloContainingIgnoreCaseAndEstadoContainingIgnoreCaseOrderByFechaenvioDesc(
            String modulo,
            String estado);
}
